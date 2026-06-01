package com.agg.ami_persistence_service.dto;

import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Document(collection = "meter_data_1hour")
public class MeterDataHourlyAggregate implements Comparable<MeterDataHourlyAggregate> {
	
	String id;
	String servicePointId;
	int year;
    int dayOfYear;
    int hourOfDay;
	double consumptionKwh;
    int countOfReads;    
	
    @Override
	public int compareTo(MeterDataHourlyAggregate hourlyAggregate) {		
		return Double.compare(consumptionKwh, hourlyAggregate.getConsumptionKwh());
	}
}
