package it.pagopa.selfcare.pagopa.backoffice.scheduler.function;

@FunctionalInterface
public interface MapInRequiredClass<I, O> {

    O map(I input);
}