package com.cloudator.files.cloudatorfiles.jwt;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.springframework.stereotype.Component;

@Component
public class SecretKeyReader {
    private Properties properties;

    private void loadProperties() {
        properties = new Properties();
        try (InputStream input = new FileInputStream("src/main/resources/application.properties")) {
            // Cargar el archivo de propiedades
            properties.load(input);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public String getProperty(String key) {
        return properties.getProperty(key);
    }
}
