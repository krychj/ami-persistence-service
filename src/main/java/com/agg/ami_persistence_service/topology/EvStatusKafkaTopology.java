package com.agg.ami_persistence_service.topology;

import java.time.Duration;

import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.common.utils.Bytes;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.Materialized;
import org.apache.kafka.streams.state.KeyValueStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.agg.ami_persistence_service.config.AppConfig;
import com.agg.ami_persistence_service.config.kafka.EvAnalysisStatusSerde;
import com.agg.ami_persistence_service.config.kafka.MeterDataAvailableDaysAggregateSerde;
import com.agg.ami_persistence_service.config.kafka.MeterDataDailyAggregateSerde;
import com.agg.ami_persistence_service.dto.EvStatus;
import com.agg.ami_persistence_service.dto.MeterDataAvailableDaysAggregate;
import com.agg.ami_persistence_service.dto.MeterDataDailyAggregate;

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
				
		String inputTopic2 = tenantId + ".ami.daily";
		String outputTopic2 = tenantId + ".available.days-store";		
		
		KStream<String, MeterDataDailyAggregate> streamDailyMeterData = builder.stream(inputTopic2, Consumed.with(Serdes.String(), new MeterDataDailyAggregateSerde()));
		
		streamDailyMeterData
				.groupByKey()
				.aggregate(MeterDataAvailableDaysAggregate::new, 
						(key, value, aggregate) -> aggregateAvailableDays(key, value, aggregate),
						Materialized.<String, MeterDataAvailableDaysAggregate, KeyValueStore<Bytes, byte[]>> as(outputTopic2)
								.withValueSerde(new MeterDataAvailableDaysAggregateSerde())
								.withRetention(Duration.ofMillis(86400000)));
		
		return builder.build();
	}
	
	MeterDataAvailableDaysAggregate aggregateAvailableDays(String key, MeterDataDailyAggregate value, MeterDataAvailableDaysAggregate aggregate) {    	
    	if(aggregate.getServicePointId() == null) {
    		aggregate.setServicePointId(value.getServicePointId());    		
    	}
    	aggregate.setAvailableDays(aggregate.getAvailableDays() + 1);
    	return aggregate;    	
    }
}
