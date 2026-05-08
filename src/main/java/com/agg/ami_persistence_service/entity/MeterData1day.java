package com.agg.ami_persistence_service.entity;

import java.time.Instant;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Document(collection = "meter_data_1day")
public class MeterData1day implements Comparable<MeterData1day>{
	
	@Id
	String id;
	
	String servicePointId;
	int year;
    int dayOfYear;
    Instant readTimestamp;
    float kWh;   
    
    @Override
	public int compareTo(MeterData1day md) {		
		return Float.compare(kWh, md.getKWh());
	}
}
