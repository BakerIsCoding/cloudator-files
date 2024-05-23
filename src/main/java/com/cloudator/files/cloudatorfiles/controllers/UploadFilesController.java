package com.cloudator.files.cloudatorfiles.controllers;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Base64;
import java.util.Date;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.cloudator.files.cloudatorfiles.entity.File;
import com.cloudator.files.cloudatorfiles.jwt.JsonWebTokenManager;
import com.cloudator.files.cloudatorfiles.jwt.JsonWebTokenReceiver;
import com.cloudator.files.cloudatorfiles.services.SecurityService;

import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/upload")
public class UploadFilesController {

    @Autowired  
    private JsonWebTokenReceiver jwtReceiver;

    @Autowired
    private JsonWebTokenManager jwtManager;

    @Value("${secretencryptor}")
    private String SECRET_KEY_ENCRYPTOR;

    @Value("${directory}")
    private String directory;

    @Value("${domain}")
    private String domain;

    @PostMapping("/file")
    public ResponseEntity<String> uploadFile(@RequestParam("file") MultipartFile uploadedFile,
            @RequestParam("owner") Long owner) {
        if (uploadedFile.isEmpty()) {
            return ResponseEntity.badRequest().body("Seleccione un archivo para cargar.");
        }
        try {

            SecurityService securityService = new SecurityService(SECRET_KEY_ENCRYPTOR);

            String ownerDirectory = directory + owner + "/";
            //Construye la ruta completa donde se guardará el archivo.
            Path path = Paths.get(ownerDirectory + uploadedFile.getOriginalFilename());

            if (!Files.exists(path.getParent())) {
                Files.createDirectories(path.getParent());
            }

            //Verifica si el archivo ya existe.
            if (Files.exists(path)) {
                return ResponseEntity.status(HttpStatus.CONFLICT).body("El archivo ya existe.");
            }
            
            //Guarda el archivo en el sistema de archivos.
            Files.write(path, uploadedFile.getBytes());

            String fileName = uploadedFile.getOriginalFilename();
            String fileType = uploadedFile.getContentType();
            String filePath = path.toString();
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

            String encodedOwner = Base64.getUrlEncoder().encodeToString(ownerR.getBytes(StandardCharsets.UTF_8));
            String encodedFilename = Base64.getUrlEncoder().encodeToString(filenameR.getBytes(StandardCharsets.UTF_8));

            String url = domain + "download?owner=" + encodedOwner +"&filename=" + encodedFilename; //Cambiar por http://localhost:8080/download?owner=

            file.setUrl(url);

            String downloadUrl = securityService.encryptData(file.getUrl());

            String jwtFinal = jwtManager.createFileServerUpload(filenameR, filetypeR, filerouteR, filedateR, filesizeR, ownerR, ispublicR, downloadUrl);


            jwtReceiver.recibirToken(jwtFinal);

            return ResponseEntity.ok(jwtFinal);
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body("Error al cargar el archivo: " + e.getMessage());
        }
    }

    public String decodeFromUrl(String encodedData) {
        byte[] decodedBytes = Base64.getUrlDecoder().decode(encodedData);
        return new String(decodedBytes, StandardCharsets.UTF_8);
    }

    @PostMapping("/create-directory")
    public ResponseEntity<String> createDirectoryForUser(@RequestParam("userId") String userId) {
        System.out.println("Entra en crear carpeta.");

        try {
            String decodedId = decodeFromUrl(userId);

            SecurityService securityService = new SecurityService(SECRET_KEY_ENCRYPTOR);
            String createIdUser = securityService.decryptData(decodedId);
            //String createIdUser = userId.toString();
            System.out.println("Entra en try.");
            String baseDirectory = directory + createIdUser + "/";
            String profilePictureDirectory = baseDirectory + "pfp/";

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

    @PostMapping("/{owner}/pfpic")
    public ResponseEntity<String> uploadProfilePicture(@RequestBody byte[] uploadedFile, @RequestHeader("Content-Type") String contentType, @PathVariable("owner") String owner) {
        if (uploadedFile.length == 0) {
            return ResponseEntity.badRequest().body("Seleccione una imagen para cargar.");
        }

        if (!"application/octet-stream".equals(contentType)) {
            return ResponseEntity.status(HttpStatus.UNSUPPORTED_MEDIA_TYPE).body("Formato de archivo no soportado. Solo se permiten archivos octet-stream.");
        }

        try {
            String ownerDirectory = directory + owner + "/pfp/";
            Path path = Paths.get(ownerDirectory + "profile.jpg");
            Files.createDirectories(path.getParent());  // Crear directorios si no existen
            Files.write(path, uploadedFile);

            String imageUrl = domain + "upload/" + owner + "/pfp/profile.jpg";
            return ResponseEntity.ok(imageUrl);
        } catch (IOException e) {
            return ResponseEntity.internalServerError().body("Error al cargar la imagen de perfil: " + e.getMessage());
        }
    }


    @GetMapping("/{owner}/pfp/profile.jpg")
    public ResponseEntity<Object> serveProfileImage(@PathVariable String owner, HttpServletRequest request) {

        try {
            String ownerDirectory = directory + owner + "/pfp/";
            Path path = Paths.get(ownerDirectory + "profile.jpg");
            Resource imgFile = new UrlResource(path.toUri());

            if (imgFile.exists() || imgFile.isReadable()) {
                return ResponseEntity.ok()
                        .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + imgFile.getFilename() + "\"")
                        .contentType(MediaType.IMAGE_JPEG)
                        .body(imgFile);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (MalformedURLException e) {
            return ResponseEntity.internalServerError().body("Error al servir la imagen: " + e.getMessage());
        }
    }

}
