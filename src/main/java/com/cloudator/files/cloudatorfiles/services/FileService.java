package com.cloudator.files.cloudatorfiles.services;

import org.springframework.stereotype.Service;
import com.cloudator.files.cloudatorfiles.entity.File;
import com.cloudator.files.cloudatorfiles.repository.FileRepository;

@Service
public class FileService {

    private final FileRepository fileRepository;

    // Constructor para la inyección de dependencias
    public FileService(FileRepository fileRepository) {
        this.fileRepository = fileRepository;
    }

    public String uploadFile(File file) {
        fileRepository.save(file); // Guarda el archivo en la base de datos
        return "Fichero subido correctamente";
    }

    // Aquí puedes añadir otros métodos para actualizar, eliminar, etc.
}
