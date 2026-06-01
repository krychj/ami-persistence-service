package com.agg.ami_persistence_service.dto;

import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Document(collection = "meter_data_1day")
public class MeterDataDailyAggregate implements Comparable<MeterDataDailyAggregate> {
	
	String id;
	String servicePointId;
    int year;
    int dayOfYear;
	double consumptionKwh;
    int countOfReads;    
	
    @Override
	public int compareTo(MeterDataDailyAggregate dailyAggregate) {		
		return Double.compare(consumptionKwh, dailyAggregate.getConsumptionKwh());
	}
}
