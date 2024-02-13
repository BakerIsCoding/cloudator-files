package com.cloudator.files.cloudatorfiles.controllers;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/upload")
public class UploadFilesController {

    private static final String DIRECTORY = "E://Eduardo/pruebadescargas/";

    @PostMapping("/file")
    public String uploadFile(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return "Seleccione un archivo para cargar.";
        }

        try {
            // Construye la ruta completa donde se guardará el archivo
            Path path = Paths.get(DIRECTORY + file.getOriginalFilename());
            // Asegúrate de que los directorios existan
            if (!Files.exists(path.getParent())) {
                Files.createDirectories(path.getParent());
            }
            // Guarda el archivo en el sistema de archivos
            Files.write(path, file.getBytes());

            return "Archivo cargado correctamente: " + file.getOriginalFilename();
        } catch (IOException e) {
            e.printStackTrace();
            return "Error al cargar el archivo: " + e.getMessage();
        }
    }

}
