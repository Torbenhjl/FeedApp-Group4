package com.oblig1.oblig1.Controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class TestController {
    @GetMapping("/helloUser")
    public String helloUser() {
        return "helloUser";
    }
}
