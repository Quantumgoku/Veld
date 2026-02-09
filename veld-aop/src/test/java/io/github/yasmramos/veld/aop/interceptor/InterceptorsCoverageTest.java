package io.github.yasmramos.veld.aop.interceptor;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class InterceptorsCoverageTest {

    @Test
    void testLoggingInterceptor() {
        LoggingInterceptor interceptor = new LoggingInterceptor();
        interceptor.beforeMethod("testMethod", new Object[]{"arg1"});
        interceptor.afterMethod("testMethod", "result");
        interceptor.afterThrowing("testMethod", new RuntimeException("error"));
        assertNotNull(interceptor);
    }

    @Test
    void testTimingInterceptor() {
        TimingInterceptor interceptor = new TimingInterceptor();
        interceptor.beforeMethod("testMethod", new Object[]{});
        interceptor.afterMethod("testMethod", null);
        interceptor.afterThrowing("testMethod", new RuntimeException());
        assertNotNull(interceptor);
    }

    @Test
    void testTransactionInterceptor() {
        TransactionInterceptor interceptor = new TransactionInterceptor();
        interceptor.beforeMethod("testMethod", new Object[]{});
        interceptor.afterMethod("testMethod", null);
        interceptor.afterThrowing("testMethod", new RuntimeException());
        assertNotNull(interceptor);
    }

    @Test
    void testValidationInterceptor() {
        ValidationInterceptor interceptor = new ValidationInterceptor();
        interceptor.beforeMethod("testMethod", new Object[]{});
        interceptor.afterMethod("testMethod", null);
        interceptor.afterThrowing("testMethod", new RuntimeException());
        assertNotNull(interceptor);
    }
}
