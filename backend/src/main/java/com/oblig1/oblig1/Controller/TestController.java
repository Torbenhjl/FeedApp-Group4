package com.oblig1.oblig1.Controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {
    @GetMapping("/helloUser")
    public String helloUser() {
        return "helloUser";
    }
}
