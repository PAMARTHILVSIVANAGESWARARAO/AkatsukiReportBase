package com.akatsuki_news_application.AkatsukiReportBase.source.components;

import java.util.ArrayList;
import java.util.LinkedHashMap;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/news")
public class NewsFetchingComponent {

    @GetMapping("/fetch")
    public LinkedHashMap<String, Object> fetchNews(@RequestParam("url") String targetUrl) {

        LinkedHashMap<String, Object> response = new LinkedHashMap<>();

        try {

            Document doc = Jsoup.connect(targetUrl)
                    .userAgent("Mozilla/5.0")
                    .timeout(5000)
                    .get();

            String rawTitle = doc.title();

            String cleanedTitle = rawTitle
                    .replace("Google News - ", "")
                    .replace(" - Latest", "")
                    .trim();

            Elements headlines = doc.select("a.svxzne");

            ArrayList<String> al = new ArrayList<>();

            for (Element headline : headlines) {
                al.add(headline.text());
            }

            response.put("status", "success");
            response.put("requestedUrl", targetUrl);
            response.put("title", cleanedTitle);
            response.put("headlines", al);

        } catch (Exception e) {
            response.put("status", "error");
            response.put("message", e.getMessage());
        }

        return response;
    }
}