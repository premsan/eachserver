package com.eachserver.db;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.sql.JDBCType;
import java.util.HashMap;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.jdbc.core.mapping.JdbcValue;

public class StringStringMapJdbcConverter {

    @RequiredArgsConstructor
    public static class StringToStringStringMapConverter
            implements Converter<String, Map<String, String>> {

        private final ObjectMapper objectMapper;

        @Override
        public HashMap<String, String> convert(final String source) {
            try {
                return objectMapper.readValue(source, new TypeReference<>() {});
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        }
    }

    @RequiredArgsConstructor
    public static class StringStringMapToJdbcValueConverter
            implements Converter<Map<String, String>, JdbcValue> {

        private final ObjectMapper objectMapper;

        @Override
        public JdbcValue convert(final Map<String, String> source) {
            try {
                return JdbcValue.of(objectMapper.writeValueAsString(source), JDBCType.VARCHAR);
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
