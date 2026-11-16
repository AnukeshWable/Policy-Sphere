package com.policysphere.policy.dto.response;

import com.policysphere.policy.enums.PolicyStatus;
import com.policysphere.policy.enums.PolicyType;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Builder
public class PolicyResponseDTO {

    private Long id;
    private String policyNumber;
    private Long customerId;
    private PolicyType policyType;
    private BigDecimal premiumAmount;
    private LocalDate startDate;
    private LocalDate endDate;
    private PolicyStatus status;
    private LocalDateTime createdAt;
}
