package com.cloudator.files.cloudatorfiles.controllers;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
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
            // String decryptedOwner = securityService.decryptData(owner);
            // String ownerDirectory = DIRECTORY + decryptedOwner + "/";

            String ownerDirectory = DIRECTORY + owner + "/";
            // Construye la ruta completa donde se guardará el archivo.
            Path path = Paths.get(ownerDirectory + uploadedFile.getOriginalFilename());
            // Path path = Paths.get(DIRECTORY + uploadedFile.getOriginalFilename());

            if (!Files.exists(path.getParent())) {
                Files.createDirectories(path.getParent());
            }
            // Guarda el archivo en el sistema de archivos.
            Files.write(path, uploadedFile.getBytes());

            String fileName = uploadedFile.getOriginalFilename();
            String fileType = uploadedFile.getContentType();
            String filePath = path.toString();
            // String[] filePathSplit = filePath.split(fileName);
            Date date = new Date();
            Long fileSize = uploadedFile.getSize(); 

            String directoryPath = filePath.substring(0, filePath.lastIndexOf("\\") + 1);
            String createdToken = jwtManager.createToken(2);
            System.out.println(createdToken);
            jwtReceiver.mostrarInformacionToken(createdToken);
            // System.out.println(validated);
            

            File file = new File();
            file.setFilename(fileName);
            file.setFiletype(fileType);
            file.setFileroute(directoryPath);
            file.setFiledate(date);
            file.setFilesize(fileSize);
            file.setOwner(owner);
            file.setIspublic(true);

            String filenameR = securityService.encryptData(fileName);
            String filetypeR = securityService.encryptData(fileType);
            String filerouteR = securityService.encryptData(directoryPath);
            String filedateR = securityService.encryptData(date.toString());
            String filesizeR = securityService.encryptData(fileSize.toString());
            String ownerR = securityService.encryptData(owner.toString());
            String ispublicR = securityService.encryptData("true");

            String jwtFinal = jwtManager.createFileServerUpload(filenameR, filetypeR, filerouteR, filedateR, filesizeR, ownerR, ispublicR);

            System.out.println(jwtFinal);

            jwtReceiver.mostrarInformacionToken(jwtFinal);

            // Guarda la metadata del archivo en la base de datos.
            //fileService.uploadFile(file);

            return ResponseEntity.ok(jwtFinal);
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body("Error al cargar el archivo: " + e.getMessage());
        }
    }
}
