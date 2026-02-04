# Contributing to AWS Cost Audit Engine

Thank you for your interest in contributing. This document outlines guidelines for adding new cost detection rules and improvements.

## Adding a New Cost Rule

### 1. Create the Rule Class

Create a new file in `src/main/java/com/awsaudit/rules/` implementing the `CostRule` interface:

```java
package com.awsaudit.rules;

import org.springframework.stereotype.Component;
import com.awsaudit.context.AuditContext;
import com.awsaudit.domain.Recommendation;

@Component
public class MyNewRule implements CostRule {
    
    private final SomeAwsClient client;
    
    public MyNewRule(SomeAwsClient client) {
        this.client = client;
    }
    
    @Override
    public List<Recommendation> evaluate(AuditContext context) {
        // Implementation here
        return recommendations;
    }
}
```

### 2. Follow Implementation Guidelines

- **Constructor injection only**: No static fields or service locators
- **Read-only APIs**: Use only DescribeX operations, never modify AWS resources
- **Handle pagination**: Use nextToken loops where the AWS API supports pagination
- **Use UTC for time**: When working with CloudWatch or timestamps
- **Consistent recommendations**: Use existing ResourceType enum or extend it
- **Error handling**: Let AWS SDK exceptions propagate; AuditService catches them

### 3. Add Resource Type if Needed

Edit `src/main/java/com/awsaudit/domain/ResourceType.java`:

```java
public enum ResourceType {
    EC2,
    EBS,
    EIP,
    MY_NEW_RESOURCE_TYPE  // Add here
}
```

### 4. Test Locally

Ensure the rule compiles:
```bash
mvn clean compile
```

Verify it's picked up by the RuleEngine:
```bash
mvn spring-boot:run
curl http://localhost:8080/audit/recommendations
```

Your new rule should automatically be injected and executed.

## Code Guidelines

### Standards

- **Java 17+ syntax**: Use modern language features
- **No Lombok**: Keep code explicit for readability
- **No external CSV libraries**: Use StringBuilder for CSV generation
- **Spring dependency injection**: Use constructor injection exclusively
- **Immutable domain models**: Recommendation objects are immutable

### Naming Conventions

- Rules: `{DetectionPattern}Rule` (e.g., `UnusedElasticIpRule`)
- Classes: PascalCase
- Methods: camelCase
- Constants: UPPER_SNAKE_CASE

### Error Handling

- Prefer failing fast with clear messages
- Don't catch exceptions in rule code; let them bubble up for service-level handling
- Log errors only if logging framework is integrated

## Testing

### Unit Tests

Place tests in `src/test/java/com/awsaudit/` matching the source package structure.

Example test structure (when testing framework is added):
- `rules/IdleEc2RuleTest.java`
- `service/AuditServiceTest.java`

### Integration Tests

Run the application and manually test endpoints:
```bash
mvn spring-boot:run
curl http://localhost:8080/audit/recommendations
```

## Commit Guidelines

- Write clear, descriptive commit messages
- One logical change per commit
- Include issue reference if applicable

## Pull Request Process

1. Create a feature branch from `main`
2. Implement changes following guidelines above
3. Ensure `mvn clean compile` passes
4. Document your changes in comments if complex logic is involved
5. Request review from maintainers
6. Address feedback promptly

## Performance Considerations

- Avoid unnecessary AWS API calls; cache results appropriately
- Lookback windows should be configurable (currently hardcoded to 7 days)
- Consider pagination for large result sets

## Questions?

Review existing rule implementations:
- `src/main/java/com/awsaudit/rules/IdleEc2Rule.java`
- `src/main/java/com/awsaudit/rules/UnattachedEbsRule.java`
- `src/main/java/com/awsaudit/rules/UnusedElasticIpRule.java`

These serve as reference implementations for new rules.
