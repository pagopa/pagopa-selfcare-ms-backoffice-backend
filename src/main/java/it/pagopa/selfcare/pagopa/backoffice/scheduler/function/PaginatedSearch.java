package it.pagopa.selfcare.pagopa.backoffice.scheduler.function;

@FunctionalInterface
public interface PaginatedSearch<T> {

    T search(int limit, int page, String filter);
}
