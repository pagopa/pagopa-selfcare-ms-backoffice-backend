package it.pagopa.selfcare.pagopa.backoffice.core;

import it.pagopa.selfcare.pagopa.backoffice.connector.api.TavoloOpConnector;
import it.pagopa.selfcare.pagopa.backoffice.connector.model.tavoloop.TavoloOp;
import it.pagopa.selfcare.pagopa.backoffice.connector.model.tavoloop.TavoloOpEntitiesList;
import it.pagopa.selfcare.pagopa.backoffice.connector.model.tavoloop.TavoloOpOperations;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class TavoloOpServiceImpl implements  TavoloOpService{


    private TavoloOpConnector tavoloOpConnector;

    @Autowired
    public TavoloOpServiceImpl(TavoloOpConnector tavoloOpConnector) {
        this.tavoloOpConnector = tavoloOpConnector;
    }

    @Override
    public TavoloOpOperations insert(TavoloOp tavoloOp) {
        return tavoloOpConnector.insert(tavoloOp);
    }

    @Override
    public TavoloOpOperations update(TavoloOp tavoloOp) {
        return tavoloOpConnector.update(tavoloOp);
    }

    @Override
    public TavoloOpOperations findByTaxCode(String code) {
        return tavoloOpConnector.findByTaxCode(code);
    }

    @Override
    public TavoloOpEntitiesList findAll() {
        return tavoloOpConnector.findAll();
    }
}
