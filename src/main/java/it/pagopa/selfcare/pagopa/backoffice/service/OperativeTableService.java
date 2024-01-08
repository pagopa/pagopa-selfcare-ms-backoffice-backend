package it.pagopa.selfcare.pagopa.backoffice.service;

import it.pagopa.selfcare.pagopa.backoffice.entity.TavoloOpEntity;
import it.pagopa.selfcare.pagopa.backoffice.entity.TavoloOpOperations;
import it.pagopa.selfcare.pagopa.backoffice.exception.AppError;
import it.pagopa.selfcare.pagopa.backoffice.exception.AppException;
import it.pagopa.selfcare.pagopa.backoffice.mapper.TavoloOpMapper;
import it.pagopa.selfcare.pagopa.backoffice.model.connector.tavoloop.TavoloOpEntitiesList;
import it.pagopa.selfcare.pagopa.backoffice.model.tavoloop.TavoloOpDto;
import it.pagopa.selfcare.pagopa.backoffice.model.tavoloop.TavoloOpResource;
import it.pagopa.selfcare.pagopa.backoffice.model.tavoloop.TavoloOpResourceList;
import it.pagopa.selfcare.pagopa.backoffice.repository.TavoloOpRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.AuditorAware;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class OperativeTableService {

    @Autowired
    private AuditorAware<String> auditorAware;

    @Autowired
    private TavoloOpRepository tavoloOpRepository;

    public TavoloOpResourceList getOperativeTables() {
        TavoloOpEntitiesList tavoloOpResourceList = findAll();
        return TavoloOpMapper.toResource(tavoloOpResourceList);
    }

    public TavoloOpResource getOperativeTable(String ecCode) {
        TavoloOpOperations tavoloOpOperations = tavoloOpRepository.findByTaxCode(ecCode);
        TavoloOpResource response = TavoloOpMapper.toResource(tavoloOpOperations);
        if(response == null) {
            throw new AppException(AppError.OPERATIVE_TABLE_NOT_FOUND, ecCode);
        }
        return response;
    }


    /**
     * map the request as an entity and save a new tavoloOp on DB
     *
     * @param tavoloOp the request
     * @return the new entity
     */
    public TavoloOpOperations insertOperativeTable(TavoloOpDto tavoloOp) {
        TavoloOpEntity tavoloOpEntity = mapTavoloOpEntity(tavoloOp);
        return tavoloOpRepository.insert(tavoloOpEntity);
    }


    public TavoloOpOperations updateOperativeTable(TavoloOpDto tavoloOp) {
        TavoloOpEntity tavoloOpEntity = mapTavoloOpEntity(tavoloOp);
        return tavoloOpRepository.save(tavoloOpEntity);
    }


    // Private Methods


    /**
     * map a class in another
     *
     * @param tavoloOp the request
     * @return the TavoloOp entity
     */
    private TavoloOpEntity mapTavoloOpEntity(TavoloOpDto tavoloOp) {
        TavoloOpEntity tavoloOpEntity = new TavoloOpEntity();
        tavoloOpEntity.setName(tavoloOp.getName());
        tavoloOpEntity.setEmail(tavoloOp.getEmail());
        tavoloOpEntity.setTaxCode(tavoloOp.getTaxCode());
        tavoloOpEntity.setTelephone(tavoloOp.getTelephone());
        tavoloOpEntity.setReferent(tavoloOp.getReferent());
        tavoloOpEntity.setCreatedBy(auditorAware.getCurrentAuditor().orElse(null));
        tavoloOpEntity.setModifiedBy(auditorAware.getCurrentAuditor().orElse(null));
        tavoloOpEntity.setId(tavoloOp.getTaxCode());
        return tavoloOpEntity;
    }


    private TavoloOpEntitiesList findAll() {
        List<TavoloOpEntity> entities = tavoloOpRepository.findAll();

        List<TavoloOpOperations> tavoloOpOperationsList = new ArrayList<>(entities);
        return TavoloOpEntitiesList.builder()
                .tavoloOpOperationsList(tavoloOpOperationsList)
                .build();
    }
}
