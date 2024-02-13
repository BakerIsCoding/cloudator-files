package com.cloudator.files.cloudatorfiles.services;

import org.springframework.web.multipart.MultipartFile;

public interface IUploadFilesService {
    String handleFileUpload (MultipartFile file) throws Exception;
}
