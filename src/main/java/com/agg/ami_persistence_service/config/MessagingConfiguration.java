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
import com.agg.ami_persistence_service.service.MeterDataService;

@Configuration
public class MessagingConfiguration {

	MeterDataService meterDataService;
	AppConfig appConfig;
	
	public MessagingConfiguration(MeterDataService meterDataService, AppConfig appConfig) {
		this.meterDataService = meterDataService;
		this.appConfig = appConfig;
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
}
