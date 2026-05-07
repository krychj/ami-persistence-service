package com.agg.ami_persistence_service.config;

import java.util.List;
import java.util.function.Consumer;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.Message;

import com.agg.ami_persistence_service.dto.MeterData;

import com.agg.ami_persistence_service.service.MeterDataService;

@Configuration
public class MessagingConfiguration {

	MeterDataService meterDataService;
	
	public MessagingConfiguration(MeterDataService meterDataService) {
		this.meterDataService = meterDataService;
	}
	
	// Handles events coming from 'ami.raw.15min' topic.
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
}
