package com.yoyojobcare.auth.kukuapp.ku_ku_app.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HealthController {

    @GetMapping("/")
    public String home() {
        return "KuKu backend running";
    }

    @GetMapping("/ping")
    public String ping() {
        return "OK";
    }

}
