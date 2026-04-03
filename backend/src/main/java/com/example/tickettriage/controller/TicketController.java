package com.example.tickettriage.controller;

import com.example.tickettriage.dto.TicketAnalyzeRequest;
import com.example.tickettriage.dto.TicketDto;
import com.example.tickettriage.service.TicketService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@CrossOrigin(origins = "*")
public class TicketController {

    private final TicketService ticketService;

    public TicketController(TicketService ticketService) {
        this.ticketService = ticketService;
    }

    @PostMapping("/tickets/analyze")
    public ResponseEntity<TicketDto> analyze(@Valid @RequestBody TicketAnalyzeRequest request) {
        String message = request.getMessage();
        TicketDto analysis = ticketService.analyzeAndSave(message);
        return ResponseEntity.ok(analysis);
    }

    @GetMapping("/tickets")
    public ResponseEntity<List<TicketDto>> listTickets() {
        return ResponseEntity.ok(ticketService.listTickets());
    }
}

