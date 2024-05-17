package com.cloudator.files.cloudatorfiles.utils;
import org.jasypt.util.text.AES256TextEncryptor;

public class Decryptor {

    private AES256TextEncryptor textEncryptor;

    public Decryptor(String secretKeyEncryptor) {
        textEncryptor = new AES256TextEncryptor();
        textEncryptor.setPassword(secretKeyEncryptor);
    }

    public String decrypt(String encryptedData) {
        return textEncryptor.decrypt(encryptedData);
    }
}
