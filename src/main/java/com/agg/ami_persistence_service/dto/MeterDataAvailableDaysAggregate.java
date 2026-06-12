package com.agg.ami_persistence_service.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class MeterDataAvailableDaysAggregate {
		
	String servicePointId;    
    int availableDays;
}
