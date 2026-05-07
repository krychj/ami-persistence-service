package com.agg.ami_persistence_service.entity;

import java.time.Instant;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Document(collection = "meter_data_1hour")
public class MeterData1hour implements Comparable<MeterData1hour>{
	
	@Id
	String id;
	
	String servicePointId;
    Instant readTimestamp;
    float kWh;   
    
    @Override
	public int compareTo(MeterData1hour md) {		
		return Float.compare(kWh, md.getKWh());
	}
}
