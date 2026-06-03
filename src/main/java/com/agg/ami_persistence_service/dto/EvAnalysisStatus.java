package com.agg.ami_persistence_service.dto;

import java.time.Instant;

import lombok.Data;

@Data
public class EvAnalysisStatus {
	
	String servicePointId;
	EV_STATE evState;
	Instant firstDetectedAt;
	Instant lastFullAnalysisAt;
	Instant nextFullAnalysisAt;
}
