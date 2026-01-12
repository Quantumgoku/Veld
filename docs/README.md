# Veld Framework Documentation

Comprehensive documentation for Veld, an ultra-fast Dependency Injection framework for Java with zero reflection at runtime.

## Documentation Structure

### Getting Started

| Topic | Description |
|-------|-------------|
| [Getting Started](getting-started.md) | Quick start guide for new users |
| [Installation](getting-started.md#installation) | Maven and Gradle setup |
| [First Application](getting-started.md#your-first-application) | Create your first Veld app |

### Core Concepts

| Topic | Description |
|-------|-------------|
| [Architecture](architecture.md) | Three-phase build process and runtime behavior |
| [Core Features](core-features.md) | Injection patterns, EventBus, lifecycle |
| [Annotations Reference](annotations.md) | Complete list of all annotations |

### Advanced Topics

| Topic | Description |
|-------|-------------|
| [AOP Guide](aop.md) | Aspect-Oriented Programming with Veld |
| [EventBus](eventbus.md) | Event-driven communication |
| [Benchmarks](benchmarks.md) | Performance comparisons and metrics |

### References

| Topic | Description |
|-------|-------------|
| [API Reference](api.md) | Veld class API documentation |
| [Examples](examples.md) | Example projects and code patterns |
| [Performance Benchmarks](benchmarks.md) | Detailed performance metrics |

## Quick Links

- [Main README](../README.md) - Project overview and quick start
- [GitHub Repository](https://github.com/yasmramos/Veld)
- [Examples](../veld-example/) - Working example projects
- [Spring Boot Example](../veld-spring-boot-example/) - Spring Boot integration

## Module Documentation

| Module | Documentation |
|--------|---------------|
| `veld-annotations` | [Annotations Reference](annotations.md) |
| `veld-runtime` | [API Reference](api.md), [Core Features](core-features.md) |
| `veld-processor` | [Architecture](architecture.md) |
| `veld-weaver` | [Architecture](architecture.md) |
| `veld-maven-plugin` | [Getting Started](getting-started.md) |
| `veld-aop` | [AOP Guide](aop.md) |
| `veld-resilience` | [Core Features - Resilience](core-features.md#resilience-features) |
| `veld-cache` | [Core Features - Caching](core-features.md#caching) |
| `veld-validation` | [Core Features - Validation](core-features.md#validation) |
| `veld-security` | [Core Features - Security](core-features.md#security) |
| `veld-metrics` | [Core Features - Metrics](core-features.md#metrics) |
| `veld-tx` | [Core Features - Transactions](core-features.md#transactions) |
| `veld-spring-boot-starter` | [Spring Boot Example](../veld-spring-boot-example/) |

## Version Compatibility

| Veld Version | Java Version | Spring Boot Version |
|--------------|--------------|---------------------|
| 1.0.3 | 17+ | 3.x |
| 1.1.0 | 17+ | 3.x |

## Contributing

See the [Contributing Guide](../CONTRIBUTING.md) for guidelines on how to contribute to Veld.

## License

Apache License 2.0 - see [LICENSE](../LICENSE)
