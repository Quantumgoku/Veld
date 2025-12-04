package com.veld.runtime;

import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for VeldException.
 */
@DisplayName("VeldException Tests")
class VeldExceptionTest {
    
    @Nested
    @DisplayName("Constructor Tests")
    class ConstructorTests {
        
        @Test
        @DisplayName("Should create exception with message")
        void shouldCreateExceptionWithMessage() {
            VeldException exception = new VeldException("Test message");
            
            assertEquals("Test message", exception.getMessage());
        }
        
        @Test
        @DisplayName("Should create exception with message and cause")
        void shouldCreateExceptionWithMessageAndCause() {
            Throwable cause = new RuntimeException("Cause");
            VeldException exception = new VeldException("Test message", cause);
            
            assertEquals("Test message", exception.getMessage());
            assertSame(cause, exception.getCause());
        }
        
        @Test
        @DisplayName("Should create exception with cause only")
        void shouldCreateExceptionWithCauseOnly() {
            Throwable cause = new RuntimeException("Cause");
            VeldException exception = new VeldException(cause);
            
            assertSame(cause, exception.getCause());
        }
    }
    
    @Nested
    @DisplayName("Inheritance Tests")
    class InheritanceTests {
        
        @Test
        @DisplayName("Should be a RuntimeException")
        void shouldBeARuntimeException() {
            VeldException exception = new VeldException("Test");
            
            assertTrue(exception instanceof RuntimeException);
        }
        
        @Test
        @DisplayName("Should be throwable")
        void shouldBeThrowable() {
            assertThrows(VeldException.class, () -> {
                throw new VeldException("Test");
            });
        }
    }
    
    @Nested
    @DisplayName("Catch Tests")
    class CatchTests {
        
        @Test
        @DisplayName("Should be catchable as VeldException")
        void shouldBeCatchableAsVeldException() {
            try {
                throw new VeldException("Test");
            } catch (VeldException e) {
                assertEquals("Test", e.getMessage());
            }
        }
        
        @Test
        @DisplayName("Should be catchable as RuntimeException")
        void shouldBeCatchableAsRuntimeException() {
            try {
                throw new VeldException("Test");
            } catch (RuntimeException e) {
                assertTrue(e instanceof VeldException);
            }
        }
    }
}
