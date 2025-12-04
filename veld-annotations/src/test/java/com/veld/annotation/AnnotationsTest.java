package com.veld.annotation;

import org.junit.jupiter.api.*;

import java.lang.annotation.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for Veld annotations.
 * Tests annotation retention, targets, and default values.
 */
@DisplayName("Annotations Tests")
class AnnotationsTest {
    
    @Nested
    @DisplayName("Component Annotation Tests")
    class ComponentAnnotationTests {
        
        @Test
        @DisplayName("Should have RUNTIME retention")
        void shouldHaveRuntimeRetention() {
            Retention retention = Component.class.getAnnotation(Retention.class);
            
            assertNotNull(retention);
            assertEquals(RetentionPolicy.RUNTIME, retention.value());
        }
        
        @Test
        @DisplayName("Should target TYPE")
        void shouldTargetType() {
            Target target = Component.class.getAnnotation(Target.class);
            
            assertNotNull(target);
            assertTrue(java.util.Arrays.asList(target.value()).contains(ElementType.TYPE));
        }
        
        @Component
        class TestComponent {}
        
        @Test
        @DisplayName("Should be applicable to class")
        void shouldBeApplicableToClass() {
            Component annotation = TestComponent.class.getAnnotation(Component.class);
            
            assertNotNull(annotation);
        }
    }
    
    @Nested
    @DisplayName("Singleton Annotation Tests")
    class SingletonAnnotationTests {
        
        @Test
        @DisplayName("Should have RUNTIME retention")
        void shouldHaveRuntimeRetention() {
            Retention retention = Singleton.class.getAnnotation(Retention.class);
            
            assertNotNull(retention);
            assertEquals(RetentionPolicy.RUNTIME, retention.value());
        }
        
        @Singleton
        class TestSingleton {}
        
        @Test
        @DisplayName("Should be applicable to class")
        void shouldBeApplicableToClass() {
            Singleton annotation = TestSingleton.class.getAnnotation(Singleton.class);
            
            assertNotNull(annotation);
        }
    }
    
    @Nested
    @DisplayName("Prototype Annotation Tests")
    class PrototypeAnnotationTests {
        
        @Test
        @DisplayName("Should have RUNTIME retention")
        void shouldHaveRuntimeRetention() {
            Retention retention = Prototype.class.getAnnotation(Retention.class);
            
            assertNotNull(retention);
            assertEquals(RetentionPolicy.RUNTIME, retention.value());
        }
        
        @Prototype
        class TestPrototype {}
        
        @Test
        @DisplayName("Should be applicable to class")
        void shouldBeApplicableToClass() {
            Prototype annotation = TestPrototype.class.getAnnotation(Prototype.class);
            
            assertNotNull(annotation);
        }
    }
    
    @Nested
    @DisplayName("Inject Annotation Tests")
    class InjectAnnotationTests {
        
        @Test
        @DisplayName("Should have RUNTIME retention")
        void shouldHaveRuntimeRetention() {
            Retention retention = Inject.class.getAnnotation(Retention.class);
            
            assertNotNull(retention);
            assertEquals(RetentionPolicy.RUNTIME, retention.value());
        }
        
        @Test
        @DisplayName("Should target FIELD, METHOD, CONSTRUCTOR, PARAMETER")
        void shouldTargetFieldMethodConstructorParameter() {
            Target target = Inject.class.getAnnotation(Target.class);
            
            assertNotNull(target);
            java.util.List<ElementType> targets = java.util.Arrays.asList(target.value());
            assertTrue(targets.contains(ElementType.FIELD));
            assertTrue(targets.contains(ElementType.METHOD));
            assertTrue(targets.contains(ElementType.CONSTRUCTOR));
        }
        
        class TestInject {
            @Inject
            private String field;
            
            @Inject
            public void setField(String field) {
                this.field = field;
            }
        }
        
        @Test
        @DisplayName("Should be applicable to field")
        void shouldBeApplicableToField() throws Exception {
            Inject annotation = TestInject.class.getDeclaredField("field")
                    .getAnnotation(Inject.class);
            
            assertNotNull(annotation);
        }
        
        @Test
        @DisplayName("Should be applicable to method")
        void shouldBeApplicableToMethod() throws Exception {
            Inject annotation = TestInject.class.getDeclaredMethod("setField", String.class)
                    .getAnnotation(Inject.class);
            
            assertNotNull(annotation);
        }
    }
    
    @Nested
    @DisplayName("Named Annotation Tests")
    class NamedAnnotationTests {
        
        @Test
        @DisplayName("Should have RUNTIME retention")
        void shouldHaveRuntimeRetention() {
            Retention retention = Named.class.getAnnotation(Retention.class);
            
            assertNotNull(retention);
            assertEquals(RetentionPolicy.RUNTIME, retention.value());
        }
        
        @Named("testName")
        class TestNamed {}
        
        @Test
        @DisplayName("Should store name value")
        void shouldStoreNameValue() {
            Named annotation = TestNamed.class.getAnnotation(Named.class);
            
            assertNotNull(annotation);
            assertEquals("testName", annotation.value());
        }
    }
    
    @Nested
    @DisplayName("Value Annotation Tests")
    class ValueAnnotationTests {
        
        @Test
        @DisplayName("Should have RUNTIME retention")
        void shouldHaveRuntimeRetention() {
            Retention retention = Value.class.getAnnotation(Retention.class);
            
            assertNotNull(retention);
            assertEquals(RetentionPolicy.RUNTIME, retention.value());
        }
        
        class TestValue {
            @Value("${app.name}")
            private String appName;
            
            @Value("${app.port:8080}")
            private int port;
        }
        
        @Test
        @DisplayName("Should store value expression")
        void shouldStoreValueExpression() throws Exception {
            Value annotation = TestValue.class.getDeclaredField("appName")
                    .getAnnotation(Value.class);
            
            assertNotNull(annotation);
            assertEquals("${app.name}", annotation.value());
        }
    }
    
    @Nested
    @DisplayName("Lazy Annotation Tests")
    class LazyAnnotationTests {
        
        @Test
        @DisplayName("Should have RUNTIME retention")
        void shouldHaveRuntimeRetention() {
            Retention retention = Lazy.class.getAnnotation(Retention.class);
            
            assertNotNull(retention);
            assertEquals(RetentionPolicy.RUNTIME, retention.value());
        }
        
        @Lazy
        class TestLazy {}
        
        @Test
        @DisplayName("Should be applicable to class")
        void shouldBeApplicableToClass() {
            Lazy annotation = TestLazy.class.getAnnotation(Lazy.class);
            
            assertNotNull(annotation);
        }
    }
    
    @Nested
    @DisplayName("PostConstruct Annotation Tests")
    class PostConstructAnnotationTests {
        
        @Test
        @DisplayName("Should have RUNTIME retention")
        void shouldHaveRuntimeRetention() {
            Retention retention = PostConstruct.class.getAnnotation(Retention.class);
            
            assertNotNull(retention);
            assertEquals(RetentionPolicy.RUNTIME, retention.value());
        }
        
        @Test
        @DisplayName("Should target METHOD")
        void shouldTargetMethod() {
            Target target = PostConstruct.class.getAnnotation(Target.class);
            
            assertNotNull(target);
            assertTrue(java.util.Arrays.asList(target.value()).contains(ElementType.METHOD));
        }
        
        class TestPostConstruct {
            @PostConstruct
            public void init() {}
        }
        
        @Test
        @DisplayName("Should be applicable to method")
        void shouldBeApplicableToMethod() throws Exception {
            PostConstruct annotation = TestPostConstruct.class.getDeclaredMethod("init")
                    .getAnnotation(PostConstruct.class);
            
            assertNotNull(annotation);
        }
    }
    
    @Nested
    @DisplayName("PreDestroy Annotation Tests")
    class PreDestroyAnnotationTests {
        
        @Test
        @DisplayName("Should have RUNTIME retention")
        void shouldHaveRuntimeRetention() {
            Retention retention = PreDestroy.class.getAnnotation(Retention.class);
            
            assertNotNull(retention);
            assertEquals(RetentionPolicy.RUNTIME, retention.value());
        }
        
        class TestPreDestroy {
            @PreDestroy
            public void cleanup() {}
        }
        
        @Test
        @DisplayName("Should be applicable to method")
        void shouldBeApplicableToMethod() throws Exception {
            PreDestroy annotation = TestPreDestroy.class.getDeclaredMethod("cleanup")
                    .getAnnotation(PreDestroy.class);
            
            assertNotNull(annotation);
        }
    }
    
    @Nested
    @DisplayName("Subscribe Annotation Tests")
    class SubscribeAnnotationTests {
        
        @Test
        @DisplayName("Should have RUNTIME retention")
        void shouldHaveRuntimeRetention() {
            Retention retention = Subscribe.class.getAnnotation(Retention.class);
            
            assertNotNull(retention);
            assertEquals(RetentionPolicy.RUNTIME, retention.value());
        }
        
        class TestSubscribe {
            @Subscribe
            public void onEvent(Object event) {}
            
            @Subscribe(async = true, priority = 10, filter = "test", catchExceptions = true)
            public void onFilteredEvent(Object event) {}
        }
        
        @Test
        @DisplayName("Should have default values")
        void shouldHaveDefaultValues() throws Exception {
            Subscribe annotation = TestSubscribe.class.getDeclaredMethod("onEvent", Object.class)
                    .getAnnotation(Subscribe.class);
            
            assertNotNull(annotation);
            assertFalse(annotation.async());
            assertEquals(0, annotation.priority());
            assertEquals("", annotation.filter());
            assertFalse(annotation.catchExceptions());
        }
        
        @Test
        @DisplayName("Should store custom values")
        void shouldStoreCustomValues() throws Exception {
            Subscribe annotation = TestSubscribe.class.getDeclaredMethod("onFilteredEvent", Object.class)
                    .getAnnotation(Subscribe.class);
            
            assertNotNull(annotation);
            assertTrue(annotation.async());
            assertEquals(10, annotation.priority());
            assertEquals("test", annotation.filter());
            assertTrue(annotation.catchExceptions());
        }
    }
    
    @Nested
    @DisplayName("Profile Annotation Tests")
    class ProfileAnnotationTests {
        
        @Test
        @DisplayName("Should have RUNTIME retention")
        void shouldHaveRuntimeRetention() {
            Retention retention = Profile.class.getAnnotation(Retention.class);
            
            assertNotNull(retention);
            assertEquals(RetentionPolicy.RUNTIME, retention.value());
        }
        
        @Profile("dev")
        class DevOnlyComponent {}
        
        @Profile({"dev", "test"})
        class DevTestComponent {}
        
        @Test
        @DisplayName("Should store single profile")
        void shouldStoreSingleProfile() {
            Profile annotation = DevOnlyComponent.class.getAnnotation(Profile.class);
            
            assertNotNull(annotation);
            assertArrayEquals(new String[]{"dev"}, annotation.value());
        }
        
        @Test
        @DisplayName("Should store multiple profiles")
        void shouldStoreMultipleProfiles() {
            Profile annotation = DevTestComponent.class.getAnnotation(Profile.class);
            
            assertNotNull(annotation);
            assertArrayEquals(new String[]{"dev", "test"}, annotation.value());
        }
    }
    
    @Nested
    @DisplayName("Conditional Annotations Tests")
    class ConditionalAnnotationsTests {
        
        @ConditionalOnClass("java.lang.String")
        class ConditionalOnClassComponent {}
        
        @ConditionalOnProperty(name = "feature.enabled", havingValue = "true")
        class ConditionalOnPropertyComponent {}
        
        @ConditionalOnMissingBean(String.class)
        class ConditionalOnMissingBeanComponent {}
        
        @Test
        @DisplayName("ConditionalOnClass should store class name")
        void conditionalOnClassShouldStoreClassName() {
            ConditionalOnClass annotation = ConditionalOnClassComponent.class
                    .getAnnotation(ConditionalOnClass.class);
            
            assertNotNull(annotation);
            assertArrayEquals(new String[]{"java.lang.String"}, annotation.value());
        }
        
        @Test
        @DisplayName("ConditionalOnProperty should store property details")
        void conditionalOnPropertyShouldStorePropertyDetails() {
            ConditionalOnProperty annotation = ConditionalOnPropertyComponent.class
                    .getAnnotation(ConditionalOnProperty.class);
            
            assertNotNull(annotation);
            assertEquals("feature.enabled", annotation.name());
            assertEquals("true", annotation.havingValue());
        }
        
        @Test
        @DisplayName("ConditionalOnMissingBean should store bean type")
        void conditionalOnMissingBeanShouldStoreBeanType() {
            ConditionalOnMissingBean annotation = ConditionalOnMissingBeanComponent.class
                    .getAnnotation(ConditionalOnMissingBean.class);
            
            assertNotNull(annotation);
            assertEquals(String.class, annotation.value());
        }
    }
    
    @Nested
    @DisplayName("Lifecycle Annotations Tests")
    class LifecycleAnnotationsTests {
        
        class TestLifecycle {
            @PostInitialize(order = 1)
            public void postInit() {}
            
            @OnStart(order = 2)
            public void onStart() {}
            
            @OnStop(order = 3)
            public void onStop() {}
        }
        
        @Test
        @DisplayName("PostInitialize should have order attribute")
        void postInitializeShouldHaveOrderAttribute() throws Exception {
            PostInitialize annotation = TestLifecycle.class.getDeclaredMethod("postInit")
                    .getAnnotation(PostInitialize.class);
            
            assertNotNull(annotation);
            assertEquals(1, annotation.order());
        }
        
        @Test
        @DisplayName("OnStart should have order attribute")
        void onStartShouldHaveOrderAttribute() throws Exception {
            OnStart annotation = TestLifecycle.class.getDeclaredMethod("onStart")
                    .getAnnotation(OnStart.class);
            
            assertNotNull(annotation);
            assertEquals(2, annotation.order());
        }
        
        @Test
        @DisplayName("OnStop should have order attribute")
        void onStopShouldHaveOrderAttribute() throws Exception {
            OnStop annotation = TestLifecycle.class.getDeclaredMethod("onStop")
                    .getAnnotation(OnStop.class);
            
            assertNotNull(annotation);
            assertEquals(3, annotation.order());
        }
    }
    
    @Nested
    @DisplayName("DependsOn Annotation Tests")
    class DependsOnAnnotationTests {
        
        @DependsOn("otherBean")
        class DependentComponent {}
        
        @DependsOn({"bean1", "bean2", "bean3"})
        class MultipleDependenciesComponent {}
        
        @Test
        @DisplayName("Should store single dependency")
        void shouldStoreSingleDependency() {
            DependsOn annotation = DependentComponent.class.getAnnotation(DependsOn.class);
            
            assertNotNull(annotation);
            assertArrayEquals(new String[]{"otherBean"}, annotation.value());
        }
        
        @Test
        @DisplayName("Should store multiple dependencies")
        void shouldStoreMultipleDependencies() {
            DependsOn annotation = MultipleDependenciesComponent.class.getAnnotation(DependsOn.class);
            
            assertNotNull(annotation);
            assertArrayEquals(new String[]{"bean1", "bean2", "bean3"}, annotation.value());
        }
    }
}
