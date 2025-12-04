package com.veld.runtime.event;

import org.junit.jupiter.api.*;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for Event base class.
 */
@DisplayName("Event Tests")
class EventTest {
    
    static class TestEvent extends Event {
        TestEvent(Object source) {
            super(source);
        }
    }
    
    @Nested
    @DisplayName("Construction Tests")
    class ConstructionTests {
        
        @Test
        @DisplayName("Should create event with source")
        void shouldCreateEventWithSource() {
            Object source = new Object();
            TestEvent event = new TestEvent(source);
            
            assertSame(source, event.getSource());
        }
        
        @Test
        @DisplayName("Should throw exception for null source")
        void shouldThrowExceptionForNullSource() {
            assertThrows(IllegalArgumentException.class, () -> 
                new TestEvent(null));
        }
        
        @Test
        @DisplayName("Should set timestamp on creation")
        void shouldSetTimestampOnCreation() {
            Instant before = Instant.now();
            TestEvent event = new TestEvent(this);
            Instant after = Instant.now();
            
            assertNotNull(event.getTimestamp());
            assertFalse(event.getTimestamp().isBefore(before));
            assertFalse(event.getTimestamp().isAfter(after));
        }
    }
    
    @Nested
    @DisplayName("Cancellation Tests")
    class CancellationTests {
        
        @Test
        @DisplayName("Should not be cancelled by default")
        void shouldNotBeCancelledByDefault() {
            TestEvent event = new TestEvent(this);
            
            assertFalse(event.isCancelled());
        }
        
        @Test
        @DisplayName("Should be cancellable")
        void shouldBeCancellable() {
            TestEvent event = new TestEvent(this);
            
            event.cancel();
            
            assertTrue(event.isCancelled());
        }
    }
    
    @Nested
    @DisplayName("Property Tests")
    class PropertyTests {
        
        @Test
        @DisplayName("Should set and get property")
        void shouldSetAndGetProperty() {
            TestEvent event = new TestEvent(this);
            
            event.setProperty("key", "value");
            
            assertEquals("value", event.getProperty("key"));
        }
        
        @Test
        @DisplayName("Should return null for missing property")
        void shouldReturnNullForMissingProperty() {
            TestEvent event = new TestEvent(this);
            
            assertNull(event.getProperty("missing"));
        }
        
        @Test
        @DisplayName("Should return default for missing property")
        void shouldReturnDefaultForMissingProperty() {
            TestEvent event = new TestEvent(this);
            
            assertEquals("default", event.getProperty("missing", "default"));
        }
        
        @Test
        @DisplayName("Should check if property exists")
        void shouldCheckIfPropertyExists() {
            TestEvent event = new TestEvent(this);
            event.setProperty("key", "value");
            
            assertTrue(event.hasProperty("key"));
            assertFalse(event.hasProperty("missing"));
        }
    }
    
    @Nested
    @DisplayName("ToString Tests")
    class ToStringTests {
        
        @Test
        @DisplayName("Should return meaningful toString")
        void shouldReturnMeaningfulToString() {
            TestEvent event = new TestEvent(this);
            
            String result = event.toString();
            
            assertNotNull(result);
            assertTrue(result.contains("TestEvent"));
        }
    }
}
