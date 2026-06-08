package com.agg.ami_persistence_service.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.agg.ami_persistence_service.dto.SpEvAssessment;
import com.agg.ami_persistence_service.repo.SpEvAssessmentRepo;

@Service
public class SpEvAssessmentService {
	
	SpEvAssessmentRepo spEvAssessmentRepo;
	
	public SpEvAssessmentService(SpEvAssessmentRepo spEvAssessmentRepo) {
		this.spEvAssessmentRepo = spEvAssessmentRepo;
	}

	public void save(List<SpEvAssessment> assessments) {
		spEvAssessmentRepo.saveAll(assessments);
	}
}
