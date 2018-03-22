package me.bayang.reader.config;

import java.util.concurrent.Executor;

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
    
    // TODO configurer le thread pool
    // http://www.baeldung.com/spring-async
    
    @Bean(name = "threadPoolTaskExecutor")
    public Executor threadPoolTaskExecutor() {
        ThreadPoolTaskExecutor t = new ThreadPoolTaskExecutor();
        t.setMaxPoolSize(200);
        return t;
    }
    
    @Bean
    public ObjectMapper getMapper() {
            ObjectMapper mapper = new ObjectMapper().registerModule(new JavaTimeModule());
            
            mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
            mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
            
            mapper.disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);
            mapper.setSerializationInclusion(Include.NON_NULL);
            
            mapper.registerModule(new AfterburnerModule());
            
            return mapper;
    }

}
