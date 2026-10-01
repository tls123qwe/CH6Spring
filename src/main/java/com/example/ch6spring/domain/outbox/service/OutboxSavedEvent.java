package com.example.ch6spring.domain.outbox.service;

public record OutboxSavedEvent(
        Long outboxEventId
) { }