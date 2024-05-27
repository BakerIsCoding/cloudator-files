package com.cloudator.files.cloudatorfiles.services.impl;

import com.cloudator.files.cloudatorfiles.services.IUploadFilesService;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class UploadFilesServiceImpl implements IUploadFilesService {
    
    @Override
    public String handleFileUpload(MultipartFile file) throws Exception{
        try {
            String fileOriginalName = file.getOriginalFilename();
            byte[] bytes = file.getBytes();
            long fileSize = file.getSize();
            long maxFileSize = 10000 * 1024 * 1024;

            if(fileSize > maxFileSize){
                return "El tamaño máximo del fichero debe de ser 10 GB";
            }

            //String fileExtension = fileOriginalName.substring(fileOriginalName.lastIndexOf(".") +1);
            File folder = new File("src/main/resources/files/");
            
            if(!folder.exists()){
                folder.mkdirs();
            }

            Path path = Paths.get("src/main/resources/files/" + fileOriginalName);

            Files.write(path, bytes);

            return "Archivo subido con éxito";

        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }
    }
}
