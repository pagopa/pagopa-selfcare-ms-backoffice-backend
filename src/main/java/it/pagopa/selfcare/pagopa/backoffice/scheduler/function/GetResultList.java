package it.pagopa.selfcare.pagopa.backoffice.scheduler.function;

import java.util.List;

public interface GetResultList<I, O> {

    List<O> get(I input);
}