package com.agg.ami_persistence_service.service;

import java.time.Instant;
import java.time.ZoneOffset;
import java.util.List;

import org.springframework.data.mongodb.core.BulkOperations;
import org.springframework.data.mongodb.core.MongoTemplate;

import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import com.agg.ami_persistence_service.dto.MeterData;
import com.agg.ami_persistence_service.dto.MeterDataDailyAggregate;
import com.agg.ami_persistence_service.dto.MeterDataHourlyAggregate;

@Service
public class MeterDataService {

	MongoTemplate mongoTemplate;;
	
	public MeterDataService(MongoTemplate mongoTemplate) {
		this.mongoTemplate = mongoTemplate;
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
	
	public void persistMeterData1dayBatch(List<MeterDataDailyAggregate> readings) {

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
	    bulkOps.execute();
	}
}
