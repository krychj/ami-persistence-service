package com.agg.ami_persistence_service.repo;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.agg.ami_persistence_service.dto.SpEvAssessment;

@Repository
public interface SpEvAssessmentRepo extends MongoRepository<SpEvAssessment, String> {

}

