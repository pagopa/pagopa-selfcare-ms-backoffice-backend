package it.pagopa.selfcare.pagopa.backoffice.audit;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.MDC;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

class AuditScopeTest {

    @BeforeEach
    @AfterEach
    void cleanup() {
        MDC.clear();
    }

    @Test
    void shouldSetAuditFlagInMDC() {
        assertNull(MDC.get("audit"), "MDC should be empty initially");

        try (var audit = AuditScope.enable()) {
            assertEquals("true", MDC.get("audit"), "Audit flag should be set");
        }

        assertNull(MDC.get("audit"), "MDC should be cleared after close");
    }

    @Test
    void shouldRemoveAuditFlagAfterClose() {
        try (var audit = AuditScope.enable()) {
            assertEquals("true", MDC.get("audit"));
        }

        assertNull(MDC.get("audit"), "Audit flag should be removed");
    }

    @Test
    void shouldHandleNestedScopes() {
        assertNull(MDC.get("audit"));

        try (var audit1 = AuditScope.enable()) {
            assertEquals("true", MDC.get("audit"), "First scope should set audit flag");

            try (var audit2 = AuditScope.enable()) {
                assertEquals("true", MDC.get("audit"), "Nested scope should keep audit flag");

                try (var audit3 = AuditScope.enable()) {
                    assertEquals("true", MDC.get("audit"), "Third level scope should keep audit flag");
                }

                assertEquals("true", MDC.get("audit"), "Audit flag still set after third scope close");
            }

            assertEquals("true", MDC.get("audit"), "Audit flag still set after second scope close");
        }

        assertNull(MDC.get("audit"), "Audit flag should be removed only after all scopes closed");
    }

    @Test
    void shouldBeThreadSafe() throws InterruptedException {
        int threadCount = 10;
        int iterationsPerThread = 1000;
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        CountDownLatch latch = new CountDownLatch(threadCount);
        AtomicInteger successCount = new AtomicInteger(0);

        for (int i = 0; i < threadCount; i++) {
            executor.submit(() -> {
                try {
                    for (int j = 0; j < iterationsPerThread; j++) {
                        try (var audit = AuditScope.enable()) {
                            assertEquals("true", MDC.get("audit"));
                        }
                        assertNull(MDC.get("audit"));
                    }
                    successCount.incrementAndGet();
                } finally {
                    latch.countDown();
                }
            });
        }

        assertTrue(latch.await(10, TimeUnit.SECONDS), "All threads should complete");
        assertEquals(threadCount, successCount.get(), "All threads should succeed");

        executor.shutdown();
        assertTrue(executor.awaitTermination(5, TimeUnit.SECONDS));
    }

    @Test
    void shouldHandleMultipleSequentialScopes() {
        for (int i = 0; i < 100; i++) {
            try (var audit = AuditScope.enable()) {
                assertEquals("true", MDC.get("audit"));
            }
            assertNull(MDC.get("audit"), "MDC should be clean between iterations");
        }
    }

    @Test
    void shouldHandleExceptionInScope() {
        assertNull(MDC.get("audit"));

        try {
            try (var audit = AuditScope.enable()) {
                assertEquals("true", MDC.get("audit"));
                throw new RuntimeException("Test exception");
            }
        } catch (RuntimeException e) {
            assertEquals("Test exception", e.getMessage());
        }

        assertNull(MDC.get("audit"), "MDC should be cleaned up even after exception");
    }

    @Test
    void shouldHandleExceptionInNestedScope() {
        try (var audit1 = AuditScope.enable()) {
            assertEquals("true", MDC.get("audit"));

            try {
                try (var audit2 = AuditScope.enable()) {
                    assertEquals("true", MDC.get("audit"));
                    throw new RuntimeException("Nested exception");
                }
            } catch (RuntimeException e) {
                assertEquals("Nested exception", e.getMessage());
            }

            assertEquals("true", MDC.get("audit"), "Outer scope should still have audit flag");
        }

        assertNull(MDC.get("audit"), "All scopes should be cleaned up");
    }

    @Test
    void shouldNotInterfereBetweenThreads() throws InterruptedException {
        CountDownLatch thread1Ready = new CountDownLatch(1);
        CountDownLatch thread2Ready = new CountDownLatch(1);
        CountDownLatch thread1Done = new CountDownLatch(1);
        CountDownLatch thread2Done = new CountDownLatch(1);

        Thread thread1 = new Thread(() -> {
            try (var audit = AuditScope.enable()) {
                assertEquals("true", MDC.get("audit"));
                thread1Ready.countDown();
                assertTrue(thread2Ready.await(2, TimeUnit.SECONDS));
                assertEquals("true", MDC.get("audit"), "Thread 1 should still have audit flag");
            } catch (InterruptedException e) {
                fail("Thread 1 interrupted");
            } finally {
                thread1Done.countDown();
            }
        });

        Thread thread2 = new Thread(() -> {
            try {
                assertTrue(thread1Ready.await(2, TimeUnit.SECONDS));
                assertNull(MDC.get("audit"), "Thread 2 should not have audit flag from thread 1");

                try (var audit = AuditScope.enable()) {
                    assertEquals("true", MDC.get("audit"));
                    thread2Ready.countDown();
                }

                assertNull(MDC.get("audit"));
            } catch (InterruptedException e) {
                fail("Thread 2 interrupted");
            } finally {
                thread2Done.countDown();
            }
        });

        thread1.start();
        thread2.start();

        assertTrue(thread1Done.await(5, TimeUnit.SECONDS), "Thread 1 should complete");
        assertTrue(thread2Done.await(5, TimeUnit.SECONDS), "Thread 2 should complete");
    }

    @Test
    void shouldHandleDeepNesting() {
        int depth = 50;

        deepNestedTest(depth);

        assertNull(MDC.get("audit"), "MDC should be clean after deep nesting");
    }

    private void deepNestedTest(int depth) {
        if (depth == 0) {
            assertEquals("true", MDC.get("audit"));
            return;
        }

        try (var audit = AuditScope.enable()) {
            assertEquals("true", MDC.get("audit"));
            deepNestedTest(depth - 1);
        }
    }

    @Test
    void shouldWorkWithTryWithResourcesVariableNotUsed() {
        try (var ignored = AuditScope.enable()) {
            assertEquals("true", MDC.get("audit"));
        }

        assertNull(MDC.get("audit"));
    }
}