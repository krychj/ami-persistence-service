package com.agg.ami_persistence_service.dto;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
@Document(collection = "sp_ev_assessments")
public class SpEvAssessment {
	
	@Id
	String spId;
	EV_STATE evState;
	String transformerId;
}
