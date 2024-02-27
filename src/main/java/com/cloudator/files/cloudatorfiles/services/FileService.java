package com.cloudator.files.cloudatorfiles.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.cloudator.files.cloudatorfiles.entity.File;
import com.cloudator.files.cloudatorfiles.repository.FileRepository;

@Service
public class FileService {
    
    @Autowired
    private FileRepository repo;

    public String uploadFile(File file) {
        repo.save(file);
        System.out.println("Fichero subido correctamente");
        return "Fichero subido correctamente";
    }
    
}
