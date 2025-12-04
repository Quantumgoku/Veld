package com.veld.aop;

import com.veld.annotation.AfterType;
import org.junit.jupiter.api.*;

import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for Advice class.
 */
@DisplayName("Advice Tests")
class AdviceTest {
    
    // Test class for matching
    static class TestClass {
        public void testMethod() {}
        public String getName() { return "test"; }
        public void setName(String name) {}
        public void process(int value) {}
    }
    
    // Test aspect class
    static class TestAspect {
        public void beforeAdvice(JoinPoint jp) {}
        public Object aroundAdvice(InvocationContext ctx) throws Throwable { return ctx.proceed(); }
        public void afterAdvice(JoinPoint jp) {}
        public void afterReturningAdvice(JoinPoint jp, Object result) {}
        public void afterThrowingAdvice(JoinPoint jp, Throwable ex) {}
    }
    
    private TestAspect aspect;
    
    @BeforeEach
    void setUp() {
        aspect = new TestAspect();
    }
    
    @Nested
    @DisplayName("Factory Method Tests")
    class FactoryMethodTests {
        
        @Test
        @DisplayName("Should create before advice")
        void shouldCreateBeforeAdvice() throws Exception {
            Method method = TestAspect.class.getDeclaredMethod("beforeAdvice", JoinPoint.class);
            
            Advice advice = Advice.before("execution(* test*(..))", aspect, method, 0);
            
            assertEquals(Advice.AdviceType.BEFORE, advice.getType());
        }
        
        @Test
        @DisplayName("Should create around advice")
        void shouldCreateAroundAdvice() throws Exception {
            Method method = TestAspect.class.getDeclaredMethod("aroundAdvice", InvocationContext.class);
            
            Advice advice = Advice.around("execution(* test*(..))", aspect, method, 0);
            
            assertEquals(Advice.AdviceType.AROUND, advice.getType());
        }
        
        @Test
        @DisplayName("Should create after returning advice")
        void shouldCreateAfterReturningAdvice() throws Exception {
            Method method = TestAspect.class.getDeclaredMethod("afterReturningAdvice", JoinPoint.class, Object.class);
            
            Advice advice = Advice.after("execution(* test*(..))", AfterType.RETURNING, aspect, method, 0);
            
            assertEquals(Advice.AdviceType.AFTER_RETURNING, advice.getType());
        }
        
        @Test
        @DisplayName("Should create after throwing advice")
        void shouldCreateAfterThrowingAdvice() throws Exception {
            Method method = TestAspect.class.getDeclaredMethod("afterThrowingAdvice", JoinPoint.class, Throwable.class);
            
            Advice advice = Advice.after("execution(* test*(..))", AfterType.THROWING, aspect, method, 0);
            
            assertEquals(Advice.AdviceType.AFTER_THROWING, advice.getType());
        }
        
        @Test
        @DisplayName("Should create after finally advice")
        void shouldCreateAfterFinallyAdvice() throws Exception {
            Method method = TestAspect.class.getDeclaredMethod("afterAdvice", JoinPoint.class);
            
            Advice advice = Advice.after("execution(* test*(..))", AfterType.FINALLY, aspect, method, 0);
            
            assertEquals(Advice.AdviceType.AFTER_FINALLY, advice.getType());
        }
    }
    
    @Nested
    @DisplayName("Pointcut Matching Tests")
    class PointcutMatchingTests {
        
        @Test
        @DisplayName("Should match execution pointcut with method name pattern")
        void shouldMatchExecutionPointcutWithMethodNamePattern() throws Exception {
            Method method = TestAspect.class.getDeclaredMethod("beforeAdvice", JoinPoint.class);
            Advice advice = Advice.before("execution(* test*(..))", aspect, method, 0);
            
            Method targetMethod = TestClass.class.getDeclaredMethod("testMethod");
            
            assertTrue(advice.matches(targetMethod));
        }
        
        @Test
        @DisplayName("Should not match when pattern does not match")
        void shouldNotMatchWhenPatternDoesNotMatch() throws Exception {
            Method method = TestAspect.class.getDeclaredMethod("beforeAdvice", JoinPoint.class);
            Advice advice = Advice.before("execution(* notExists*(..))", aspect, method, 0);
            
            Method targetMethod = TestClass.class.getDeclaredMethod("testMethod");
            
            assertFalse(advice.matches(targetMethod));
        }
        
        @Test
        @DisplayName("Should match getter pattern")
        void shouldMatchGetterPattern() throws Exception {
            Method method = TestAspect.class.getDeclaredMethod("beforeAdvice", JoinPoint.class);
            Advice advice = Advice.before("execution(* get*(..))", aspect, method, 0);
            
            Method targetMethod = TestClass.class.getDeclaredMethod("getName");
            
            assertTrue(advice.matches(targetMethod));
        }
        
        @Test
        @DisplayName("Should match setter pattern")
        void shouldMatchSetterPattern() throws Exception {
            Method method = TestAspect.class.getDeclaredMethod("beforeAdvice", JoinPoint.class);
            Advice advice = Advice.before("execution(* set*(..))", aspect, method, 0);
            
            Method targetMethod = TestClass.class.getDeclaredMethod("setName", String.class);
            
            assertTrue(advice.matches(targetMethod));
        }
        
        @Test
        @DisplayName("Should check if advice could match class")
        void shouldCheckIfAdviceCouldMatchClass() throws Exception {
            Method method = TestAspect.class.getDeclaredMethod("beforeAdvice", JoinPoint.class);
            Advice advice = Advice.before("execution(* test*(..))", aspect, method, 0);
            
            assertTrue(advice.couldMatch(TestClass.class));
        }
    }
    
    @Nested
    @DisplayName("Comparison Tests")
    class ComparisonTests {
        
        @Test
        @DisplayName("Should compare by order")
        void shouldCompareByOrder() throws Exception {
            Method method = TestAspect.class.getDeclaredMethod("beforeAdvice", JoinPoint.class);
            
            Advice high = Advice.before("execution(* *(..))", aspect, method, 1);
            Advice low = Advice.before("execution(* *(..))", aspect, method, 10);
            
            assertTrue(high.compareTo(low) < 0);
        }
    }
    
    @Nested
    @DisplayName("Properties Tests")
    class PropertiesTests {
        
        @Test
        @DisplayName("Should return aspect instance")
        void shouldReturnAspectInstance() throws Exception {
            Method method = TestAspect.class.getDeclaredMethod("beforeAdvice", JoinPoint.class);
            Advice advice = Advice.before("execution(* *(..))", aspect, method, 0);
            
            assertSame(aspect, advice.getAspectInstance());
        }
        
        @Test
        @DisplayName("Should return pointcut expression")
        void shouldReturnPointcutExpression() throws Exception {
            Method method = TestAspect.class.getDeclaredMethod("beforeAdvice", JoinPoint.class);
            Advice advice = Advice.before("execution(* test*(..))", aspect, method, 0);
            
            assertEquals("execution(* test*(..))", advice.getPointcutExpression());
        }
    }
}
