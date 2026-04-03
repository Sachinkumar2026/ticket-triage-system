package com.example.tickettriage.analyzer;

import com.example.tickettriage.model.TicketCategory;
import com.example.tickettriage.model.TicketPriority;
import com.example.tickettriage.rules.TicketRules;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

@Service
public class RuleBasedTicketAnalyzer implements TicketAnalyzer {

    private static final String CUSTOM_REFUND_KEYWORD = "refund";

    private final TicketRules rules;

    public RuleBasedTicketAnalyzer(TicketRules rules) {
        this.rules = rules;
    }

    @Override
    public TicketAnalysisResult analyze(String message) {
        String normalized = normalize(message);
        if (normalized.isBlank()) {
            throw new IllegalArgumentException("message must not be blank");
        }

        Map<TicketCategory, Integer> matchCounts = new EnumMap<>(TicketCategory.class);
        for (TicketCategory category : rules.getCategoryKeywords().keySet()) {
            matchCounts.put(category, 0);
        }

        Set<String> matchedKeywords = new LinkedHashSet<>();
        Set<String> urgencySignals = new LinkedHashSet<>();

        // 1) Keyword-based classification (category match count).
        for (Map.Entry<TicketCategory, List<String>> entry : rules.getCategoryKeywords().entrySet()) {
            TicketCategory category = entry.getKey();
            for (String keyword : entry.getValue()) {
                if (normalized.contains(keyword)) {
                    matchCounts.merge(category, 1, Integer::sum);
                    matchedKeywords.add(keyword);
                }
            }
        }

        // Pick the best category (ties are resolved using a fixed priority order).
        TicketCategory bestCategory = pickBestCategory(matchCounts);

        // 2) Urgency detection + urgency scoring.
        int urgencyScore = 0;
        for (Map.Entry<String, Integer> urgency : rules.getUrgencyTerms().entrySet()) {
            String urgencyKeyword = urgency.getKey();
            int weight = urgency.getValue();
            if (normalized.contains(urgencyKeyword)) {
                urgencySignals.add(urgencyKeyword);
                urgencyScore += weight;
            }
        }

        // 3) Priority scoring with one custom rule.
        TicketCategory finalCategory = bestCategory;
        TicketPriority finalPriority;

        // Custom rule (must be visible & explainable):
        // If message contains "refund" => category = Billing AND priority = P0
        boolean refundRuleMatched = normalized.contains(CUSTOM_REFUND_KEYWORD);
        if (refundRuleMatched) {
            finalCategory = TicketCategory.BILLING;
            finalPriority = TicketPriority.P0;
        } else {
            int score = urgencyScore + rules.getBasePriorityForCategory(bestCategory);
            finalPriority = scoreToPriority(score);
        }

        // 4) Confidence score: more matched signals => higher confidence.
        // Signals are unique matches from category keywords + urgency keywords (+ custom rule signal).
        Set<String> signals = new LinkedHashSet<>();
        signals.addAll(matchedKeywords);
        signals.addAll(urgencySignals);
        if (refundRuleMatched) {
            signals.add("custom-rule: 'refund' forces Billing + P0");
        }

        double confidence = computeConfidence(signals);

        TicketAnalysisResult result = new TicketAnalysisResult();
        result.setCategory(finalCategory);
        result.setPriority(finalPriority);
        result.setKeywords(new ArrayList<>(matchedKeywords));
        result.setUrgencySignals(new ArrayList<>(urgencySignals));
        result.setSignals(new ArrayList<>(signals));
        result.setConfidence(confidence);
        return result;
    }

    private static String normalize(String message) {
        if (message == null) {
            return "";
        }
        // Lowercase only; keep punctuation to stay transparent for explainability.
        return message.toLowerCase(Locale.ROOT);
    }

    private TicketCategory pickBestCategory(Map<TicketCategory, Integer> matchCounts) {
        TicketCategory[] order = new TicketCategory[]{
                TicketCategory.BILLING,
                TicketCategory.TECHNICAL,
                TicketCategory.ACCOUNT,
                TicketCategory.FEATURE_REQUEST,
                TicketCategory.OTHER
        };

        TicketCategory best = TicketCategory.OTHER;
        int bestCount = -1;
        for (TicketCategory category : order) {
            int count = matchCounts.getOrDefault(category, 0);
            if (count > bestCount) {
                best = category;
                bestCount = count;
            }
        }
        return bestCount <= 0 ? TicketCategory.OTHER : best;
    }

    private static TicketPriority scoreToPriority(int score) {
        if (score >= 10) {
            return TicketPriority.P0;
        }
        if (score >= 7) {
            return TicketPriority.P1;
        }
        if (score >= 4) {
            return TicketPriority.P2;
        }
        return TicketPriority.P3;
    }

    private static double computeConfidence(Set<String> signals) {
        // A simple, explainable function:
        // confidence rises as the number of matched signals increases.
        int uniqueSignals = new HashSet<>(signals).size();

        // 0 signals -> 0.1 base; 8+ signals -> ~0.99 cap
        double raw = 0.1 + 0.89 * (uniqueSignals / 8.0);
        return Math.min(0.99, Math.max(0.1, raw));
    }
}

