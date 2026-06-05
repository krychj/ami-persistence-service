package com.agg.ami_persistence_service.config.kafka;

import java.util.Map;

import org.apache.kafka.common.serialization.Deserializer;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serializer;

import com.agg.ami_persistence_service.dto.EvStatus;

public class EvAnalysisStatusSerde implements Serde<EvStatus> {
	EvAnalysisStatusSerializer serializer = new EvAnalysisStatusSerializer();
	EvAnalysisStatusDeserializer deserializer = new EvAnalysisStatusDeserializer();
	
	@Override
    public void configure(Map<String, ?> configs, boolean isKey) {
        serializer.configure(configs, isKey);
        deserializer.configure(configs, isKey);
    }
	
	@Override
	public Serializer<EvStatus> serializer() {		
		return serializer;
	}

	@Override
	public Deserializer<EvStatus> deserializer() {		
		return deserializer;
	}
}