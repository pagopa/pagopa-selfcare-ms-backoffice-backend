package it.pagopa.selfcare.pagopa.backoffice.connector.dao;

import it.pagopa.selfcare.pagopa.backoffice.connector.api.TavoloOpConnector;
import it.pagopa.selfcare.pagopa.backoffice.connector.dao.model.TavoloOp;
import it.pagopa.selfcare.pagopa.backoffice.connector.model.tavoloOp.TavoloOpOperations;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.AuditorAware;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Slf4j
@Service
public class TavoloOpConnectorImpl implements TavoloOpConnector {

    private final AuditorAware<String> auditorAware;

    private final TavoloOpRepository tavoloOpRepository;

    @Autowired
    public TavoloOpConnectorImpl(AuditorAware<String> auditorAware, TavoloOpRepository tavoloOpRepository) {
        this.auditorAware = auditorAware;
        this.tavoloOpRepository = tavoloOpRepository;
    }

    @Override
    public Optional<? extends TavoloOpOperations> findByName(String name) {
        return tavoloOpRepository.findById(name);
    }

    @Override
    public TavoloOpOperations insert(TavoloOpOperations tavoloOpOperations) {
        TavoloOp tavoloOp = (TavoloOp) tavoloOpOperations;
        tavoloOp.setCreatedBy(auditorAware.getCurrentAuditor().orElse(null));
        tavoloOp.setCreatedBy(auditorAware.getCurrentAuditor().orElse(null));
        return tavoloOpRepository.insert( (TavoloOp) tavoloOpOperations);

    }

    @Override
    public TavoloOpOperations findByTaxCode(String code) {
        //todo get one element
        return tavoloOpRepository.findAll().get(0);

    }
}
