package com.awsaudit.rules;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;

import com.awsaudit.context.AuditContext;
import com.awsaudit.domain.Recommendation;
import com.awsaudit.domain.ResourceType;

import software.amazon.awssdk.services.ec2.Ec2Client;
import software.amazon.awssdk.services.ec2.model.DescribeVolumesRequest;
import software.amazon.awssdk.services.ec2.model.DescribeVolumesResponse;
import software.amazon.awssdk.services.ec2.model.Volume;

@Component
public class UnattachedEbsRule implements CostRule {

	private final Ec2Client ec2Client;

	public UnattachedEbsRule(Ec2Client ec2Client) {
		this.ec2Client = ec2Client;
	}

	@Override
	public List<Recommendation> evaluate(AuditContext context) {
		List<Recommendation> recommendations = new ArrayList<>();

		String nextToken = null;
		do {
			DescribeVolumesRequest describeVolumesRequest = DescribeVolumesRequest.builder()
				.filters(builder -> builder
					.name("status")
					.values("available")
				)
				.nextToken(nextToken)
				.build();

			DescribeVolumesResponse describeVolumesResponse = ec2Client.describeVolumes(describeVolumesRequest);

			for (Volume volume : describeVolumesResponse.volumes()) {
				Recommendation recommendation = new Recommendation(
					volume.volumeId(),
					ResourceType.EBS,
					context.getRegion(),
					"EBS volume is unattached",
					5.0,
					0.9
				);
				recommendations.add(recommendation);
			}

			nextToken = describeVolumesResponse.nextToken();
		} while (nextToken != null);

		return recommendations;
	}
}
