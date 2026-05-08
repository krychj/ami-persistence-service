package com.agg.ami_persistence_service.service;

import java.time.Instant;
import java.time.ZoneOffset;
import java.util.List;

import org.springframework.data.mongodb.core.BulkOperations;
import org.springframework.data.mongodb.core.MongoTemplate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import com.agg.ami_persistence_service.dto.MeterData;
import com.agg.ami_persistence_service.dto.MeterDataAggregateDaily;
import com.agg.ami_persistence_service.dto.MeterDataAggregateHourly;
import com.agg.ami_persistence_service.entity.MeterData15min;

@Service
public class MeterDataService {

	MongoTemplate mongoTemplate;;
	
	@Autowired
	public MeterDataService(MongoTemplate mongoTemplate) {
		this.mongoTemplate = mongoTemplate;
	}
	
	public void persistMeterData15minBatch(List<MeterData> readings) {

	    BulkOperations bulkOps = mongoTemplate.bulkOps(BulkOperations.BulkMode.UNORDERED, MeterData15min.class);

	    readings.forEach(md -> {	    	
	    	Query query = Query.query(Criteria.where("id").is(md.getId() + "|" + getIntervalSlot(md.getReadTimestamp())));	        
	        Update update = new Update();
	        update.set("servicePointId", md.getServicePointId());
	        update.set("readTimestamp", md.getReadTimestamp());
	        update.set("kWh", md.getkWh());
	        bulkOps.upsert(query, update);
	    });
	    bulkOps.execute();
	}
	
	public int getIntervalSlot(Instant timestamp) {
        // Returns 0, 1, 2, or 3 — which 15-min slot within the hour
        return timestamp.atZone(ZoneOffset.UTC).getMinute() / 15;
    }
	
	public void persistMeterData1hourBatch(List<MeterDataAggregateHourly> readings) {

	    BulkOperations bulkOps = mongoTemplate.bulkOps(BulkOperations.BulkMode.UNORDERED, MeterDataAggregateHourly.class);

	    readings.forEach(md -> {	    	
	    	Query query = Query.query(Criteria.where("id").is(md.getId() + "|" + md.getHourOfDay()));	        
	        Update update = new Update();
	        update.set("servicePointId", md.getServicePointId());
	        update.set("year", md.getYear());
	        update.set("dayOfYear", md.getDayOfYear());
	        update.set("hourOfDay", md.getHourOfDay());
	        update.set("totalKWh", md.getTotalKWh());
	        update.set("readingCount", md.getReadingCount());
	        bulkOps.upsert(query, update);
	    });
	    bulkOps.execute();
	}
	
	public void persistMeterData1dayBatch(List<MeterDataAggregateDaily> readings) {

	    BulkOperations bulkOps = mongoTemplate.bulkOps(BulkOperations.BulkMode.UNORDERED, MeterDataAggregateDaily.class);

	    readings.forEach(md -> {	    	
	    	Query query = Query.query(Criteria.where("id").is(md.getId()));	        
	        Update update = new Update();
	        update.set("servicePointId", md.getServicePointId());
	        update.set("year", md.getYear());
	        update.set("dayOfYear", md.getDayOfYear());
	        update.set("totalKWh", md.getTotalKWh());
	        update.set("readingCount", md.getReadingCount());
	        bulkOps.upsert(query, update);
	    });
	    bulkOps.execute();
	}
}
