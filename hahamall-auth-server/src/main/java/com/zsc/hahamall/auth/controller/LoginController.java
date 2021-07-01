package com.zsc.hahamall.auth.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import javax.validation.constraints.Min;

@Controller
public class LoginController {

    @GetMapping("/login.html")
    public String loginPage(){
        return  "login";
    }
    @GetMapping("/reg.html")
    public String regPage(){

        return "reg";
    }
}
