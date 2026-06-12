package com.agg.ami_persistence_service.config.kafka;

import java.util.Map;

import org.apache.kafka.common.serialization.Deserializer;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serializer;

import com.agg.ami_persistence_service.dto.MeterDataDailyAggregate;

public class MeterDataDailyAggregateSerde implements Serde<MeterDataDailyAggregate> {
	MeterDataDailyAggregateSerializer serializer = new MeterDataDailyAggregateSerializer();
	MeterDataDailyAggregateDeserializer deserializer = new MeterDataDailyAggregateDeserializer();
	
	@Override
    public void configure(Map<String, ?> configs, boolean isKey) {
        serializer.configure(configs, isKey);
        deserializer.configure(configs, isKey);
    }
	
	@Override
	public Serializer<MeterDataDailyAggregate> serializer() {		
		return serializer;
	}

	@Override
	public Deserializer<MeterDataDailyAggregate> deserializer() {		
		return deserializer;
	}
}