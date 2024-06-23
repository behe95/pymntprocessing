package com.pymntprocessing.pymntprocessing.config;

import com.pymntprocessing.pymntprocessing.util.DatabaseDateTimeConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ConverterConfig {
    @Bean
    public DatabaseDateTimeConverter databaseDateTimeConverter() {
        return new DatabaseDateTimeConverter();
    }
}
