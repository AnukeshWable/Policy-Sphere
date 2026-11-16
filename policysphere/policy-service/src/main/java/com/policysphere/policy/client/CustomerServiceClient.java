package com.policysphere.policy.client;

import com.policysphere.policy.exception.CustomerNotFoundException;
import com.policysphere.policy.exception.CustomerServiceException;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * Wraps the Feign client and translates Feign failures into our own exceptions,
 * so the service layer and GlobalExceptionHandler stay unchanged.
 */
@Component
@RequiredArgsConstructor
public class CustomerServiceClient {

    private final CustomerFeignClient customerFeignClient;

    public CustomerResponse getCustomer(Long customerId) {
        try {
            CustomerResponse customer = customerFeignClient.getCustomerById(customerId);
            if (customer == null) {
                throw new CustomerServiceException("Customer Service returned an empty response", null);
            }
            return customer;

        } catch (FeignException.NotFound ex) {
            throw new CustomerNotFoundException(customerId);

        } catch (FeignException ex) {
            // Covers: connection refused / timeout (RetryableException), 5xx, other 4xx,
            // and "no instance registered in Eureka" (load balancer answers 503)
            throw new CustomerServiceException("Customer Service is unavailable or the call failed", ex);
        }
    }
}
