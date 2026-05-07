package com.agg.ami_persistence_service.dto;

import java.time.Instant;

public class MeterData {
	
	String id;
	String servicePointId;
    Instant readTimestamp;
    float usage_kWh;
	
	public MeterData() {

	}
	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}
	
    public String getServicePointId() {
		return servicePointId;
	}
	public void setServicePointId(String servicePointId) {
		this.servicePointId = servicePointId;
	}
	public Instant getReadTimestamp() {
		return readTimestamp;
	}
	public void setReadTimestamp(Instant readTimestamp) {
		this.readTimestamp = readTimestamp;
	}
	public float getUsage_kWh() {
		return usage_kWh;
	}
	public void setUsage_kWh(float usage_kWh) {
		this.usage_kWh = usage_kWh;
	}	
}
