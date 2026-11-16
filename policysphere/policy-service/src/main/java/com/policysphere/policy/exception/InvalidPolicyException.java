package com.policysphere.policy.exception;

/** Business-rule violation (400): inactive customer, end date not after start date, etc. */
public class InvalidPolicyException extends RuntimeException {

    public InvalidPolicyException(String message) {
        super(message);
    }
}
