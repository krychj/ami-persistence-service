package com.agg.ami_persistence_service.dto;

import java.time.Instant;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class EvAnalysisRequest {
	
	String servicePointId;
	Instant requestTime;

}
