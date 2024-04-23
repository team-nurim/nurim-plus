package org.nurim.nurim.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

// Service Layer
@Service
public class PublicDataService {

    private final RestTemplate restTemplate;

    @Autowired
    public PublicDataService(RestTemplateBuilder restTemplateBuilder) {
        this.restTemplate = restTemplateBuilder.build();
    }

    public String getDataFromApi() {
        String url = "외부 API URL";
        return restTemplate.getForObject(url, String.class);
    }
}
