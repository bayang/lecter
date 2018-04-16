package me.bayang.reader.config;

import javax.annotation.PostConstruct;

import org.apache.commons.configuration2.ConfigurationDecoder;
import org.jasypt.encryption.pbe.StandardPBEStringEncryptor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class AppConfigurationDecoder implements ConfigurationDecoder {
    
    private StandardPBEStringEncryptor encryptor;
    
    @Value("${encryptor.password}")
    private String encryptorPassword;
    
    @PostConstruct
    public void init() {
        encryptor = new StandardPBEStringEncryptor();
        encryptor.setPassword(encryptorPassword);
    }

    @Override
    public String decode(String s) {
        return encryptor.decrypt(s);
    }
    
    public String encode(String in) {
        return encryptor.encrypt(in);
    }

}
