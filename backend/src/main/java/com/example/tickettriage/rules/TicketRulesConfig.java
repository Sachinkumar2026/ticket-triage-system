package com.example.tickettriage.rules;

import com.example.tickettriage.model.TicketCategory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;
import java.util.EnumMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Configuration
public class TicketRulesConfig {

    @Bean
    public TicketRules ticketRules() {
        Map<TicketCategory, List<String>> categoryKeywords = new EnumMap<>(TicketCategory.class);

        categoryKeywords.put(TicketCategory.BILLING, Arrays.asList(
                "refund",
                "billing",
                "invoice",
                "payment",
                "charged",
                "charge",
                "receipt",
                "credit note",
                "amount due",
                "paid",
                "subscription"
        ));

        categoryKeywords.put(TicketCategory.TECHNICAL, Arrays.asList(
                "error",
                "bug",
                "issue",
                "crash",
                "exception",
                "timeout",
                "failed",
                "not working",
                "server down",
                "down",
                "cannot",
                "can't"
        ));

        categoryKeywords.put(TicketCategory.ACCOUNT, Arrays.asList(
                "account",
                "login",
                "sign in",
                "password",
                "reset",
                "verification",
                "2fa",
                "locked",
                "suspended",
                "email change"
        ));

        categoryKeywords.put(TicketCategory.FEATURE_REQUEST, Arrays.asList(
                "feature",
                "request",
                "add ",
                "would like",
                "improve",
                "enhancement",
                "roadmap"
        ));

        categoryKeywords.put(TicketCategory.OTHER, List.of());

        // Urgency detection uses simple keyword presence checks.
        // Weight feeds into priority scoring.
        Map<String, Integer> urgencyTerms = new LinkedHashMap<>();
        urgencyTerms.put("urgent", 6);
        urgencyTerms.put("asap", 5);
        urgencyTerms.put("immediately", 4);
        urgencyTerms.put("down", 5);
        urgencyTerms.put("critical", 6);
        urgencyTerms.put("as soon as possible", 4);

        return new TicketRules(categoryKeywords, urgencyTerms);
    }
}

