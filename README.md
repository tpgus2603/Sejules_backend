

세줄스 서비스
--


- 네이버 뉴스 API를 활용하여 다양한 카테고리의 뉴스 기사를 크롤링하고, GPT API를 이용하여 기사를 자동으로 요약하고 주제를 분류한다
- 사용자는 **정치, 경제, 사회, 세계, IT/기술, 노동, 환경, 인권, 문화, 라이프** 카테고리에 해당하는 뉴스를 가져와 분석할 수 있다.
- 사용자가 원하는카테고리별로 핵심 정보 제공과 3줄 요약, 3가지 핵심 키워드를 제공한다

시스템 개요
--

![image](https://github.com/user-attachments/assets/2a14ea11-e550-46a5-b5c2-762b99aaf68f)

**파이썬을 이용하여 네이버 뉴스 크롤링 및 AI 기반 뉴스 요약** 

- 네이버 뉴스 API를 활용한 기사 수집
- BeautifulSoup을 이용한 웹 크롤링
- OpenAI GPT 모델을 활용한 뉴스 자동 요약 및 주제 분류
- 비동기기반으로 대량의 뉴스 데이터를 빠르게 처리
- MySQL 데이터베이스 연동을 통해 뉴스 데이터 저장
- crontab을 활용하여 자정마다 해당 로직 수행

**스프링부트를 활용한 앱의 API서버 구축**

- Rest api로 클라이언트의 요청에 맞게 json반환
- JPA, QueryDSl사용
- Aws의 ec2와 rds를 이용하여 배포
- 자정 마다 서버 재가동 스크립트 실행


회원가입
--
![image](https://github.com/user-attachments/assets/79b72ace-1ae3-4458-894c-16c2ad3fe82b)

홈 - 주제별 / 오늘의 뉴스
--
![image](https://github.com/user-attachments/assets/109c6369-be8e-45fe-a7cf-c9c2aaa99eec)


검색
--
![image](https://github.com/user-attachments/assets/1be446f4-3676-4248-8120-edede773b0fe)

카테고리/스크랩
--
![image](https://github.com/user-attachments/assets/2e0c1c95-c27c-41d8-9844-b6fbd4832f0e)

뉴스본문
--
![image](https://github.com/user-attachments/assets/4c21b878-077c-4507-b27f-7b7289c7557e)




BM구조
-
![image](https://github.com/user-attachments/assets/1d9850be-346e-4677-b2ed-de2db8a6bbc2)

