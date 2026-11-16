package com.policysphere.policy.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
@Builder
public class PolicySummaryDTO {

    private long totalPolicies;
    private long activePolicies;
    private long cancelledPolicies;
    private BigDecimal totalActivePremium;
    private BigDecimal averageActivePremium;
}
