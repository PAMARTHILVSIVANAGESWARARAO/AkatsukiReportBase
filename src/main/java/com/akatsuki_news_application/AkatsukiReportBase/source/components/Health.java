package com.akatsuki_news_application.AkatsukiReportBase.source.components;

import java.util.HashMap;
import java.util.LinkedHashMap;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
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

    @GetMapping("scrap")
    public LinkedHashMap<String, String> scrapper() {
        LinkedHashMap<String, String> tm = new LinkedHashMap<>();

        try {
            Document document = Jsoup
                    .connect("https://www.geeksforgeeks.org/window-sliding-technique/")
                    .get();

      
            String title = document.select("h1").first().text();
            tm.put("title", title);

        
            String description = document.select(".text").first().text();
            tm.put("description", description);

         
            String javaCode = document.select(".language-java").first() != null
                    ? document.select(".language-java").first().text()
                    : "No Java code found";
            tm.put("javaCode", javaCode);

        } catch (Exception e) {
            tm.put("message", "Error: " + e.getMessage());
            return tm;
        }

        return tm;
    }

}
