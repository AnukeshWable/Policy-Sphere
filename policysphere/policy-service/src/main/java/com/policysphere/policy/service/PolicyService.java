package com.policysphere.policy.service;

import com.policysphere.policy.dto.request.PolicyRequestDTO;
import com.policysphere.policy.dto.response.PolicyResponseDTO;

import java.util.List;

public interface PolicyService {

    PolicyResponseDTO createPolicy(PolicyRequestDTO request);

    PolicyResponseDTO getPolicyById(Long id);

    List<PolicyResponseDTO> getAllPolicies();

    List<PolicyResponseDTO> getPoliciesByCustomer(Long customerId);

    PolicyResponseDTO cancelPolicy(Long id);
}
