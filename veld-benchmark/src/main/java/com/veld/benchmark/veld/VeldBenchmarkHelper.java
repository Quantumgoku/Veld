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

import com.veld.benchmark.common.*;
import com.veld.runtime.VeldContainer;

/**
 * Helper to create VeldContainer with manually registered components for benchmarks.
 * This simulates what the annotation processor would generate.
 */
public class VeldBenchmarkHelper {
    
    private VeldBenchmarkHelper() {}
    
    /**
     * Creates a VeldContainer with simple service dependencies.
     */
    public static VeldContainer createSimpleContainer() {
        ManualVeldRegistry registry = new ManualVeldRegistry()
            // Logger
            .singleton(Logger.class, "logger", c -> new VeldLogger())
            // SimpleService
            .singleton(VeldSimpleService.class, "simpleService", c -> 
                new VeldSimpleService(c.get(Logger.class)));
        
        return new VeldContainer(registry);
    }
    
    /**
     * Creates a VeldContainer with complex service dependencies.
     */
    public static VeldContainer createComplexContainer() {
        ManualVeldRegistry registry = new ManualVeldRegistry()
            // Logger
            .singleton(Logger.class, "logger", c -> new VeldLogger())
            // Repository
            .singleton(Repository.class, "repository", c -> new VeldRepository())
            // Validator
            .singleton(Validator.class, "validator", c -> new VeldValidator())
            // ComplexService  
            .singleton(VeldComplexService.class, "complexService", c ->
                new VeldComplexService(
                    c.get(Repository.class),
                    c.get(Logger.class),
                    c.get(Validator.class)
                ));
        
        return new VeldContainer(registry);
    }
    
    /**
     * Creates a VeldContainer with prototype service for throughput testing.
     */
    public static VeldContainer createPrototypeContainer() {
        ManualVeldRegistry registry = new ManualVeldRegistry()
            // Logger - singleton
            .singleton(Logger.class, "logger", c -> new VeldLogger())
            // SimpleService - prototype (new instance each time)
            .prototype(VeldSimpleService.class, "simpleService", c -> 
                new VeldSimpleService(c.get(Logger.class)));
        
        return new VeldContainer(registry);
    }
}
