package com.awsaudit.context;

import java.time.LocalDate;

public class AuditContext {
	private final String region;
	private final LocalDate lookbackStartDate;
	private final LocalDate lookbackEndDate;
	private final double cpuThreshold;

	public AuditContext(
		String region,
		LocalDate lookbackStartDate,
		LocalDate lookbackEndDate,
		double cpuThreshold
	) {
		this.region = region;
		this.lookbackStartDate = lookbackStartDate;
		this.lookbackEndDate = lookbackEndDate;
		this.cpuThreshold = cpuThreshold;
	}

	public String getRegion() {
		return region;
	}

	public LocalDate getLookbackStartDate() {
		return lookbackStartDate;
	}

	public LocalDate getLookbackEndDate() {
		return lookbackEndDate;
	}

	public double getCpuThreshold() {
		return cpuThreshold;
	}
}
