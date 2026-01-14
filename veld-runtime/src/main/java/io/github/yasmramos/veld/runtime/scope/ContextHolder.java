package io.github.yasmramos.veld.runtime.scope;

/**
 * Simple context holder that allows setting and getting values across threads.
 * Unlike ThreadLocal, this is designed for cases where the same context
 * needs to be accessed from multiple threads (e.g., thread pools).
 *
 * @param <T> the type of context value
 */
public final class ContextHolder<T> {

    private volatile T value;

    /**
     * Creates a new ContextHolder with a null initial value.
     */
    public ContextHolder() {
        this.value = null;
    }

    /**
     * Sets the context value.
     *
     * @param value the value to set
     */
    public void set(T value) {
        this.value = value;
    }

    /**
     * Gets the current context value.
     *
     * @return the current value, or null if not set
     */
    public T get() {
        return value;
    }

    /**
     * Clears the context value.
     */
    public void clear() {
        this.value = null;
    }
}
