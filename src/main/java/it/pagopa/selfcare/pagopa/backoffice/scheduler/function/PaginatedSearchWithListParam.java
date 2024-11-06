package it.pagopa.selfcare.pagopa.backoffice.scheduler.function;

import java.util.List;

public interface PaginatedSearchWithListParam<T> {

    T search(int limit, int page, List<String> filter);
}
