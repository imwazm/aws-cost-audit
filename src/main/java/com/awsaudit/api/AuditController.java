package com.awsaudit.api;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import software.amazon.awssdk.services.costexplorer.model.GetCostAndUsageResponse;

import com.awsaudit.service.CostExplorerService;

@RestController
@RequestMapping("/audit")
public class AuditController {

	private final CostExplorerService costExplorerService;

	public AuditController(CostExplorerService costExplorerService) {
		this.costExplorerService = costExplorerService;
	}

	@GetMapping("/cost-summary")
	public GetCostAndUsageResponse getCostSummary() {
		return costExplorerService.fetchLast30DaysCosts();
	}
}
