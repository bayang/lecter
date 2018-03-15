package me.bayang.reader.config;

import java.util.concurrent.Executor;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.AsyncConfigurer;
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
        return new ThreadPoolTaskExecutor();
    }
    
    @Bean
    public ObjectMapper getMapper() {
            ObjectMapper mapper = new ObjectMapper().registerModule(new JavaTimeModule());
            
            // without this jackson writes the date as a numeric timestamp (e.g. 125698745), 
            // but we want dates to be displayed in ISO 8601 format (e.g. 2016-11-23T11:22:09+00:00)
            mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
            mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
            
            // without this jackson throws an exception when serializing empty beans like
            // OpenSessionRequest instead of giving an empty json object (like this --> '{}')
            mapper.disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);
            mapper.setSerializationInclusion(Include.NON_NULL);
            
            // optimisation serialization  /deserialization
            mapper.registerModule(new AfterburnerModule());
            
            return mapper;
    }

}
