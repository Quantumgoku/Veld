package com.veld.runtime.event;

import com.veld.annotation.Subscribe;
import org.junit.jupiter.api.*;

import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for EventSubscriber.
 */
@DisplayName("EventSubscriber Tests")
class EventSubscriberTest {
    
    static class TestEvent extends Event {
        TestEvent(Object source) {
            super(source);
        }
    }
    
    static class TestSubscriber {
        boolean called = false;
        
        @Subscribe(priority = 5, async = true, filter = "test", catchExceptions = true)
        public void onEvent(TestEvent event) {
            called = true;
        }
        
        @Subscribe
        public void simpleHandler(TestEvent event) {
            called = true;
        }
        
        public void throwingHandler(TestEvent event) throws Exception {
            throw new RuntimeException("Test error");
        }
    }
    
    private TestSubscriber subscriber;
    private Method method;
    private Method simpleMethod;
    private Method throwingMethod;
    
    @BeforeEach
    void setUp() throws Exception {
        subscriber = new TestSubscriber();
        method = TestSubscriber.class.getDeclaredMethod("onEvent", TestEvent.class);
        simpleMethod = TestSubscriber.class.getDeclaredMethod("simpleHandler", TestEvent.class);
        throwingMethod = TestSubscriber.class.getDeclaredMethod("throwingHandler", TestEvent.class);
    }
    
    @Nested
    @DisplayName("Construction Tests")
    class ConstructionTests {
        
        @Test
        @DisplayName("Should create subscriber with all properties")
        void shouldCreateSubscriberWithAllProperties() {
            EventSubscriber eventSubscriber = new EventSubscriber(
                subscriber, method, TestEvent.class, true, 5, "test", true);
            
            assertSame(subscriber, eventSubscriber.getTarget());
            assertSame(method, eventSubscriber.getMethod());
            assertEquals(TestEvent.class, eventSubscriber.getEventType());
            assertTrue(eventSubscriber.isAsync());
            assertEquals(5, eventSubscriber.getPriority());
            assertEquals("test", eventSubscriber.getFilter());
            assertTrue(eventSubscriber.isCatchExceptions());
        }
    }
    
    @Nested
    @DisplayName("Filter Tests")
    class FilterTests {
        
        @Test
        @DisplayName("Should detect filter presence")
        void shouldDetectFilterPresence() {
            EventSubscriber withFilter = new EventSubscriber(
                subscriber, method, TestEvent.class, false, 0, "condition", false);
            EventSubscriber withoutFilter = new EventSubscriber(
                subscriber, method, TestEvent.class, false, 0, "", false);
            EventSubscriber nullFilter = new EventSubscriber(
                subscriber, method, TestEvent.class, false, 0, null, false);
            
            assertTrue(withFilter.hasFilter());
            assertFalse(withoutFilter.hasFilter());
            assertFalse(nullFilter.hasFilter());
        }
    }
    
    @Nested
    @DisplayName("Invocation Tests")
    class InvocationTests {
        
        @Test
        @DisplayName("Should invoke handler method")
        void shouldInvokeHandlerMethod() throws Exception {
            EventSubscriber eventSubscriber = new EventSubscriber(
                subscriber, simpleMethod, TestEvent.class, false, 0, "", false);
            
            eventSubscriber.invoke(new TestEvent(this));
            
            assertTrue(subscriber.called);
        }
        
        @Test
        @DisplayName("Should wrap exceptions in RuntimeException")
        void shouldWrapExceptionsInRuntimeException() {
            EventSubscriber eventSubscriber = new EventSubscriber(
                subscriber, throwingMethod, TestEvent.class, false, 0, "", false);
            
            assertThrows(RuntimeException.class, () -> 
                eventSubscriber.invoke(new TestEvent(this)));
        }
    }
    
    @Nested
    @DisplayName("Comparison Tests")
    class ComparisonTests {
        
        @Test
        @DisplayName("Should compare by priority")
        void shouldCompareByPriority() {
            EventSubscriber high = new EventSubscriber(
                subscriber, method, TestEvent.class, false, 10, "", false);
            EventSubscriber low = new EventSubscriber(
                subscriber, method, TestEvent.class, false, 1, "", false);
            
            assertTrue(high.compareTo(low) < 0);
            assertTrue(low.compareTo(high) > 0);
        }
        
        @Test
        @DisplayName("Should be equal for same priority")
        void shouldBeEqualForSamePriority() {
            EventSubscriber s1 = new EventSubscriber(
                subscriber, method, TestEvent.class, false, 5, "", false);
            EventSubscriber s2 = new EventSubscriber(
                subscriber, method, TestEvent.class, false, 5, "", false);
            
            assertEquals(0, s1.compareTo(s2));
        }
    }
    
    @Nested
    @DisplayName("ToString Tests")
    class ToStringTests {
        
        @Test
        @DisplayName("Should return meaningful toString")
        void shouldReturnMeaningfulToString() {
            EventSubscriber eventSubscriber = new EventSubscriber(
                subscriber, method, TestEvent.class, true, 5, "filter", true);
            
            String result = eventSubscriber.toString();
            
            assertNotNull(result);
            assertTrue(result.contains("EventSubscriber"));
            assertTrue(result.contains("onEvent"));
        }
    }
}
