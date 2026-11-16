package com.policysphere.policy.client;

/** Only the fields Policy Service needs from Customer Service's response (extra JSON fields are ignored). */
public record CustomerResponse(Long id, String status) {

    public boolean isActive() {
        return "ACTIVE".equals(status);
    }
}
