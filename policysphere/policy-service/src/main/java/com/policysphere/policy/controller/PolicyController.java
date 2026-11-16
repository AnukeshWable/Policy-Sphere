package com.policysphere.policy.controller;

import com.policysphere.policy.dto.request.PolicyRequestDTO;
import com.policysphere.policy.dto.response.PolicyResponseDTO;
import com.policysphere.policy.service.PolicyService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/policies")
@RequiredArgsConstructor
public class PolicyController {

    private final PolicyService policyService;

    @PostMapping
    public ResponseEntity<PolicyResponseDTO> create(@Valid @RequestBody PolicyRequestDTO request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(policyService.createPolicy(request));
    }

    @GetMapping("/{id}")
    public ResponseEntity<PolicyResponseDTO> getById(@PathVariable Long id) {
        return ResponseEntity.ok(policyService.getPolicyById(id));
    }

    @GetMapping
    public ResponseEntity<List<PolicyResponseDTO>> getAll() {
        return ResponseEntity.ok(policyService.getAllPolicies());
    }

    @GetMapping("/customer/{customerId}")
    public ResponseEntity<List<PolicyResponseDTO>> getByCustomer(@PathVariable Long customerId) {
        return ResponseEntity.ok(policyService.getPoliciesByCustomer(customerId));
    }

    @PatchMapping("/{id}/cancel")
    public ResponseEntity<PolicyResponseDTO> cancel(@PathVariable Long id) {
        return ResponseEntity.ok(policyService.cancelPolicy(id));
    }
}
