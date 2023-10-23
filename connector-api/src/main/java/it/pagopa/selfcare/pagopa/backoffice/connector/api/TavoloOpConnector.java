package it.pagopa.selfcare.pagopa.backoffice.connector.api;

import it.pagopa.selfcare.pagopa.backoffice.connector.model.tavoloop.TavoloOp;
import it.pagopa.selfcare.pagopa.backoffice.connector.model.tavoloop.TavoloOpOperations;

public interface TavoloOpConnector {

    TavoloOpOperations findByTaxCode(String code);

    TavoloOpOperations insert(TavoloOp tavoloOp);
 }
