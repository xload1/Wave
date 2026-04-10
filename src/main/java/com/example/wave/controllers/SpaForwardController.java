package com.example.wave.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class SpaForwardController {

    @GetMapping({
            "/",
            "/profile",
            "/recommendations",
            "/matches",
            "/search"
    })
    public String forward() {
        return "forward:/index.html";
    }
}
