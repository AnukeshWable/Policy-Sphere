package com.policysphere.policy.exception;

/** Invalid state transition (409): e.g. cancelling an already cancelled policy. */
public class PolicyStateException extends RuntimeException {

    public PolicyStateException(String message) {
        super(message);
    }
}
