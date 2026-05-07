package com.agg.ami_persistence_service.service;

import java.util.List;

import org.springframework.data.mongodb.core.BulkOperations;
import org.springframework.data.mongodb.core.MongoTemplate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import com.agg.ami_persistence_service.dto.MeterData;
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
	    	Query query = Query.query(Criteria.where("id").is(md.getId()));	        
	        Update update = new Update();
	        update.set("servicePointId", md.getServicePointId());
	        update.set("readTimestamp", md.getReadTimestamp());
	        update.set("kWh", md.getUsage_kWh());
	        bulkOps.upsert(query, update);
	    });
	    bulkOps.execute();
	}
}
