package com.cloudator.files.cloudatorfiles.jwt;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;

import jakarta.annotation.PostConstruct;

import java.util.Date;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class JsonWebTokenManager {

    // Asegúrate de usar una clave secreta segura y única para tu aplicación
    @Value("${secretkey}")
    private String SECRET_KEY;
    private Algorithm ALGORITHM;

    @PostConstruct
    public void init() {
        ALGORITHM = Algorithm.HMAC256(SECRET_KEY);
    }

    /**
     * Crea un token JWT con un payload básico.
     *
     * @return Un token JWT como String.
     */
    public String createToken(Integer id) {
        return JWT.create()
                .withClaim("id", id)
                .withExpiresAt(new Date(System.currentTimeMillis() + 3600 * 1000)) // 1 hora de validez
                .withIssuer("FILESERVER")
                .sign(ALGORITHM);
    }

    public String createFileServerUpload(String filenameR, String filetypeR, String filerouteR,
            String filedateR, String filesizeR, String ownerR, String ispublicR, String downloadUrl) {
        return JWT.create()
                .withClaim("filename", filenameR)
                .withClaim("filetype", filetypeR)
                .withClaim("fileroute", filerouteR)
                .withClaim("filedate", filedateR)
                .withClaim("filesize", filesizeR)
                .withClaim("owner", ownerR)
                .withClaim("ispublic", ispublicR)
                .withClaim("url", downloadUrl)
                .withExpiresAt(new Date(System.currentTimeMillis() + 3600 * 1000)) // 1 hora de validez
                .withIssuer("FILESERVER")
                .sign(ALGORITHM);
    }
}
