package com.cloudator.files.cloudatorfiles.controllers;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Date;


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
import com.cloudator.files.cloudatorfiles.services.FileService;
import com.cloudator.files.cloudatorfiles.services.SecurityService;


@RestController
@RequestMapping("/upload")
public class UploadFilesController {

    private static final String DIRECTORY = "E://Eduardo/pruebadescargas/";
    
    @Autowired
    private FileService fileService;

    @Autowired
    private JsonWebTokenValidator jwtValidator;

    @Autowired
    private JsonWebTokenReceiver jwtReceiver;

    @Autowired
    private JsonWebTokenManager jwtManager;

    @PostMapping("/file")
    public ResponseEntity<String> uploadFile(@RequestParam("file") MultipartFile uploadedFile, @RequestParam("owner") String owner) {
        if (uploadedFile.isEmpty()) {
            return ResponseEntity.badRequest().body("Seleccione un archivo para cargar.");
        }

        try {

            SecurityService securityService = new SecurityService("${secretkey}");
            String decryptedOwner = securityService.decryptData(owner);
            //String ownerDirectory = DIRECTORY + decryptedOwner + "/";

            String ownerDirectory = DIRECTORY + owner + "/";
            // Construye la ruta completa donde se guardará el archivo.
            Path path = Paths.get(ownerDirectory + uploadedFile.getOriginalFilename());
            //Path path = Paths.get(DIRECTORY + uploadedFile.getOriginalFilename());

            if (!Files.exists(path.getParent())) {
                Files.createDirectories(path.getParent());
            }
            // Guarda el archivo en el sistema de archivos.
            Files.write(path, uploadedFile.getBytes());

            String fileName = uploadedFile.getOriginalFilename();
            String fileType = uploadedFile.getContentType();
            String filePath = path.toString();
            //String[] filePathSplit = filePath.split(fileName);
            Date date = new Date();
            Float fileSize = (float) uploadedFile.getSize();
            
            String directoryPath = filePath.substring(0, filePath.lastIndexOf("\\") + 1);
            String createdToken = jwtManager.createToken(2);
            System.out.println(createdToken);
            jwtReceiver.mostrarInformacionToken(createdToken);
            //System.out.println(validated); 
            

            File file = new File();
            file.setFilename(fileName);
            file.setFiletype(fileType);
            file.setFileroute(directoryPath);
            file.setFiledate(date);
            file.setFilesize(fileSize);
            file.setOwner(owner);
            file.setIspublic(true);

            // Guarda la metadata del archivo en la base de datos.
            fileService.uploadFile(file);

            return ResponseEntity.ok("Archivo cargado correctamente: " + uploadedFile.getOriginalFilename());
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body("Error al cargar el archivo: " + e.getMessage());
        }
    }
}
