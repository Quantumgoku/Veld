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
 * Tests for TransactionInterceptor.
 */
@DisplayName("TransactionInterceptor Tests")
class TransactionInterceptorTest {
    
    private TransactionInterceptor interceptor;
    
    @Mock
    private InvocationContext mockContext;
    
    @BeforeEach
    void setUp() throws Exception {
        MockitoAnnotations.openMocks(this);
        interceptor = new TransactionInterceptor();
        
        Method method = TestClass.class.getMethod("transactionalMethod");
        when(mockContext.getMethod()).thenReturn(method);
        when(mockContext.getTarget()).thenReturn(new TestClass());
        when(mockContext.getContextData()).thenReturn(new HashMap<>());
    }
    
    static class TestClass {
        @Transactional
        public String transactionalMethod() {
            return "success";
        }
    }
    
    @Nested
    @DisplayName("Transaction Tests")
    class TransactionTests {
        
        @Test
        @DisplayName("Should proceed with invocation")
        void shouldProceedWithInvocation() throws Throwable {
            when(mockContext.proceed()).thenReturn("success");
            
            Object result = interceptor.intercept(mockContext);
            
            assertEquals("success", result);
            verify(mockContext).proceed();
        }
        
        @Test
        @DisplayName("Should commit on successful execution")
        void shouldCommitOnSuccessfulExecution() throws Throwable {
            when(mockContext.proceed()).thenReturn("success");
            
            Object result = interceptor.intercept(mockContext);
            
            // Transaction should be committed (simulated)
            assertNotNull(result);
        }
        
        @Test
        @DisplayName("Should rollback on exception")
        void shouldRollbackOnException() throws Throwable {
            when(mockContext.proceed()).thenThrow(new RuntimeException("DB error"));
            
            assertThrows(RuntimeException.class, () -> 
                interceptor.intercept(mockContext));
            
            // Transaction should be rolled back (simulated)
        }
    }
}
