package com.veld.runtime.condition;

import org.junit.jupiter.api.*;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Tests for ProfileCondition.
 */
@DisplayName("ProfileCondition Tests")
class ProfileConditionTest {
    
    @Mock
    private ConditionContext mockContext;
    
    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        // Clear profile property before each test
        System.clearProperty("veld.profiles.active");
    }
    
    @AfterEach
    void tearDown() {
        System.clearProperty("veld.profiles.active");
    }
    
    @Nested
    @DisplayName("Profile Match Tests")
    class ProfileMatchTests {
        
        @Test
        @DisplayName("Should match when profile is active")
        void shouldMatchWhenProfileIsActive() {
            System.setProperty("veld.profiles.active", "dev");
            ProfileCondition condition = new ProfileCondition("dev");
            
            assertTrue(condition.matches(mockContext));
        }
        
        @Test
        @DisplayName("Should match when one of multiple profiles is active")
        void shouldMatchWhenOneOfMultipleProfilesIsActive() {
            System.setProperty("veld.profiles.active", "dev,test");
            ProfileCondition condition = new ProfileCondition("test");
            
            assertTrue(condition.matches(mockContext));
        }
        
        @Test
        @DisplayName("Should not match when profile is not active")
        void shouldNotMatchWhenProfileIsNotActive() {
            System.setProperty("veld.profiles.active", "prod");
            ProfileCondition condition = new ProfileCondition("dev");
            
            assertFalse(condition.matches(mockContext));
        }
        
        @Test
        @DisplayName("Should not match when no profiles are active")
        void shouldNotMatchWhenNoProfilesAreActive() {
            ProfileCondition condition = new ProfileCondition("dev");
            
            assertFalse(condition.matches(mockContext));
        }
    }
    
    @Nested
    @DisplayName("Multiple Required Profiles Tests")
    class MultipleRequiredProfilesTests {
        
        @Test
        @DisplayName("Should match when all required profiles are active")
        void shouldMatchWhenAllRequiredProfilesAreActive() {
            System.setProperty("veld.profiles.active", "dev,test,local");
            ProfileCondition condition = new ProfileCondition("dev", "test");
            
            assertTrue(condition.matches(mockContext));
        }
        
        @Test
        @DisplayName("Should not match when not all required profiles are active")
        void shouldNotMatchWhenNotAllRequiredProfilesAreActive() {
            System.setProperty("veld.profiles.active", "dev");
            ProfileCondition condition = new ProfileCondition("dev", "test");
            
            assertFalse(condition.matches(mockContext));
        }
    }
    
    @Nested
    @DisplayName("Negated Profile Tests")
    class NegatedProfileTests {
        
        @Test
        @DisplayName("Should match negated profile when profile is not active")
        void shouldMatchNegatedProfileWhenProfileIsNotActive() {
            System.setProperty("veld.profiles.active", "dev");
            ProfileCondition condition = new ProfileCondition("!prod");
            
            assertTrue(condition.matches(mockContext));
        }
        
        @Test
        @DisplayName("Should not match negated profile when profile is active")
        void shouldNotMatchNegatedProfileWhenProfileIsActive() {
            System.setProperty("veld.profiles.active", "prod");
            ProfileCondition condition = new ProfileCondition("!prod");
            
            assertFalse(condition.matches(mockContext));
        }
    }
    
    @Nested
    @DisplayName("Empty Profile Tests")
    class EmptyProfileTests {
        
        @Test
        @DisplayName("Should match when no profiles required")
        void shouldMatchWhenNoProfilesRequired() {
            ProfileCondition condition = new ProfileCondition();
            
            assertTrue(condition.matches(mockContext));
        }
    }
}
