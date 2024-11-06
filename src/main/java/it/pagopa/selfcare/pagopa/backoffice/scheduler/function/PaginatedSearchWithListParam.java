package it.pagopa.selfcare.pagopa.backoffice.scheduler.function;

import java.util.List;

@FunctionalInterface
public interface PaginatedSearchWithListParam<T> {

    T search(int limit, int page, List<String> filter);
}
