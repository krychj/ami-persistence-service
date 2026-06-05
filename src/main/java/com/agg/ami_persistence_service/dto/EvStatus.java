package com.agg.ami_persistence_service.dto;

import java.time.Instant;

import com.agg.ami_persistence_service.config.AppConfig;

import lombok.Data;

@Data
public class EvStatus {
	
	String servicePointId;
	EV_STATE evState;
	Instant firstDetectedAt;
	Instant lastFullAnalysisAt;
	int numOfDaysUsedInAnalysis;
	Instant nextFullAnalysisAt;
	
	
	public boolean needsUpdate(AppConfig appConfig) {		
	    Instant now = Instant.now();	    
	    if(evState == EV_STATE.CONFIRMED_EV && nextFullAnalysisAt.isBefore(now)) {
	    		return false;
	    }	    
	    return true;
	}
}
