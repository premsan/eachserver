package com.eachserver.db;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Arrays;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jdbc.repository.config.AbstractJdbcConfiguration;

@Configuration
@RequiredArgsConstructor
public class JdbcConfiguration extends AbstractJdbcConfiguration {

    private final ObjectMapper objectMapper;

    @Override
    protected List<?> userConverters() {

        return Arrays.asList(
                new StringStringMapJdbcConverter.StringStringMapToJdbcValueConverter(objectMapper),
                new StringStringMapJdbcConverter.StringToStringStringMapConverter(objectMapper),
                new StringCollectionJdbcConverter.StringCollectionToJdbcValueConverter(
                        objectMapper),
                new StringCollectionJdbcConverter.StringToStringCollectionConverter(objectMapper),
                new ReferenceCollectionJdbcConverter.ReferenceCollectionToJdbcValueConverter(
                        objectMapper),
                new ReferenceCollectionJdbcConverter.StringToReferenceCollectionConverter(
                        objectMapper));
    }
}
