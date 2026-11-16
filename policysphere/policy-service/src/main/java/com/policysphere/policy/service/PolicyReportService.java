package com.policysphere.policy.service;

import com.policysphere.policy.dto.response.PolicyResponseDTO;
import com.policysphere.policy.dto.response.PolicySummaryDTO;
import com.policysphere.policy.enums.PolicyType;

import java.util.List;
import java.util.Map;

public interface PolicyReportService {

    PolicySummaryDTO getSummary();

    Map<PolicyType, Long> getPolicyCountByType();

    List<PolicyResponseDTO> getActivePolicies();

    List<PolicyResponseDTO> getExpiringPolicies();
}
