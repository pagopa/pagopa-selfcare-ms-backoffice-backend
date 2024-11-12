package it.pagopa.selfcare.pagopa.backoffice.scheduler.function;

@FunctionalInterface
public interface NumberOfTotalPagesSearch {

    int search(int limit, int page, String filter);
}