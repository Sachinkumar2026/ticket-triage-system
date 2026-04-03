package com.example.tickettriage.model;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "tickets")
public class Ticket {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 2000)
    private String message;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TicketCategory category;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TicketPriority priority;

    @ElementCollection
    @CollectionTable(name = "ticket_keywords", joinColumns = @JoinColumn(name = "ticket_id"))
    @Column(name = "keyword", nullable = false)
    private List<String> keywords = new ArrayList<>();

    @ElementCollection
    @CollectionTable(name = "ticket_urgency_signals", joinColumns = @JoinColumn(name = "ticket_id"))
    @Column(name = "signal", nullable = false)
    private List<String> urgencySignals = new ArrayList<>();

    @ElementCollection
    @CollectionTable(name = "ticket_signals", joinColumns = @JoinColumn(name = "ticket_id"))
    @Column(name = "signal", nullable = false)
    private List<String> signals = new ArrayList<>();

    @Column(nullable = false)
    private double confidence;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    public Ticket() {
    }

    @PrePersist
    private void onPrePersist() {
        this.createdAt = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public TicketCategory getCategory() {
        return category;
    }

    public void setCategory(TicketCategory category) {
        this.category = category;
    }

    public TicketPriority getPriority() {
        return priority;
    }

    public void setPriority(TicketPriority priority) {
        this.priority = priority;
    }

    public List<String> getKeywords() {
        return keywords;
    }

    public void setKeywords(List<String> keywords) {
        this.keywords = keywords;
    }

    public List<String> getUrgencySignals() {
        return urgencySignals;
    }

    public void setUrgencySignals(List<String> urgencySignals) {
        this.urgencySignals = urgencySignals;
    }

    public List<String> getSignals() {
        return signals;
    }

    public void setSignals(List<String> signals) {
        this.signals = signals;
    }

    public double getConfidence() {
        return confidence;
    }

    public void setConfidence(double confidence) {
        this.confidence = confidence;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}

