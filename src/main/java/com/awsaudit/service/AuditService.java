package com.awsaudit.service;

import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.awsaudit.context.AuditContext;
import com.awsaudit.domain.Recommendation;
import com.awsaudit.engine.RuleEngine;

@Service
public class AuditService {

	private final RuleEngine ruleEngine;
	private final String awsRegion;

	public AuditService(RuleEngine ruleEngine, @Value("${aws.region:us-east-1}") String awsRegion) {
		this.ruleEngine = ruleEngine;
		this.awsRegion = awsRegion;
	}

	public List<Recommendation> getRecommendations() {
		LocalDate endDate = LocalDate.now();
		LocalDate startDate = endDate.minusDays(7);
		double cpuThreshold = 2.0;

		AuditContext context = new AuditContext(
			awsRegion,
			startDate,
			endDate,
			cpuThreshold
		);

		return ruleEngine.runAll(context);
	}
}
