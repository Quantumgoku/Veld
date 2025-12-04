package com.veld.runtime.lifecycle.event;

import org.junit.jupiter.api.*;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for Context lifecycle events.
 */
@DisplayName("Context Events Tests")
class ContextEventsTest {
    
    private final Object source = new Object();
    
    @Nested
    @DisplayName("ContextRefreshedEvent Tests")
    class ContextRefreshedEventTests {
        
        @Test
        @DisplayName("Should create event with bean count and init time")
        void shouldCreateEventWithBeanCountAndInitTime() {
            ContextRefreshedEvent event = new ContextRefreshedEvent(source, 10, 500);
            
            assertEquals(10, event.getBeanCount());
            assertEquals(500, event.getInitializationTimeMs());
        }
        
        @Test
        @DisplayName("Should have source")
        void shouldHaveSource() {
            ContextRefreshedEvent event = new ContextRefreshedEvent(source, 10, 500);
            
            assertSame(source, event.getSource());
        }
        
        @Test
        @DisplayName("Should have timestamp")
        void shouldHaveTimestamp() {
            ContextRefreshedEvent event = new ContextRefreshedEvent(source, 10, 500);
            
            assertNotNull(event.getTimestamp());
        }
    }
    
    @Nested
    @DisplayName("ContextStartedEvent Tests")
    class ContextStartedEventTests {
        
        @Test
        @DisplayName("Should create event with lifecycle count")
        void shouldCreateEventWithLifecycleCount() {
            ContextStartedEvent event = new ContextStartedEvent(source, 5);
            
            assertEquals(5, event.getLifecycleBeansStarted());
        }
        
        @Test
        @DisplayName("Should have source")
        void shouldHaveSource() {
            ContextStartedEvent event = new ContextStartedEvent(source, 5);
            
            assertSame(source, event.getSource());
        }
    }
    
    @Nested
    @DisplayName("ContextStoppedEvent Tests")
    class ContextStoppedEventTests {
        
        @Test
        @DisplayName("Should create event with lifecycle count")
        void shouldCreateEventWithLifecycleCount() {
            ContextStoppedEvent event = new ContextStoppedEvent(source, 3);
            
            assertEquals(3, event.getLifecycleBeansStopped());
        }
        
        @Test
        @DisplayName("Should have source")
        void shouldHaveSource() {
            ContextStoppedEvent event = new ContextStoppedEvent(source, 3);
            
            assertSame(source, event.getSource());
        }
    }
    
    @Nested
    @DisplayName("ContextClosedEvent Tests")
    class ContextClosedEventTests {
        
        @Test
        @DisplayName("Should create event with uptime and destroyed count")
        void shouldCreateEventWithUptimeAndDestroyedCount() {
            Duration uptime = Duration.ofMinutes(10);
            ContextClosedEvent event = new ContextClosedEvent(source, uptime, 8);
            
            assertEquals(uptime, event.getUptime());
            assertEquals(8, event.getBeansDestroyed());
        }
        
        @Test
        @DisplayName("Should have source")
        void shouldHaveSource() {
            Duration uptime = Duration.ofMinutes(10);
            ContextClosedEvent event = new ContextClosedEvent(source, uptime, 8);
            
            assertSame(source, event.getSource());
        }
    }
    
    @Nested
    @DisplayName("ContextEvent Base Tests")
    class ContextEventBaseTests {
        
        @Test
        @DisplayName("All context events should extend Event")
        void allContextEventsShouldExtendEvent() {
            ContextRefreshedEvent refreshed = new ContextRefreshedEvent(source, 0, 0);
            ContextStartedEvent started = new ContextStartedEvent(source, 0);
            ContextStoppedEvent stopped = new ContextStoppedEvent(source, 0);
            ContextClosedEvent closed = new ContextClosedEvent(source, Duration.ZERO, 0);
            
            assertTrue(refreshed instanceof com.veld.runtime.event.Event);
            assertTrue(started instanceof com.veld.runtime.event.Event);
            assertTrue(stopped instanceof com.veld.runtime.event.Event);
            assertTrue(closed instanceof com.veld.runtime.event.Event);
        }
    }
}
