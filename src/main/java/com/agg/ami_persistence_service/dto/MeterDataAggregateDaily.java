package com.agg.ami_persistence_service.dto;

import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "meter_data_1day")
public class MeterDataAggregateDaily {
	
	String id;
	String servicePointId;
	int year;
    int dayOfYear;
    double totalKWh;
    int countOfReads;
	
    public MeterDataAggregateDaily() {
    	
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

	public int getYear() {
		return year;
	}

	public void setYear(int year) {
		this.year = year;
	}

	public int getDayOfYear() {
		return dayOfYear;
	}

	public void setDayOfYear(int dayOfYear) {
		this.dayOfYear = dayOfYear;
	}

	public double getTotalKWh() {
		return totalKWh;
	}

	public void setTotalKWh(double totalKWh) {
		this.totalKWh = totalKWh;
	}

	public int getCountOfReads() {
		return countOfReads;
	}

	public void setCountOfReads(int countOfReads) {
		this.countOfReads = countOfReads;
	}
}
