package com.policysphere.policy.client;

import com.policysphere.policy.exception.CustomerNotFoundException;
import com.policysphere.policy.exception.CustomerServiceException;
import feign.FeignException;
import io.github.resilience4j.circuitbreaker.CallNotPermittedException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class CustomerFeignFallbackFactory implements FallbackFactory<CustomerFeignClient> {

    @Override
    public CustomerFeignClient create(Throwable cause) {
        return new CustomerFeignClient() {
            @Override
            public CustomerResponse getCustomerById(Long id) {
                if (cause instanceof FeignException.NotFound) {
                    throw new CustomerNotFoundException(id);          // 404 is a real answer
                }
                if (cause instanceof CallNotPermittedException) {
                    log.warn("Circuit OPEN: lookup for {} rejected without calling Customer Service", id);
                    throw new CustomerServiceException("Customer Service is temporarily unavailable (circuit open).", cause);
                }
                log.error("Customer lookup for id {} failed: {}", id, cause.toString());
                throw new CustomerServiceException("Customer Service is unavailable or the call failed", cause);
            }
        };
    }
}