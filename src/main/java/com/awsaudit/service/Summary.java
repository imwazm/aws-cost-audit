package com.awsaudit.service;

import java.util.HashMap;
import java.util.Map;

import com.awsaudit.domain.ResourceType;

public class Summary {
	private final double totalEstimatedMonthlySavings;
	private final int recommendationCount;
	private final Map<ResourceType, Integer> countByResourceType;

	public Summary(double totalEstimatedMonthlySavings, int recommendationCount, Map<ResourceType, Integer> countByResourceType) {
		this.totalEstimatedMonthlySavings = totalEstimatedMonthlySavings;
		this.recommendationCount = recommendationCount;
		this.countByResourceType = new HashMap<>(countByResourceType);
	}

	public double getTotalEstimatedMonthlySavings() {
		return totalEstimatedMonthlySavings;
	}

	public int getRecommendationCount() {
		return recommendationCount;
	}

	public Map<ResourceType, Integer> getCountByResourceType() {
		return new HashMap<>(countByResourceType);
	}
}
