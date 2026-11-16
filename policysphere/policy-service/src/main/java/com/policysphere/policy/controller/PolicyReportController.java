package com.policysphere.policy.controller;

import com.policysphere.policy.dto.response.PolicyResponseDTO;
import com.policysphere.policy.dto.response.PolicySummaryDTO;
import com.policysphere.policy.enums.PolicyType;
import com.policysphere.policy.service.PolicyReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/policies/reports")
@RequiredArgsConstructor
public class PolicyReportController {

    private final PolicyReportService reportService;

    @GetMapping("/summary")
    public ResponseEntity<PolicySummaryDTO> summary() {
        return ResponseEntity.ok(reportService.getSummary());
    }

    @GetMapping("/by-type")
    public ResponseEntity<Map<PolicyType, Long>> byType() {
        return ResponseEntity.ok(reportService.getPolicyCountByType());
    }

    @GetMapping("/active")
    public ResponseEntity<List<PolicyResponseDTO>> active() {
        return ResponseEntity.ok(reportService.getActivePolicies());
    }

    @GetMapping("/expiring")
    public ResponseEntity<List<PolicyResponseDTO>> expiring() {
        return ResponseEntity.ok(reportService.getExpiringPolicies());
    }
}
