package com.example.oilstorage.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

// LoginController.java
@Controller
public class LoginController {
    @GetMapping("/login")
    public String loginPage() {
        return "login"; // 对应模板文件路径
    }
}