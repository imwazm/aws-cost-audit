package com.awsaudit.service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.awsaudit.context.AuditContext;
import com.awsaudit.domain.Recommendation;
import com.awsaudit.domain.ResourceType;
import com.awsaudit.engine.RuleEngine;

import software.amazon.awssdk.core.exception.SdkException;

@Service
public class AuditService {

	private final RuleEngine ruleEngine;
	private final String awsRegion;
	private Summary lastSummary;

	public AuditService(RuleEngine ruleEngine, @Value("${aws.region:us-east-1}") String awsRegion) {
		this.ruleEngine = ruleEngine;
		this.awsRegion = awsRegion;
	}

	public List<Recommendation> getRecommendations() {
		try {
			LocalDate endDate = LocalDate.now();
			LocalDate startDate = endDate.minusDays(7);
			double cpuThreshold = 2.0;

			AuditContext context = new AuditContext(
				awsRegion,
				startDate,
				endDate,
				cpuThreshold
			);

			List<Recommendation> recommendations = ruleEngine.runAll(context);
			
			sortRecommendations(recommendations);
			computeSummary(recommendations);
			
			return recommendations;
		} catch (SdkException e) {
			return new ArrayList<>();
		}
	}

	private void sortRecommendations(List<Recommendation> recommendations) {
		recommendations.sort((r1, r2) -> {
			int confidenceComparison = Double.compare(r2.getConfidenceScore(), r1.getConfidenceScore());
			if (confidenceComparison != 0) {
				return confidenceComparison;
			}
			return Double.compare(r2.getEstimatedMonthlySavings(), r1.getEstimatedMonthlySavings());
		});
	}

	private Summary computeSummary(List<Recommendation> recommendations) {
		double totalSavings = recommendations.stream()
			.mapToDouble(Recommendation::getEstimatedMonthlySavings)
			.sum();
		
		Map<ResourceType, Integer> countByType = new HashMap<>();
		for (Recommendation rec : recommendations) {
			countByType.put(rec.getResourceType(), countByType.getOrDefault(rec.getResourceType(), 0) + 1);
		}
		
		this.lastSummary = new Summary(totalSavings, recommendations.size(), countByType);
		return this.lastSummary;
	}

	public Summary getLastSummary() {
		return lastSummary;
	}
}
