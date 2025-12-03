# Veld DI Framework

[![CI/CD](https://github.com/yasmramos/Veld/actions/workflows/ci.yml/badge.svg)](https://github.com/yasmramos/Veld/actions/workflows/ci.yml)
[![Java](https://img.shields.io/badge/Java-11%2B-orange)](https://openjdk.java.net/)
[![Maven](https://img.shields.io/badge/Maven-3.6%2B-blue)](https://maven.apache.org/)
[![License](https://img.shields.io/badge/License-Apache%202.0-green.svg)](LICENSE)
[![GitHub Release](https://img.shields.io/github/v/release/yasmramos/Veld)](https://github.com/yasmramos/Veld/releases)

A lightweight, compile-time Dependency Injection framework for Java using pure ASM bytecode generation. **Zero reflection, zero runtime overhead.**

## Features

- **Pure Bytecode Generation**: Uses ASM to generate optimized factory classes at compile time
- **Zero Reflection**: All dependency resolution happens via generated code, not reflection
- **JSR-330 Compatible**: Full support for `javax.inject.*` annotations
- **Jakarta Inject Compatible**: Full support for `jakarta.inject.*` annotations  
- **Mixed Annotations**: Use Veld, JSR-330, and Jakarta annotations together seamlessly
- **Scope Management**: Built-in Singleton and Prototype scopes
- **Lazy Initialization**: `@Lazy` for deferred component creation
- **Provider Injection**: `Provider<T>` for on-demand instance creation
- **Optional Injection**: `@Optional` and `Optional<T>` for graceful handling of missing dependencies
- **Interface-Based Injection**: Inject by interface, resolved to concrete implementations
- **Lifecycle Callbacks**: `@PostConstruct` and `@PreDestroy` support
- **Circular Dependency Detection**: Compile-time detection with clear error messages
- **Lightweight**: Minimal runtime footprint

## Quick Start

### 1. Add Dependencies

```xml
<dependencies>
    <!-- Veld Annotations -->
    <dependency>
        <groupId>com.veld</groupId>
        <artifactId>veld-annotations</artifactId>
        <version>1.0.0-alpha.3</version>
    </dependency>
    
    <!-- Veld Runtime -->
    <dependency>
        <groupId>com.veld</groupId>
        <artifactId>veld-runtime</artifactId>
        <version>1.0.0-alpha.3</version>
    </dependency>
    
    <!-- Veld Processor (compile-time only) -->
    <dependency>
        <groupId>com.veld</groupId>
        <artifactId>veld-processor</artifactId>
        <version>1.0.0-alpha.3</version>
        <scope>provided</scope>
    </dependency>
</dependencies>
```

### 2. Configure Annotation Processor

```xml
<build>
    <plugins>
        <plugin>
            <groupId>org.apache.maven.plugins</groupId>
            <artifactId>maven-compiler-plugin</artifactId>
            <version>3.11.0</version>
            <configuration>
                <source>11</source>
                <target>11</target>
                <annotationProcessorPaths>
                    <path>
                        <groupId>com.veld</groupId>
                        <artifactId>veld-processor</artifactId>
                        <version>1.0.0-alpha.3</version>
                    </path>
                </annotationProcessorPaths>
            </configuration>
        </plugin>
    </plugins>
</build>
```

### 3. Define Components

```java
import com.veld.annotation.Inject;
import com.veld.annotation.Singleton;

@Singleton
public class UserService {
    
    @Inject
    UserRepository userRepository;
    
    public User findUser(Long id) {
        return userRepository.findById(id);
    }
}
```

### 4. Use the Container

```java
import com.veld.runtime.VeldContainer;

public class Main {
    public static void main(String[] args) {
        VeldContainer container = new VeldContainer();
        
        try {
            UserService userService = container.get(UserService.class);
            User user = userService.findUser(1L);
        } finally {
            container.close();
        }
    }
}
```

## Annotation Support

Veld supports annotations from three sources, which can be mixed freely:

| Feature | Veld | JSR-330 (javax) | Jakarta |
|---------|------|-----------------|---------|
| Injection | `@Inject` | `@Inject` | `@Inject` |
| Singleton | `@Singleton` | `@Singleton` | `@Singleton` |
| Prototype | `@Prototype` | - | - |
| Lazy | `@Lazy` | - | - |
| Optional | `@Optional` | - | - |
| Qualifier | `@Named` | `@Named` | `@Named` |
| Provider | `Provider<T>` | `Provider<T>` | `Provider<T>` |
| Post-construct | `@PostConstruct` | - | - |
| Pre-destroy | `@PreDestroy` | - | - |

> **Note**: `@Singleton`, `@Prototype`, and `@Lazy` automatically imply `@Component`, so you don't need to add both.

### Example: Mixed Annotations

```java
@Singleton  // Veld annotation (implies @Component)
public class PaymentService {
    
    @javax.inject.Inject
    private LogService logService;
    
    @jakarta.inject.Inject
    public void setConfig(ConfigService config) {
        this.config = config;
    }
}
```

## Injection Types

### Constructor Injection

```java
@Singleton
public class OrderService {
    private final PaymentService paymentService;
    
    @Inject
    public OrderService(PaymentService paymentService) {
        this.paymentService = paymentService;
    }
}
```

### Field Injection

```java
@Singleton
public class UserService {
    @Inject
    UserRepository userRepository;  // Must be non-private (no reflection)
}
```

### Method Injection

```java
@Singleton
public class NotificationService {
    private EmailService emailService;
    
    @Inject
    public void setEmailService(EmailService emailService) {
        this.emailService = emailService;
    }
}
```

## Interface-Based Injection

```java
public interface IUserRepository {
    User findById(Long id);
}

@Singleton
public class UserRepositoryImpl implements IUserRepository {
    @Override
    public User findById(Long id) { /* ... */ }
}

@Singleton
public class UserService {
    @Inject
    IUserRepository userRepository;  // Injects UserRepositoryImpl
}
```

## Scopes

### Singleton (Default)

```java
@Singleton
public class ConfigService {
    // One instance shared across the container
}
```

### Prototype

```java
@Prototype
public class RequestContext {
    // New instance created for each injection
}
```

## Lazy Initialization

Components marked with `@Lazy` are not instantiated until first accessed:

```java
@Singleton
@Lazy
public class ExpensiveService {
    
    public ExpensiveService() {
        // Heavy initialization - only happens when first requested
        loadLargeDataset();
    }
}
```

## Provider Injection

Use `Provider<T>` for on-demand instance creation, especially useful with `@Prototype` components:

```java
@Singleton
public class ReportGenerator {
    
    @Inject
    Provider<RequestContext> contextProvider;
    
    public void generateReports() {
        // Each call creates a new RequestContext
        RequestContext ctx1 = contextProvider.get();
        RequestContext ctx2 = contextProvider.get();
        // ctx1 != ctx2
    }
}
```

Veld supports all three Provider types:
- `com.veld.runtime.Provider<T>`
- `javax.inject.Provider<T>`
- `jakarta.inject.Provider<T>`

## Optional Injection

Handle missing dependencies gracefully without throwing exceptions:

### Using @Optional Annotation

```java
@Singleton
public class MyService {
    
    @Inject
    @Optional
    CacheService cache;  // Will be null if CacheService is not registered
    
    public void doWork() {
        if (cache != null) {
            cache.put("key", "value");
        }
    }
}
```

### Using Optional<T> Wrapper

```java
@Singleton
public class MyService {
    
    @Inject
    java.util.Optional<MetricsService> metrics;  // Will be Optional.empty() if not found
    
    public void doWork() {
        metrics.ifPresent(m -> m.recordEvent("work.done"));
    }
}
```

### Container Methods

```java
VeldContainer container = new VeldContainer();

// Returns null if not found
CacheService cache = container.tryGet(CacheService.class);

// Returns Optional.empty() if not found
Optional<MetricsService> metrics = container.getOptional(MetricsService.class);
```

## Lifecycle Callbacks

```java
@Singleton
public class DatabaseService {
    
    @PostConstruct
    public void init() {
        // Called after dependency injection
        connection = createConnection();
    }
    
    @PreDestroy
    public void cleanup() {
        // Called when container closes
        connection.close();
    }
}
```

## Project Structure

```
Veld/
├── pom.xml                    # Parent POM
├── veld-annotations/          # Annotation definitions
│   └── src/main/java/
│       └── com/veld/annotation/
│           ├── Component.java
│           ├── Inject.java
│           ├── Singleton.java
│           ├── Prototype.java
│           ├── Lazy.java
│           ├── Optional.java
│           ├── Named.java
│           ├── PostConstruct.java
│           └── PreDestroy.java
├── veld-processor/            # Compile-time annotation processor
│   └── src/main/java/
│       └── com/veld/processor/
│           ├── VeldProcessor.java      # Main processor
│           ├── AnnotationHelper.java   # Multi-source annotation detection
│           ├── ComponentInfo.java      # Component metadata
│           ├── DependencyGraph.java    # Cycle detection
│           └── ...
├── veld-runtime/              # Runtime container
│   └── src/main/java/
│       └── com/veld/runtime/
│           ├── VeldContainer.java
│           ├── ComponentRegistry.java
│           ├── ComponentFactory.java
│           ├── Provider.java
│           └── LazyHolder.java
└── veld-example/              # Example application
    └── src/main/java/
        └── com/veld/example/
            ├── Main.java
            └── ...
```

## Requirements

- **Java**: 11 or higher
- **Maven**: 3.6+

## Build

```bash
# Build all modules
mvn clean install

# Run tests with coverage
mvn clean verify

# View coverage report
open veld-example/target/site/jacoco-aggregate/index.html
```

## Run Example

```bash
cd veld-example
mvn exec:java -Dexec.mainClass="com.veld.example.Main"
```

## How It Works

1. **Compile Time**: The annotation processor scans for `@Component` classes (or `@Singleton`, `@Prototype`, `@Lazy`)
2. **Analysis**: Builds a dependency graph and validates for cycles
3. **Generation**: Creates optimized factory classes using ASM bytecode
4. **Runtime**: Container uses generated factories - no reflection needed

### Generated Code Example

For a component like:

```java
@Singleton
public class UserService {
    @Inject LogService logService;
}
```

Veld generates an optimized factory:

```java
public class UserService$$VeldFactory implements ComponentFactory<UserService> {
    public UserService create(VeldContainer container) {
        UserService instance = new UserService();
        instance.logService = container.get(LogService.class);
        return instance;
    }
}
```

## Changelog

### v1.0.0-alpha.3 (2025-12-03)
- Added `@Optional` annotation for optional dependency injection
- Added `Optional<T>` wrapper support for optional dependencies
- Added `container.tryGet()` method (returns null if not found)
- Added `container.getOptional()` method (returns Optional.empty() if not found)
- Optional dependencies excluded from circular dependency detection

### v1.0.0-alpha.2 (2025-12-02)
- Added `@Lazy` annotation for deferred initialization
- Added `Provider<T>` support for on-demand injection
- Support for `javax.inject.Provider` and `jakarta.inject.Provider`
- Simplified annotations: `@Singleton`, `@Prototype`, `@Lazy` now imply `@Component`

### v1.0.0-alpha.1 (2025-12-01)
- Initial release
- Constructor, field, and method injection
- Singleton and Prototype scopes
- JSR-330 and Jakarta Inject compatibility
- Compile-time circular dependency detection
- Lifecycle callbacks (@PostConstruct, @PreDestroy)

## License

This project is licensed under the Apache License 2.0 - see the [LICENSE](LICENSE) file for details.

## Contributing

Contributions are welcome! Please feel free to submit a Pull Request.
