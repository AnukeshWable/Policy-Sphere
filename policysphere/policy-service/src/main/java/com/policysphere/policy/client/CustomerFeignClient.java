package com.policysphere.policy.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * Declarative HTTP client. "customer-service" is the Eureka service name, so Feign
 * resolves a live instance through the load balancer, with no host or port anywhere.
 */
@FeignClient(name = "customer-service")
public interface CustomerFeignClient {

    @GetMapping("/api/v1/customers/{id}")
    CustomerResponse getCustomerById(@PathVariable("id") Long id);
}
