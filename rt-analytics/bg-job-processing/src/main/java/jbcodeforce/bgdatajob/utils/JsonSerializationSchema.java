package jbcodeforce.bgdatajob.utils;

import org.apache.flink.api.common.serialization.SerializationSchema;
import org.apache.flink.shaded.jackson2.com.fasterxml.jackson.core.JsonProcessingException;
import org.apache.flink.shaded.jackson2.com.fasterxml.jackson.databind.ObjectMapper;

public class JsonSerializationSchema<T> implements SerializationSchema<T> {
    ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public byte[] serialize(Object element) {
        try {
               return objectMapper.writeValueAsBytes(element);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return null;
        }
    }
    
}
