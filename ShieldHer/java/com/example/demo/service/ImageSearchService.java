package com.example.demo.service;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import java.util.concurrent.TimeUnit;

@Service
public class ImageSearchService {

    @Value("${serp.api.key}")
    private String serpApiKey;

    private final OkHttpClient client = new OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .build();

    public String searchImage(String imageUrl) {
        try {
            System.out.println("=== SerpAPI Search Starting ===");
            System.out.println("Image URL: " + imageUrl);
            
            String encodedUrl = java.net.URLEncoder.encode(imageUrl, "UTF-8");
            String url = "https://serpapi.com/search.json"
                + "?engine=google_reverse_image"
                + "&image_url=" + encodedUrl
                + "&api_key=" + serpApiKey;

            System.out.println("API URL: " + url);

            Request request = new Request.Builder()
                .url(url)
                .build();

            Response response = client.newCall(request).execute();
            String result = response.body().string();
            
            System.out.println("=== SerpAPI Response ===");
            System.out.println(result.substring(0, Math.min(500, result.length())));
            
            
            System.out.println("FULL RESPONSE: " + result); 
            return result;

        } catch (Exception e) {
            System.out.println("=== SerpAPI ERROR ===");
            System.out.println(e.getMessage());
            return "{\"error\": \"" + e.getMessage() + "\"}";
        }
    }
}