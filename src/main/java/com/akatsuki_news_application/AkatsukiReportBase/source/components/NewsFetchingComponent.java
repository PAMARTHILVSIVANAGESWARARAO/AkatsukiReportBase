package com.akatsuki_news_application.AkatsukiReportBase.source.components;

import java.util.ArrayList;
import java.util.LinkedHashMap;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Component;

@Component
public class NewsFetchingComponent {

    public LinkedHashMap<String, Object> fetchNews(String targetUrl) {

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