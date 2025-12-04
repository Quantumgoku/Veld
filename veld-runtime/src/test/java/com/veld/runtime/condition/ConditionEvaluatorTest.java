package com.veld.runtime.condition;

import org.junit.jupiter.api.*;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Tests for ConditionEvaluator.
 */
@DisplayName("ConditionEvaluator Tests")
class ConditionEvaluatorTest {
    
    private ConditionEvaluator evaluator;
    
    @Mock
    private ConditionContext mockContext;
    
    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        evaluator = new ConditionEvaluator();
    }
    
    @Nested
    @DisplayName("Condition Registration Tests")
    class ConditionRegistrationTests {
        
        @Test
        @DisplayName("Should register condition")
        void shouldRegisterCondition() {
            Condition condition = ctx -> true;
            
            evaluator.registerCondition("testBean", condition);
            
            assertTrue(evaluator.hasConditions("testBean"));
        }
        
        @Test
        @DisplayName("Should register multiple conditions for same bean")
        void shouldRegisterMultipleConditionsForSameBean() {
            Condition condition1 = ctx -> true;
            Condition condition2 = ctx -> true;
            
            evaluator.registerCondition("testBean", condition1);
            evaluator.registerCondition("testBean", condition2);
            
            assertTrue(evaluator.hasConditions("testBean"));
        }
        
        @Test
        @DisplayName("Should report no conditions for unregistered bean")
        void shouldReportNoConditionsForUnregisteredBean() {
            assertFalse(evaluator.hasConditions("unknownBean"));
        }
    }
    
    @Nested
    @DisplayName("Condition Evaluation Tests")
    class ConditionEvaluationTests {
        
        @Test
        @DisplayName("Should return true when all conditions match")
        void shouldReturnTrueWhenAllConditionsMatch() {
            evaluator.registerCondition("testBean", ctx -> true);
            evaluator.registerCondition("testBean", ctx -> true);
            
            assertTrue(evaluator.shouldSkip("testBean", mockContext));
        }
        
        @Test
        @DisplayName("Should return false when any condition fails")
        void shouldReturnFalseWhenAnyConditionFails() {
            evaluator.registerCondition("testBean", ctx -> true);
            evaluator.registerCondition("testBean", ctx -> false);
            
            assertFalse(evaluator.shouldSkip("testBean", mockContext));
        }
        
        @Test
        @DisplayName("Should return true for bean without conditions")
        void shouldReturnTrueForBeanWithoutConditions() {
            assertTrue(evaluator.shouldSkip("unknownBean", mockContext));
        }
        
        @Test
        @DisplayName("Should pass context to conditions")
        void shouldPassContextToConditions() {
            Condition condition = mock(Condition.class);
            when(condition.matches(mockContext)).thenReturn(true);
            evaluator.registerCondition("testBean", condition);
            
            evaluator.shouldSkip("testBean", mockContext);
            
            verify(condition).matches(mockContext);
        }
    }
    
    @Nested
    @DisplayName("Bean Names Tests")
    class BeanNamesTests {
        
        @Test
        @DisplayName("Should return all beans with conditions")
        void shouldReturnAllBeansWithConditions() {
            evaluator.registerCondition("bean1", ctx -> true);
            evaluator.registerCondition("bean2", ctx -> true);
            
            Set<String> beanNames = evaluator.getBeansWithConditions();
            
            assertEquals(2, beanNames.size());
            assertTrue(beanNames.contains("bean1"));
            assertTrue(beanNames.contains("bean2"));
        }
        
        @Test
        @DisplayName("Should return empty set when no conditions registered")
        void shouldReturnEmptySetWhenNoConditionsRegistered() {
            Set<String> beanNames = evaluator.getBeansWithConditions();
            
            assertTrue(beanNames.isEmpty());
        }
    }
    
    @Nested
    @DisplayName("Clear Tests")
    class ClearTests {
        
        @Test
        @DisplayName("Should clear all conditions")
        void shouldClearAllConditions() {
            evaluator.registerCondition("bean1", ctx -> true);
            evaluator.registerCondition("bean2", ctx -> true);
            
            evaluator.clear();
            
            assertFalse(evaluator.hasConditions("bean1"));
            assertFalse(evaluator.hasConditions("bean2"));
        }
    }
}
