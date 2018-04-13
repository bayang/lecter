package me.bayang.reader.config;

import java.io.File;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;

import org.apache.commons.configuration2.FileBasedConfiguration;
import org.apache.commons.configuration2.PropertiesConfiguration;
import org.apache.commons.configuration2.builder.FileBasedConfigurationBuilder;
import org.apache.commons.configuration2.builder.fluent.Parameters;
import org.apache.commons.lang3.SystemUtils;
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

import okhttp3.OkHttpClient;

@Configuration
@EnableAsync
public class AppConfig {
    
    public static final File appDir = SystemUtils.getUserHome();
    public static File appConfigDir;
    // FIXME detect OS and build file path accordingly
    static {
        if (SystemUtils.IS_OS_LINUX || SystemUtils.IS_OS_UNIX 
                || SystemUtils.IS_OS_MAC || SystemUtils.IS_OS_WINDOWS) {
            appConfigDir = new File(appDir, ".config/lecter");
        }
    }
    
    @Value("${appVersion}")
    public String appVersion = "";
    
    // http://www.baeldung.com/spring-async
    @Bean(name = "threadPoolTaskExecutor")
    public Executor threadPoolTaskExecutor() {
        ThreadPoolTaskExecutor t = new ThreadPoolTaskExecutor();
        t.setMaxPoolSize(50);
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
    
    @Bean("baseOkHttpClient")
    public OkHttpClient okHttpClient() {
        return new OkHttpClient.Builder()
                .followRedirects(true)
                .followSslRedirects(true)
                .readTimeout(1, TimeUnit.MINUTES)
                .connectTimeout(1, TimeUnit.MINUTES)
                .build();
    }

}
