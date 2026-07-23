package com.akatsuki_news_application.AkatsukiReportBase.source.components;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
public class NewsFetchingComponent {

    private static final Logger logger = LoggerFactory.getLogger(NewsFetchingComponent.class);

    private static final String GROQ_API_URL = "https://api.groq.com/openai/v1/chat/completions";

    private final RestClient restClient;
    private final String groqApiKey;
    private final String groqModel;

    public NewsFetchingComponent(@Value("${GROQ_API_KEY}") String groqApiKey,
                                  @Value("${spring.ai.openai.chat.options.model:llama3-8b-8192}") String groqModel) {
        this.restClient = RestClient.create();
        this.groqApiKey = groqApiKey;
        this.groqModel = groqModel;
    }

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
                String originalText = headline.text();
                String rewrittenText = rewriteHeadline(originalText);
                al.add(rewrittenText);
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

    /**
     * Sends the raw headline to Groq AI (via direct REST API call) to rewrite it
     * in clear, proper English — expanding abbreviations, fixing grammar, and making
     * it more readable. Falls back to the original headline on any error.
     */
    @SuppressWarnings("unchecked")
    private String rewriteHeadline(String headline) {
        try {
            String prompt = """
                    Rewrite this news headline in clear, proper English.
                    Rules:
                    - Expand abbreviations and acronyms (e.g., 'pm' → 'Prime Minister', 'us' → 'United States', 'dept' → 'department')
                    - Fix grammar and capitalization
                    - Make it a natural, readable sentence
                    - Do NOT add any extra commentary or explanation
                    - Return ONLY the rewritten headline, nothing else

                    Original headline: "%s"
                    Rewritten headline:""".formatted(headline);

            Map<String, Object> requestBody = Map.of(
                    "model", groqModel,
                    "messages", new Object[]{
                            Map.of("role", "user", "content", prompt)
                    },
                    "temperature", 0.5
            );

            Map<String, Object> response = restClient.post()
                    .uri(GROQ_API_URL)
                    .header("Authorization", "Bearer " + groqApiKey)
                    .header("Content-Type", "application/json")
                    .body(requestBody)
                    .retrieve()
                    .body(Map.class);

            if (response == null) {
                logger.warn("Groq returned null response for headline: {}", headline);
                return headline;
            }

            java.util.List<Map<String, Object>> choices =
                    (java.util.List<Map<String, Object>>) response.get("choices");

            if (choices == null || choices.isEmpty()) {
                logger.warn("Groq returned no choices for headline: {}", headline);
                return headline;
            }

            Map<String, Object> message = (Map<String, Object>) choices.get(0).get("message");
            if (message == null) {
                logger.warn("Groq returned no message in first choice for headline: {}", headline);
                return headline;
            }

            String rewritten = (String) message.get("content");
            if (rewritten == null || rewritten.isBlank()) {
                logger.warn("Groq returned empty content for headline: {}", headline);
                return headline;
            }

            String cleaned = rewritten.trim()
                    .replaceAll("^\"|\"$", "")   // remove surrounding quotes if any
                    .replaceAll("\\s+", " ");    // normalize whitespace

            logger.debug("Headline rewritten: '{}' → '{}'", headline, cleaned);
            return cleaned;

        } catch (Exception e) {
            logger.error("Failed to rewrite headline via Groq: '{}'. Error: {}", headline, e.getMessage());
            return headline;
        }
    }
}
