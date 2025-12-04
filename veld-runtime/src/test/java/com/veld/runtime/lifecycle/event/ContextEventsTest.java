package com.veld.runtime.lifecycle.event;

import org.junit.jupiter.api.*;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for Context lifecycle events.
 */
@DisplayName("Context Events Tests")
class ContextEventsTest {
    
    @Nested
    @DisplayName("ContextStartedEvent Tests")
    class ContextStartedEventTests {
        
        @Test
        @DisplayName("Should create with source and lifecycle count")
        void shouldCreateWithSourceAndLifecycleCount() {
            Object source = new Object();
            
            ContextStartedEvent event = new ContextStartedEvent(source, 5);
            
            assertSame(source, event.getSource());
            assertEquals(5, event.getLifecycleBeanCount());
        }
        
        @Test
        @DisplayName("Should have timestamp")
        void shouldHaveTimestamp() {
            ContextStartedEvent event = new ContextStartedEvent(this, 0);
            
            assertNotNull(event.getTimestamp());
        }
        
        @Test
        @DisplayName("Should have meaningful toString")
        void shouldHaveMeaningfulToString() {
            ContextStartedEvent event = new ContextStartedEvent(this, 3);
            
            String toString = event.toString();
            
            assertTrue(toString.contains("ContextStartedEvent"));
            assertTrue(toString.contains("lifecycleBeanCount=3"));
        }
    }
    
    @Nested
    @DisplayName("ContextStoppedEvent Tests")
    class ContextStoppedEventTests {
        
        @Test
        @DisplayName("Should create with source and lifecycle count")
        void shouldCreateWithSourceAndLifecycleCount() {
            Object source = new Object();
            
            ContextStoppedEvent event = new ContextStoppedEvent(source, 10);
            
            assertSame(source, event.getSource());
            assertEquals(10, event.getLifecycleBeanCount());
        }
        
        @Test
        @DisplayName("Should have timestamp")
        void shouldHaveTimestamp() {
            ContextStoppedEvent event = new ContextStoppedEvent(this, 0);
            
            assertNotNull(event.getTimestamp());
        }
        
        @Test
        @DisplayName("Should have meaningful toString")
        void shouldHaveMeaningfulToString() {
            ContextStoppedEvent event = new ContextStoppedEvent(this, 7);
            
            String toString = event.toString();
            
            assertTrue(toString.contains("ContextStoppedEvent"));
            assertTrue(toString.contains("lifecycleBeanCount=7"));
        }
    }
    
    @Nested
    @DisplayName("ContextClosedEvent Tests")
    class ContextClosedEventTests {
        
        @Test
        @DisplayName("Should create with source, uptime and destroyed count")
        void shouldCreateWithSourceUptimeAndDestroyedCount() {
            Object source = new Object();
            Duration uptime = Duration.ofMinutes(30);
            
            ContextClosedEvent event = new ContextClosedEvent(source, uptime, 15);
            
            assertSame(source, event.getSource());
            assertEquals(uptime, event.getUptime());
            assertEquals(15, event.getDestroyedBeanCount());
        }
        
        @Test
        @DisplayName("Should have timestamp")
        void shouldHaveTimestamp() {
            ContextClosedEvent event = new ContextClosedEvent(this, Duration.ZERO, 0);
            
            assertNotNull(event.getTimestamp());
        }
        
        @Test
        @DisplayName("Should have meaningful toString")
        void shouldHaveMeaningfulToString() {
            ContextClosedEvent event = new ContextClosedEvent(this, Duration.ofHours(1), 20);
            
            String toString = event.toString();
            
            assertTrue(toString.contains("ContextClosedEvent"));
            assertTrue(toString.contains("destroyedBeanCount=20"));
            assertTrue(toString.contains("uptime="));
        }
    }
}
