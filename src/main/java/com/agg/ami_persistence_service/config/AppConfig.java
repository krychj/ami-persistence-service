package com.agg.ami_persistence_service.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

@Configuration
@EnableScheduling
public class AppConfig {

	@Value("${spring.application.name:ami-aggregation-service}")
    private String applicationName;
	
	@Value("${tenant.id:single-tenant}")
    public String tenantId;
	
	@Value("${ev.analysis.frequency.days:10}")
    public String evAnalysisFrequencyDays;

	public String getApplicationName() {
		return applicationName;
	}

	public void setApplicationName(String applicationName) {
		this.applicationName = applicationName;
	}

	public String getTenantId() {
		return tenantId;
	}

	public void setTenantId(String tenantId) {
		this.tenantId = tenantId;
	}

	public int getEvAnalysisFrequencyDays() {
		return Integer.valueOf(evAnalysisFrequencyDays);
	}

	public void setEvAnalysisFrequencyDays(String evAnalysisFrequencyDays) {
		this.evAnalysisFrequencyDays = evAnalysisFrequencyDays;
	}	
}
