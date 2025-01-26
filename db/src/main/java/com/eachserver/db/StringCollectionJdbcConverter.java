package com.eachserver.db;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.sql.JDBCType;
import java.util.Collection;
import lombok.RequiredArgsConstructor;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.jdbc.core.mapping.JdbcValue;

public class StringCollectionJdbcConverter {

    @RequiredArgsConstructor
    public static class StringToStringCollectionConverter
            implements Converter<String, Collection<String>> {

        private final ObjectMapper objectMapper;

        @Override
        public Collection<String> convert(final String source) {
            try {
                return objectMapper.readValue(source, new TypeReference<>() {});
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        }
    }

    @RequiredArgsConstructor
    public static class StringCollectionToJdbcValueConverter
            implements Converter<Collection<String>, JdbcValue> {

        private final ObjectMapper objectMapper;

        @Override
        public JdbcValue convert(final Collection<String> source) {
            try {
                return JdbcValue.of(objectMapper.writeValueAsString(source), JDBCType.VARCHAR);
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
