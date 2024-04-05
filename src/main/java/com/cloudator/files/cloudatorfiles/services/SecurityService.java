package com.cloudator.files.cloudatorfiles.services;

import com.cloudator.files.cloudatorfiles.utils.Encryptor;

import com.cloudator.files.cloudatorfiles.utils.Decryptor;

public class SecurityService {

    private final Encryptor encryptor;
    private final Decryptor decryptor;

    public SecurityService(String SECRET_KEY_ENCRYPTOR) {
        this.encryptor = new Encryptor(SECRET_KEY_ENCRYPTOR);
        this.decryptor = new Decryptor(SECRET_KEY_ENCRYPTOR);
    }

    public String encryptData(String data) {
        return encryptor.encrypt(data);
    }

    public String decryptData(String encryptedData) {
        return decryptor.decrypt(encryptedData);
    }
}

