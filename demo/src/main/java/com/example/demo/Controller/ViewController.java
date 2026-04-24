package com.example.demo.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ViewController {
    @GetMapping({ "/{path:[^\\.]*}", "/**/{path:[^\\.]*}" })
    public String redirect() {
        return "forward:/index.html";
    }
}