package com.ilhanozkan.libraryManagementSystem.controller;

import com.ilhanozkan.libraryManagementSystem.model.dto.event.BookAvailabilityEvent;
import com.ilhanozkan.libraryManagementSystem.service.BookAvailabilityPublisher;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

import java.time.Duration;
import java.util.UUID;

@RestController
@RequestMapping("/reactive/books")
@RequiredArgsConstructor
@Tag(name = "Reactive Book API", description = "APIs for real-time book availability updates")
@Slf4j
public class ReactiveBookController {

    private final BookAvailabilityPublisher bookAvailabilityPublisher;

    @GetMapping(value = "/availability/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    @Operation(summary = "Stream book availability changes", 
               description = "Returns a stream of Server-Sent Events (SSE) for real-time book availability updates")
    public Flux<BookAvailabilityEvent> streamBookAvailability() {
        log.debug("Client connected to book availability stream");
        
        // Create an initial connection event
        BookAvailabilityEvent connectionEvent = new BookAvailabilityEvent(
            UUID.randomUUID(), 
            "CONNECTION_ESTABLISHED", 
            "000-0000000000", 
            0, 
            0, 
            System.currentTimeMillis()
        );
        
        // Create a heartbeat to keep the connection alive
        Flux<BookAvailabilityEvent> heartbeat = Flux.interval(Duration.ofSeconds(30))
            .map(tick -> new BookAvailabilityEvent(
                UUID.randomUUID(),
                "HEARTBEAT",
                "000-0000000000",
                0,
                0,
                System.currentTimeMillis()
            ));
        
        // Combine the initial event, heartbeat, and the actual events
        return Flux.just(connectionEvent)
            .concatWith(bookAvailabilityPublisher.getEventStream())
            .mergeWith(heartbeat);
    }
} 