package com.axion.demo.config;

import org.axonframework.serialization.Serializer;
import org.axonframework.serialization.json.JacksonSerializer;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SerializerConfiguration {
    @Qualifier("defaultAxonXStream")
    @Bean
    public Serializer eventSerializer() {
        return JacksonSerializer.builder().build();
    }
}
