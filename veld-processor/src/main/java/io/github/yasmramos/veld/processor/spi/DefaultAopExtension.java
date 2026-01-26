package io.github.yasmramos.veld.processor.spi;

import io.github.yasmramos.veld.spi.aop.AopComponentNode;
import io.github.yasmramos.veld.spi.aop.AopExtension;
import io.github.yasmramos.veld.spi.aop.AopGenerationContext;
import io.github.yasmramos.veld.spi.extension.ExtensionDescriptor;
import io.github.yasmramos.veld.spi.extension.ExtensionPhase;

import java.util.Map;

/**
 * Implementación default de {@link AopExtension} que usa {@code AopClassGenerator}.
 * 
 * <p>Esta es la extensión AOP que se carga por defecto cuando no hay personalizaciones.
 * Genera wrappers AOP compilando interceptores directamente en las clases.</p>
 * 
 * <p>Esta implementación está registrada automáticamente en:</p>
 * <ul>
 *   <li>{@code META-INF/services/io.github.yasmramos.veld.spi.aop.AopExtension}</li>
 * </ul>
 * 
 * @author Veld Team
 * @version 1.0.0
 */
public final class DefaultAopExtension implements AopExtension {
    
    private static final String EXTENSION_ID = "veld/default-aop";
    private static final int ORDER = 0; // Primera extensión, mayor prioridad
    
    @Override
    public ExtensionDescriptor getDescriptor() {
        return new ExtensionDescriptor(
            EXTENSION_ID,
            ExtensionPhase.GENERATION,
            ORDER
        );
    }
    
    @Override
    public Map<String, String> generateAopWrappers(java.util.List<AopComponentNode> components, 
                                                     AopGenerationContext context) {
        // La generación real se hace en SpiAopExtensionExecutor
        // Este método retorna vacío para indicar que se use la generación default
        return Map.of();
    }
    
    @Override
    public boolean overridesDefaultGeneration() {
        // No sobrescribimos - dejamos que el executor maneje la generación
        return false;
    }
}
