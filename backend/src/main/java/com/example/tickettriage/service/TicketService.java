package com.example.tickettriage.service;

import com.example.tickettriage.analyzer.TicketAnalysisResult;
import com.example.tickettriage.analyzer.TicketAnalyzer;
import com.example.tickettriage.dto.TicketDto;
import com.example.tickettriage.model.Ticket;
import com.example.tickettriage.repository.TicketRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

@Service
public class TicketService {

    private final TicketRepository ticketRepository;
    private final TicketAnalyzer ticketAnalyzer;

    public TicketService(TicketRepository ticketRepository, TicketAnalyzer ticketAnalyzer) {
        this.ticketRepository = ticketRepository;
        this.ticketAnalyzer = ticketAnalyzer;
    }

    public TicketDto analyzeAndSave(String message) {
        TicketAnalysisResult analysis = ticketAnalyzer.analyze(message);

        Ticket ticket = new Ticket();
        ticket.setMessage(message);
        ticket.setCategory(analysis.getCategory());
        ticket.setPriority(analysis.getPriority());
        ticket.setKeywords(analysis.getKeywords());
        ticket.setUrgencySignals(analysis.getUrgencySignals());
        ticket.setSignals(analysis.getSignals());
        ticket.setConfidence(analysis.getConfidence());

        Ticket saved = ticketRepository.save(ticket);
        return toDto(saved);
    }

    public List<TicketDto> listTickets() {
        List<Ticket> tickets = ticketRepository.findAllByOrderByCreatedAtDesc();
        List<TicketDto> result = new ArrayList<>();
        for (Ticket ticket : tickets) {
            result.add(toDto(ticket));
        }
        return result;
    }

    private static TicketDto toDto(Ticket ticket) {
        TicketDto dto = new TicketDto();
        dto.setId(ticket.getId());
        dto.setMessage(ticket.getMessage());
        dto.setCategory(ticket.getCategory().getLabel());
        dto.setPriority(ticket.getPriority().getLabel());
        dto.setKeywords(ticket.getKeywords());
        dto.setUrgencySignals(ticket.getUrgencySignals());

        // If signals were not stored for some reason, fall back to a union of keywords + urgency.
        Set<String> signals = new LinkedHashSet<>();
        if (ticket.getSignals() != null) {
            signals.addAll(ticket.getSignals());
        }
        if (ticket.getKeywords() != null) {
            signals.addAll(ticket.getKeywords());
        }
        if (ticket.getUrgencySignals() != null) {
            signals.addAll(ticket.getUrgencySignals());
        }
        dto.setSignals(new ArrayList<>(signals));

        dto.setConfidence(ticket.getConfidence());
        dto.setCreatedAt(ticket.getCreatedAt());
        return dto;
    }
}

