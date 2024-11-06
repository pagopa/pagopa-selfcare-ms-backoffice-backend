package it.pagopa.selfcare.pagopa.backoffice.scheduler.function;

import java.util.List;

public interface NumberOfTotalPagesSearchWithListParam {

    int search(int limit, int page, List<String> filter);
}