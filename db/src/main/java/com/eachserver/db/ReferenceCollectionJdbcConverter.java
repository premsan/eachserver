package com.eachserver.db;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.sql.JDBCType;
import java.util.Collection;
import lombok.RequiredArgsConstructor;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.jdbc.core.mapping.JdbcValue;

@RequiredArgsConstructor
public class ReferenceCollectionJdbcConverter {

    private final ObjectMapper objectMapper;

    @RequiredArgsConstructor
    public static class StringToReferenceCollectionConverter
            implements Converter<String, Collection<Reference>> {

        private final ObjectMapper objectMapper;

        @Override
        public Collection<Reference> convert(final String source) {
            try {
                return objectMapper.readValue(source, new TypeReference<>() {});
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        }
    }

    @RequiredArgsConstructor
    public static class ReferenceCollectionToJdbcValueConverter
            implements Converter<Collection<Reference>, JdbcValue> {

        private final ObjectMapper objectMapper;

        @Override
        public JdbcValue convert(final Collection<Reference> source) {
            try {
                return JdbcValue.of(objectMapper.writeValueAsString(source), JDBCType.VARCHAR);
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
