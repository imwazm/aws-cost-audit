package com.awsaudit.api;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.awsaudit.service.CostExplorerService;

import software.amazon.awssdk.core.exception.SdkException;
import software.amazon.awssdk.services.costexplorer.model.GetCostAndUsageResponse;

@RestController
@RequestMapping("/cost")
public class CostController {

	private final CostExplorerService costExplorerService;

	public CostController(CostExplorerService costExplorerService) {
		this.costExplorerService = costExplorerService;
	}

	@GetMapping("/summary")
	public ResponseEntity<?> getSummary() {
		try {
			return ResponseEntity.ok(costExplorerService.fetchLast30DaysCosts());
		} catch (SdkException e) {
			return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
				.body("{\"error\":\"AWS service unavailable\"}");
		}
	}
}
