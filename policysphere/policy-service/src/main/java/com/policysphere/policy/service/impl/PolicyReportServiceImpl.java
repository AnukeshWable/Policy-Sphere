package com.policysphere.policy.service.impl;

import com.policysphere.policy.dto.response.PolicyResponseDTO;
import com.policysphere.policy.dto.response.PolicySummaryDTO;
import com.policysphere.policy.entity.Policy;
import com.policysphere.policy.enums.PolicyStatus;
import com.policysphere.policy.enums.PolicyType;
import com.policysphere.policy.mapper.PolicyMapper;
import com.policysphere.policy.repository.PolicyRepository;
import com.policysphere.policy.service.PolicyReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.EnumMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Reporting logic, kept separate from CRUD.
 *
 * NOTE (learning exercise): every report loads all policies with findAll() and computes in memory
 * with the Stream API. For 10,000 rows that is fine. At real scale you would push this into SQL
 * (GROUP BY / SUM / COUNT) so the database does the work.
 */
@Service
@RequiredArgsConstructor
public class PolicyReportServiceImpl implements PolicyReportService {

    private static final int EXPIRY_WINDOW_DAYS = 30;

    private final PolicyRepository policyRepository;
    private final PolicyMapper policyMapper;

    @Override
    @Transactional(readOnly = true)
    public PolicySummaryDTO getSummary() {
        List<Policy> policies = policyRepository.findAll();

        long total = policies.size();

        // filter() + count()
        long active = policies.stream()
                .filter(p -> p.getStatus() == PolicyStatus.ACTIVE)
                .count();

        long cancelled = policies.stream()
                .filter(p -> p.getStatus() == PolicyStatus.CANCELLED)
                .count();

        // filter() + map() + reduce(): BigDecimal, never double, for money
        BigDecimal totalActivePremium = policies.stream()
                .filter(p -> p.getStatus() == PolicyStatus.ACTIVE)
                .map(Policy::getPremiumAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // guard against division by zero when there are no active policies
        BigDecimal averageActivePremium = active == 0
                ? BigDecimal.ZERO.setScale(2)
                : totalActivePremium.divide(BigDecimal.valueOf(active), 2, RoundingMode.HALF_UP);

        return PolicySummaryDTO.builder()
                .totalPolicies(total)
                .activePolicies(active)
                .cancelledPolicies(cancelled)
                .totalActivePremium(totalActivePremium)
                .averageActivePremium(averageActivePremium)
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public Map<PolicyType, Long> getPolicyCountByType() {
        // groupingBy() + counting(); EnumMap gives a deterministic intermediate map
        Map<PolicyType, Long> countByType = policyRepository.findAll().stream()
                .collect(Collectors.groupingBy(
                        Policy::getPolicyType,
                        () -> new EnumMap<>(PolicyType.class),
                        Collectors.counting()));

        // sorted() on the entries (highest count first, ties by type name) -> LinkedHashMap keeps that order in JSON
        return countByType.entrySet().stream()
                .sorted(Map.Entry.<PolicyType, Long>comparingByValue(Comparator.reverseOrder())
                        .thenComparing(Map.Entry.comparingByKey()))
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (a, b) -> a,
                        LinkedHashMap::new));
    }

    @Override
    @Transactional(readOnly = true)
    public List<PolicyResponseDTO> getActivePolicies() {
        // filter() -> sorted() -> map() -> collect(); highest premium first
        return policyRepository.findAll().stream()
                .filter(p -> p.getStatus() == PolicyStatus.ACTIVE)
                .sorted(Comparator.comparing(Policy::getPremiumAmount).reversed())
                .map(policyMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<PolicyResponseDTO> getExpiringPolicies() {
        LocalDate today = LocalDate.now();
        LocalDate windowEnd = today.plusDays(EXPIRY_WINDOW_DAYS);

        // filter() with date operations (window is inclusive of both ends); soonest expiry first
        return policyRepository.findAll().stream()
                .filter(p -> p.getStatus() == PolicyStatus.ACTIVE)
                .filter(p -> !p.getEndDate().isBefore(today) && !p.getEndDate().isAfter(windowEnd))
                .sorted(Comparator.comparing(Policy::getEndDate))
                .map(policyMapper::toResponse)
                .collect(Collectors.toList());
    }
}
