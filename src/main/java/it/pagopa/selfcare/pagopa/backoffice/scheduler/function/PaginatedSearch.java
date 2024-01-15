package it.pagopa.selfcare.pagopa.backoffice.scheduler.function;

public interface PaginatedSearch<T> {

    T search(int limit, int page, String filter);
}
