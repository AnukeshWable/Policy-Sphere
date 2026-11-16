package com.policysphere.policy.service.impl;

import com.policysphere.policy.client.CustomerResponse;
import com.policysphere.policy.client.CustomerServiceClient;
import com.policysphere.policy.dto.request.PolicyRequestDTO;
import com.policysphere.policy.dto.response.PolicyResponseDTO;
import com.policysphere.policy.entity.Policy;
import com.policysphere.policy.enums.PolicyStatus;
import com.policysphere.policy.exception.InvalidPolicyException;
import com.policysphere.policy.exception.PolicyNotFoundException;
import com.policysphere.policy.exception.PolicyStateException;
import com.policysphere.policy.mapper.PolicyMapper;
import com.policysphere.policy.repository.PolicyRepository;
import com.policysphere.policy.service.PolicyService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PolicyServiceImpl implements PolicyService {

    private final PolicyRepository policyRepository;
    private final PolicyMapper policyMapper;
    private final CustomerServiceClient customerServiceClient;
    private final TransactionTemplate transactionTemplate;

    /**
     * Deliberately NOT @Transactional: the remote call to Customer Service happens first,
     * outside any transaction, so a slow/down Customer Service never holds a DB connection.
     * Only the database write runs inside a transaction (via TransactionTemplate).
     */
    @Override
    public PolicyResponseDTO createPolicy(PolicyRequestDTO request) {
        validateDates(request);

        CustomerResponse customer =
                customerServiceClient.getCustomer(request.getCustomerId()); // 404 / 503 thrown here
        if (!customer.isActive()) {
            throw new InvalidPolicyException(
                    "Customer " + request.getCustomerId() + " is not ACTIVE; cannot create policy");
        }

        return transactionTemplate.execute(status -> {
            // Insert with a unique placeholder, then derive the real number from the generated ID
            Policy policy = policyRepository.saveAndFlush(
                    policyMapper.toEntity(request, "TMP-" + UUID.randomUUID()));
            policy.setPolicyNumber(String.format("POL-%d-%06d", LocalDate.now().getYear(), policy.getId()));
            return policyMapper.toResponse(policyRepository.save(policy));
        });
    }

    @Override
    @Transactional(readOnly = true)
    public PolicyResponseDTO getPolicyById(Long id) {
        return policyMapper.toResponse(findOrThrow(id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<PolicyResponseDTO> getAllPolicies() {
        return policyRepository.findAll().stream().map(policyMapper::toResponse).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<PolicyResponseDTO> getPoliciesByCustomer(Long customerId) {
        return policyRepository.findByCustomerId(customerId).stream()
                .map(policyMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional
    public PolicyResponseDTO cancelPolicy(Long id) {
        Policy policy = findOrThrow(id);
        if (policy.getStatus() == PolicyStatus.CANCELLED) {
            throw new PolicyStateException("Policy " + policy.getPolicyNumber() + " is already cancelled");
        }
        policy.setStatus(PolicyStatus.CANCELLED);
        return policyMapper.toResponse(policyRepository.save(policy));
    }

    private void validateDates(PolicyRequestDTO request) {
        if (!request.getEndDate().isAfter(request.getStartDate())) {
            throw new InvalidPolicyException("End date must be after start date");
        }
    }

    private Policy findOrThrow(Long id) {
        return policyRepository.findById(id).orElseThrow(() -> new PolicyNotFoundException(id));
    }
}
