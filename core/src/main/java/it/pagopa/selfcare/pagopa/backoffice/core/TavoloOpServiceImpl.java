package it.pagopa.selfcare.pagopa.backoffice.core;

import it.pagopa.selfcare.pagopa.backoffice.connector.api.TavoloOpConnector;
import it.pagopa.selfcare.pagopa.backoffice.connector.model.tavoloOp.TavoloOpOperations;
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
    public TavoloOpOperations insert(TavoloOpOperations tavoloOpOperations) {
        return tavoloOpConnector.insert(tavoloOpOperations);
    }

    @Override
    public TavoloOpOperations findByTaxCode(String code) {
        return tavoloOpConnector.findByTaxCode(code);
    }
}
