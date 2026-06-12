package com.agg.ami_persistence_service.config.kafka;

import org.apache.kafka.common.errors.SerializationException;
import org.apache.kafka.common.serialization.Serializer;

import com.agg.ami_persistence_service.dto.MeterDataAvailableDaysAggregate;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

public class MeterDataAvailableDaysAggregateSerializer implements Serializer<MeterDataAvailableDaysAggregate> {

	private final ObjectMapper objectMapper = new ObjectMapper();

	@Override
	public byte[] serialize(String topic, MeterDataAvailableDaysAggregate data) {
		objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
		try {
            if (data == null){
                System.out.println("Null received at serializing");
                return null;
            }            
            return objectMapper.writeValueAsBytes(data);
        } catch (Exception e) {
            throw new SerializationException("Error when serializing MeterDataAvailableDaysAggregate to byte[]");
        }
	}
}