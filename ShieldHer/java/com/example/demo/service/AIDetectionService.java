package com.example.demo.service;

import okhttp3.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class AIDetectionService {

    @Value("${sightengine.api.user}")
    private String apiUser;

    @Value("${sightengine.api.secret}")
    private String apiSecret;

    private final OkHttpClient client = new OkHttpClient();

    public String detectAI(String imageUrl) {
        try {
            String encodedUrl = java.net.URLEncoder.encode(imageUrl, "UTF-8");
            String url = "https://api.sightengine.com/1.0/check.json"
                + "?url=" + encodedUrl
                + "&models=genai,faces"
                + "&api_user=" + apiUser
                + "&api_secret=" + apiSecret;

            Request request = new Request.Builder()
                .url(url)
                .build();

            Response response = client.newCall(request).execute();
            String result = response.body().string();
            System.out.println("AI Detection Result: " + result);
            return result;

        } catch (Exception e) {
            System.out.println("AI Detection Error: " + e.getMessage());
            return "{\"error\": \"" + e.getMessage() + "\"}";
        }
    }
}