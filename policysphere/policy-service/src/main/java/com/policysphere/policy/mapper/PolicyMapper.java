package com.policysphere.policy.mapper;

import com.policysphere.policy.dto.request.PolicyRequestDTO;
import com.policysphere.policy.dto.response.PolicyResponseDTO;
import com.policysphere.policy.entity.Policy;
import com.policysphere.policy.enums.PolicyStatus;
import org.springframework.stereotype.Component;

@Component
public class PolicyMapper {

    /** Policy number is assigned by the service after the ID is known. */
    public Policy toEntity(PolicyRequestDTO dto, String temporaryPolicyNumber) {
        return Policy.builder()
                .policyNumber(temporaryPolicyNumber)
                .customerId(dto.getCustomerId())
                .policyType(dto.getPolicyType())
                .premiumAmount(dto.getPremiumAmount())
                .startDate(dto.getStartDate())
                .endDate(dto.getEndDate())
                .status(PolicyStatus.ACTIVE)
                .build();
    }

    public PolicyResponseDTO toResponse(Policy policy) {
        return PolicyResponseDTO.builder()
                .id(policy.getId())
                .policyNumber(policy.getPolicyNumber())
                .customerId(policy.getCustomerId())
                .policyType(policy.getPolicyType())
                .premiumAmount(policy.getPremiumAmount())
                .startDate(policy.getStartDate())
                .endDate(policy.getEndDate())
                .status(policy.getStatus())
                .createdAt(policy.getCreatedAt())
                .build();
    }
}
