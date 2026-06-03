package com.agg.ami_persistence_service.config.kafka;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

import static org.apache.kafka.streams.StreamsConfig.APPLICATION_ID_CONFIG;
import static org.apache.kafka.streams.StreamsConfig.BOOTSTRAP_SERVERS_CONFIG;
import static org.apache.kafka.streams.StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG;
import static org.apache.kafka.streams.StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG;
import static org.apache.kafka.streams.StreamsConfig.STATE_DIR_CONFIG;

import org.apache.kafka.common.serialization.Serdes;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.annotation.EnableKafkaStreams;
import org.springframework.kafka.annotation.KafkaStreamsDefaultConfiguration;
import org.springframework.kafka.config.KafkaStreamsConfiguration;

@Configuration
@EnableKafka
@EnableKafkaStreams
public class KafkaStreamConfig {

	@Value("${spring.application.name:ev-load-disaggregation-engine}")
    private String applicationName;
	
    @Bean(name = KafkaStreamsDefaultConfiguration.DEFAULT_STREAMS_CONFIG_BEAN_NAME)
    KafkaStreamsConfiguration kStreamsConfig(@Value("${spring.cloud.stream.kafka.binder.brokers}") String brokers) throws IOException {        
    	Map<String, Object> props = new HashMap<>();
        props.put(APPLICATION_ID_CONFIG, applicationName);
        props.put(BOOTSTRAP_SERVERS_CONFIG, brokers);        
		props.put(DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass().getName());
        props.put(DEFAULT_VALUE_SERDE_CLASS_CONFIG, EvAnalysisStatusSerde.class.getName());
        Path stateDirectory = Files.createTempDirectory("kafka-streams-dir");
        props.put(STATE_DIR_CONFIG, stateDirectory.toAbsolutePath().toString());

        return new KafkaStreamsConfiguration(props);
    }
}