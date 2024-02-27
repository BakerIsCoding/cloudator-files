package com.cloudator.files.cloudatorfiles.jwt;

import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;

import jakarta.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class JsonWebTokenValidator {
    /**
     * Verifica si el token JWT es válido.
     *
     * @param token El token JWT a verificar.
     * @return Un booleano indicando si el token es válido o no.
     */

    @Value("${secretkey}")
    private String SECRET_KEY;
    private Algorithm ALGORITHM;

    @PostConstruct
    public void init(){
        ALGORITHM = Algorithm.HMAC256(SECRET_KEY);
    }
    
    public boolean verifyToken(String token) {
        System.out.println("SECRET_KEY");
        System.out.println(SECRET_KEY);
        try {
            JWTVerifier verifier = JWT.require(ALGORITHM)
                    .withIssuer("FILESERVER")
                    .build();
            DecodedJWT jwt = verifier.verify(token);
            return true;
        } catch (JWTVerificationException exception) {
            // En un caso real, aquí podrías loguear o manejar el error de verificación
            return false;
        }
    }
}
