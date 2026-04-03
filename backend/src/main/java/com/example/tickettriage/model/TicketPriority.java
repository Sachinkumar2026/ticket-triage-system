package com.example.tickettriage.model;

public enum TicketPriority {
    P0("P0"),
    P1("P1"),
    P2("P2"),
    P3("P3");

    private final String label;

    TicketPriority(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}

