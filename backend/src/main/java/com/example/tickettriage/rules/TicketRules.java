package com.example.tickettriage.rules;

import com.example.tickettriage.model.TicketCategory;

import java.util.List;
import java.util.Map;

/**
 * Config-like storage for keywords used by the rule-based analyzer.
 * Kept as plain Java maps so the logic stays explainable.
 */
public class TicketRules {

    private final Map<TicketCategory, List<String>> categoryKeywords;
    private final Map<String, Integer> urgencyTerms;

    public TicketRules(Map<TicketCategory, List<String>> categoryKeywords, Map<String, Integer> urgencyTerms) {
        this.categoryKeywords = categoryKeywords;
        this.urgencyTerms = urgencyTerms;
    }

    public Map<TicketCategory, List<String>> getCategoryKeywords() {
        return categoryKeywords;
    }

    public Map<String, Integer> getUrgencyTerms() {
        return urgencyTerms;
    }

    public int getBasePriorityForCategory(TicketCategory category) {
        return switch (category) {
            case BILLING -> 2;
            case TECHNICAL -> 2;
            case ACCOUNT -> 1;
            case FEATURE_REQUEST -> 0;
            case OTHER -> 0;
        };
    }
}

