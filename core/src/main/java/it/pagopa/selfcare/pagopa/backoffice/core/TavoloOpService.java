package it.pagopa.selfcare.pagopa.backoffice.core;

import it.pagopa.selfcare.pagopa.backoffice.connector.model.tavoloop.TavoloOp;
import it.pagopa.selfcare.pagopa.backoffice.connector.model.tavoloop.TavoloOpOperations;

public interface TavoloOpService {

    TavoloOpOperations findByTaxCode(String code);
    TavoloOpOperations insert(TavoloOp tavoloOp);
}
