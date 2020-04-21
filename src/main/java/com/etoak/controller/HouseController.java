package com.etoak.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/house")
public class HouseController {

    @RequestMapping("/toAdd")
    public String toAdd(){
        return "/house/Add";
    }
}
