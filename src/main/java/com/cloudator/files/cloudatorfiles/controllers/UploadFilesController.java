package com.cloudator.files.cloudatorfiles.controllers;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Base64;
import java.util.Date;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import com.cloudator.files.cloudatorfiles.entity.File;
import com.cloudator.files.cloudatorfiles.jwt.JsonWebTokenManager;
import com.cloudator.files.cloudatorfiles.jwt.JsonWebTokenReceiver;
import com.cloudator.files.cloudatorfiles.jwt.JsonWebTokenValidator;
import com.cloudator.files.cloudatorfiles.services.SecurityService;

@RestController
@RequestMapping("/upload")
public class UploadFilesController {

    private static final String DIRECTORY = "/home/host/srv/cloudfiles/"; //Cambiar por "/home/host/srv/cloudfiles/" antes de hacer Commit.

    @Autowired
    private JsonWebTokenValidator jwtValidator;

    @Autowired  
    private JsonWebTokenReceiver jwtReceiver;

    @Autowired
    private JsonWebTokenManager jwtManager;

    @Value("${secretencryptor}")
    private String SECRET_KEY_ENCRYPTOR;

    @PostMapping("/file")
    public ResponseEntity<String> uploadFile(@RequestParam("file") MultipartFile uploadedFile,
            @RequestParam("owner") Long owner) {
        if (uploadedFile.isEmpty()) {
            return ResponseEntity.badRequest().body("Seleccione un archivo para cargar.");
        }
        try {

            SecurityService securityService = new SecurityService(SECRET_KEY_ENCRYPTOR);

            String ownerDirectory = DIRECTORY + owner + "/";
            //Construye la ruta completa donde se guardará el archivo.
            Path path = Paths.get(ownerDirectory + uploadedFile.getOriginalFilename());

            if (!Files.exists(path.getParent())) {
                Files.createDirectories(path.getParent());
            }
            //Guarda el archivo en el sistema de archivos.
            Files.write(path, uploadedFile.getBytes());

            String fileName = uploadedFile.getOriginalFilename();
            String fileType = uploadedFile.getContentType();
            String filePath = path.toString();
            //String[] filePathSplit = filePath.split(fileName);
            Date date = new Date();
            Long fileSize = uploadedFile.getSize();

            String directoryPath = filePath.substring(0, filePath.lastIndexOf("\\") + 1);
            String createdToken = jwtManager.createToken(2);
            jwtReceiver.recibirToken(createdToken);
            

            File file = new File();
            file.setFilename(fileName);
            file.setFiletype(fileType);
            file.setFileroute(directoryPath);
            file.setFiledate(date);
            file.setFilesize(fileSize);
            file.setOwner(owner);
            file.setIspublic(true);
            

            String filenameR = securityService.encryptData(file.getFilename());
            String filetypeR = securityService.encryptData(file.getFiletype());
            String filerouteR = securityService.encryptData(file.getFileroute());
            String filedateR = securityService.encryptData(file.getFiledate().toString());
            String filesizeR = securityService.encryptData(file.getFilesize().toString());
            String ownerR = securityService.encryptData(file.getOwner().toString());
            String ispublicR = securityService.encryptData(file.getIspublic().toString());


            String decryptedOwner = securityService.decryptData(ownerR);
            String decryptedFilename = securityService.decryptData(filenameR);


            String encodedOwner = Base64.getUrlEncoder().encodeToString(ownerR.getBytes(StandardCharsets.UTF_8));
            String encodedFilename = Base64.getUrlEncoder().encodeToString(filenameR.getBytes(StandardCharsets.UTF_8));

            String url = "https://host.cloudator.live/download?owner=" + encodedOwner +"&filename=" + encodedFilename;
            

            file.setUrl(url);

            String downloadUrl = securityService.encryptData(file.getUrl());

            String jwtFinal = jwtManager.createFileServerUpload(filenameR, filetypeR, filerouteR, filedateR, filesizeR, ownerR, ispublicR, downloadUrl);

            //String jwtFinal = jwtManager.createFileServerUpload(filenameR, filetypeR, filerouteR, filedateR, filesizeR, ownerR, ispublicR, url);
            //String jwtFinal = jwtManager.createFileServerUpload(filenameR, filetypeR, filerouteR, filedateR, filesizeR, ownerR, ispublicR);


            jwtReceiver.recibirToken(jwtFinal);

            return ResponseEntity.ok(jwtFinal);
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body("Error al cargar el archivo: " + e.getMessage());
        }
    }

    @PostMapping("/create-directory")
    public ResponseEntity<String> createDirectoryForUser(@RequestParam("userId") Long userId) {
        String baseDirectory = DIRECTORY + userId + "/";
        String profilePictureDirectory = baseDirectory + "pfp/";

        try {
            SecurityService securityService = new SecurityService(SECRET_KEY_ENCRYPTOR);
            String createIdUser = securityService.decryptData(userId.toString());

            //Crea la carpeta base para el usuario.
            Files.createDirectories(Paths.get(baseDirectory));
            //Crea la subcarpeta "pfp".
            Files.createDirectories(Paths.get(profilePictureDirectory));
            return ResponseEntity.ok("Directorios creados correctamente para el usuario: " + createIdUser);
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body("Error al crear directorios: " + e.getMessage());
        }
    }
}
