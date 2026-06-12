package com.agg.ami_persistence_service.controller;

import java.time.Instant;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import com.agg.ami_persistence_service.config.AppConfig;
import com.agg.ami_persistence_service.service.MeterDataService;

@RestController
public class EvAnalysisRequestController {

	AppConfig appConfig;
	MeterDataService meterDataService;
	
	public EvAnalysisRequestController(AppConfig appConfig, MeterDataService meterDataService) {
		this.appConfig = appConfig;
		this.meterDataService = meterDataService;
	}
	
	@PostMapping(value = "/requestEvAnalysis")
	public ResponseEntity<String> requestEvAnalysis() {			
		String tenantId = appConfig.getTenantId();
		List<String> spIds = meterDataService.getAllServicePointIds();
		Instant requestTime = Instant.now();
		for(String spId : spIds) {			
			Integer availableDays = meterDataService.getNumberOfAvailableDaysForSpId(tenantId, spId);
			if (availableDays != null && availableDays >= 30) {
				System.out.println("EVLD requested for spId = " + spId + " at 30 days.");
				meterDataService.publishEvAnalysisRequest(tenantId, spId, requestTime);
			} /*else if (evStatus.needsUpdate(appConfig)){
				System.out.println("EVLD requested for spId = " + spId + " fired when updated was needed.");
				meterDataService.publishEvAnalysisRequest(tenantId, spId, requestTime);
			} */
		}		
		return ResponseEntity.ok("Analysis requested.");
	 }
}
