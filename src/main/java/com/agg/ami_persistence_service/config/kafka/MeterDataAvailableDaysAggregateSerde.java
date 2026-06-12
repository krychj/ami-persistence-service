package com.agg.ami_persistence_service.config.kafka;

import java.util.Map;

import org.apache.kafka.common.serialization.Deserializer;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serializer;

import com.agg.ami_persistence_service.dto.MeterDataAvailableDaysAggregate;

public class MeterDataAvailableDaysAggregateSerde implements Serde<MeterDataAvailableDaysAggregate> {
	MeterDataAvailableDaysAggregateSerializer serializer = new MeterDataAvailableDaysAggregateSerializer();
	MeterDataAvailableDaysAggregateDeserializer deserializer = new MeterDataAvailableDaysAggregateDeserializer();
	
	@Override
    public void configure(Map<String, ?> configs, boolean isKey) {
        serializer.configure(configs, isKey);
        deserializer.configure(configs, isKey);
    }
	
	@Override
	public Serializer<MeterDataAvailableDaysAggregate> serializer() {		
		return serializer;
	}

	@Override
	public Deserializer<MeterDataAvailableDaysAggregate> deserializer() {		
		return deserializer;
	}
}