package com.veld.runtime.condition;

import org.junit.jupiter.api.*;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for ClassCondition.
 */
@DisplayName("ClassCondition Tests")
class ClassConditionTest {
    
    @Mock
    private ConditionContext mockContext;
    
    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }
    
    @Nested
    @DisplayName("Existing Class Tests")
    class ExistingClassTests {
        
        @Test
        @DisplayName("Should match when class exists")
        void shouldMatchWhenClassExists() {
            ClassCondition condition = new ClassCondition("java.lang.String");
            
            assertTrue(condition.matches(mockContext));
        }
        
        @Test
        @DisplayName("Should match for multiple existing classes")
        void shouldMatchForMultipleExistingClasses() {
            ClassCondition condition = new ClassCondition(
                "java.lang.String", "java.util.List", "java.util.Map");
            
            assertTrue(condition.matches(mockContext));
        }
    }
    
    @Nested
    @DisplayName("Missing Class Tests")
    class MissingClassTests {
        
        @Test
        @DisplayName("Should not match when class does not exist")
        void shouldNotMatchWhenClassDoesNotExist() {
            ClassCondition condition = new ClassCondition(
                "com.nonexistent.FakeClass");
            
            assertFalse(condition.matches(mockContext));
        }
        
        @Test
        @DisplayName("Should not match when any class is missing")
        void shouldNotMatchWhenAnyClassIsMissing() {
            ClassCondition condition = new ClassCondition(
                "java.lang.String", "com.nonexistent.FakeClass");
            
            assertFalse(condition.matches(mockContext));
        }
    }
    
    @Nested
    @DisplayName("Empty Classes Tests")
    class EmptyClassesTests {
        
        @Test
        @DisplayName("Should match when no classes specified")
        void shouldMatchWhenNoClassesSpecified() {
            ClassCondition condition = new ClassCondition();
            
            assertTrue(condition.matches(mockContext));
        }
    }
    
    @Nested
    @DisplayName("ToString Tests")
    class ToStringTests {
        
        @Test
        @DisplayName("Should return meaningful toString")
        void shouldReturnMeaningfulToString() {
            ClassCondition condition = new ClassCondition("java.lang.String");
            
            String result = condition.toString();
            
            assertNotNull(result);
            assertTrue(result.contains("ClassCondition"));
        }
    }
}
