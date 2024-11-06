package it.pagopa.selfcare.pagopa.backoffice.scheduler.function;

import java.util.List;

@FunctionalInterface
public interface GetResultList<I, O> {

    List<O> get(I input);
}