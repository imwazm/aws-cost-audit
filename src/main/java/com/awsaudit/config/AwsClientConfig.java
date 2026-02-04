package com.awsaudit.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.cloudwatch.CloudWatchClient;
import software.amazon.awssdk.services.costexplorer.CostExplorerClient;
import software.amazon.awssdk.services.ec2.Ec2Client;

@Configuration
public class AwsClientConfig {

	@Bean
	public CostExplorerClient costExplorerClient() {
		return CostExplorerClient.builder()
			.region(Region.US_EAST_1)
			.build();
	}

	@Bean
	public Ec2Client ec2Client() {
		return Ec2Client.builder()
			.region(Region.US_EAST_1)
			.build();
	}

	@Bean
	public CloudWatchClient cloudWatchClient() {
		return CloudWatchClient.builder()
			.region(Region.US_EAST_1)
			.build();
	}
}
