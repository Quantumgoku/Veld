package com.veld.runtime.condition;

import org.junit.jupiter.api.*;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for PropertyCondition.
 */
@DisplayName("PropertyCondition Tests")
class PropertyConditionTest {
    
    @Mock
    private ConditionContext mockContext;
    
    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }
    
    @Nested
    @DisplayName("Property Existence Tests")
    class PropertyExistenceTests {
        
        @Test
        @DisplayName("Should match when system property exists")
        void shouldMatchWhenSystemPropertyExists() {
            System.setProperty("test.condition.property", "value");
            try {
                PropertyCondition condition = new PropertyCondition(
                    "test.condition.property", "", false);
                
                assertTrue(condition.matches(mockContext));
            } finally {
                System.clearProperty("test.condition.property");
            }
        }
        
        @Test
        @DisplayName("Should not match when property does not exist")
        void shouldNotMatchWhenPropertyDoesNotExist() {
            PropertyCondition condition = new PropertyCondition(
                "nonexistent.property", "", false);
            
            assertFalse(condition.matches(mockContext));
        }
    }
    
    @Nested
    @DisplayName("Property Value Tests")
    class PropertyValueTests {
        
        @Test
        @DisplayName("Should match when property has expected value")
        void shouldMatchWhenPropertyHasExpectedValue() {
            System.setProperty("test.value.property", "expected");
            try {
                PropertyCondition condition = new PropertyCondition(
                    "test.value.property", "expected", false);
                
                assertTrue(condition.matches(mockContext));
            } finally {
                System.clearProperty("test.value.property");
            }
        }
        
        @Test
        @DisplayName("Should not match when property has different value")
        void shouldNotMatchWhenPropertyHasDifferentValue() {
            System.setProperty("test.value.property", "actual");
            try {
                PropertyCondition condition = new PropertyCondition(
                    "test.value.property", "expected", false);
                
                assertFalse(condition.matches(mockContext));
            } finally {
                System.clearProperty("test.value.property");
            }
        }
    }
    
    @Nested
    @DisplayName("Match If Missing Tests")
    class MatchIfMissingTests {
        
        @Test
        @DisplayName("Should match missing property when matchIfMissing is true")
        void shouldMatchMissingPropertyWhenMatchIfMissingIsTrue() {
            PropertyCondition condition = new PropertyCondition(
                "nonexistent.property", "", true);
            
            assertTrue(condition.matches(mockContext));
        }
        
        @Test
        @DisplayName("Should not match missing property when matchIfMissing is false")
        void shouldNotMatchMissingPropertyWhenMatchIfMissingIsFalse() {
            PropertyCondition condition = new PropertyCondition(
                "nonexistent.property", "", false);
            
            assertFalse(condition.matches(mockContext));
        }
    }
    
    @Nested
    @DisplayName("Environment Variable Tests")
    class EnvironmentVariableTests {
        
        @Test
        @DisplayName("Should match PATH environment variable")
        void shouldMatchPathEnvironmentVariable() {
            PropertyCondition condition = new PropertyCondition("PATH", "", false);
            
            assertTrue(condition.matches(mockContext));
        }
    }
}
