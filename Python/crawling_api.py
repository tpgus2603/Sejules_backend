#!/usr/bin/env python
# coding: utf-8

# In[48]:


from sqlalchemy import create_engine
from bs4 import BeautifulSoup
from openai import OpenAI
import numpy as np
import pandas as pd
import re
import time
import asyncio
import aiohttp
import nest_asyncio
import mysql.connector
import requests


# In[4]:


Keyword = ["정치", "경제", "사회", "세계", "IT/기술", "노동", "환경", "인권", "문화", "라이프"]
Article_data = pd.DataFrame()


# In[5]:


# 네이버 API 중 포털 검색을 통해 Naver News의 기사를 가져오고 이를 바탕으로 파일을 생성하는 방법

import json
import urllib.request

# Naver API 인증 정보
client_id = ""
client_secret = ""

# 검색어 및 API URL
for i in Keyword:
    
    query = i
    encText = urllib.parse.quote(query)
    url = "https://openapi.naver.com/v1/search/news.json"

    # 한 번에 가져올 결과 수 (최대 100)
    display = 100  # 1~100 범위 가능
    # 최대 시작 페이지 설정 (API 정책에 따라 1000건 제한)
    max_results = 1000  # 결과 총 개수 (API 최대치: 1000개)
    news_urls = []  # 뉴스 기사 URL 저장

    # 요청 함수
    def fetch_news(start):
        request_url = f"{url}?query={encText}&display={display}&start={start}"
        request = urllib.request.Request(request_url)
        request.add_header("X-Naver-Client-Id", client_id)
        request.add_header("X-Naver-Client-Secret", client_secret)
        response = urllib.request.urlopen(request)
        rescode = response.getcode()

        if rescode == 200:
            response_body = response.read()
            return json.loads(response_body.decode('utf-8'))['items']
        else:
            print(f"Error Code: {rescode}")
            return []

    # 검색 결과 반복 요청
    for start in range(1, max_results + 1, display):
        items = fetch_news(start)
        if not items:  # 더 이상 결과가 없으면 종료
            break
        # URL 필터링 및 저장
        for item in items:
            if item['link'].startswith("https://n.news.naver.com/mnews/article/"):
                news_urls.append(item['link'])

# 결과 확인
print(f"총 저장된 뉴스 기사 수: {len(news_urls)}")
print(news_urls)


# In[27]:


Role = "정치, 경제, 사회, 세계, IT/기술, 노동, 환경, 인권, 문화, 라이프 각각의 전문가 10명이 한 자리에 모였습니다. 이들은 뉴스 기사의 <제목>과 <내용>을 가지고 뉴스의 카테고리를 정하는 한편 뉴스의 내용을 요약할 것입니다. 요약과 주제 선정을 위해 이들은 각각 자신의 도메인 지식을 최대한 활용하여 토의하고 열띤 토론을 펼칠 것입니다. 토론 시 이들은 자신의 생각과 타인의 생각을 논리적으로 비판하고 정확한 주제 선정과 요약을 위해 업데이트 되는 정보를 활용하는 것을 아끼지 않을 것입니다. 토론이 끝난 뒤 각 전문가들은 가장 알맞다고 생각하는 카테고리에 투표합니다. 그리고 투표 결과에 따라 가장 많이 투표를 받은 첫 번째 주제가 대표 주제가 됩니다. "
Form = "#출력 형식:\n주제: 정치, 경제, 사회, 세계, IT/기술, 노동, 환경, 인권, 문화, 라이프 중 한 가지로 주제를 선정하여 제시한다. \n제시는 다음과 같이 대괄호에 넣어서 표현해줘:[첫 번째 주제], \n\n기사 내용 요약: 한 줄 씩 각 총 세줄로 기사를 요약하여 제시한다. 이 때 한 줄 당 글자 수는 15자를 넘지 말아야 한다. \n제시는 다음과 같이 진행: [첫 번째 요약], [두 번째 요약], [세 번째 요약]\n\n핵심 기사 내용: 30자 내외로 기사의 전체 내용을 요약하여 제시한다. 단 기사 요약은 최대한 내용에 가깝게 하여 진행한다.\n제시는 다음과 같이 진행: [핵심 기사 내용]\n\n키워드 단어: 기사에서 사용된 핵심 키워드 단어를 3가지 제시한다.\n제시는 다음과 같이 진행: [키워드 단어1], [키워드 단어2], [키워드 단어3] \\nn제시된 키워드 단어를 설명한다. \n제시는 다음과 같이 진행: [키워드 단어1 설명], [키워드 단어2 설명], [키워드 단어3 설명]"


# In[39]:


# 중복 제거 및 짝 관리 함수
def remove_keyword_duplicates(keywords, descriptions):
    # 키워드와 설명을 묶어서 중복 제거
    combined = list(dict.fromkeys(zip(keywords, descriptions)))
    # 분리하여 반환
    unique_keywords, unique_descriptions = zip(*combined) if combined else ([], [])
    return list(unique_keywords), list(unique_descriptions)

# 안전하게 리스트에서 값을 추출하는 함수
def safe_get(lst, index, default=None):
    return lst[index] if index < len(lst) else default

# OpenAI GPT 호출 함수
async def fetch_gpt_response(session, url, headers, data):
    async with session.post(url, headers=headers, json=data) as response:
        return await response.json()

# 뉴스 기사 크롤링 및 GPT 요청 처리 함수
async def process_article(session, url, headers):
    try:
        # 기사 크롤링
        webpage = requests.get(url)
        soup = BeautifulSoup(webpage.content, "html.parser")
        article_name = soup.h2.string if soup.h2 else "제목 없음"
        media_temp = soup.find('img')
        media_company = media_temp.get('title') if media_temp else "신문사 없음"
        date_temp = soup.select("div.media_end_head.go_trans > div.media_end_head_info.nv_notrans > div.media_end_head_info_datestamp > div:nth-child(1) > span")
        article_date = date_temp[0].text if date_temp else "날짜 없음"
        repoter_temp = soup.select("div.media_end_head.go_trans > div.media_end_head_info.nv_notrans > div.media_end_head_journalist > a > em")
        article_repoter = repoter_temp[0].text if repoter_temp else "기자 없음"
        article_temp = soup.select("#dic_area")
        clean_article = article_temp[0].text.replace("\n", "") if article_temp else "내용 없음"

        data = {
            "model": "gpt-4o-mini",
            "messages": [
                {"role": "system", "content": Role},
                {"role": "user", "content": Form},
                {"role": "user", "content": "제시된 조건을 지키며 출력 형식에 맞춰 답을 제공해줘"},
                {"role": "user", "content": article_name},
                {"role": "user", "content": clean_article}
            ],
            "temperature": 0.0
        }
        
        # 요청 간 지연 추가
        await asyncio.sleep(60)  # 요청 사이에  대기
        
        # GPT API 호출
        gpt_response = await fetch_gpt_response(session, "https://api.openai.com/v1/chat/completions", headers, data)
        summarize = gpt_response.get("choices", [{}])[0].get("message", {}).get("content", "")
    
        # 항목별 패턴 정의
        patterns = {
            "주제": r'주제:\s*(\[[^\]]+\](?:,\s*\[[^\]]+\])*)',
            "기사 내용 요약": r'기사 내용 요약:\s*(\[[^\]]+\](?:,\s*\[[^\]]+\])*)',
            "핵심 기사 내용": r'핵심 기사 내용:\s*\[(.*?)\]',
            "키워드 단어": r'키워드 단어:\s*(\[[^\]]+\](?:,\s*\[[^\]]+\])*)',
            "키워드 단어 설명": r'키워드 단어 설명:\s*(\[[^\]]+?:\s[^\]]+\](?:,\s*\[[^\]]+?:\s[^\]]+\])*)'
        }
        
        #결과 저장 딕셔너리
        result = {}
        for key, pattern in patterns.items():
            match = re.search(pattern, summarize)
            if match:
                if key in ["주제", "키워드 단어", "기사 내용 요약"]:
                    items = match.group(1).split("], [")
                    result[key] = [item.strip("[]") for item in items]
                elif key == "핵심 기사 내용":
                    result[key] = [match.group(1)]
                elif key == "키워드 단어 설명":
                    descriptions = [description for _, description in re.findall(r'\[([^\]]+?):\s([^\]]+?)\]', match.group(1))]
                    result["키워드 단어 설명"] = descriptions

        # 키워드 중복 제거
        keywords = result.get("키워드 단어", [])
        descriptions = result.get("키워드 단어 설명", [])
        unique_keywords, unique_descriptions = remove_keyword_duplicates(keywords, descriptions)

        # 기사 데이터를 구성하여 반환
        return {
            "제목": article_name,
            "기자": article_repoter,
            "일자": article_date,
            "기사 전문": clean_article,
            "신문사": media_company,
            "URL": url,
            "주제 1": safe_get(result.get("주제", []), 0),
            "기사 요약 1": safe_get(result.get("기사 내용 요약", []), 0),
            "기사 요약 2": safe_get(result.get("기사 내용 요약", []), 1),
            "기사 요약 3": safe_get(result.get("기사 내용 요약", []), 2),
            "전체 요약": safe_get(result.get("핵심 기사 내용", []), 0),
            "키워드 1": safe_get(unique_keywords, 0),
            "키워드 1 설명": safe_get(unique_descriptions, 0),
            "키워드 2": safe_get(unique_keywords, 1),
            "키워드 2 설명": safe_get(unique_descriptions, 1),
            "키워드 3": safe_get(unique_keywords, 2),
            "키워드 3 설명": safe_get(unique_descriptions, 2)
        }
    
    except Exception as e:
        print(f"Error processing {url}: {e}")
        return None

    
    
# 전체 뉴스 처리
async def process_news(news_urls, api_key):
    headers = {"Authorization": f"Bearer {api_key}"}
    async with aiohttp.ClientSession() as session:
        tasks = [process_article(session, url, headers) for url in news_urls]
        results = await asyncio.gather(*tasks)
        return [res for res in results if res]

# 실행
api_key = ""

# 현재 이벤트 루프를 재사용 가능하도록 설정
nest_asyncio.apply()

# 기존 코드 실행
result_data = asyncio.run(process_news(news_urls, api_key))

# 데이터프레임 생성
Article_data = pd.DataFrame(result_data)
print(Article_data)


# In[44]:


clean_Article_data = Article_data.dropna()


# In[46]:


csv_file_path = "article_data.csv"
clean_Article_data.to_csv(csv_file_path, index=False, encoding="utf-8-sig")
print(f"CSV 파일이 '{csv_file_path}' 이름으로 저장되었습니다.")


# In[47]:


# 1. MySQL 데이터베이스 연결
connection = mysql.connector.connect(
    host="localhost",        # MySQL 서버 주소
    user="your_username",    # 사용자 이름
    password="your_password",# 비밀번호
    database="your_database" # 데이터베이스 이름
)

# SQLAlchemy 엔진 생성 (Pandas와 연동하기 위해)
engine = create_engine("mysql+mysqlconnector://your_username:your_password@localhost/your_database")

# 3. 테이블에 데이터 삽입
# 'users' 테이블로 데이터를 삽입 (기존에 테이블이 없다면 생성)
clean_Article_data.to_sql(name='users', con=engine, if_exists='replace', index=False)

print("Data inserted successfully!")

# 4. 연결 닫기
connection.close()


# In[ ]:




