package com.example.tickettriage.model;

public enum TicketCategory {
    BILLING("Billing"),
    TECHNICAL("Technical"),
    ACCOUNT("Account"),
    FEATURE_REQUEST("Feature Request"),
    OTHER("Other");

    private final String label;

    TicketCategory(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}

