package com.agg.ami_persistence_service.topology;

import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.common.utils.Bytes;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.kstream.Materialized;
import org.apache.kafka.streams.state.KeyValueStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.agg.ami_persistence_service.config.AppConfig;
import com.agg.ami_persistence_service.config.kafka.EvAnalysisStatusSerde;
import com.agg.ami_persistence_service.dto.EvStatus;

@Component
public class EvStatusKafkaTopology {

	AppConfig appConfig;
	
	public EvStatusKafkaTopology(AppConfig appConfig) {
		this.appConfig = appConfig;
	}
	
	@Autowired
	public Topology createTopology(StreamsBuilder builder) {
		String tenantId = appConfig.getTenantId();
		String inputTopic1 = tenantId + ".ev-analysis-results";
		String outputTopic1 = tenantId + ".ev-status-store";
		builder
		    .table(inputTopic1,
		    		Materialized.<String, EvStatus, KeyValueStore<Bytes, byte[]>> as(outputTopic1)
		            .withKeySerde(Serdes.String()).withValueSerde(new EvAnalysisStatusSerde())
		    );		
		return builder.build();
	}
}
