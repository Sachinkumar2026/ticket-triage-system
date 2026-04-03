package com.example.tickettriage.dto;

import jakarta.validation.constraints.NotBlank;

public class TicketAnalyzeRequest {

    @NotBlank(message = "message must not be empty")
    private String message;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}

