package hello.goodnews.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/api/home")
@RestController
public class HomeController {
    @GetMapping()
    public String home()
    {
        return "home";
    }

}
