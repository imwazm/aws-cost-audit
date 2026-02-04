package com.awsaudit.engine;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;

import com.awsaudit.context.AuditContext;
import com.awsaudit.domain.Recommendation;
import com.awsaudit.rules.CostRule;

@Component
public class RuleEngine {

	private final List<CostRule> rules;

	public RuleEngine(List<CostRule> rules) {
		this.rules = rules;
	}

	public List<Recommendation> runAll(AuditContext context) {
		List<Recommendation> aggregatedRecommendations = new ArrayList<>();

		for (CostRule rule : rules) {
			List<Recommendation> ruleResults = rule.evaluate(context);
			aggregatedRecommendations.addAll(ruleResults);
		}

		return aggregatedRecommendations;
	}
}
