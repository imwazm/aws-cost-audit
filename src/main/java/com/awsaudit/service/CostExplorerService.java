package com.awsaudit.service;

import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.costexplorer.CostExplorerClient;
import software.amazon.awssdk.services.costexplorer.model.DateInterval;
import software.amazon.awssdk.services.costexplorer.model.GetCostAndUsageRequest;
import software.amazon.awssdk.services.costexplorer.model.GetCostAndUsageResponse;
import software.amazon.awssdk.services.costexplorer.model.Granularity;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Service
public class CostExplorerService {

	private final CostExplorerClient client;

	public CostExplorerService(CostExplorerClient client) {
		this.client = client;
	}

	public GetCostAndUsageResponse fetchLast30DaysCosts() {
		LocalDate endDate = LocalDate.now();
		LocalDate startDate = endDate.minusDays(30);
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

		GetCostAndUsageRequest request = GetCostAndUsageRequest.builder()
			.timePeriod(DateInterval.builder()
				.start(startDate.format(formatter))
				.end(endDate.format(formatter))
				.build())
			.granularity(Granularity.DAILY)
			.metrics("UnblendedCost")
			.build();

		return client.getCostAndUsage(request);
	}
}
