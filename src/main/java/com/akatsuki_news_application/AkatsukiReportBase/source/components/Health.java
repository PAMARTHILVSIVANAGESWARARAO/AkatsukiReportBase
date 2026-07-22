package com.akatsuki_news_application.AkatsukiReportBase.source.components;

import java.util.HashMap;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping
@RestController
public class Health {

    @GetMapping("/")
    public HashMap<String, String> HomePage() {
        HashMap<String, String> hm = new HashMap<>();

        hm.put("message", "api is working");
        return hm;
    }

   
    
}
