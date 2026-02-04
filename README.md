# AWS Cost Audit Engine

A Spring Boot application that identifies cost optimization opportunities in AWS environments through pluggable, rule-based analysis. The engine evaluates infrastructure for waste patterns and surfaces ranked recommendations for cost reduction.

## Overview

AWS Cost Audit Engine analyzes AWS resources to detect unused or underutilized infrastructure. It applies multiple independent detection rules, ranks findings by confidence and estimated savings, and exports results in machine-readable formats.

The system is designed around safety principles: read-only AWS access, no auto-remediation, and transparent rule implementations. Each rule is isolated and can be developed, tested, and deployed independently.

## Features

- **Rule-based analysis**: Pluggable cost detection rules with consistent interface
- **Ranked recommendations**: Sorted by confidence score (descending) then estimated monthly savings (descending)
- **Multiple export formats**: JSON API responses and CSV downloads
- **Cost explorer integration**: Raw AWS Cost Explorer data access
- **Graceful degradation**: Empty results when AWS credentials are unavailable
- **Configurable region**: AWS region selection via `aws.region` property (default: us-east-1)
- **Constructor injection**: Clean, testable dependency model

## Implemented Cost Rules

| Rule | Detection | Data Source | Lookback |
|------|-----------|-------------|----------|
| Idle EC2 Instances | Running instances with CPU utilization < 2% | CloudWatch (hourly average) | 7 days |
| Unattached EBS Volumes | Volumes in "available" state | EC2 DescribeVolumes | Current state |
| Unused Elastic IPs | Allocated but unassociated Elastic IPs | EC2 DescribeAddresses | Current state |

## API Endpoints

### Get Audit Recommendations (JSON)
```
GET /audit/recommendations
```
Returns a sorted list of cost optimization recommendations as JSON.

**Response**: Array of Recommendation objects
- `id`: Unique identifier (UUID)
- `resourceId`: AWS resource identifier
- `resourceType`: Type of resource (EC2, EBS, EIP)
- `region`: AWS region
- `reason`: Human-readable explanation
- `estimatedMonthlySavings`: Dollar amount (double)
- `confidenceScore`: 0.0–1.0 indicating certainty

### Download Audit Recommendations (CSV)
```
GET /audit/recommendations.csv
```
Downloads recommendations as a CSV file with headers.

**Headers**: ID, Resource ID, Resource Type, Region, Reason, Estimated Monthly Savings, Confidence Score

### Get Cost Summary
```
GET /cost/summary
```
Returns raw AWS Cost Explorer data for the last 30 days.

**Response**: GetCostAndUsageResponse from AWS SDK v2

## Architecture

### High-Level Design

```
HTTP Request
    ↓
Controller (zero business logic)
    ↓
Service (orchestration & ranking)
    ↓
RuleEngine (rule aggregation)
    ↓
CostRule implementations (isolated analysis)
    ↓
AWS SDK v2 clients (EC2, CloudWatch)
```

### Key Components

- **RuleEngine**: Executes all registered CostRule implementations and aggregates results
- **AuditService**: Creates AuditContext, invokes RuleEngine, sorts recommendations, caches results for CSV export
- **AuditController**: Exposes `/audit` endpoints for recommendations
- **CostController**: Exposes `/cost` endpoint for raw cost data
- **CostRule interface**: Contract for rule implementations
- **Recommendation domain model**: Immutable representation of a cost finding

### Error Handling

AWS SDK runtime exceptions are caught and handled gracefully:
- **AuditService**: Returns empty recommendation list
- **CostController**: Returns HTTP 503 with error message

## Running Locally

### Prerequisites
- Java 17+
- Maven 3.8+
- AWS credentials configured (via environment variables, IAM role, or credential file)
- Read-only EC2, CloudWatch, and Cost Explorer permissions

### Build
```bash
mvn clean compile
```

### Run
```bash
mvn spring-boot:run
```

The application starts on port 8080 by default.

### Configuration

Set AWS region via environment variable:
```bash
export AWS_REGION=us-west-2
mvn spring-boot:run
```

Or in `src/main/resources/application.properties`:
```properties
aws.region=us-west-2
```

## Required AWS Permissions

Minimum IAM policy for read-only access:

```json
{
  "Version": "2012-10-17",
  "Statement": [
    {
      "Effect": "Allow",
      "Action": [
        "ec2:DescribeInstances",
        "ec2:DescribeVolumes",
        "ec2:DescribeAddresses"
      ],
      "Resource": "*"
    },
    {
      "Effect": "Allow",
      "Action": [
        "cloudwatch:GetMetricStatistics"
      ],
      "Resource": "*"
    },
    {
      "Effect": "Allow",
      "Action": [
        "ce:GetCostAndUsage"
      ],
      "Resource": "*"
    }
  ]
}
```

## Technology Stack

- **Language**: Java 17
- **Framework**: Spring Boot 3.5.10
- **Build**: Maven 3
- **AWS SDK**: v2.25.46 (BOM managed)
- **HTTP**: Spring Web (REST controllers)

## Design Principles

1. **Read-only access**: All AWS interactions are queries, never modifications
2. **No auto-remediation**: Recommendations are presented; users decide actions
3. **Pluggable rules**: New CostRule implementations can be added without touching existing code
4. **Zero controller logic**: Controllers delegate entirely to services
5. **Safe defaults**: Graceful handling when AWS access fails or is unavailable

## Testing

Run the test suite:
```bash
mvn test
```

## Future Enhancements

See [FUTURE_IMPROVEMENTS.md](FUTURE_IMPROVEMENTS.md) for planned features under consideration.

## License

Proprietary (adjust as needed)

## Authors

Developed as a modular cost analysis platform for AWS infrastructure assessment.
