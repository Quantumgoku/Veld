/*
 * Copyright 2024 Veld Framework
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.veld.benchmark.veld;

import com.veld.runtime.ComponentFactory;
import com.veld.runtime.ComponentRegistry;
import com.veld.runtime.Scope;
import com.veld.runtime.VeldContainer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Manual ComponentRegistry implementation for benchmarks.
 * Allows programmatic registration of components without annotation processing.
 */
public class ManualVeldRegistry implements ComponentRegistry {
    
    private final List<ComponentFactory<?>> factories = new ArrayList<>();
    private final Map<Class<?>, ComponentFactory<?>> byType = new HashMap<>();
    private final Map<String, ComponentFactory<?>> byName = new HashMap<>();
    
    /**
     * Registers a singleton component.
     */
    public <T> ManualVeldRegistry singleton(Class<T> type, Function<VeldContainer, T> creator) {
        return singleton(type, type.getSimpleName(), creator);
    }
    
    /**
     * Registers a singleton component with a name.
     */
    public <T> ManualVeldRegistry singleton(Class<T> type, String name, Function<VeldContainer, T> creator) {
        ComponentFactory<T> factory = new SimpleFactory<>(type, name, Scope.SINGLETON, creator);
        register(factory);
        return this;
    }
    
    /**
     * Registers a prototype component.
     */
    public <T> ManualVeldRegistry prototype(Class<T> type, Function<VeldContainer, T> creator) {
        return prototype(type, type.getSimpleName(), creator);
    }
    
    /**
     * Registers a prototype component with a name.
     */
    public <T> ManualVeldRegistry prototype(Class<T> type, String name, Function<VeldContainer, T> creator) {
        ComponentFactory<T> factory = new SimpleFactory<>(type, name, Scope.PROTOTYPE, creator);
        register(factory);
        return this;
    }
    
    private <T> void register(ComponentFactory<T> factory) {
        factories.add(factory);
        byType.put(factory.getComponentType(), factory);
        byName.put(factory.getComponentName(), factory);
    }
    
    @Override
    public List<ComponentFactory<?>> getAllFactories() {
        return new ArrayList<>(factories);
    }
    
    @Override
    @SuppressWarnings("unchecked")
    public <T> ComponentFactory<T> getFactory(Class<T> type) {
        return (ComponentFactory<T>) byType.get(type);
    }
    
    @Override
    public ComponentFactory<?> getFactory(String name) {
        return byName.get(name);
    }
    
    @Override
    @SuppressWarnings("unchecked")
    public <T> List<ComponentFactory<? extends T>> getFactoriesForType(Class<T> type) {
        return factories.stream()
                .filter(f -> type.isAssignableFrom(f.getComponentType()))
                .map(f -> (ComponentFactory<? extends T>) f)
                .collect(Collectors.toList());
    }
    
    /**
     * Simple ComponentFactory implementation.
     */
    private static class SimpleFactory<T> implements ComponentFactory<T> {
        private final Class<T> type;
        private final String name;
        private final Scope scope;
        private final Function<VeldContainer, T> creator;
        
        SimpleFactory(Class<T> type, String name, Scope scope, Function<VeldContainer, T> creator) {
            this.type = type;
            this.name = name;
            this.scope = scope;
            this.creator = creator;
        }
        
        @Override
        public Class<T> getComponentType() {
            return type;
        }
        
        @Override
        public String getComponentName() {
            return name;
        }
        
        @Override
        public Scope getScope() {
            return scope;
        }
        
        @Override
        public boolean isLazy() {
            return false;
        }
        
        @Override
        public T create(VeldContainer container) {
            return creator.apply(container);
        }
        
        @Override
        public void invokePostConstruct(T instance) {
            // No-op for benchmarks
        }
        
        @Override
        public void invokePreDestroy(T instance) {
            // No-op for benchmarks
        }
    }
}
