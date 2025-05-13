package com.ilhanozkan.libraryManagementSystem.service;

import com.ilhanozkan.libraryManagementSystem.model.dto.event.BookAvailabilityEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.time.Duration;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class BookAvailabilityPublisherTest {

    private BookAvailabilityPublisher publisher;
    private BookAvailabilityEvent event1;
    private BookAvailabilityEvent event2;

    @BeforeEach
    void setUp() {
        publisher = new BookAvailabilityPublisher();
        event1 = new BookAvailabilityEvent(UUID.randomUUID(), "Book 1", "9780061120084", 3, 2, System.currentTimeMillis());
        event2 = new BookAvailabilityEvent(UUID.randomUUID(), "Book 2", "9780451524935", 8, 8, System.currentTimeMillis() );
    }

    @Test
    void shouldPublishAndReceiveEvents() {
        // Subscribe to the event stream
        Flux<BookAvailabilityEvent> eventFlux = publisher.getEventStream();
        
        // Create a StepVerifier to verify the events
        StepVerifier.create(eventFlux.take(2))
            .then(() -> publisher.publishEvent(event1))
            .then(() -> publisher.publishEvent(event2))
            .expectNext(event1)
            .expectNext(event2)
            .verifyComplete();
    }

    @Test
    void shouldAllowMultipleSubscribers() {
        // Create two subscribers
        AtomicInteger subscriber1Count = new AtomicInteger(0);
        AtomicInteger subscriber2Count = new AtomicInteger(0);
        
        // First subscriber
        publisher.getEventStream().take(2).subscribe(event -> {
            subscriber1Count.incrementAndGet();
        });
        
        // Second subscriber
        publisher.getEventStream().take(2).subscribe(event -> {
            subscriber2Count.incrementAndGet();
        });
        
        // Publish events
        publisher.publishEvent(event1);
        publisher.publishEvent(event2);
        
        // Wait a bit for the events to be processed
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        // Both subscribers should receive both events
        assertThat(subscriber1Count.get()).isEqualTo(2);
        assertThat(subscriber2Count.get()).isEqualTo(2);
    }

    @Test
    void shouldHandleBackPressure() {
        // Create a slow consumer
        StepVerifier.withVirtualTime(() -> publisher.getEventStream()
                .delayElements(Duration.ofMillis(500))
                .take(3))
            .then(() -> {
                // Rapidly publish multiple events
                publisher.publishEvent(event1);
                publisher.publishEvent(event2);
                publisher.publishEvent(new BookAvailabilityEvent(UUID.randomUUID(), "Book 3", "9780061120084", 12, 10, System.currentTimeMillis()));
                publisher.publishEvent(new BookAvailabilityEvent(UUID.randomUUID(), "Book 4", "9780451524935", 18, 15, System.currentTimeMillis()));
            })
            // Advance the virtual time to process delayed elements
            .thenAwait(Duration.ofSeconds(2))
            // We should still receive all 3 events that we take()
            .expectNextCount(3)
            .verifyComplete();
    }

    @Test
    void shouldCacheEvents() {
        // Publish an event before having any subscribers
        publisher.publishEvent(event1);
        
        // Subscribe after the event was published
        StepVerifier.create(publisher.getEventStream().take(1))
            // Should still receive the cached event
            .expectNext(event1)
            .verifyComplete();
    }
}