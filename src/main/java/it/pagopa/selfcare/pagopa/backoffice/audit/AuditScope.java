package it.pagopa.selfcare.pagopa.backoffice.audit;

import org.slf4j.MDC;

public final class AuditScope implements AutoCloseable {
    private static final String MDC_KEY = "audit";
    private static final String MDC_VALUE = "true";

    private static final ThreadLocal<State> STATE =
            ThreadLocal.withInitial(State::new);

    private final State state;
    private final boolean isOwner;

    private AuditScope(State state, boolean isOwner) {
        this.state = state;
        this.isOwner = isOwner;
    }

    public static AuditScope enable() {
        State state = STATE.get();

        if (state.depth++ == 0) {
            MDC.put(MDC_KEY, MDC_VALUE);
            return new AuditScope(state, true);
        }

        return new AuditScope(state, false);
    }

    @Override
    public void close() {
        if (--state.depth == 0 && isOwner) {
            MDC.remove(MDC_KEY);
        }
    }

    private static final class State {
        int depth = 0;
    }
}