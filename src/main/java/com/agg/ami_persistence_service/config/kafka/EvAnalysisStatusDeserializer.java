package com.agg.ami_persistence_service.config.kafka;

import org.apache.kafka.common.errors.SerializationException;
import org.apache.kafka.common.serialization.Deserializer;

import com.agg.ami_persistence_service.dto.EvStatus;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

public class EvAnalysisStatusDeserializer implements Deserializer<EvStatus> {

	private ObjectMapper objectMapper = new ObjectMapper();	

	@Override
	public EvStatus deserialize(String topic, byte[] data) {
		objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
		try {
			if (data == null) {
				System.out.println("Null received at deserializing");
				return null;
			}
			return objectMapper.readValue(new String(data, "UTF-8"), EvStatus.class);
		} catch (Exception e) {
			throw new SerializationException("Error when deserializing byte[] to EvAnalysisStatus");
		}
	}
}