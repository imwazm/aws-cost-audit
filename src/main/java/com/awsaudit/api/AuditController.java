package com.awsaudit.api;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.awsaudit.domain.Recommendation;
import com.awsaudit.service.AuditService;

@RestController
@RequestMapping("/audit")
public class AuditController {

	private final AuditService auditService;

	public AuditController(AuditService auditService) {
		this.auditService = auditService;
	}

	@GetMapping("/recommendations")
	public List<Recommendation> getRecommendations() {
		return auditService.getRecommendations();
	}
}
