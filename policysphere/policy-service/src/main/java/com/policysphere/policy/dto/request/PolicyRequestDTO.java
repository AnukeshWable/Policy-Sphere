package com.policysphere.policy.dto.request;

import com.policysphere.policy.enums.PolicyType;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
public class PolicyRequestDTO {

    @NotNull(message = "Customer ID is required")
    @Positive(message = "Customer ID must be positive")
    private Long customerId;

    @NotNull(message = "Policy type is required")
    private PolicyType policyType;

    @NotNull(message = "Premium amount is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "Premium amount must be greater than zero")
    @Digits(integer = 10, fraction = 2, message = "Premium must have at most 10 integer and 2 decimal digits")
    private BigDecimal premiumAmount;

    @NotNull(message = "Start date is required")
    @FutureOrPresent(message = "Start date cannot be in the past")
    private LocalDate startDate;

    @NotNull(message = "End date is required")
    private LocalDate endDate;
}
