package com.example.tickettriage.analyzer;

import com.example.tickettriage.model.TicketCategory;
import com.example.tickettriage.model.TicketPriority;

import java.util.List;

public class TicketAnalysisResult {
    private TicketCategory category;
    private TicketPriority priority;
    private List<String> keywords;
    private List<String> urgencySignals;
    private List<String> signals;
    private double confidence;

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
}

