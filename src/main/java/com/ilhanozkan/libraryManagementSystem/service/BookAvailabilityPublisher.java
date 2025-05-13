package com.ilhanozkan.libraryManagementSystem.service;

import com.ilhanozkan.libraryManagementSystem.model.dto.event.BookAvailabilityEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;

@Service
@Slf4j
public class BookAvailabilityPublisher {
    // Create a many-to-many sink that broadcasts to all subscribers
    private final Sinks.Many<BookAvailabilityEvent> sink;
    // Create a flux that subscribers can subscribe to
    private final Flux<BookAvailabilityEvent> flux;

    public BookAvailabilityPublisher() {
        // Create a sink that allows many subscribers and emits to all of them
        this.sink = Sinks.many().multicast().onBackpressureBuffer();
        this.flux = sink.asFlux().cache(100);
        log.info("BookAvailabilityPublisher initialized");
    }

    /**
     * Publishes a book availability event
     * @param event The event to publish
     */
    public void publishEvent(BookAvailabilityEvent event) {
        log.debug("Publishing book availability event: {}", event);
        Sinks.EmitResult result = sink.tryEmitNext(event);
        if (result.isFailure()) {
            log.error("Failed to emit event: {}", result);
            throw new RuntimeException("Failed to emit event: " + result);
        }
    }

    /**
     * Returns a flux that emits book availability events
     * @return Flux of book availability events
     */
    public Flux<BookAvailabilityEvent> getEventStream() {
        return flux;
    }
} 