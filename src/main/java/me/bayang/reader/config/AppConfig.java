package me.bayang.reader.config;

import java.io.File;
import java.util.concurrent.Executor;

import org.apache.commons.configuration2.FileBasedConfiguration;
import org.apache.commons.configuration2.PropertiesConfiguration;
import org.apache.commons.configuration2.builder.FileBasedConfigurationBuilder;
import org.apache.commons.configuration2.builder.fluent.Parameters;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.module.afterburner.AfterburnerModule;

@Configuration
@EnableAsync
public class AppConfig {
    
    // FIXME detect OS and build file path accordingly
    // TODO use http://commons.apache.org/proper/commons-lang/javadocs/api-3.7/index.html
    public static final File appDir = new File(System.getProperty("user.home"));
    public static final File appConfigDir = new File(appDir, ".config/lecter");
    
    // TODO configure thread pool
    // http://www.baeldung.com/spring-async
    
    @Value("${appVersion}")
    public String appVersion = "";
    
    @Bean(name = "threadPoolTaskExecutor")
    public Executor threadPoolTaskExecutor() {
        ThreadPoolTaskExecutor t = new ThreadPoolTaskExecutor();
        t.setMaxPoolSize(200);
        return t;
    }
    
    @Bean(name="mapper")
    public ObjectMapper getMapper() {
            ObjectMapper mapper = new ObjectMapper().registerModule(new JavaTimeModule());
            
            mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
            mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
            
            mapper.disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);
            mapper.setSerializationInclusion(Include.NON_NULL);
            
            mapper.registerModule(new AfterburnerModule());
            
            return mapper;
    }
    
    @Bean("tokenConfig")
    public FileBasedConfigurationBuilder<FileBasedConfiguration> tokenConfig() {
        Parameters params = new Parameters();
        File propertiesFile = new File(appConfigDir, "token.properties");

        FileBasedConfigurationBuilder<FileBasedConfiguration> builder =
         new FileBasedConfigurationBuilder<FileBasedConfiguration>(PropertiesConfiguration.class)
         .configure(params.fileBased()
             .setFile(propertiesFile));
        builder.setAutoSave(true);
        return builder;
    }
    
    @Bean("userConfig")
    public FileBasedConfigurationBuilder<FileBasedConfiguration> userConfig() {
        Parameters params = new Parameters();
        File propertiesFile = new File(appConfigDir, "user.properties");

        FileBasedConfigurationBuilder<FileBasedConfiguration> builder =
         new FileBasedConfigurationBuilder<FileBasedConfiguration>(PropertiesConfiguration.class)
         .configure(params.fileBased()
             .setFile(propertiesFile));
        builder.setAutoSave(true);
        return builder;
    }
    
    @Bean("applicationConfig")
    public FileBasedConfigurationBuilder<FileBasedConfiguration> applicationConfig() {
        Parameters params = new Parameters();
        File propertiesFile = new File(appConfigDir, "config.properties");

        FileBasedConfigurationBuilder<FileBasedConfiguration> builder =
         new FileBasedConfigurationBuilder<FileBasedConfiguration>(PropertiesConfiguration.class)
         .configure(params.fileBased()
             .setFile(propertiesFile));
        builder.setAutoSave(true);
        return builder;
    }

}
