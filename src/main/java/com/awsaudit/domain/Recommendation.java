package com.awsaudit.domain;

import java.util.UUID;

public class Recommendation {
	private final String id;
	private final String resourceId;
	private final ResourceType resourceType;
	private final String region;
	private final String reason;
	private final double estimatedMonthlySavings;
	private final double confidenceScore;

	public Recommendation(
		String resourceId,
		ResourceType resourceType,
		String region,
		String reason,
		double estimatedMonthlySavings,
		double confidenceScore
	) {
		this.id = UUID.randomUUID().toString();
		this.resourceId = resourceId;
		this.resourceType = resourceType;
		this.region = region;
		this.reason = reason;
		this.estimatedMonthlySavings = estimatedMonthlySavings;
		this.confidenceScore = confidenceScore;
	}

	public String getId() {
		return id;
	}

	public String getResourceId() {
		return resourceId;
	}

	public ResourceType getResourceType() {
		return resourceType;
	}

	public String getRegion() {
		return region;
	}

	public String getReason() {
		return reason;
	}

	public double getEstimatedMonthlySavings() {
		return estimatedMonthlySavings;
	}

	public double getConfidenceScore() {
		return confidenceScore;
	}
}
