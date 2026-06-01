package com.agg.ami_persistence_service.dto;

import java.time.Instant;

import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Data;

@Data
@Document(collection = "meter_data_15min")
public class MeterData {
	
	String id;
	String servicePointId;
    Instant readTimestamp;
    float consumptionKwh;
}
