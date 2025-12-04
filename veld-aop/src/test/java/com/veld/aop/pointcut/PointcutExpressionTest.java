package com.veld.aop.pointcut;

import org.junit.jupiter.api.*;

import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for PointcutExpression.
 */
@DisplayName("PointcutExpression Tests")
class PointcutExpressionTest {
    
    // Test classes
    interface TestInterface {
        void interfaceMethod();
    }
    
    static class TestClass implements TestInterface {
        public void testMethod() {}
        public String getValue() { return null; }
        public void setValue(String val) {}
        public void process(int a, String b) {}
        private void privateMethod() {}
        
        @Override
        public void interfaceMethod() {}
    }
    
    static class OtherClass {
        public void otherMethod() {}
    }
    
    @Nested
    @DisplayName("Execution Pointcut Tests")
    class ExecutionPointcutTests {
        
        @Test
        @DisplayName("Should match any method with wildcard")
        void shouldMatchAnyMethodWithWildcard() throws Exception {
            PointcutExpression expr = PointcutExpression.parse("execution(* *(..))");
            Method method = TestClass.class.getMethod("testMethod");
            
            assertTrue(expr.matches(method));
        }
        
        @Test
        @DisplayName("Should match method by name prefix")
        void shouldMatchMethodByNamePrefix() throws Exception {
            PointcutExpression expr = PointcutExpression.parse("execution(* get*(..))");
            
            assertTrue(expr.matches(TestClass.class.getMethod("getValue")));
            assertFalse(expr.matches(TestClass.class.getMethod("setValue", String.class)));
        }
        
        @Test
        @DisplayName("Should match method by exact name")
        void shouldMatchMethodByExactName() throws Exception {
            PointcutExpression expr = PointcutExpression.parse("execution(* testMethod(..))");
            
            assertTrue(expr.matches(TestClass.class.getMethod("testMethod")));
            assertFalse(expr.matches(TestClass.class.getMethod("getValue")));
        }
        
        @Test
        @DisplayName("Should match method by name suffix")
        void shouldMatchMethodByNameSuffix() throws Exception {
            PointcutExpression expr = PointcutExpression.parse("execution(* *Method(..))");
            
            assertTrue(expr.matches(TestClass.class.getMethod("testMethod")));
            assertTrue(expr.matches(TestClass.class.getMethod("interfaceMethod")));
            assertFalse(expr.matches(TestClass.class.getMethod("getValue")));
        }
        
        @Test
        @DisplayName("Should match method by return type")
        void shouldMatchMethodByReturnType() throws Exception {
            PointcutExpression expr = PointcutExpression.parse("execution(String *(..))");
            
            assertTrue(expr.matches(TestClass.class.getMethod("getValue")));
            assertFalse(expr.matches(TestClass.class.getMethod("testMethod")));
        }
        
        @Test
        @DisplayName("Should match void return type")
        void shouldMatchVoidReturnType() throws Exception {
            PointcutExpression expr = PointcutExpression.parse("execution(void *(..))");
            
            assertTrue(expr.matches(TestClass.class.getMethod("testMethod")));
            assertFalse(expr.matches(TestClass.class.getMethod("getValue")));
        }
    }
    
    @Nested
    @DisplayName("Within Pointcut Tests")
    class WithinPointcutTests {
        
        @Test
        @DisplayName("Should match methods within class")
        void shouldMatchMethodsWithinClass() throws Exception {
            PointcutExpression expr = PointcutExpression.parse("within(com.veld.aop.pointcut.PointcutExpressionTest$TestClass)");
            
            assertTrue(expr.matches(TestClass.class.getMethod("testMethod")));
            assertFalse(expr.matches(OtherClass.class.getMethod("otherMethod")));
        }
        
        @Test
        @DisplayName("Should match methods within package pattern")
        void shouldMatchMethodsWithinPackagePattern() throws Exception {
            PointcutExpression expr = PointcutExpression.parse("within(com.veld.aop..*)");
            
            assertTrue(expr.matches(TestClass.class.getMethod("testMethod")));
        }
    }
    
    @Nested
    @DisplayName("Annotation Pointcut Tests")
    class AnnotationPointcutTests {
        
        @Test
        @DisplayName("Should match methods with annotation")
        void shouldMatchMethodsWithAnnotation() throws Exception {
            PointcutExpression expr = PointcutExpression.parse("@annotation(Override)");
            
            assertTrue(expr.matches(TestClass.class.getMethod("interfaceMethod")));
            assertFalse(expr.matches(TestClass.class.getMethod("testMethod")));
        }
    }
    
    @Nested
    @DisplayName("Combined Pointcut Tests")
    class CombinedPointcutTests {
        
        @Test
        @DisplayName("Should match with AND operator")
        void shouldMatchWithAndOperator() throws Exception {
            PointcutExpression expr = PointcutExpression.parse(
                "execution(* get*(..)) && execution(String *(..))");
            
            assertTrue(expr.matches(TestClass.class.getMethod("getValue")));
            assertFalse(expr.matches(TestClass.class.getMethod("testMethod")));
        }
        
        @Test
        @DisplayName("Should match with OR operator")
        void shouldMatchWithOrOperator() throws Exception {
            PointcutExpression expr = PointcutExpression.parse(
                "execution(* get*(..)) || execution(* set*(..))");
            
            assertTrue(expr.matches(TestClass.class.getMethod("getValue")));
            assertTrue(expr.matches(TestClass.class.getMethod("setValue", String.class)));
            assertFalse(expr.matches(TestClass.class.getMethod("testMethod")));
        }
        
        @Test
        @DisplayName("Should match with NOT operator")
        void shouldMatchWithNotOperator() throws Exception {
            PointcutExpression expr = PointcutExpression.parse("!execution(* get*(..))");
            
            assertFalse(expr.matches(TestClass.class.getMethod("getValue")));
            assertTrue(expr.matches(TestClass.class.getMethod("testMethod")));
        }
    }
    
    @Nested
    @DisplayName("Class Matching Tests")
    class ClassMatchingTests {
        
        @Test
        @DisplayName("Should check if expression could match class")
        void shouldCheckIfExpressionCouldMatchClass() {
            PointcutExpression expr = PointcutExpression.parse("execution(* test*(..))");
            
            assertTrue(expr.couldMatch(TestClass.class));
        }
        
        @Test
        @DisplayName("Should not match class without matching methods")
        void shouldNotMatchClassWithoutMatchingMethods() {
            PointcutExpression expr = PointcutExpression.parse("execution(* nonExistent*(..))");
            
            assertFalse(expr.couldMatch(TestClass.class));
        }
    }
    
    @Nested
    @DisplayName("Parse Error Tests")
    class ParseErrorTests {
        
        @Test
        @DisplayName("Should handle null expression")
        void shouldHandleNullExpression() {
            PointcutExpression expr = PointcutExpression.parse(null);
            
            assertNotNull(expr);
            // Should match nothing or everything depending on implementation
        }
        
        @Test
        @DisplayName("Should handle empty expression")
        void shouldHandleEmptyExpression() {
            PointcutExpression expr = PointcutExpression.parse("");
            
            assertNotNull(expr);
        }
    }
    
    @Nested
    @DisplayName("ToString Tests")
    class ToStringTests {
        
        @Test
        @DisplayName("Should return meaningful toString")
        void shouldReturnMeaningfulToString() {
            PointcutExpression expr = PointcutExpression.parse("execution(* test*(..))");
            
            String result = expr.toString();
            
            assertNotNull(result);
        }
    }
}
