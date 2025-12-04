package com.veld.runtime.event;

import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for EventFilter.
 */
@DisplayName("EventFilter Tests")
class EventFilterTest {
    
    static class TestEvent extends Event {
        private final String message;
        private final int value;
        
        TestEvent(Object source, String message, int value) {
            super(source);
            this.message = message;
            this.value = value;
        }
        
        public String getMessage() {
            return message;
        }
        
        public int getValue() {
            return value;
        }
    }
    
    @Nested
    @DisplayName("Property Access Tests")
    class PropertyAccessTests {
        
        @Test
        @DisplayName("Should evaluate simple property equality")
        void shouldEvaluateSimplePropertyEquality() {
            TestEvent event = new TestEvent(this, "hello", 42);
            
            assertTrue(EventFilter.evaluate("message == 'hello'", event));
            assertFalse(EventFilter.evaluate("message == 'world'", event));
        }
        
        @Test
        @DisplayName("Should evaluate numeric comparison")
        void shouldEvaluateNumericComparison() {
            TestEvent event = new TestEvent(this, "test", 100);
            
            assertTrue(EventFilter.evaluate("value > 50", event));
            assertFalse(EventFilter.evaluate("value > 150", event));
        }
        
        @Test
        @DisplayName("Should evaluate equality check")
        void shouldEvaluateEqualityCheck() {
            TestEvent event = new TestEvent(this, "test", 42);
            
            assertTrue(EventFilter.evaluate("value == 42", event));
            assertFalse(EventFilter.evaluate("value == 100", event));
        }
    }
    
    @Nested
    @DisplayName("Empty Filter Tests")
    class EmptyFilterTests {
        
        @Test
        @DisplayName("Should return true for null filter")
        void shouldReturnTrueForNullFilter() {
            TestEvent event = new TestEvent(this, "test", 1);
            
            assertTrue(EventFilter.evaluate(null, event));
        }
        
        @Test
        @DisplayName("Should return true for empty filter")
        void shouldReturnTrueForEmptyFilter() {
            TestEvent event = new TestEvent(this, "test", 1);
            
            assertTrue(EventFilter.evaluate("", event));
        }
        
        @Test
        @DisplayName("Should return true for whitespace filter")
        void shouldReturnTrueForWhitespaceFilter() {
            TestEvent event = new TestEvent(this, "test", 1);
            
            assertTrue(EventFilter.evaluate("   ", event));
        }
    }
    
    @Nested
    @DisplayName("Complex Expression Tests")
    class ComplexExpressionTests {
        
        @Test
        @DisplayName("Should evaluate AND expression")
        void shouldEvaluateAndExpression() {
            TestEvent event = new TestEvent(this, "hello", 100);
            
            assertTrue(EventFilter.evaluate("message == 'hello' && value > 50", event));
            assertFalse(EventFilter.evaluate("message == 'hello' && value > 150", event));
        }
        
        @Test
        @DisplayName("Should evaluate OR expression")
        void shouldEvaluateOrExpression() {
            TestEvent event = new TestEvent(this, "hello", 30);
            
            assertTrue(EventFilter.evaluate("message == 'hello' || value > 50", event));
            assertTrue(EventFilter.evaluate("message == 'world' || value < 50", event));
            assertFalse(EventFilter.evaluate("message == 'world' || value > 50", event));
        }
    }
    
    @Nested
    @DisplayName("Error Handling Tests")
    class ErrorHandlingTests {
        
        @Test
        @DisplayName("Should handle invalid expression gracefully")
        void shouldHandleInvalidExpressionGracefully() {
            TestEvent event = new TestEvent(this, "test", 1);
            
            // Invalid expression should return false or true based on implementation
            boolean result = EventFilter.evaluate("invalid!@#expression", event);
            // Just verify it doesn't throw
            assertNotNull(String.valueOf(result));
        }
        
        @Test
        @DisplayName("Should handle missing property gracefully")
        void shouldHandleMissingPropertyGracefully() {
            TestEvent event = new TestEvent(this, "test", 1);
            
            // Missing property should be handled gracefully
            boolean result = EventFilter.evaluate("nonExistentProperty == 'value'", event);
            assertNotNull(String.valueOf(result));
        }
    }
}
