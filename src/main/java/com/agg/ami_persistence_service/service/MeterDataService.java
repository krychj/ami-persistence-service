package com.agg.ami_persistence_service.service;

import java.time.Instant;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StoreQueryParameters;
import org.apache.kafka.streams.state.KeyValueIterator;
import org.apache.kafka.streams.state.QueryableStoreTypes;
import org.apache.kafka.streams.state.ReadOnlyKeyValueStore;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.data.mongodb.core.BulkOperations;
import org.springframework.data.mongodb.core.MongoTemplate;

import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.kafka.config.StreamsBuilderFactoryBean;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;
import org.springframework.util.MimeTypeUtils;

import com.agg.ami_persistence_service.dto.EvAnalysisRequest;
import com.agg.ami_persistence_service.dto.EvStatus;
import com.agg.ami_persistence_service.dto.MeterData;
import com.agg.ami_persistence_service.dto.MeterDataAvailableDaysAggregate;
import com.agg.ami_persistence_service.dto.MeterDataDailyAggregate;
import com.agg.ami_persistence_service.dto.MeterDataHourlyAggregate;
import com.mongodb.bulk.BulkWriteResult;

@Service
public class MeterDataService {

	MongoTemplate mongoTemplate;
	StreamsBuilderFactoryBean factoryBean;
	StreamBridge streamBridge;
	
	public MeterDataService(MongoTemplate mongoTemplate, StreamsBuilderFactoryBean factoryBean,
			StreamBridge streamBridge) {
		
		this.mongoTemplate = mongoTemplate;
		this.factoryBean = factoryBean;
		this.streamBridge = streamBridge;
	}
	
	public void persistMeterData15minBatch(List<MeterData> readings) {

	    BulkOperations bulkOps = mongoTemplate.bulkOps(BulkOperations.BulkMode.UNORDERED, MeterData.class);

	    readings.forEach(md -> {	    	
	    	Query query = Query.query(Criteria.where("id").is(md.getId() + "|" + getIntervalSlot(md.getReadTimestamp())));	        
	        Update update = new Update();
	        update.set("servicePointId", md.getServicePointId());
	        update.set("readTimestamp", md.getReadTimestamp());
	        update.set("consumptionKwh", md.getConsumptionKwh());
	        bulkOps.upsert(query, update);
	    });
	    bulkOps.execute();
	}
	
	public int getIntervalSlot(Instant timestamp) {
        // Returns 0, 1, 2, or 3 — which 15-min slot within the hour
        return timestamp.atZone(ZoneOffset.UTC).getMinute() / 15;
    }
	
	public void persistMeterData1hourBatch(List<MeterDataHourlyAggregate> readings) {

	    BulkOperations bulkOps = mongoTemplate.bulkOps(BulkOperations.BulkMode.UNORDERED, MeterDataHourlyAggregate.class);

	    readings.forEach(md -> {	    	
	    	Query query = Query.query(Criteria.where("id").is(md.getId() + "|" + md.getHourOfDay()));	        
	        Update update = new Update();	        
	        update.set("servicePointId", md.getServicePointId());
	        update.set("year", md.getYear());
	        update.set("dayOfYear", md.getDayOfYear());
	        update.set("hourOfDay", md.getHourOfDay());
	        update.set("consumptionKwh", md.getConsumptionKwh());
	        update.set("countOfReads", md.getCountOfReads());
	        bulkOps.upsert(query, update);
	    });
	    bulkOps.execute();
	}
	
	public int persistMeterData1dayBatch(List<MeterDataDailyAggregate> readings) {

	    BulkOperations bulkOps = mongoTemplate.bulkOps(BulkOperations.BulkMode.UNORDERED, MeterDataDailyAggregate.class);

	    readings.forEach(md -> {	    	
	    	Query query = Query.query(Criteria.where("id").is(md.getId()));	        
	        Update update = new Update();
	        update.set("servicePointId", md.getServicePointId());
	        update.set("year", md.getYear());
	        update.set("dayOfYear", md.getDayOfYear());
	        update.set("consumptionKwh", md.getConsumptionKwh());
	        update.set("countOfReads", md.getCountOfReads());
	        bulkOps.upsert(query, update);
	    });
	    BulkWriteResult result = bulkOps.execute();
	    int newDocs = result.getUpserts().size();
	    int modifiedDocs = result.getModifiedCount();
	    return newDocs + modifiedDocs;
	}
	
	public EvStatus getEvStatus(String tenantId, String servicePointId) {
		KafkaStreams kafkaStreams =  factoryBean.getKafkaStreams();
		String stateStoreEvStatus = tenantId + ".ev-status-store";
		ReadOnlyKeyValueStore<String, EvStatus> store = 
				kafkaStreams.store(StoreQueryParameters.fromNameAndType(stateStoreEvStatus, QueryableStoreTypes.keyValueStore()));
		EvStatus evStatus = store.get(servicePointId);
		return evStatus;
	}
	
	public List<EvStatus> getAllEvStatuses(String tenantId) {
		KafkaStreams kafkaStreams =  factoryBean.getKafkaStreams();
		String stateStoreEvStatus = tenantId + ".ev-status-store";
		ReadOnlyKeyValueStore<String, EvStatus> store = 
				kafkaStreams.store(StoreQueryParameters.fromNameAndType(stateStoreEvStatus, QueryableStoreTypes.keyValueStore()));
		KeyValueIterator<String, EvStatus> evStatuses = store.all();
		List<EvStatus> all = new ArrayList<>();
		evStatuses.forEachRemaining(status -> {
			all.add(status.value);
		});
		return all;
	}
	
	public Integer getNumberOfAvailableDaysForSpId(String tenantId, String servicePointId) {
		KafkaStreams kafkaStreams =  factoryBean.getKafkaStreams();
		String stateStoreEvStatus = tenantId + ".available.days-store";
		ReadOnlyKeyValueStore<String, MeterDataAvailableDaysAggregate> store = 
				kafkaStreams.store(StoreQueryParameters.fromNameAndType(stateStoreEvStatus, QueryableStoreTypes.keyValueStore()));
		MeterDataAvailableDaysAggregate availableDays = store.get(servicePointId);
		Integer numOfDaysAvailable = availableDays != null ? store.get(servicePointId).getAvailableDays() : 0;
		return numOfDaysAvailable;
	}
	
	public void publishEvAnalysisRequest(String tenantId, String servicePointId, Instant requestTime) {		
		byte[] messageKeyByteArray = servicePointId.getBytes();
		EvAnalysisRequest request = EvAnalysisRequest.builder()
				.servicePointId(servicePointId)
				.requestTime(requestTime)
				.build();
		Message<EvAnalysisRequest> message = MessageBuilder.withPayload(request)
				.setHeader(KafkaHeaders.KEY, messageKeyByteArray)					
				.build();
		String topic = tenantId + ".ev-analysis-requests";
		streamBridge.send(topic, message, MimeTypeUtils.APPLICATION_JSON);
	}
	
	public List<String> getAllServicePointIds() {
        Query query = new Query();
        query.fields().include("servicePointId");

        return mongoTemplate.findAll(MeterDataDailyAggregate.class)
                .stream()
                .map(MeterDataDailyAggregate::getServicePointId)
                .distinct()
                .collect(Collectors.toList());
    }
}
