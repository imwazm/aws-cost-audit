package com.awsaudit.rules;

import java.time.Instant;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;

import com.awsaudit.context.AuditContext;
import com.awsaudit.domain.Recommendation;
import com.awsaudit.domain.ResourceType;

import software.amazon.awssdk.services.cloudwatch.CloudWatchClient;
import software.amazon.awssdk.services.cloudwatch.model.GetMetricStatisticsRequest;
import software.amazon.awssdk.services.cloudwatch.model.GetMetricStatisticsResponse;
import software.amazon.awssdk.services.cloudwatch.model.Statistic;
import software.amazon.awssdk.services.ec2.Ec2Client;
import software.amazon.awssdk.services.ec2.model.DescribeInstancesRequest;
import software.amazon.awssdk.services.ec2.model.DescribeInstancesResponse;
import software.amazon.awssdk.services.ec2.model.Instance;
import software.amazon.awssdk.services.ec2.model.Reservation;

@Component
public class IdleEc2Rule implements CostRule {

	private final Ec2Client ec2Client;
	private final CloudWatchClient cloudWatchClient;

	public IdleEc2Rule(Ec2Client ec2Client, CloudWatchClient cloudWatchClient) {
		this.ec2Client = ec2Client;
		this.cloudWatchClient = cloudWatchClient;
	}

	@Override
	public List<Recommendation> evaluate(AuditContext context) {
		List<Recommendation> recommendations = new ArrayList<>();

		String nextToken = null;
		do {
			DescribeInstancesRequest describeRequest = DescribeInstancesRequest.builder()
				.filters(builder -> builder
					.name("instance-state-name")
					.values("running")
				)
				.nextToken(nextToken)
				.build();

			DescribeInstancesResponse describeResponse = ec2Client.describeInstances(describeRequest);

			for (Reservation reservation : describeResponse.reservations()) {
				for (Instance instance : reservation.instances()) {
					double averageCpu = getAverageCpuUtilization(
						instance.instanceId(),
						context.getLookbackStartDate(),
						context.getLookbackEndDate()
					);

					if (averageCpu < context.getCpuThreshold()) {
						Recommendation recommendation = new Recommendation(
							instance.instanceId(),
							ResourceType.EC2,
							context.getRegion(),
							"Instance has low CPU utilization (" + String.format("%.2f", averageCpu) + "%) over the past 7 days",
							10.0,
							0.95
						);
						recommendations.add(recommendation);
					}
				}
			}

			nextToken = describeResponse.nextToken();
		} while (nextToken != null);

		return recommendations;
	}

	private double getAverageCpuUtilization(String instanceId, java.time.LocalDate startDate, java.time.LocalDate endDate) {
		Instant startInstant = startDate.atStartOfDay(ZoneOffset.UTC).toInstant();
		Instant endInstant = endDate.atStartOfDay(ZoneOffset.UTC).toInstant();

		GetMetricStatisticsRequest metricsRequest = GetMetricStatisticsRequest.builder()
			.namespace("AWS/EC2")
			.metricName("CPUUtilization")
			.dimensions(builder -> builder
				.name("InstanceId")
				.value(instanceId)
			)
			.statistics(Statistic.AVERAGE)
			.startTime(startInstant)
			.endTime(endInstant)
			.period(3600)
			.build();

		GetMetricStatisticsResponse metricsResponse = cloudWatchClient.getMetricStatistics(metricsRequest);

		if (metricsResponse.datapoints().isEmpty()) {
			return 0.0;
		}

		double sum = metricsResponse.datapoints().stream()
			.mapToDouble(datapoint -> datapoint.average())
			.sum();

		return sum / metricsResponse.datapoints().size();
	}
}
