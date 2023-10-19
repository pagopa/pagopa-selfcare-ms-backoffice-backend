package it.pagopa.selfcare.pagopa.backoffice.core;

import it.pagopa.selfcare.pagopa.backoffice.connector.model.tavoloOp.TavoloOp;
import it.pagopa.selfcare.pagopa.backoffice.connector.model.tavoloOp.TavoloOpOperations;

public interface TavoloOpService {

    TavoloOpOperations findByTaxCode(String code);
    TavoloOpOperations insert(TavoloOp tavoloOp);
}
