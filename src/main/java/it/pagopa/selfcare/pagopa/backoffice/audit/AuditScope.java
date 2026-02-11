package it.pagopa.selfcare.pagopa.backoffice.audit;

import org.slf4j.MDC;

public final class AuditScope implements AutoCloseable {
    private static final String MDC_AUDIT_KEY = "audit";
    private static final String MDC_AUDIT_VALUE = "true";

    private static final ThreadLocal<AuditState> AUDIT_STATE =
            ThreadLocal.withInitial(AuditState::new);

    private final AuditState state;
    private final boolean isOwner;

    private AuditScope(AuditState state, boolean isOwner) {
        this.state = state;
        this.isOwner = isOwner;
    }

    public static AuditScope enable() {
        AuditState state = AUDIT_STATE.get();

        if (state.depth++ == 0) {
            MDC.put(MDC_AUDIT_KEY, MDC_AUDIT_VALUE);
            return new AuditScope(state, true);
        }

        return new AuditScope(state, false);
    }

    @Override
    public void close() {
        if (--state.depth == 0) {
            if (isOwner) {
                MDC.remove(MDC_AUDIT_KEY);
            }
            AUDIT_STATE.remove();
        }
    }

    private static final class AuditState {
        int depth = 0;
    }
}