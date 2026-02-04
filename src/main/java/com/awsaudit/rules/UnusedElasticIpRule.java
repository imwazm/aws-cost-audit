package com.awsaudit.rules;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;

import com.awsaudit.context.AuditContext;
import com.awsaudit.domain.Recommendation;
import com.awsaudit.domain.ResourceType;

import software.amazon.awssdk.services.ec2.Ec2Client;
import software.amazon.awssdk.services.ec2.model.Address;
import software.amazon.awssdk.services.ec2.model.DescribeAddressesRequest;
import software.amazon.awssdk.services.ec2.model.DescribeAddressesResponse;

@Component
public class UnusedElasticIpRule implements CostRule {

	private final Ec2Client ec2Client;

	public UnusedElasticIpRule(Ec2Client ec2Client) {
		this.ec2Client = ec2Client;
	}

	@Override
	public List<Recommendation> evaluate(AuditContext context) {
		List<Recommendation> recommendations = new ArrayList<>();

		DescribeAddressesRequest describeAddressesRequest = DescribeAddressesRequest.builder()
			.build();

		DescribeAddressesResponse describeAddressesResponse = ec2Client.describeAddresses(describeAddressesRequest);

		for (Address address : describeAddressesResponse.addresses()) {
			if (address.associationId() == null && address.networkInterfaceId() == null) {
				Recommendation recommendation = new Recommendation(
					address.publicIp(),
					ResourceType.EIP,
					context.getRegion(),
					"Elastic IP is allocated but not associated",
					3.6,
					0.95
				);
				recommendations.add(recommendation);
			}
		}

		return recommendations;
	}
}
