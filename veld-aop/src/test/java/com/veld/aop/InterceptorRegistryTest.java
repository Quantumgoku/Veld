package com.veld.aop;

import com.veld.annotation.*;
import org.junit.jupiter.api.*;

import java.lang.reflect.Method;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for InterceptorRegistry.
 */
@DisplayName("InterceptorRegistry Tests")
class InterceptorRegistryTest {
    
    private InterceptorRegistry registry;
    
    @BeforeEach
    void setUp() {
        registry = InterceptorRegistry.getInstance();
        registry.clear();
    }
    
    @AfterEach
    void tearDown() {
        registry.clear();
    }
    
    // Test aspect
    @Aspect(order = 1)
    static class TestAspect {
        @Around("execution(* test*(..))")
        public Object around(InvocationContext ctx) throws Throwable {
            return ctx.proceed();
        }
        
        @Before("execution(* test*(..))")
        public void before(JoinPoint jp) {
            // Before advice
        }
        
        @After(value = "execution(* test*(..))", type = AfterType.RETURNING)
        public void afterReturning(JoinPoint jp, Object result) {
            // After returning advice
        }
    }
    
    // Non-aspect class
    static class NotAnAspect {
        public void someMethod() {}
    }
    
    // Test interceptor binding
    @InterceptorBinding
    @java.lang.annotation.Retention(java.lang.annotation.RetentionPolicy.RUNTIME)
    @java.lang.annotation.Target({java.lang.annotation.ElementType.TYPE, java.lang.annotation.ElementType.METHOD})
    @interface TestBinding {}
    
    // Test interceptor
    @Interceptor(priority = 1)
    @TestBinding
    static class TestInterceptor {
        @AroundInvoke
        public Object intercept(InvocationContext ctx) throws Throwable {
            return ctx.proceed();
        }
    }
    
    // Invalid interceptor without @AroundInvoke
    @Interceptor
    @TestBinding
    static class InvalidInterceptor {
        public void notAroundInvoke() {}
    }
    
    // Target class for testing
    @TestBinding
    static class TargetClass {
        @TestBinding
        public void testMethod() {}
        
        public void otherMethod() {}
    }
    
    @Nested
    @DisplayName("Singleton Tests")
    class SingletonTests {
        
        @Test
        @DisplayName("Should return same instance")
        void shouldReturnSameInstance() {
            InterceptorRegistry r1 = InterceptorRegistry.getInstance();
            InterceptorRegistry r2 = InterceptorRegistry.getInstance();
            
            assertSame(r1, r2);
        }
    }
    
    @Nested
    @DisplayName("Aspect Registration Tests")
    class AspectRegistrationTests {
        
        @Test
        @DisplayName("Should register aspect")
        void shouldRegisterAspect() {
            TestAspect aspect = new TestAspect();
            
            assertDoesNotThrow(() -> registry.registerAspect(aspect));
        }
        
        @Test
        @DisplayName("Should throw exception for non-aspect")
        void shouldThrowExceptionForNonAspect() {
            NotAnAspect notAspect = new NotAnAspect();
            
            assertThrows(IllegalArgumentException.class, () -> 
                registry.registerAspect(notAspect));
        }
        
        @Test
        @DisplayName("Should detect advices for class after registering aspect")
        void shouldDetectAdvicesForClassAfterRegisteringAspect() {
            TestAspect aspect = new TestAspect();
            registry.registerAspect(aspect);
            
            // The aspect matches execution(* test*(..)), so it should match TestAspect itself
            assertTrue(registry.hasAdvicesFor(TestAspect.class));
        }
    }
    
    @Nested
    @DisplayName("Interceptor Registration Tests")
    class InterceptorRegistrationTests {
        
        @Test
        @DisplayName("Should register interceptor")
        void shouldRegisterInterceptor() {
            TestInterceptor interceptor = new TestInterceptor();
            
            assertDoesNotThrow(() -> registry.registerInterceptor(interceptor));
        }
        
        @Test
        @DisplayName("Should throw exception for class without @Interceptor")
        void shouldThrowExceptionForClassWithoutInterceptorAnnotation() {
            assertThrows(IllegalArgumentException.class, () -> 
                registry.registerInterceptor(new Object()));
        }
        
        @Test
        @DisplayName("Should throw exception for interceptor without @AroundInvoke")
        void shouldThrowExceptionForInterceptorWithoutAroundInvoke() {
            InvalidInterceptor invalid = new InvalidInterceptor();
            
            assertThrows(IllegalArgumentException.class, () -> 
                registry.registerInterceptor(invalid));
        }
        
        @Test
        @DisplayName("Should detect advices for class with binding annotation")
        void shouldDetectAdvicesForClassWithBindingAnnotation() {
            TestInterceptor interceptor = new TestInterceptor();
            registry.registerInterceptor(interceptor);
            
            assertTrue(registry.hasAdvicesFor(TargetClass.class));
        }
    }
    
    @Nested
    @DisplayName("Interceptor Retrieval Tests")
    class InterceptorRetrievalTests {
        
        @Test
        @DisplayName("Should get interceptors for method with binding")
        void shouldGetInterceptorsForMethodWithBinding() throws Exception {
            TestInterceptor interceptor = new TestInterceptor();
            registry.registerInterceptor(interceptor);
            
            Method method = TargetClass.class.getMethod("testMethod");
            List<MethodInterceptor> interceptors = registry.getInterceptors(method);
            
            assertFalse(interceptors.isEmpty());
        }
        
        @Test
        @DisplayName("Should return empty list for method without bindings")
        void shouldReturnEmptyListForMethodWithoutBindings() throws Exception {
            Method method = NotAnAspect.class.getMethod("someMethod");
            List<MethodInterceptor> interceptors = registry.getInterceptors(method);
            
            assertTrue(interceptors.isEmpty());
        }
        
        @Test
        @DisplayName("Should cache interceptors for same method")
        void shouldCacheInterceptorsForSameMethod() throws Exception {
            TestInterceptor interceptor = new TestInterceptor();
            registry.registerInterceptor(interceptor);
            
            Method method = TargetClass.class.getMethod("testMethod");
            List<MethodInterceptor> first = registry.getInterceptors(method);
            List<MethodInterceptor> second = registry.getInterceptors(method);
            
            assertSame(first, second);
        }
    }
    
    @Nested
    @DisplayName("Statistics Tests")
    class StatisticsTests {
        
        @Test
        @DisplayName("Should return statistics")
        void shouldReturnStatistics() {
            TestAspect aspect = new TestAspect();
            registry.registerAspect(aspect);
            TestInterceptor interceptor = new TestInterceptor();
            registry.registerInterceptor(interceptor);
            
            String stats = registry.getStatistics();
            
            assertNotNull(stats);
            assertTrue(stats.contains("InterceptorRegistry"));
            assertTrue(stats.contains("aspects"));
            assertTrue(stats.contains("advices"));
        }
    }
    
    @Nested
    @DisplayName("Clear Tests")
    class ClearTests {
        
        @Test
        @DisplayName("Should clear all registrations")
        void shouldClearAllRegistrations() {
            TestAspect aspect = new TestAspect();
            registry.registerAspect(aspect);
            TestInterceptor interceptor = new TestInterceptor();
            registry.registerInterceptor(interceptor);
            
            registry.clear();
            
            assertFalse(registry.hasAdvicesFor(TargetClass.class));
        }
    }
}
