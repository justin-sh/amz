package com.lu.justin.tool.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping(value = "/file")
public class FileUploadController {

    @GetMapping(value = "/")
    public String home() {
        System.out.println("test");
        return "index.html";
    }
}
