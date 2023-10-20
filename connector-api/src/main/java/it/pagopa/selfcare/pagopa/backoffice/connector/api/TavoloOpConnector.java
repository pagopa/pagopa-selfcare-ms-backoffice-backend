package it.pagopa.selfcare.pagopa.backoffice.connector.api;

import it.pagopa.selfcare.pagopa.backoffice.connector.model.tavoloop.TavoloOp;
import it.pagopa.selfcare.pagopa.backoffice.connector.model.tavoloop.TavoloOpOperations;

import java.util.Optional;

public interface TavoloOpConnector {
 
    TavoloOpOperations insert(TavoloOp tavoloOp);
 }
