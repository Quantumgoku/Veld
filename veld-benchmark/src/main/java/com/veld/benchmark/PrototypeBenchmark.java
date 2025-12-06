/*
 * Copyright 2024 Veld Framework
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.veld.benchmark;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.veld.benchmark.common.ComplexService;
import com.veld.benchmark.common.Service;
import com.veld.benchmark.dagger.DaggerPrototypeComponent;
import com.veld.benchmark.dagger.PrototypeComponent;
import com.veld.benchmark.guice.GuicePrototypeModule;
import com.veld.benchmark.spring.SpringPrototypeConfig;
import com.veld.benchmark.veld.VeldPrototypeService;
import com.veld.runtime.VeldContainer;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.util.concurrent.TimeUnit;

/**
 * Benchmarks measuring prototype (new instance) creation time.
 * 
 * This measures the time to create new instances of components,
 * which is important for request-scoped dependencies in web applications.
 */
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@State(Scope.Benchmark)
@Fork(value = 2, jvmArgs = {"-Xms512m", "-Xmx512m"})
@Warmup(iterations = 3, time = 1)
@Measurement(iterations = 5, time = 1)
public class PrototypeBenchmark {
    
    private VeldContainer veldContainer;
    private AnnotationConfigApplicationContext springContext;
    private Injector guiceInjector;
    private PrototypeComponent daggerComponent;
    
    // Pre-computed index for fast access
    private int prototypeIndex;
    
    @Setup(Level.Trial)
    public void setup() {
        veldContainer = new VeldContainer();
        springContext = new AnnotationConfigApplicationContext(SpringPrototypeConfig.class);
        guiceInjector = Guice.createInjector(new GuicePrototypeModule());
        daggerComponent = DaggerPrototypeComponent.create();
        
        // Pre-compute index for fast benchmarks
        prototypeIndex = veldContainer.indexFor(VeldPrototypeService.class);
    }
    
    @TearDown(Level.Trial)
    public void teardown() {
        if (veldContainer != null) {
            veldContainer.close();
        }
        if (springContext != null) {
            springContext.close();
        }
    }
    
    // ==================== SIMPLE PROTOTYPE ====================
    
    @Benchmark
    public void veldPrototypeSimple(Blackhole bh) {
        Service service = veldContainer.get(VeldPrototypeService.class);
        bh.consume(service);
    }
    
    @Benchmark
    public void veldFastPrototypeSimple(Blackhole bh) {
        Service service = veldContainer.fastGet(prototypeIndex);
        bh.consume(service);
    }
    
    @Benchmark
    public void springPrototypeSimple(Blackhole bh) {
        Service service = springContext.getBean("prototypeSimpleService", Service.class);
        bh.consume(service);
    }
    
    @Benchmark
    public void guicePrototypeSimple(Blackhole bh) {
        Service service = guiceInjector.getInstance(Service.class);
        bh.consume(service);
    }
    
    @Benchmark
    public void daggerPrototypeSimple(Blackhole bh) {
        Service service = daggerComponent.simpleService();
        bh.consume(service);
    }
    
    // ==================== COMPLEX PROTOTYPE ====================
    
    @Benchmark
    public void springPrototypeComplex(Blackhole bh) {
        ComplexService service = springContext.getBean("prototypeComplexService", ComplexService.class);
        bh.consume(service);
    }
    
    @Benchmark
    public void guicePrototypeComplex(Blackhole bh) {
        ComplexService service = guiceInjector.getInstance(ComplexService.class);
        bh.consume(service);
    }
    
    @Benchmark
    public void daggerPrototypeComplex(Blackhole bh) {
        ComplexService service = daggerComponent.complexService();
        bh.consume(service);
    }
}
