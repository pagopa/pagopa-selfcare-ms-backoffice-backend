package it.pagopa.selfcare.pagopa.backoffice.connector.api;

import it.pagopa.selfcare.pagopa.backoffice.connector.model.tavoloOp.TavoloOpOperations;
import it.pagopa.selfcare.pagopa.backoffice.connector.model.wrapper.WrapperEntitiesOperations;

import java.util.Optional;

public interface TavoloOpConnector {


    Optional<? extends TavoloOpOperations> findByName(String name);

    TavoloOpOperations insert(TavoloOpOperations tavoloOpOperations);
 }
