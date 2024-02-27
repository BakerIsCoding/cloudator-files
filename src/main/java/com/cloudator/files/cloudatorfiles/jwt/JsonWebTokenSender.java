package com.cloudator.files.cloudatorfiles.jwt;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class JsonWebTokenSender {
    @Autowired
    private RestTemplate restTemplate;

    public void sendToken(String token, String url) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.TEXT_PLAIN);

        HttpEntity<String> request = new HttpEntity<>(token, headers);

        restTemplate.postForObject(url, request, String.class);
    }
}
