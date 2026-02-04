# Future Improvements

This document outlines features and enhancements under consideration. **These are not currently implemented.** This document exists to communicate roadmap direction and is updated as priorities evolve.

## Planned Features

### Multi-Region Support
- **Status**: Not implemented
- **Description**: Currently hardcoded to single region. Extend to audit across multiple AWS regions in parallel.
- **Effort**: Medium
- **Benefits**: Comprehensive cost analysis across global infrastructure
- **Implementation approach**: Multi-threading or async execution across regions

### Configurable Thresholds
- **Status**: Not implemented
- **Description**: Currently hardcoded values (e.g., 2% CPU threshold, 7-day lookback). Expose as configuration.
- **Effort**: Low
- **Benefits**: Flexibility for different organizational standards
- **Implementation approach**: Move constants to AuditContext or external configuration

### Filtering and Sorting
- **Status**: Not implemented
- **Description**: API query parameters for filtering (by resource type, region, savings threshold) and custom sorting.
- **Effort**: Low
- **Benefits**: Better UX for large result sets
- **Implementation approach**: Add QueryParameters to controllers; filter/sort in AuditService

### Database Persistence
- **Status**: Not implemented
- **Description**: Store recommendations in database for historical tracking and trend analysis.
- **Effort**: Medium
- **Benefits**: Audit history; ability to track cost reduction over time
- **Implementation approach**: Add Spring Data JPA; create Recommendation entity

### Scheduled Audits
- **Status**: Not implemented
- **Description**: Run audits on a schedule (hourly, daily) and store results for comparison.
- **Effort**: Medium
- **Benefits**: Continuous monitoring; alerting on new findings
- **Implementation approach**: Add Spring Scheduler; create audit job with persistence

### Alert Integration
- **Status**: Not implemented
- **Description**: Send alerts (email, SNS, Slack) when high-impact recommendations are discovered.
- **Effort**: Medium
- **Benefits**: Proactive cost management; team awareness
- **Implementation approach**: EventListener pattern; SNS or email integration

### Auto-Remediation (Optional)
- **Status**: Not implemented
- **Description**: Provide safe, user-approved auto-remediation (stop idle instances, delete unused volumes).
- **Effort**: High
- **Benefits**: Automated cost reduction
- **Risk**: Requires extensive testing and safeguards; not recommended for early versions
- **Implementation approach**: If pursued, separate remediation module with explicit approval workflow

### Advanced Rules
- **Status**: Not implemented
- **Description**: Additional cost detection patterns:
  - RDS idle databases (low connections/queries)
  - Unused security groups
  - Unused NAT gateways
  - Oversized instances (memory/CPU underutilization)
  - Old snapshots or AMIs
  - Unused Load Balancer listeners
  - Long-running Batch jobs
  - Reserved Instance utilization
- **Effort**: Low-to-Medium per rule
- **Benefits**: More comprehensive cost analysis

### DTO Mapping
- **Status**: Not implemented
- **Description**: Separate API DTOs from domain models for response shape flexibility.
- **Effort**: Low
- **Benefits**: API evolution independence; field-level exposure control
- **Implementation approach**: Add mapstruct or manual mapper; create response DTOs

### API Versioning
- **Status**: Not implemented
- **Description**: Support multiple API versions (e.g., /v1, /v2) for backward compatibility.
- **Effort**: Low
- **Benefits**: Safe API evolution; backward compatibility
- **Implementation approach**: Path-based versioning with multiple controller mappings

### Pagination for Large Result Sets
- **Status**: Not implemented
- **Description**: Implement cursor or offset-based pagination for API responses.
- **Effort**: Low
- **Benefits**: Performance; manageable responses
- **Implementation approach**: Add page/limit query parameters; return metadata

### OpenAPI/Swagger Documentation
- **Status**: Not implemented
- **Description**: Auto-generated API documentation with Springdoc-OpenAPI.
- **Effort**: Low
- **Benefits**: Better API discoverability; interactive documentation
- **Implementation approach**: Add springdoc-openapi dependency; configure annotations

### Frontend Dashboard
- **Status**: Not implemented
- **Description**: Web UI for viewing recommendations, historical trends, and drilling into details.
- **Effort**: High
- **Benefits**: Better visibility; non-technical user access
- **Technology**: React, Vue, or Angular with REST API client

### Reporting
- **Status**: Not implemented
- **Description**: Generate PDF/HTML reports summarizing findings and potential savings.
- **Effort**: Medium
- **Benefits**: Executive summaries; easy sharing
- **Implementation approach**: Thymeleaf or iText for templating/generation

### Metrics and Observability
- **Status**: Not implemented
- **Description**: Expose Micrometer metrics; structured logging; distributed tracing.
- **Effort**: Low
- **Benefits**: Operational visibility; debugging support
- **Implementation approach**: Add actuator; configure logging framework; optional Jaeger/Zipkin

### Performance Optimization
- **Status**: Not implemented
- **Description**: Cache AWS API responses; parallelize rule execution; optimize CloudWatch queries.
- **Effort**: Medium
- **Benefits**: Faster audit cycles; reduced AWS API costs
- **Implementation approach**: Redis caching; parallel streams; query batching

### Webhook Integration
- **Status**: Not implemented
- **Description**: Send notifications to external systems (Jira, ServiceNow, custom webhooks).
- **Effort**: Low-to-Medium
- **Benefits**: Integration with existing tools; workflow automation
- **Implementation approach**: HttpClient for outbound calls; configurable endpoints

### Configuration Management
- **Status**: Not implemented
- **Description**: Externalize configuration (Spring Cloud Config, AWS Parameter Store).
- **Effort**: Low
- **Benefits**: Easier deployment; environment-specific settings
- **Implementation approach**: Spring Cloud Config or AWS Systems Manager Parameter Store integration

### Testing Framework Integration
- **Status**: Not implemented
- **Description**: Add JUnit 5, Mockito, and integration tests with TestContainers.
- **Effort**: Low
- **Benefits**: Code confidence; regression prevention
- **Implementation approach**: Standard Spring Boot testing patterns

## Technical Debt & Refactoring

### Remove Unused Imports
- Clean up unused imports across codebase

### Suppress Unchecked Warnings
- Address `@SuppressWarnings("unchecked")` warnings in IdleEc2Rule

### Centralize Constants
- Extract hardcoded values (thresholds, periods) to a constants file

### Improve Error Messages
- More descriptive error responses to API clients

### Documentation
- Javadoc for public classes and methods
- Architecture decision records (ADRs)

## Investigation Areas

### Cost Estimation Accuracy
- Current estimatedMonthlySavings values are placeholders
- Research realistic calculation models based on AWS pricing
- Consider resource size, instance family, region

### Performance at Scale
- How does rule execution perform with 10k+ resources?
- CloudWatch query optimization for large fleets
- RuleEngine parallelization strategy

### High Availability
- Multi-instance deployment patterns
- Session state management
- API rate limiting

## Non-Priorities (Explicitly Out of Scope)

- Frontend included in this repository (separate frontend application recommended)
- Auto-remediation without explicit approval
- Real-time streaming of findings
- Machine learning for cost prediction
- Multi-account management (separate audit per account recommended)
- Database-driven rule configuration (configuration as code preferred)

## Contributing Ideas

Community contributions are welcome. When proposing new features:

1. Clearly separate from currently implemented functionality
2. Consider the "safe and read-only" design principle
3. Ensure AWS API patterns align with existing rules
4. Propose testing strategy
5. Update this document if feature becomes priority

See [CONTRIBUTING.md](CONTRIBUTING.md) for details on submitting improvements.
