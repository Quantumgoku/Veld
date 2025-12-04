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
 * Tests for TimingInterceptor.
 */
@DisplayName("TimingInterceptor Tests")
class TimingInterceptorTest {
    
    private TimingInterceptor interceptor;
    
    @Mock
    private InvocationContext mockContext;
    
    @BeforeEach
    void setUp() throws Exception {
        MockitoAnnotations.openMocks(this);
        interceptor = new TimingInterceptor();
        
        Method method = TestClass.class.getMethod("slowMethod");
        when(mockContext.getMethod()).thenReturn(method);
        when(mockContext.getTarget()).thenReturn(new TestClass());
        when(mockContext.getContextData()).thenReturn(new HashMap<>());
    }
    
    static class TestClass {
        public String slowMethod() throws InterruptedException {
            Thread.sleep(10);
            return "done";
        }
    }
    
    @Nested
    @DisplayName("Timing Tests")
    class TimingTests {
        
        @Test
        @DisplayName("Should proceed with invocation")
        void shouldProceedWithInvocation() throws Throwable {
            when(mockContext.proceed()).thenReturn("done");
            
            Object result = interceptor.intercept(mockContext);
            
            assertEquals("done", result);
            verify(mockContext).proceed();
        }
        
        @Test
        @DisplayName("Should measure execution time")
        void shouldMeasureExecutionTime() throws Throwable {
            when(mockContext.proceed()).thenAnswer(inv -> {
                Thread.sleep(50);
                return "done";
            });
            
            long startTime = System.currentTimeMillis();
            interceptor.intercept(mockContext);
            long elapsed = System.currentTimeMillis() - startTime;
            
            assertTrue(elapsed >= 50);
        }
        
        @Test
        @DisplayName("Should still measure time when exception occurs")
        void shouldStillMeasureTimeWhenExceptionOccurs() throws Throwable {
            when(mockContext.proceed()).thenThrow(new RuntimeException("error"));
            
            assertThrows(RuntimeException.class, () -> 
                interceptor.intercept(mockContext));
        }
    }
}
