package com.veld.aop.interceptor;

import com.veld.aop.InvocationContext;
import org.junit.jupiter.api.*;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.lang.reflect.Method;
import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Tests for ValidationInterceptor.
 */
@DisplayName("ValidationInterceptor Tests")
class ValidationInterceptorTest {
    
    private ValidationInterceptor interceptor;
    
    @Mock
    private InvocationContext mockContext;
    
    @BeforeEach
    void setUp() throws Exception {
        MockitoAnnotations.openMocks(this);
        interceptor = new ValidationInterceptor();
        
        Method method = TestClass.class.getMethod("validatedMethod", String.class);
        when(mockContext.getMethod()).thenReturn(method);
        when(mockContext.getTarget()).thenReturn(new TestClass());
        when(mockContext.getContextData()).thenReturn(new HashMap<>());
    }
    
    static class TestClass {
        @Validated
        public String validatedMethod(String input) {
            return input;
        }
    }
    
    @Nested
    @DisplayName("Validation Tests")
    class ValidationTests {
        
        @Test
        @DisplayName("Should proceed with valid parameters")
        void shouldProceedWithValidParameters() throws Throwable {
            when(mockContext.getParameters()).thenReturn(new Object[]{"valid input"});
            when(mockContext.proceed()).thenReturn("valid input");
            
            Object result = interceptor.intercept(mockContext);
            
            assertEquals("valid input", result);
            verify(mockContext).proceed();
        }
        
        @Test
        @DisplayName("Should reject null parameters when not allowed")
        void shouldRejectNullParametersWhenNotAllowed() throws Throwable {
            when(mockContext.getParameters()).thenReturn(new Object[]{null});
            
            // Validation interceptor should validate and potentially throw
            // Behavior depends on implementation
            assertThrows(Exception.class, () -> 
                interceptor.intercept(mockContext));
        }
        
        @Test
        @DisplayName("Should validate before proceeding")
        void shouldValidateBeforeProceeding() throws Throwable {
            when(mockContext.getParameters()).thenReturn(new Object[]{"test"});
            when(mockContext.proceed()).thenReturn("test");
            
            interceptor.intercept(mockContext);
            
            // Proceed should be called after validation
            verify(mockContext).proceed();
        }
    }
}
