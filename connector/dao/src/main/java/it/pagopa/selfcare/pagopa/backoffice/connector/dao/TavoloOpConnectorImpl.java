package it.pagopa.selfcare.pagopa.backoffice.connector.dao;

import it.pagopa.selfcare.pagopa.backoffice.connector.api.TavoloOpConnector;
import it.pagopa.selfcare.pagopa.backoffice.connector.dao.model.TavoloOpEntity;
import it.pagopa.selfcare.pagopa.backoffice.connector.model.tavoloop.TavoloOp;
import it.pagopa.selfcare.pagopa.backoffice.connector.model.tavoloop.TavoloOpEntitiesList;
import it.pagopa.selfcare.pagopa.backoffice.connector.model.tavoloop.TavoloOpOperations;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

import static com.fasterxml.jackson.databind.ser.std.NumberSerializers.addAll;
import org.springframework.stereotype.Service;

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
    public TavoloOpOperations insert(TavoloOp tavoloOp) {
        TavoloOpEntity tavoloOpEntity = new TavoloOpEntity();
        tavoloOpEntity.setName(tavoloOp.getName());
        tavoloOpEntity.setEmail(tavoloOp.getEmail());
        tavoloOpEntity.setTaxCode(tavoloOp.getTaxCode());
        tavoloOpEntity.setTelephone(tavoloOp.getTelephone());
        tavoloOpEntity.setReferent(tavoloOp.getReferent());
        tavoloOpEntity.setCreatedBy(auditorAware.getCurrentAuditor().orElse(null));
        tavoloOpEntity.setModifiedBy(auditorAware.getCurrentAuditor().orElse(null));

        return tavoloOpRepository.insert(tavoloOpEntity);

    }

    @Override
    public TavoloOpOperations findByTaxCode(String code) {
        return tavoloOpRepository.findByTaxCode(code);
    }

    public TavoloOpEntitiesList findAll() {

        List<TavoloOpEntity> entities = tavoloOpRepository.findAll();
        TavoloOpEntitiesList tavoloOpOperations = new TavoloOpEntitiesList();
        List<TavoloOpOperations> tavoloOpOperationsList = new ArrayList<>(entities);
        tavoloOpOperations.setTavoloOpOperationsList(tavoloOpOperationsList);
        return tavoloOpOperations;
    }

}
