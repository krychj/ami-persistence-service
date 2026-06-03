package com.agg.ami_persistence_service.config.kafka;

import java.util.Collections;
import java.util.Properties;

import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class KafkaTopicConfig {
	
	public KafkaTopicConfig(@Value("${spring.cloud.stream.kafka.binder.brokers}") String brokers,
			@Value("${tenant.id}") String tenantId) {
		
		String inputTopic = tenantId + ".ev-analysis-result";		
		String bootstrapAddress = brokers;
		
		
		Properties properties = new Properties();
        properties.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapAddress);
        AdminClient adminClient = AdminClient.create(properties);

    	Integer partitionNumber = 2;
		
    	NewTopic newTopic1 = new NewTopic(inputTopic, partitionNumber, (short) 1);    	
        adminClient.createTopics(Collections.singletonList(newTopic1));        
	}
}