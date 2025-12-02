# Veld DI Framework

[![CI/CD](https://github.com/yasmramos/Veld/actions/workflows/ci.yml/badge.svg)](https://github.com/yasmramos/Veld/actions/workflows/ci.yml)
[![Java](https://img.shields.io/badge/Java-11%2B-orange)](https://openjdk.java.net/)
[![Maven](https://img.shields.io/badge/Maven-3.6%2B-blue)](https://maven.apache.org/)
[![License](https://img.shields.io/badge/License-Apache%202.0-green.svg)](LICENSE)

A lightweight, compile-time Dependency Injection framework for Java using pure ASM bytecode generation. **Zero reflection, zero runtime overhead.**

## Features

- **Pure Bytecode Generation**: Uses ASM to generate optimized factory classes at compile time
- **Zero Reflection**: All dependency resolution happens via generated code, not reflection
- **JSR-330 Compatible**: Full support for `javax.inject.*` annotations
- **Jakarta Inject Compatible**: Full support for `jakarta.inject.*` annotations  
- **Mixed Annotations**: Use Veld, JSR-330, and Jakarta annotations together seamlessly
- **Scope Management**: Built-in Singleton and Prototype scopes
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
        <version>1.0.0-SNAPSHOT</version>
    </dependency>
    
    <!-- Veld Runtime -->
    <dependency>
        <groupId>com.veld</groupId>
        <artifactId>veld-runtime</artifactId>
        <version>1.0.0-SNAPSHOT</version>
    </dependency>
    
    <!-- Veld Processor (compile-time only) -->
    <dependency>
        <groupId>com.veld</groupId>
        <artifactId>veld-processor</artifactId>
        <version>1.0.0-SNAPSHOT</version>
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
                        <version>1.0.0-SNAPSHOT</version>
                    </path>
                </annotationProcessorPaths>
            </configuration>
        </plugin>
    </plugins>
</build>
```

### 3. Define Components

```java
import com.veld.annotation.Component;
import com.veld.annotation.Inject;
import com.veld.annotation.Singleton;

@Component
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
        try (VeldContainer container = VeldContainer.create()) {
            UserService userService = container.get(UserService.class);
            User user = userService.findUser(1L);
        }
    }
}
```

## Annotation Support

Veld supports annotations from three sources, which can be mixed freely:

| Feature | Veld | JSR-330 (javax) | Jakarta |
|---------|------|-----------------|---------|
| Component | `@Component` | - | - |
| Injection | `@Inject` | `@Inject` | `@Inject` |
| Singleton | `@Singleton` | `@Singleton` | `@Singleton` |
| Prototype | `@Prototype` | - | - |
| Qualifier | `@Named` | `@Named` | `@Named` |
| Post-construct | `@PostConstruct` | `@PostConstruct` | `@PostConstruct` |
| Pre-destroy | `@PreDestroy` | `@PreDestroy` | `@PreDestroy` |

### Example: Mixed Annotations

```java
@Component
@jakarta.inject.Singleton
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
@Component
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
@Component
public class UserService {
    @Inject
    UserRepository userRepository;  // Must be non-private (no reflection)
}
```

### Method Injection

```java
@Component
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

@Component
@Singleton
public class UserRepositoryImpl implements IUserRepository {
    @Override
    public User findById(Long id) { /* ... */ }
}

@Component
public class UserService {
    @Inject
    IUserRepository userRepository;  // Injects UserRepositoryImpl
}
```

## Scopes

### Singleton (Default)

```java
@Component
@Singleton
public class ConfigService {
    // One instance shared across the container
}
```

### Prototype

```java
@Component
@Prototype
public class RequestContext {
    // New instance created for each injection
}
```

## Lifecycle Callbacks

```java
@Component
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
│           └── ComponentFactory.java
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

1. **Compile Time**: The annotation processor scans for `@Component` classes
2. **Analysis**: Builds a dependency graph and validates for cycles
3. **Generation**: Creates optimized factory classes using ASM bytecode
4. **Runtime**: Container uses generated factories - no reflection needed

### Generated Code Example

For a component like:

```java
@Component
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

## License

This project is licensed under the Apache License 2.0 - see the [LICENSE](LICENSE) file for details.

## Contributing

Contributions are welcome! Please feel free to submit a Pull Request.
