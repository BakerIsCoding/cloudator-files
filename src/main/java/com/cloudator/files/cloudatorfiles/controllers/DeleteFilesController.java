package com.cloudator.files.cloudatorfiles.controllers;

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
        //Se usa la clase java.io.File para manejar archivos del sistema.
        String filePath = directory + id + java.io.File.separator + filename;

        java.io.File file = new java.io.File(filePath);

        if (file.exists()) {
            if (file.delete()) {
                return new ResponseEntity<>("File deleted successfully.", HttpStatus.OK);
            } else {
                return new ResponseEntity<>("Failed to delete the file.", HttpStatus.INTERNAL_SERVER_ERROR);
            }
        } else {
            return new ResponseEntity<>("File not found.", HttpStatus.NOT_FOUND);
        }
    }
}
