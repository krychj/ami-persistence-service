package com.agg.ami_persistence_service.service;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import org.joda.time.Instant;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import com.agg.ami_persistence_service.dto.ServicePointTransformerMapping;

import jakarta.annotation.PostConstruct;

@Service
public class ServicePointTransformerMappingService {

	private volatile Map<String, String> mappingCache = new ConcurrentHashMap<>(300_000);
	RestClient.Builder restClientBuilder;
	
	public ServicePointTransformerMappingService(RestClient.Builder restClientBuilder) {
		this.restClientBuilder = restClientBuilder;
	}
	
	@PostConstruct
    public void loadAllMappings() {
        mappingCache = buildFreshMap();
    }

	//@Scheduled(fixedDelay = 15 * 60 * 1000)
    public void refreshMappings() {        
        System.out.println("Refreshing servicePoint-to-transformer mapping at " + Instant.now());
		Map<String, String> fresh = buildFreshMap();       
        this.mappingCache = fresh;
    }

    private Map<String, String> buildFreshMap() {
        Map<String, String> fresh =
            new ConcurrentHashMap<>(300_000);
        
        restClientBuilder
	        .baseUrl("http://localhost:8084")
	        .defaultHeader(
	            HttpHeaders.CONTENT_TYPE,
	            MediaType.APPLICATION_JSON_VALUE);
        
		List<ServicePointTransformerMapping> mapping = restClientBuilder.build().get().uri("servicePointTransformerMapping")
        	.retrieve().body(new ParameterizedTypeReference<List<ServicePointTransformerMapping>>() {});

        mapping.forEach(m -> fresh.put(m.getSpId(), m.getTransfrmerId()));        
        return fresh;
    }
    
    public Optional<String> getTransformerId(String spId) {
        return Optional.ofNullable(mappingCache.get(spId));
    }
}
