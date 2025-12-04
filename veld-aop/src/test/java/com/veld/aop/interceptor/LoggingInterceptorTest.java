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
 * Tests for LoggingInterceptor.
 */
@DisplayName("LoggingInterceptor Tests")
class LoggingInterceptorTest {
    
    private LoggingInterceptor interceptor;
    
    @Mock
    private InvocationContext mockContext;
    
    @BeforeEach
    void setUp() throws Exception {
        MockitoAnnotations.openMocks(this);
        interceptor = new LoggingInterceptor();
        
        Method method = TestClass.class.getMethod("testMethod", String.class, int.class);
        when(mockContext.getMethod()).thenReturn(method);
        when(mockContext.getTarget()).thenReturn(new TestClass());
        when(mockContext.getParameters()).thenReturn(new Object[]{"test", 42});
        when(mockContext.getContextData()).thenReturn(new HashMap<>());
    }
    
    static class TestClass {
        public String testMethod(String param1, int param2) {
            return "result";
        }
    }
    
    @Nested
    @DisplayName("Interception Tests")
    class InterceptionTests {
        
        @Test
        @DisplayName("Should proceed with invocation")
        void shouldProceedWithInvocation() throws Throwable {
            when(mockContext.proceed()).thenReturn("result");
            
            Object result = interceptor.intercept(mockContext);
            
            assertEquals("result", result);
            verify(mockContext).proceed();
        }
        
        @Test
        @DisplayName("Should log method entry and exit")
        void shouldLogMethodEntryAndExit() throws Throwable {
            when(mockContext.proceed()).thenReturn("result");
            
            // This should not throw and should log
            Object result = interceptor.intercept(mockContext);
            
            assertNotNull(result);
        }
        
        @Test
        @DisplayName("Should handle exception during invocation")
        void shouldHandleExceptionDuringInvocation() throws Throwable {
            RuntimeException exception = new RuntimeException("Test error");
            when(mockContext.proceed()).thenThrow(exception);
            
            assertThrows(RuntimeException.class, () -> 
                interceptor.intercept(mockContext));
        }
        
        @Test
        @DisplayName("Should propagate exception")
        void shouldPropagateException() throws Throwable {
            RuntimeException exception = new RuntimeException("Test error");
            when(mockContext.proceed()).thenThrow(exception);
            
            RuntimeException thrown = assertThrows(RuntimeException.class, () -> 
                interceptor.intercept(mockContext));
            
            assertSame(exception, thrown);
        }
    }
}
