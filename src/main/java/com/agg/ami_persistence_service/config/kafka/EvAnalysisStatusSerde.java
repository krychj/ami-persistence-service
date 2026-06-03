package com.agg.ami_persistence_service.config.kafka;

import java.util.Map;

import org.apache.kafka.common.serialization.Deserializer;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serializer;

import com.agg.ami_persistence_service.dto.EvAnalysisStatus;

public class EvAnalysisStatusSerde implements Serde<EvAnalysisStatus> {
	EvAnalysisStatusSerializer serializer = new EvAnalysisStatusSerializer();
	EvAnalysisStatusDeserializer deserializer = new EvAnalysisStatusDeserializer();
	
	@Override
    public void configure(Map<String, ?> configs, boolean isKey) {
        serializer.configure(configs, isKey);
        deserializer.configure(configs, isKey);
    }
	
	@Override
	public Serializer<EvAnalysisStatus> serializer() {		
		return serializer;
	}

	@Override
	public Deserializer<EvAnalysisStatus> deserializer() {		
		return deserializer;
	}
}