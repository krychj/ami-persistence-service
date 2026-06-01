package com.agg.ami_persistence_service.config;

import java.util.List;
import java.util.function.Consumer;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.Message;

import com.agg.ami_persistence_service.dto.MeterData;
import com.agg.ami_persistence_service.dto.MeterDataDailyAggregate;
import com.agg.ami_persistence_service.dto.MeterDataHourlyAggregate;
import com.agg.ami_persistence_service.service.MeterDataService;

@Configuration
public class MessagingConfiguration {

	MeterDataService meterDataService;
	
	public MessagingConfiguration(MeterDataService meterDataService) {
		this.meterDataService = meterDataService;
	}
	
	// Handles events coming from '[tenantId].ami.raw.15min' topic.
	@Bean
	public Consumer<Message<List<MeterData>>> handleNewMeterData15min() {
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
	public Consumer<Message<List<MeterDataHourlyAggregate>>> handleNewMeterData1hour() {
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
	public Consumer<Message<List<MeterDataDailyAggregate>>> handleNewMeterData1day() {
		return message -> {
			if (message == null || message.getPayload().isEmpty()) {
				return;
			}
			List<MeterDataDailyAggregate> readings = message.getPayload();
			meterDataService.persistMeterData1dayBatch(readings);
		};
	}
}
