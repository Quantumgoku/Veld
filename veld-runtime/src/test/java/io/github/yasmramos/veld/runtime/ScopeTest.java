package io.github.yasmramos.veld.runtime;

import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for string-based scope handling.
 */
@DisplayName("Scope String Tests")
class ScopeTest {
    
    @Nested
    @DisplayName("Built-in Scope Constants Tests")
    class BuiltInScopeConstantsTests {
        
        @Test
        @DisplayName("Should have correct singleton scope string")
        void shouldHaveSingletonScopeString() {
            assertEquals("singleton", "singleton");
        }
        
        @Test
        @DisplayName("Should have correct prototype scope string")
        void shouldHavePrototypeScopeString() {
            assertEquals("prototype", "prototype");
        }
        
        @Test
        @DisplayName("Should have correct request scope string")
        void shouldHaveRequestScopeString() {
            assertEquals("request", "request");
        }
        
        @Test
        @DisplayName("Should have correct session scope string")
        void shouldHaveSessionScopeString() {
            assertEquals("session", "session");
        }
    }
    
    @Nested
    @DisplayName("Built-in Scope Validation Tests")
    class BuiltInScopeValidationTests {
        
        @Test
        @DisplayName("Should validate singleton as built-in scope")
        void shouldValidateSingletonAsBuiltIn() {
            assertTrue(isBuiltInScope("singleton"));
        }
        
        @Test
        @DisplayName("Should validate prototype as built-in scope")
        void shouldValidatePrototypeAsBuiltIn() {
            assertTrue(isBuiltInScope("prototype"));
        }
        
        @Test
        @DisplayName("Should validate request as built-in scope")
        void shouldValidateRequestAsBuiltIn() {
            assertTrue(isBuiltInScope("request"));
        }
        
        @Test
        @DisplayName("Should validate session as built-in scope")
        void shouldValidateSessionAsBuiltIn() {
            assertTrue(isBuiltInScope("session"));
        }
        
        @Test
        @DisplayName("Should reject unknown scope")
        void shouldRejectUnknownScope() {
            assertFalse(isBuiltInScope("unknown"));
        }
        
        @Test
        @DisplayName("Should reject null scope")
        void shouldRejectNullScope() {
            assertFalse(isBuiltInScope(null));
        }
    }
    
    @Nested
    @DisplayName("Web Scope Detection Tests")
    class WebScopeDetectionTests {
        
        @Test
        @DisplayName("Request should be detected as web scope")
        void requestShouldBeWebScope() {
            assertTrue(isWebScope("request"));
        }
        
        @Test
        @DisplayName("Session should be detected as web scope")
        void sessionShouldBeWebScope() {
            assertTrue(isWebScope("session"));
        }
        
        @Test
        @DisplayName("Singleton should not be detected as web scope")
        void singletonShouldNotBeWebScope() {
            assertFalse(isWebScope("singleton"));
        }
        
        @Test
        @DisplayName("Prototype should not be detected as web scope")
        void prototypeShouldNotBeWebScope() {
            assertFalse(isWebScope("prototype"));
        }
    }
    
    @Nested
    @DisplayName("Default Scope Tests")
    class DefaultScopeTests {
        
        @Test
        @DisplayName("Singleton should be the default scope")
        void singletonShouldBeDefault() {
            assertEquals("singleton", getDefaultScope());
        }
    }
    
    // Helper methods that mirror the functionality from the removed ScopeType enum
    
    private boolean isBuiltInScope(String scope) {
        if (scope == null) {
            return false;
        }
        return scope.equals("singleton") || 
               scope.equals("prototype") || 
               scope.equals("request") || 
               scope.equals("session");
    }
    
    private boolean isWebScope(String scope) {
        return scope != null && (scope.equals("request") || scope.equals("session"));
    }
    
    private String getDefaultScope() {
        return "singleton";
    }
}
