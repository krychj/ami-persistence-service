package com.agg.ami_persistence_service.config;

import java.time.Instant;
import java.util.List;
import java.util.function.Consumer;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.Message;

import com.agg.ami_persistence_service.dto.EvStatus;
import com.agg.ami_persistence_service.dto.MeterData;
import com.agg.ami_persistence_service.dto.MeterDataDailyAggregate;
import com.agg.ami_persistence_service.dto.MeterDataHourlyAggregate;
import com.agg.ami_persistence_service.dto.SpEvAssessment;
import com.agg.ami_persistence_service.service.MeterDataService;
import com.agg.ami_persistence_service.service.ServicePointTransformerMappingService;
import com.agg.ami_persistence_service.service.SpEvAssessmentService;

@Configuration
public class MessagingConfiguration {

	MeterDataService meterDataService;
	AppConfig appConfig;
	ServicePointTransformerMappingService servicePointTransformerMappingService;
	SpEvAssessmentService spEvAssessmentService;
	
	public MessagingConfiguration(MeterDataService meterDataService, AppConfig appConfig,
			ServicePointTransformerMappingService servicePointTransformerMappingService,
			SpEvAssessmentService spEvAssessmentService) {
		
		this.meterDataService = meterDataService;
		this.appConfig = appConfig;
		this.servicePointTransformerMappingService = servicePointTransformerMappingService;
		this.spEvAssessmentService = spEvAssessmentService;
	}
	
	// Handles events coming from '[tenantId].ami.raw.15min' topic.
	@Bean
	Consumer<Message<List<MeterData>>> handleNewMeterData15min() {
		return message -> {
			if (message == null || message.getPayload().isEmpty()) {
				return;
			}
			List<MeterData> readings = message.getPayload();
			meterDataService.persistMeterData15minBatch(readings);
		};
	}
	
	// Handles events coming from '[tenantId].ami.hourly' topic.
	@Bean
	Consumer<Message<List<MeterDataHourlyAggregate>>> handleNewMeterData1hour() {
		return message -> {
			if (message == null || message.getPayload().isEmpty()) {
				return;
			}
			List<MeterDataHourlyAggregate> readings = message.getPayload();
			meterDataService.persistMeterData1hourBatch(readings);
		};
	}
	
	// Handles events coming from '[tenantId].ami.daily' topic.
	@Bean
	Consumer<Message<List<MeterDataDailyAggregate>>> handleNewMeterData1day() {
		return message -> {
			if (message == null || message.getPayload().isEmpty()) {
				return;
			}
			List<MeterDataDailyAggregate> readings = message.getPayload();
			int result = meterDataService.persistMeterData1dayBatch(readings);
			String tenantId = appConfig.getTenantId();
			if(result > 0) {
				Instant requestTime = Instant.now();
				for(MeterDataDailyAggregate dailyAggregate : readings) {
					String spId = dailyAggregate.getServicePointId();
					EvStatus evStatus = meterDataService.getEvStatus(tenantId, spId);
					if (evStatus == null || evStatus.needsUpdate(appConfig)) {
						meterDataService.publishEvAnalysisRequest(tenantId, spId, requestTime);
					}
				}				
			}
		};
	}
	
	// Handles events coming from '[tenantId].ev-analysis-results' topic.
	@Bean
	Consumer<Message<List<EvStatus>>> handleNewEvAnalysisResults() {
		return message -> {
			if (message == null || message.getPayload().isEmpty()) {
				return;
			}
			List<EvStatus> evStatusEvents = message.getPayload();
			List<SpEvAssessment> assessments = evStatusEvents.stream()
				.map(evStatus -> SpEvAssessment.builder()
					.spId(evStatus.getServicePointId())
					.evState(evStatus.getEvState())
					.transformerId(servicePointTransformerMappingService.getTransformerId(evStatus.getServicePointId()).get())
					.build()).toList();
			spEvAssessmentService.save(assessments);
		};
	}
}
