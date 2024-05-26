package com.cloudator.files.cloudatorfiles.controllers;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class DeleteFilesController {

    @Value("${directory}")
    private String directory;

    @PostMapping("/file/delete")
    public ResponseEntity<String> deleteFile(@RequestParam("id") Long id, @RequestParam("filename") String filename) {
        // Se usa la clase java.io.File para manejar archivos del sistema.
        String filePath = directory + id + java.io.File.separator + filename;

        java.io.File file = new java.io.File(filePath);

        if (file.exists()) {
            if (file.delete()) {
                return new ResponseEntity<>("Archivo borrado con éxito.", HttpStatus.OK);
            } else {
                return new ResponseEntity<>("Error al borrar el archivo.", HttpStatus.INTERNAL_SERVER_ERROR);
            }
        } else {
            return new ResponseEntity<>("Archivo no encontrado.", HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping("/file/account/delete")
    public ResponseEntity<String> deleteAccount(@RequestParam("id") Long id) {

        String directoryPath = directory + id;

        Path pathToBeDeleted = Paths.get(directoryPath);

        if (Files.exists(pathToBeDeleted)) {
            try {
                Files.walkFileTree(pathToBeDeleted, new SimpleFileVisitor<Path>() {
                    @Override
                    public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                        Files.delete(file);
                        return FileVisitResult.CONTINUE;
                    }

                    @Override
                    public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
                        Files.delete(dir);
                        return FileVisitResult.CONTINUE;
                    }
                });
                return new ResponseEntity<>("Cuenta borrada con éxito.", HttpStatus.OK);
            } catch (IOException e) {
                return new ResponseEntity<>("Error al borrar la cuenta.", HttpStatus.INTERNAL_SERVER_ERROR);
            }
        } else {
            return new ResponseEntity<>("Cuenta no encontrada.", HttpStatus.NOT_FOUND);
        }
    }

}
