package com.akatsuki_news_application.AkatsukiReportBase.source.components;

import java.util.LinkedHashMap;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
@RestController
@RequestMapping
public class Health {
    @GetMapping("/")
    public static LinkedHashMap<String , String> Home(){
        LinkedHashMap<String , String> lhm = new LinkedHashMap<>();

        lhm.put("message" , "api is working");

        return lhm;

    }
}
