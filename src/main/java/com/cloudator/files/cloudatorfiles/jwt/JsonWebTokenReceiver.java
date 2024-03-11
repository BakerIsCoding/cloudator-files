package com.cloudator.files.cloudatorfiles.jwt;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.auth0.jwt.JWT;
import com.auth0.jwt.interfaces.DecodedJWT;

@Component
public class JsonWebTokenReceiver {
    
    /**
     * Decodifica y muestra el contenido del payload de un JWT.
     *
     * @param token El token JWT a decodificar.
     */
    
    @Autowired
    private JsonWebTokenValidator validator;

    public void mostrarInformacionToken(String token) {
        if (validator.verifyToken(token)) {
            DecodedJWT jwt = JWT.decode(token);
            
            System.out.println("Información del Token:");
            System.out.println("Issuer: " + jwt.getIssuer());
            System.out.println("Expiration Time: " + jwt.getExpiresAt());
            // Aquí puedes acceder a más información del payload si es necesario
        } else {
            System.out.println("El token JWT no es válido.");
        }
    }
}

