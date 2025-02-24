package com.ldh.forum.board.common.service;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

@Service
public class ProfanityFilterService {

    private static final String API_URL = "https://www.purgomalum.com/service/containsprofanity?text=";
    private final RestTemplate restTemplate;

    public ProfanityFilterService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public boolean containsProfanity(String username) {

        try {
            String requestUrl = API_URL + username;
            String response = restTemplate.getForObject(requestUrl, String.class);

            return "true".equalsIgnoreCase(response);
        } catch (RestClientException e) {
            System.err.println("Profanity API request failed: " + e.getMessage());
            return false;
        }
    }
}

