package com.awsaudit.rules;

import java.util.List;

import com.awsaudit.context.AuditContext;
import com.awsaudit.domain.Recommendation;

public interface CostRule {
	List<Recommendation> evaluate(AuditContext context);
}
