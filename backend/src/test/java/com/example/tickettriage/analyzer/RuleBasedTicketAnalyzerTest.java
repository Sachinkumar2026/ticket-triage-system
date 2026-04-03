package com.example.tickettriage.analyzer;

import com.example.tickettriage.model.TicketCategory;
import com.example.tickettriage.model.TicketPriority;
import com.example.tickettriage.rules.TicketRules;
import com.example.tickettriage.rules.TicketRulesConfig;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class RuleBasedTicketAnalyzerTest {

    private final RuleBasedTicketAnalyzer analyzer;

    public RuleBasedTicketAnalyzerTest() {
        TicketRulesConfig config = new TicketRulesConfig();
        TicketRules rules = config.ticketRules();
        this.analyzer = new RuleBasedTicketAnalyzer(rules);
    }

    @Test
    public void refundRuleForcesBillingAndP0() {
        String message = "Please refund my payment. Charged twice. Urgent!";
        TicketAnalysisResult result = analyzer.analyze(message);

        assertEquals(TicketCategory.BILLING, result.getCategory());
        assertEquals(TicketPriority.P0, result.getPriority());
        assertTrue(result.getSignals().contains("custom-rule: 'refund' forces Billing + P0"));
    }

    @Test
    public void urgencyDownAndUrgentGivesHighPriority() {
        String message = "Server is down and urgent. We see an error and timeout.";
        TicketAnalysisResult result = analyzer.analyze(message);

        assertEquals(TicketCategory.TECHNICAL, result.getCategory());
        assertEquals(TicketPriority.P0, result.getPriority());
        assertTrue(result.getUrgencySignals().contains("urgent"));
        assertTrue(result.getUrgencySignals().contains("down"));
    }

    @Test
    public void featureRequestWithoutUrgencyIsLowPriority() {
        String message = "Feature request: Please add export to CSV. Would like an improvement.";
        TicketAnalysisResult result = analyzer.analyze(message);

        assertEquals(TicketCategory.FEATURE_REQUEST, result.getCategory());
        assertEquals(TicketPriority.P3, result.getPriority());
    }

    @Test
    public void confidenceGrowsWithMatchedSignals() {
        TicketAnalysisResult emptySignals = analyzer.analyze("hello");
        TicketAnalysisResult moreSignals = analyzer.analyze("urgent down server error");

        assertEquals(0, emptySignals.getSignals().size());
        assertTrue(moreSignals.getSignals().size() > emptySignals.getSignals().size());
        assertTrue(moreSignals.getConfidence() > emptySignals.getConfidence());
    }
}

