package com.veld.runtime.condition;

import org.junit.jupiter.api.*;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Tests for MissingBeanCondition.
 */
@DisplayName("MissingBeanCondition Tests")
class MissingBeanConditionTest {
    
    @Mock
    private ConditionContext mockContext;
    
    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }
    
    @Nested
    @DisplayName("Bean Type Tests")
    class BeanTypeTests {
        
        @Test
        @DisplayName("Should match when bean type is missing")
        void shouldMatchWhenBeanTypeIsMissing() {
            when(mockContext.containsBean(String.class)).thenReturn(false);
            MissingBeanCondition condition = new MissingBeanCondition(String.class);
            
            assertTrue(condition.matches(mockContext));
        }
        
        @Test
        @DisplayName("Should not match when bean type exists")
        void shouldNotMatchWhenBeanTypeExists() {
            when(mockContext.containsBean(String.class)).thenReturn(true);
            MissingBeanCondition condition = new MissingBeanCondition(String.class);
            
            assertFalse(condition.matches(mockContext));
        }
    }
    
    @Nested
    @DisplayName("Bean Name Tests")
    class BeanNameTests {
        
        @Test
        @DisplayName("Should match when bean name is missing")
        void shouldMatchWhenBeanNameIsMissing() {
            when(mockContext.containsBean("myBean")).thenReturn(false);
            MissingBeanCondition condition = new MissingBeanCondition("myBean");
            
            assertTrue(condition.matches(mockContext));
        }
        
        @Test
        @DisplayName("Should not match when bean name exists")
        void shouldNotMatchWhenBeanNameExists() {
            when(mockContext.containsBean("myBean")).thenReturn(true);
            MissingBeanCondition condition = new MissingBeanCondition("myBean");
            
            assertFalse(condition.matches(mockContext));
        }
    }
    
    @Nested
    @DisplayName("Combined Condition Tests")
    class CombinedConditionTests {
        
        @Test
        @DisplayName("Should match when both type and name are missing")
        void shouldMatchWhenBothTypeAndNameAreMissing() {
            when(mockContext.containsBean(String.class)).thenReturn(false);
            when(mockContext.containsBean("myBean")).thenReturn(false);
            MissingBeanCondition condition = new MissingBeanCondition(String.class, "myBean");
            
            assertTrue(condition.matches(mockContext));
        }
        
        @Test
        @DisplayName("Should not match when type exists")
        void shouldNotMatchWhenTypeExists() {
            when(mockContext.containsBean(String.class)).thenReturn(true);
            when(mockContext.containsBean("myBean")).thenReturn(false);
            MissingBeanCondition condition = new MissingBeanCondition(String.class, "myBean");
            
            assertFalse(condition.matches(mockContext));
        }
        
        @Test
        @DisplayName("Should not match when name exists")
        void shouldNotMatchWhenNameExists() {
            when(mockContext.containsBean(String.class)).thenReturn(false);
            when(mockContext.containsBean("myBean")).thenReturn(true);
            MissingBeanCondition condition = new MissingBeanCondition(String.class, "myBean");
            
            assertFalse(condition.matches(mockContext));
        }
    }
}
