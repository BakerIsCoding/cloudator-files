package com.cloudator.files.cloudatorfiles.controllers;

import java.net.MalformedURLException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Base64;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
//import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.auth0.jwt.interfaces.DecodedJWT;
import com.cloudator.files.cloudatorfiles.jwt.JsonWebTokenReceiver;
import com.cloudator.files.cloudatorfiles.services.SecurityService;

import jakarta.servlet.http.HttpServletRequest;

@RestController
public class DownloadFilesController {

    private static final String DIRECTORY = "C://hector/pruebadescargas/";
    // private static final String URL = "https://";

    @Autowired
    private JsonWebTokenReceiver jwtReceiver;

    @Value("${secretencryptor}")
    private String SECRET_KEY_ENCRYPTOR; 

    public String decodeFromUrl(String encodedData) {
        byte[] decodedBytes = Base64.getUrlDecoder().decode(encodedData);
        return new String(decodedBytes, StandardCharsets.UTF_8);
    }

    @GetMapping("/download")
    public ResponseEntity<Resource> downloadFile(HttpServletRequest request,
            @RequestParam(name = "owner", required = true, defaultValue = "") String owner,
            @RequestParam(name = "filename", required = true, defaultValue = "") String filename) {
        SecurityService securityService = new SecurityService(SECRET_KEY_ENCRYPTOR);
        

        String decodedOwner = decodeFromUrl(owner);
        String decodedFilename = decodeFromUrl(filename);

        String idOwner = securityService.decryptData(decodedOwner);
        String filenameR = securityService.decryptData(decodedFilename);


        /* 
        DecodedJWT jwt = jwtReceiver.recibirTokenDeSolicitud(request);
        if (jwt == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }*/

        try {
            Path filePath = Paths.get(DIRECTORY + idOwner + "/" + filenameR).normalize();
            Resource resource = new UrlResource(filePath.toUri());

            if (resource.exists() && resource.isReadable()) {
                return ResponseEntity.ok()
                        .header(HttpHeaders.CONTENT_DISPOSITION,
                                "attachment; filename=\"" + resource.getFilename() + "\"")
                        .body(resource);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (MalformedURLException e) {
            return ResponseEntity.internalServerError().build();
        }
    }
}
