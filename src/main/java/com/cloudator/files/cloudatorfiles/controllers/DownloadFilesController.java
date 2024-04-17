package com.cloudator.files.cloudatorfiles.controllers;

import java.net.MalformedURLException;
import java.nio.file.Path;
import java.nio.file.Paths;

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

    private static final String DIRECTORY = "E://Eduardo/pruebadescargas/";
    //private static final String URL = "https://";

    @Autowired
    private JsonWebTokenReceiver jwtReceiver;

    @Value("${secretencryptor}")
    private String SECRET_KEY_ENCRYPTOR;

    /*@PostMapping("/download")
    public ResponseEntity<String> generateDownloadUrl(
            @RequestParam("idFile") String idFile,
            @RequestParam("idUser") String idUser,
            @RequestParam("idUserManage") String idUserManage,
            @RequestParam("isPublic") boolean isPublic,
            HttpServletRequest request) {
        
        System.err.println("Entra en Post 1.");
        SecurityService securityService = new SecurityService(SECRET_KEY_ENCRYPTOR);
        String userId = securityService.decryptData(idUser);
        String userManageId = securityService.decryptData(idUserManage);

        if(isPublic || userId == userManageId){

            StringBuffer urlBuffer = request.getRequestURL();
            String uri = request.getRequestURI();
            String ctx = request.getContextPath();
            String base = urlBuffer.substring(0, urlBuffer.length() - uri.length() + ctx.length()) + "/";
            String downloadUrl = base + idUser + "/";

            //String downloadUrl = URL + idUser + "/";

            return ResponseEntity.ok(downloadUrl);

        }else{
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("No autorizado para descargar el archivo");
        }
    }

    @PostMapping("/download/{filename:.+}")
    public ResponseEntity<Resource> downloadFile(@PathVariable String filename, String idUserReturn) throws Exception {
        System.out.println("Entra en post 2.");
        System.out.println("filename: " + filename);

        //SecurityService securityService = new SecurityService(SECRET_KEY_ENCRYPTOR);

        //String fileNameDes = securityService.decryptData(filename);
        //String userId = securityService.decryptData(idUserReturn);
        try {
            //Path filePath = Paths.get(DIRECTORY).resolve(userId).resolve(fileNameDes).normalize();
            System.err.println("Entra en download.");
            Path filePath = Paths.get(DIRECTORY).resolve(idUserReturn).resolve(filename).normalize();
            Resource resource = new UrlResource(filePath.toUri());
            System.out.println("Path: " + filePath);
            System.out.println("Resource: " + resource);

            if (resource.exists() && resource.isReadable()) {
                System.out.println("Entra en resource.");
                return ResponseEntity.ok()
                        .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
                        .body(resource);
            } else {
                throw new Exception("Archivo no encontrado " + filename);
            }
        } catch (MalformedURLException ex) {
            throw new Exception("Archivo no encontrado: " + filename + " desconocido.", ex);
        }
    }*/

    @GetMapping("/download")
    public ResponseEntity<Resource> downloadFile(HttpServletRequest request, @RequestParam(name = "owner", required = true, defaultValue = "") String owner,
    @RequestParam(name = "filename", required = true, defaultValue = "") String filename) {
        SecurityService securityService = new SecurityService(SECRET_KEY_ENCRYPTOR);

        System.out.println("owner sin descifrar: " + owner);
        System.out.println("filename sin descifrar: " + filename);

        String idOwner = securityService.decryptData(owner);
        String filenameR = securityService.decryptData(filename);

        System.out.println("owner descifrado: " + idOwner);
        System.out.println("filename descifrado: " + filenameR);

        DecodedJWT jwt = jwtReceiver.recibirTokenDeSolicitud(request);
        if (jwt == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        try {
            Path filePath = Paths.get(DIRECTORY + idOwner + "/" + filenameR).normalize();
            Resource resource = new UrlResource(filePath.toUri());

            if (resource.exists() && resource.isReadable()) {
                return ResponseEntity.ok()
                        .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
                        .body(resource);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (MalformedURLException e) {
            return ResponseEntity.internalServerError().build();
        }
    }
}
