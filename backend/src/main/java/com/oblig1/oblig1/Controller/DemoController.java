package com.oblig1.oblig1.Controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class DemoController {

    @GetMapping("/health")
    public String hello() {
        return "The application is running and healthy";
    }

    @GetMapping("/hello2")
    @PreAuthorize("hasRole('client_admin')")
    public String hello2() {
        return "Hello from the protected version";
    }
}
