package it.pagopa.selfcare.pagopa.backoffice.scheduler.function;

import java.util.List;

@FunctionalInterface
public interface NumberOfTotalPagesSearchWithListParam {

    int search(int limit, int page, List<String> filter);
}