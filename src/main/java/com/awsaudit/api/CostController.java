package com.awsaudit.api;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.awsaudit.service.CostExplorerService;

import software.amazon.awssdk.services.costexplorer.model.GetCostAndUsageResponse;

@RestController
@RequestMapping("/cost")
public class CostController {

	private final CostExplorerService costExplorerService;

	public CostController(CostExplorerService costExplorerService) {
		this.costExplorerService = costExplorerService;
	}

	@GetMapping("/summary")
	public GetCostAndUsageResponse getSummary() {
		return costExplorerService.fetchLast30DaysCosts();
	}
}
