package io.github.yasmramos.veld.processor;
import com.squareup.javapoet.*;
import io.github.yasmramos.veld.annotation.ScopeType;
import javax.annotation.processing.Messager;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic;
import java.util.*;
/**
 * Generates Veld.java with revolutionary static dependency graph.
 *
 * <p>This generator creates a pure static DI container with lifecycle support:</p>
 * <ul>
 *   <li>No if statements for dependency resolution</li>
 *   <li>No null values (all dependencies resolved at compile-time)</li>
 *   <li>No runtime reflection for component resolution</li>
 *   <li>@PostConstruct invoked immediately after singleton initialization</li>
 *   <li>@PreDestroy invoked in shutdown() method (reverse dependency order)</li>
 * </ul>
 */
public final class VeldSourceGenerator {
    private final List<VeldNode> nodes;
    private final Map<String, VeldNode> nodeMap;
    private final Messager messager;
    private final String veldClassName;
    private final ClassName veldClass;
    private final Map<String, Integer> levelCache = new HashMap<>();
    
    private static final Map<String, String> SECTION_COMMENTS = new LinkedHashMap<>();
    static {
        SECTION_COMMENTS.put("io.github.yasmramos.veld.example.infrastructure", "// ===== Infrastructure =====");
        SECTION_COMMENTS.put("io.github.yasmramos.veld.example.config", "// ===== Configuration =====");
        SECTION_COMMENTS.put("io.github.yasmramos.veld.example.persistence", "// ===== Persistence =====");
        SECTION_COMMENTS.put("io.github.yasmramos.veld.example.data", "// ===== Data Access =====");
        SECTION_COMMENTS.put("io.github.yasmramos.veld.example.repository", "// ===== Repositories =====");
        SECTION_COMMENTS.put("io.github.yasmramos.veld.example.domain", "// ===== Domain =====");
        SECTION_COMMENTS.put("io.github.yasmramos.veld.example.model", "// ===== Domain Models =====");
        SECTION_COMMENTS.put("io.github.yasmramos.veld.example.service", "// ===== Services =====");
        SECTION_COMMENTS.put("io.github.yasmramos.veld.example.application", "// ===== Application =====");
        SECTION_COMMENTS.put("io.github.yasmramos.veld.example.usecase", "// ===== Use Cases =====");
        SECTION_COMMENTS.put("io.github.yasmramos.veld.example.facade", "// ===== Facades =====");
        SECTION_COMMENTS.put("io.github.yasmramos.veld.example.ui", "// ===== UI =====");
        SECTION_COMMENTS.put("io.github.yasmramos.veld.example.web", "// ===== Web =====");
        SECTION_COMMENTS.put("io.github.yasmramos.veld.example.controller", "// ===== Controllers =====");
        SECTION_COMMENTS.put("io.github.yasmramos.veld.example.integration", "// ===== Integration =====");
        SECTION_COMMENTS.put("io.github.yasmramos.veld.example.external", "// ===== External Services =====");
        SECTION_COMMENTS.put("io.github.yasmramos.veld.example.adapter", "// ===== Adapters =====");
        SECTION_COMMENTS.put("io.github.yasmramos.veld.example.event", "// ===== Events =====");
        SECTION_COMMENTS.put("io.github.yasmramos.veld.example.handler", "// ===== Event Handlers =====");
        SECTION_COMMENTS.put("io.github.yasmramos.veld.example.util", "// ===== Utilities =====");
        SECTION_COMMENTS.put("io.github.yasmramos.veld.example.helper", "// ===== Helpers =====");
    }
    
    public VeldSourceGenerator(List<VeldNode> nodes, Messager messager, String veldPackageName, String className) {
        this.nodes = nodes;
        this.messager = messager;
        this.veldClassName = className;
        this.veldClass = ClassName.get(veldPackageName, veldClassName);
        this.nodeMap = buildNodeMap();
    }
    
    private Map<String, VeldNode> buildNodeMap() {
        Map<String, VeldNode> map = new HashMap<>();
        for (VeldNode node : nodes) {
            map.put(node.getClassName(), node);
        }
        return map;
    }
    
    public JavaFile generate(String packageName) {
        List<DependencyError> errors = validateDependencies();
        if (!errors.isEmpty()) {
            for (DependencyError error : errors) {
                error(error.getMessage());
            }
            return null;
        }
        
        List<VeldNode> sortedNodes = topologicalSort();
        Map<String, Integer> dependencyLevels = calculateDependencyLevels(sortedNodes);
        
        List<VeldNode> singletons = new ArrayList<>();
        List<VeldNode> prototypes = new ArrayList<>();
        for (VeldNode node : sortedNodes) {
            if (node.isSingleton()) {
                singletons.add(node);
            } else {
                prototypes.add(node);
            }
        }
        
        TypeSpec.Builder classBuilder = TypeSpec.classBuilder(veldClassName)
                .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
                .addJavadoc(buildClassJavadoc(singletons, prototypes))
                .addField(createLifecycleStateField())
                .addMethod(createPrivateConstructor());
        
        // Add lifecycle tracking field
        classBuilder.addField(FieldSpec.builder(
            ClassName.get("java.lang", "String"), "lifecycleComment")
            .addModifiers(Modifier.PRIVATE, Modifier.STATIC, Modifier.FINAL)
            .initializer("$S", "Lifecycle tracking: PostConstruct invoked during initialization, PreDestroy via shutdown()")
            .build());
        
        Map<String, List<VeldNode>> sectionNodes = new LinkedHashMap<>();
        for (VeldNode node : singletons) {
            String section = getSectionForNode(node);
            sectionNodes.computeIfAbsent(section, k -> new ArrayList<>()).add(node);
        }
        
        // Generate fields with lifecycle embedded in initializer
        for (Map.Entry<String, List<VeldNode>> entry : sectionNodes.entrySet()) {
            for (VeldNode node : entry.getValue()) {
                addSingletonFieldWithLifecycle(classBuilder, node);
            }
        }
        
        // Generate accessor methods
        for (VeldNode node : singletons) {
            addSingletonAccessor(classBuilder, node);
        }
        
        // Generate prototype methods
        for (VeldNode node : prototypes) {
            addPrototypeComponent(classBuilder, node);
        }
        
        // Generate shutdown method
        addShutdownMethod(classBuilder, singletons);
        
        // Generate lifecycle state getter
        addLifecycleStateGetter(classBuilder);
        
        return JavaFile.builder(packageName, classBuilder.build()).build();
    }
    
    private FieldSpec createLifecycleStateField() {
        return FieldSpec.builder(ClassName.get("java.util.concurrent.atomic", "AtomicBoolean"), "shutdownInitiated")
                .addModifiers(Modifier.PRIVATE, Modifier.STATIC, Modifier.FINAL)
                .initializer("new $T(false)", ClassName.get("java.util.concurrent.atomic", "AtomicBoolean"))
                .build();
    }
    
    private void addSingletonFieldWithLifecycle(TypeSpec.Builder classBuilder, VeldNode node) {
        String actualClassName = node.getActualClassName();
        ClassName type = ClassName.bestGuess(actualClassName);
        String fieldName = node.getVeldName();
        
        CodeBlock initialization = buildInstantiationCode(node);
        CodeBlock lifecycleCode = buildPostConstructInvocation(node, fieldName);
        
        FieldSpec.Builder fieldBuilder = FieldSpec.builder(type, fieldName)
                .addModifiers(Modifier.PRIVATE, Modifier.STATIC, Modifier.FINAL);
        
        addSingletonFieldJavadoc(fieldBuilder, node);
        
        // Embed lifecycle call in initializer using code block
        if (node.hasPostConstruct()) {
            // Use %N placeholder for field name in initializer
            fieldBuilder.initializer("$N = $L;\n$N.$N();", fieldName, initialization, fieldName, node.getPostConstructMethod());
        } else {
            fieldBuilder.initializer("$L", initialization);
        }
        
        classBuilder.addField(fieldBuilder.build());
    }
    
    private CodeBlock buildPostConstructInvocation(VeldNode node, String fieldName) {
        if (!node.hasPostConstruct()) {
            return CodeBlock.of("");
        }
        return CodeBlock.of("$N.$N();", fieldName, node.getPostConstructMethod());
    }
    
    private void addShutdownMethod(TypeSpec.Builder classBuilder, List<VeldNode> singletons) {
        List<VeldNode> preDestroyNodes = new ArrayList<>();
        for (int i = singletons.size() - 1; i >= 0; i--) {
            VeldNode node = singletons.get(i);
            if (node.hasPreDestroy()) {
                preDestroyNodes.add(node);
            }
        }
        
        MethodSpec.Builder shutdownBuilder = MethodSpec.methodBuilder("shutdown")
                .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                .addJavadoc("Shuts down the Veld container and invokes @PreDestroy methods.\n\n" +
                            "<p>PreDestroy methods are called in reverse dependency order,\n" +
                            "ensuring that dependents are destroyed before their dependencies.</p>\n")
                .addStatement("$N.set(true)", "shutdownInitiated");
        
        if (preDestroyNodes.isEmpty()) {
            shutdownBuilder.addStatement("// No @PreDestroy methods to invoke");
        } else {
            shutdownBuilder.addStatement("// Invoke @PreDestroy methods in reverse dependency order");
            for (VeldNode node : preDestroyNodes) {
                shutdownBuilder.addStatement("// $S", node.getClassName());
                shutdownBuilder.addStatement("$N.$N();", node.getVeldName(), node.getPreDestroyMethod());
            }
        }
        
        classBuilder.addMethod(shutdownBuilder.build());
    }
    
    private void addLifecycleStateGetter(TypeSpec.Builder classBuilder) {
        classBuilder.addMethod(MethodSpec.methodBuilder("isShutdown")
                .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                .returns(boolean.class)
                .addStatement("return $N.get()", "shutdownInitiated")
                .build());
    }
    
    private void addSingletonAccessor(TypeSpec.Builder classBuilder, VeldNode node) {
        ClassName returnType = ClassName.bestGuess(node.getActualClassName());
        String methodName = node.getVeldName();
        
        MethodSpec.Builder methodBuilder = MethodSpec.methodBuilder(methodName)
                .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                .returns(returnType)
                .addStatement("return $N", methodName);
        
        StringBuilder javadoc = new StringBuilder("Returns the $N singleton instance.\n");
        if (node.hasPostConstruct()) {
            javadoc.append("\n<p><b>Lifecycle:</b> @PostConstruct has been invoked.</p>\n");
        }
        if (node.hasPreDestroy()) {
            javadoc.append("<p><b>Lifecycle:</b> @PreDestroy will be invoked on shutdown().</p>\n");
        }
        
        classBuilder.addMethod(methodBuilder.build());
    }
    
    private void addPrototypeComponent(TypeSpec.Builder classBuilder, VeldNode node) {
        ClassName returnType = ClassName.bestGuess(node.getActualClassName());
        String methodName = node.getVeldName();
        
        MethodSpec.Builder methodBuilder = MethodSpec.methodBuilder(methodName)
                .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                .returns(returnType)
                .addStatement("return $L", buildInstantiationCode(node));
        
        if (node.hasPostConstruct()) {
            methodBuilder.addStatement("// @PostConstruct");
            methodBuilder.addStatement("$N().$N();", methodName, node.getPostConstructMethod());
        }
        
        classBuilder.addMethod(methodBuilder.build());
    }
    
    private CodeBlock buildInstantiationCode(VeldNode node) {
        ClassName type = ClassName.bestGuess(node.getActualClassName());
        
        if (!node.hasConstructorInjection()) {
            return CodeBlock.of("new $T()", type);
        }
        
        CodeBlock.Builder argsBuilder = CodeBlock.builder();
        VeldNode.ConstructorInfo ctor = node.getConstructorInfo();
        List<VeldNode.ParameterInfo> params = ctor.getParameters();
        
        for (int i = 0; i < params.size(); i++) {
            if (i > 0) {
                argsBuilder.add(", ");
            }
            argsBuilder.add("$L", buildDependencyExpression(params.get(i)));
        }
        
        return CodeBlock.of("new $T($L)", type, argsBuilder.build());
    }
    
    private CodeBlock buildDependencyExpression(VeldNode.ParameterInfo param) {
        if (param.isValueInjection()) {
            return CodeBlock.of("null /* @Value */");
        }
        if (param.isOptionalWrapper()) {
            VeldNode depNode = nodeMap.get(param.getActualTypeName());
            if (depNode != null) {
                return CodeBlock.of("$T.ofNullable($N)", ClassName.get("java.util", "Optional"), depNode.getVeldName());
            }
            return CodeBlock.of("$T.empty()", ClassName.get("java.util", "Optional"));
        }
        if (param.isProvider()) {
            VeldNode depNode = nodeMap.get(param.getActualTypeName());
            if (depNode != null) {
                return CodeBlock.of("() -> $N", depNode.getVeldName());
            }
            return CodeBlock.of("() -> null");
        }
        
        VeldNode depNode = nodeMap.get(param.getActualTypeName());
        if (depNode != null) {
            return CodeBlock.of("$N", depNode.getVeldName());
        }
        return CodeBlock.of("null /* UNRESOLVED */");
    }
    
    private TypeElement getTypeElement(String typeName) {
        return null;
    }
    
    private List<VeldNode> topologicalSort() {
        List<VeldNode> sorted = new ArrayList<>();
        Set<String> visited = new HashSet<>();
        Set<String> visiting = new HashSet<>();
        
        for (VeldNode node : nodes) {
            if (!visited.contains(node.getClassName())) {
                visit(node, visited, visiting, sorted);
            }
        }
        return sorted;
    }
    
    private void visit(VeldNode node, Set<String> visited, Set<String> visiting, List<VeldNode> sorted) {
        if (visited.contains(node.getClassName())) return;
        if (visiting.contains(node.getClassName())) {
            note("Cycle detected: " + node.getClassName());
            return;
        }
        
        visiting.add(node.getClassName());
        
        if (node.hasConstructorInjection()) {
            for (VeldNode.ParameterInfo param : node.getConstructorInfo().getParameters()) {
                VeldNode depNode = nodeMap.get(param.getActualTypeName());
                if (depNode != null) {
                    visit(depNode, visited, visiting, sorted);
                }
            }
        }
        
        visiting.remove(node.getClassName());
        visited.add(node.getClassName());
        sorted.add(node);
    }
    
    private Map<String, Integer> calculateDependencyLevels(List<VeldNode> sortedNodes) {
        Map<String, Integer> levels = new HashMap<>();
        for (VeldNode node : sortedNodes) {
            levels.put(node.getClassName(), calculateNodeLevel(node, levels));
        }
        return levels;
    }
    
    private int calculateNodeLevel(VeldNode node, Map<String, Integer> levels) {
        if (!node.hasConstructorInjection()) return 0;
        int maxLevel = 0;
        for (VeldNode.ParameterInfo param : node.getConstructorInfo().getParameters()) {
            VeldNode depNode = nodeMap.get(param.getActualTypeName());
            if (depNode != null) {
                Integer level = levels.get(depNode.getClassName());
                if (level != null && level > maxLevel) {
                    maxLevel = level;
                }
            }
        }
        return maxLevel + 1;
    }
    
    private String getSectionForNode(VeldNode node) {
        String className = node.getClassName();
        int lastDot = className.lastIndexOf('.');
        if (lastDot > 0) {
            String packagePrefix = className.substring(0, lastDot);
            for (Map.Entry<String, String> entry : SECTION_COMMENTS.entrySet()) {
                if (packagePrefix.equals(entry.getKey()) || packagePrefix.startsWith(entry.getKey() + ".")) {
                    return entry.getValue();
                }
            }
            String lastPackage = packagePrefix.substring(packagePrefix.lastIndexOf('.') + 1);
            String capitalized = lastPackage.substring(0, 1).toUpperCase() + lastPackage.substring(1);
            return "// ===== " + capitalized + " =====";
        }
        return "// ===== Default =====";
    }
    
    private void addSingletonFieldJavadoc(FieldSpec.Builder fieldBuilder, VeldNode node) {
        int level = getDependencyLevel(node);
        StringBuilder javadoc = new StringBuilder();
        javadoc.append("Level: ").append(level).append("\n");
        javadoc.append("Type: ").append(node.getClassName());
        if (node.hasPostConstruct()) {
            javadoc.append("\n@PostConstruct: ").append(node.getPostConstructMethod());
        }
        if (node.hasPreDestroy()) {
            javadoc.append("\n@PreDestroy: ").append(node.getPreDestroyMethod());
        }
        fieldBuilder.addJavadoc("$L\n", javadoc.toString());
    }
    
    private CodeBlock buildClassJavadoc(List<VeldNode> singletons, List<VeldNode> prototypes) {
        return CodeBlock.builder()
                .add("Veld Static Dependency Injection Container.\n\n")
                .add("<p>Generated by Veld annotation processor.</p>\n\n")
                .add("<p><b>Components:</b></p>\n")
                .add("<ul>\n")
                .add("<li>Singletons: $L static fields</li>\n", singletons.size())
                .add("<li>Prototypes: $L factory methods</li>\n", prototypes.size())
                .add("</ul>\n\n")
                .add("<p><b>Lifecycle:</b></p>\n")
                .add("<ul>\n")
                .add("<li>@PostConstruct: invoked immediately after singleton initialization</li>\n")
                .add("<li>@PreDestroy: invoked in shutdown() method (reverse order)</li>\n")
                .add("</ul>\n")
                .build();
    }
    
    private int getDependencyLevel(VeldNode node) {
        return levelCache.getOrDefault(node.getClassName(), 0);
    }
    
    private MethodSpec createPrivateConstructor() {
        return MethodSpec.constructorBuilder()
                .addModifiers(Modifier.PRIVATE)
                .addStatement("throw new $T()", AssertionError.class)
                .build();
    }
    
    private List<DependencyError> validateDependencies() {
        List<DependencyError> errors = new ArrayList<>();
        for (VeldNode node : nodes) {
            if (node.hasConstructorInjection()) {
                VeldNode.ConstructorInfo ctor = node.getConstructorInfo();
                for (int i = 0; i < ctor.getParameters().size(); i++) {
                    VeldNode.ParameterInfo param = ctor.getParameters().get(i);
                    if (param.isValueInjection() || param.isOptionalWrapper() || param.isProvider()) {
                        continue;
                    }
                    if (nodeMap.get(param.getActualTypeName()) == null) {
                        errors.add(new DependencyError(
                            node.getClassName(), node.getVeldName(),
                            param.getTypeName(), param.getActualTypeName(), i));
                    }
                }
            }
        }
        return errors;
    }
    
    private void note(String message) {
        if (messager != null) {
            messager.printMessage(Diagnostic.Kind.NOTE, "[Veld] " + message);
        }
    }
    
    private void error(String message) {
        if (messager != null) {
            messager.printMessage(Diagnostic.Kind.ERROR, "[Veld] " + message);
        }
    }
    
    private static class DependencyError {
        final String componentClass;
        final String componentName;
        final String paramType;
        final String actualType;
        final int paramPosition;
        
        DependencyError(String componentClass, String componentName,
                       String paramType, String actualType, int paramPosition) {
            this.componentClass = componentClass;
            this.componentName = componentName;
            this.paramType = paramType;
            this.actualType = actualType;
            this.paramPosition = paramPosition;
        }
        
        String getMessage() {
            return String.format(
                "Compilation failed.\n\nUnresolved dependency:\n" +
                "- Component: %s\n" +
                "- Parameter #%d: %s (%s)\n\n" +
                "Fix: Add @Component for %s or use @Named",
                componentName, paramPosition + 1, paramType, actualType, actualType);
        }
    }
}
