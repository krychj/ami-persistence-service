package com.agg.ami_persistence_service.topology;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.common.utils.Bytes;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.KTable;
import org.apache.kafka.streams.kstream.Materialized;
import org.apache.kafka.streams.state.KeyValueStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.agg.ami_persistence_service.config.AppConfig;
import com.agg.ami_persistence_service.config.kafka.EvAnalysisStatusSerde;
import com.agg.ami_persistence_service.dto.EV_STATE;
import com.agg.ami_persistence_service.dto.EvAnalysisStatus;
import com.agg.ami_persistence_service.dto.MeterDataDailyAggregate;
import com.agg.ami_persistence_service.dto.RoutingDecision;

@Component
public class EvStatusKafkaTopology {

	AppConfig appConfig;
	
	public EvStatusKafkaTopology(AppConfig appConfig) {
		this.appConfig = appConfig;
	}
	
	@Autowired
	public Topology createTopology(StreamsBuilder builder) {
		String tenantId = appConfig.getTenantId();
		String inputTopic1 = tenantId + ".ev-analysis-result";
		String outputTopic1 = tenantId + ".ev-status-store";
		KTable<String, EvAnalysisStatus> evStatusTable = builder
			    .table(inputTopic1,
			    		Materialized.<String, EvAnalysisStatus, KeyValueStore<Bytes, byte[]>> as(outputTopic1)
			            .withKeySerde(Serdes.String()).withValueSerde(new EvAnalysisStatusSerde())
			    );
		
		String inputTopic2 = tenantId + ".ami.hourly";
		String outputTopic2 = tenantId + ".ev-analysis-triggers";
		KStream<String, MeterDataDailyAggregate> meterDataDailyAggregateStream = builder.stream("ami-aggregates");

		meterDataDailyAggregateStream
	        .leftJoin(evStatusTable, (aggregate, evStatus) -> evAnalysisTriggerDecision(aggregate, evStatus))
	        .filter((spId, decision) -> decision.shouldFire())
	        .to(outputTopic2);
		    		
		return builder.build();
	}
	
	public RoutingDecision evAnalysisTriggerDecision(MeterDataDailyAggregate value, EvAnalysisStatus status) {		
		String spId = value.getServicePointId();
	    Instant now = Instant.now();

	    // First time we've ever seen this service point
	    if (status == null) {	    	
	    	Instant nextFullAnalysisAt = now.plus(appConfig.getEvAnalysisFrequencyDays(), ChronoUnit.DAYS);
	    	return RoutingDecision.builder()
	                .servicePointId(spId)	               
	                .currentState(EV_STATE.UNKNOWN)
	                .nextFullAnalysisAt(nextFullAnalysisAt)
	                .build();
	    } else {
	    	return RoutingDecision.builder()
	                .servicePointId(spId)	               
	                .currentState(status.getEvState())
	                .nextFullAnalysisAt(status.getNextFullAnalysisAt())
	                .build();
	    }
	}
}
