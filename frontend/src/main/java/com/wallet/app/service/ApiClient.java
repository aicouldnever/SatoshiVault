package com.wallet.app.service;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.wallet.app.util.ConfigManager;
import com.wallet.app.util.SessionManager;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

/**
 * Base HTTP client for making REST API calls to the backend.
 * Uses Java 11+ HttpClient with automatic authentication header injection.
 */
public class ApiClient {
    
    protected static final Gson gson = new Gson();
    protected static final HttpClient httpClient = HttpClient.newBuilder()
            .connectTimeout(Duration.ofMillis(ConfigManager.getBackendTimeout()))
            .build();
    
    /**
     * Make a GET request
     */
    protected static String get(String endpoint) throws Exception {
        String url = ConfigManager.getBackendUrl() + endpoint;
        
        HttpRequest.Builder requestBuilder = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .timeout(Duration.ofMillis(ConfigManager.getBackendTimeout()))
                .GET();
        
        // Add auth token if available
        if (SessionManager.getAuthToken() != null) {
            requestBuilder.header("Authorization", "Bearer " + SessionManager.getAuthToken());
        }
        
        HttpRequest request = requestBuilder.build();
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        
        if (response.statusCode() >= 200 && response.statusCode() < 300) {
            return response.body();
        } else {
            throw new ApiException("API Error: " + response.statusCode() + " - " + response.body());
        }
    }
    
    /**
     * Make a POST request with JSON body
     */
    protected static String post(String endpoint, Object body) throws Exception {
        String url = ConfigManager.getBackendUrl() + endpoint;
        String jsonBody = gson.toJson(body);
        
        HttpRequest.Builder requestBuilder = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .timeout(Duration.ofMillis(ConfigManager.getBackendTimeout()))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(jsonBody));
        
        // Add auth token if available
        if (SessionManager.getAuthToken() != null) {
            requestBuilder.header("Authorization", "Bearer " + SessionManager.getAuthToken());
        }
        
        HttpRequest request = requestBuilder.build();
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        
        if (response.statusCode() >= 200 && response.statusCode() < 300) {
            return response.body();
        } else {
            throw new ApiException("API Error: " + response.statusCode() + " - " + response.body());
        }
    }
    
    /**
     * Parse JSON string to JsonObject
     */
    protected static JsonObject parseJson(String json) {
        return gson.fromJson(json, JsonObject.class);
    }
    
    /**
     * Check if backend is available
     */
    public static boolean checkBackendHealth() {
        try {
            String response = get("/api/health");
            JsonObject json = parseJson(response);
            return "UP".equals(json.get("status").getAsString());
        } catch (Exception e) {
            return false;
        }
    }
    
    /**
     * Custom exception for API errors
     */
    public static class ApiException extends Exception {
        public ApiException(String message) {
            super(message);
        }
        
        public ApiException(String message, Throwable cause) {
            super(message, cause);
        }
    }
}
