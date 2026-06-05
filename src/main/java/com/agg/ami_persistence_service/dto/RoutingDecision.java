package com.agg.ami_persistence_service.dto;

import java.time.Instant;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RoutingDecision {
	
	private String servicePointId;   
    private EV_STATE currentState;
    private Instant nextFullAnalysisAt;

    public boolean shouldFire() {
        if((currentState == EV_STATE.CONFIRMED_EV) && (Instant.now().compareTo(nextFullAnalysisAt) == -1)) {
        	return false;
        } else if (currentState != EV_STATE.CONFIRMED_EV || (Instant.now().compareTo(nextFullAnalysisAt) != -1)) {
        	return true;
        }
        return false;
    }
}
