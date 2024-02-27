package com.cloudator.files.cloudatorfiles.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cloudator.files.cloudatorfiles.jwt.JsonWebTokenSender;

@Service
public class JsonWebTokenService {
    @Autowired
    private JsonWebTokenSender jwtSender;

    public void sendToken(String token) {
        String url = "http://localhost:8080/receive-token";
        jwtSender.sendToken(token, url);
    }
}
