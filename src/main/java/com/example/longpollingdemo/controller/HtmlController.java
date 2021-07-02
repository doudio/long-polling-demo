package com.example.longpollingdemo.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * 导航到页面
 */
@Controller
public class HtmlController {

    @GetMapping("/")
    public String index(){
        return "index";
    }

    @GetMapping("/change")
    public String change(){
        return "change";
    }

}
