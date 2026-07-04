package com.pia.ticketmanagement.model;



public enum SuspendedReason {

    PAYMENT_OVERDUE("Payment Overdue"),
    FRAUD_SUSPICION("Fraud Suspicion"),
    SECURITY_VERIFICATION("Security Verification Required"),
    POLICY_VIOLATION("Policy Violation"),
    TEMPORARY_SERVICE_HOLD("Temporary Service Hold"),
    OTHER("Other");

    private final String displayName;

    SuspendedReason(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}