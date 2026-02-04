package com.awsaudit.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import software.amazon.awssdk.services.costexplorer.CostExplorerClient;

@Configuration
public class AwsClientConfig {

	@Bean
	public CostExplorerClient costExplorerClient() {
		return CostExplorerClient.builder()
			.region(software.amazon.awssdk.regions.Region.US_EAST_1)
			.build();
	}
}
