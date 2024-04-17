package com.cloudator.files.cloudatorfiles.jwt;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.auth0.jwt.JWT;
import com.auth0.jwt.interfaces.DecodedJWT;
import jakarta.servlet.http.HttpServletRequest;

@Component
public class JsonWebTokenReceiver {
    
    /**
     * Decodifica y muestra el contenido del payload de un JWT.
     *
     * @param token El token JWT a decodificar.
     */
    
    @Autowired
    private JsonWebTokenValidator validator;

    public DecodedJWT recibirToken(String token) {
        if (validator.verifyToken(token)) {
            DecodedJWT jwt = JWT.decode(token);
            
            System.out.println("Información del Token:");
            System.out.println("Issuer: " + jwt.getIssuer());
            System.out.println("Expiration Time: " + jwt.getExpiresAt());
            // Aquí puedes acceder a más información del payload si es necesario
            return jwt;
        } else {
            System.out.println("El token JWT no es válido.");
            return null;
        }
    }

    public DecodedJWT recibirTokenDeSolicitud(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            String token = bearerToken.substring(7);
            return recibirToken(token);
        } else {
            System.out.println("No se encontró el token JWT en el encabezado Authorization.");
            return null;
        }
    }
}

