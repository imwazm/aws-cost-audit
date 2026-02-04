# Project Structure

## Directory Layout

```
aws-cost-audit/
├── src/
│   ├── main/
│   │   ├── java/com/awsaudit/
│   │   │   ├── api/                      # REST controllers
│   │   │   │   ├── AuditController.java
│   │   │   │   └── CostController.java
│   │   │   ├── config/                   # AWS SDK configuration
│   │   │   │   └── AwsClientConfig.java
│   │   │   ├── context/                  # Audit execution context
│   │   │   │   └── AuditContext.java
│   │   │   ├── domain/                   # Core domain models
│   │   │   │   ├── Recommendation.java
│   │   │   │   └── ResourceType.java
│   │   │   ├── engine/                   # Rule execution engine
│   │   │   │   └── RuleEngine.java
│   │   │   ├── rules/                    # Cost detection rules (CostRule implementations)
│   │   │   │   ├── CostRule.java         # Interface
│   │   │   │   ├── IdleEc2Rule.java
│   │   │   │   ├── UnattachedEbsRule.java
│   │   │   │   └── UnusedElasticIpRule.java
│   │   │   ├── service/                  # Business logic services
│   │   │   │   ├── AuditService.java
│   │   │   │   ├── CostExplorerService.java
│   │   │   │   └── Summary.java
│   │   │   └── AwsCostAuditApplication.java
│   │   └── resources/
│   │       └── application.properties
│   └── test/
│       └── java/com/awsaudit/
│           └── AwsCostAuditApplicationTests.java
├── pom.xml
├── mvnw / mvnw.cmd
├── README.md
├── CONTRIBUTING.md
├── PROJECT_STRUCTURE.md
└── FUTURE_IMPROVEMENTS.md
```

## Package Descriptions

### `api/`
**REST Controllers**

Handles HTTP requests and responses. Zero business logic.

- **AuditController**: Exposes `/audit` endpoints for cost recommendations
- **CostController**: Exposes `/cost` endpoint for raw AWS cost data

### `config/`
**AWS SDK Configuration**

Manages AWS client beans for dependency injection.

- **AwsClientConfig**: Creates and configures EC2Client, CloudWatchClient, CostExplorerClient

### `context/`
**Audit Execution Context**

Holds configuration and metadata for a single audit run.

- **AuditContext**: Region, lookback window, thresholds (passed to rules)

### `domain/`
**Core Domain Models**

Immutable, representation-focused classes.

- **Recommendation**: A cost optimization finding (id, resourceId, resourceType, region, reason, estimatedMonthlySavings, confidenceScore)
- **ResourceType**: Enum of AWS resource types (EC2, EBS, EIP)

### `engine/`
**Rule Execution Engine**

Orchestrates rule execution and aggregation.

- **RuleEngine**: Maintains list of CostRule beans; calls each rule.evaluate() and aggregates results

### `rules/`
**Cost Detection Rules**

Independent implementations of cost analysis logic.

- **CostRule**: Interface defining rule contract
- **IdleEc2Rule**: Detects low-CPU EC2 instances
- **UnattachedEbsRule**: Detects unattached EBS volumes
- **UnusedElasticIpRule**: Detects unassociated Elastic IPs

### `service/`
**Business Logic and Orchestration**

Coordinates rule execution, result processing, and data transformation.

- **AuditService**: Prepares AuditContext; calls RuleEngine; sorts recommendations; caches for CSV export
- **CostExplorerService**: Interfaces with AWS Cost Explorer API
- **Summary**: Internal model for recommendation aggregation metadata (count by resource type, total savings)

## Data Flow

### Request for Recommendations (JSON)
```
HTTP GET /audit/recommendations
    ↓
AuditController.getRecommendations()
    ↓
AuditService.getRecommendations()
    ├─ Create AuditContext (region, lookback dates, thresholds)
    ├─ Call RuleEngine.runAll(context)
    │   ├─ IdleEc2Rule.evaluate()
    │   ├─ UnattachedEbsRule.evaluate()
    │   └─ UnusedElasticIpRule.evaluate()
    │   → List<Recommendation>
    ├─ Sort by confidenceScore DESC, then estimatedMonthlySavings DESC
    ├─ Compute Summary (count by type, total savings)
    ├─ Cache in AuditService.lastRecommendations
    └─ Return sorted list
    ↓
Spring converts to JSON
    ↓
HTTP 200 with JSON array
```

### Request for CSV Export
```
HTTP GET /audit/recommendations.csv
    ↓
AuditController.getRecommendationsCSV()
    ↓
AuditService.getRecommendationsAsCSV()
    ├─ Check if lastRecommendations cached
    ├─ If empty, call getRecommendations() to populate cache
    ├─ Generate CSV string (header + rows)
    └─ Return CSV content
    ↓
Spring returns with Content-Type: text/csv
    ↓
HTTP 200 with CSV file
```

### Request for Cost Summary
```
HTTP GET /cost/summary
    ↓
CostController.getSummary()
    ↓
CostExplorerService.fetchLast30DaysCosts()
    ├─ Call AWS Cost Explorer API
    └─ Return GetCostAndUsageResponse
    ↓
Spring converts to JSON
    ↓
HTTP 200 with AWS response
```

## Dependency Injection Graph

```
AwsClientConfig
├─ Ec2Client (bean)
├─ CloudWatchClient (bean)
└─ CostExplorerClient (bean)

RuleEngine
├─ @Autowired List<CostRule>
│   ├─ IdleEc2Rule
│   │   └─ Ec2Client
│   │   └─ CloudWatchClient
│   ├─ UnattachedEbsRule
│   │   └─ Ec2Client
│   └─ UnusedElasticIpRule
│       └─ Ec2Client

AuditService
├─ RuleEngine
└─ @Value aws.region

CostExplorerService
└─ CostExplorerClient

AuditController
└─ AuditService

CostController
└─ CostExplorerService
```

## Configuration

### Application Properties
`src/main/resources/application.properties`

- `spring.application.name`: Application name
- `aws.region`: AWS region (default: us-east-1)

### Maven Configuration
`pom.xml`

- Parent: Spring Boot 3.5.10
- Java version: 17
- AWS SDK BOM: 2.25.46

## Extension Points

### Adding a New Rule
1. Create class in `com.awsaudit.rules` implementing `CostRule`
2. Annotate with `@Component` for automatic discovery
3. Inject AWS client via constructor
4. Implement `evaluate(AuditContext)` method
5. Add resource type to `ResourceType` enum if needed
6. RuleEngine automatically picks it up via List<CostRule> injection

### Adding a New Controller
1. Create class in `com.awsaudit.api` with `@RestController`
2. Inject required services via constructor
3. Map endpoints with `@GetMapping`, `@PostMapping`, etc.

### Modifying Context Data
Edit `AuditContext` class to add new fields (e.g., additional thresholds, date ranges)

## Testing Structure

Unit tests should mirror the source structure:
- `src/test/java/com/awsaudit/rules/` for rule tests
- `src/test/java/com/awsaudit/service/` for service tests
- `src/test/java/com/awsaudit/api/` for controller tests

## Build Artifacts

```
target/
├── classes/
│   └── com/awsaudit/...
├── generated-sources/
├── generated-test-sources/
└── maven-status/
```

## Dependencies

### Direct
- Spring Boot Web Starter
- AWS SDK v2 (ec2, cloudwatch, ce)

### Transitive
- Spring Framework
- Jackson (JSON serialization)
- SLF4J (logging facade)
- Other Spring Boot defaults

All AWS SDK dependencies managed via BOM (Bill of Materials) to ensure compatibility.
