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
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.AuditorAware;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class OperativeTableService {

    private final AuditorAware<String> auditorAware;

    private final TavoloOpRepository tavoloOpRepository;

    private final ModelMapper modelMapper;

    @Autowired
    public OperativeTableService(AuditorAware<String> auditorAware, TavoloOpRepository tavoloOpRepository, ModelMapper modelMapper) {
        this.auditorAware = auditorAware;
        this.tavoloOpRepository = tavoloOpRepository;
        this.modelMapper = modelMapper;
    }

    /**
     * Retrieve the list of operative table
     *
     * @return the operative table's list
     */
    public TavoloOpResourceList getOperativeTables() {
        TavoloOpEntitiesList tavoloOpResourceList = findAll();
        return TavoloOpMapper.toResource(tavoloOpResourceList);
    }

    /**
     * Retrieve the operative table given the creditor institution's tax code
     *
     * @param ciTaxCode creditor institution's tax code
     * @return the operative table
     */
    public TavoloOpResource getOperativeTable(String ciTaxCode) {
        Optional<TavoloOpEntity> optionalOperativeTable = this.tavoloOpRepository.findByTaxCode(ciTaxCode);

        if (optionalOperativeTable.isEmpty()) {
            throw new AppException(AppError.OPERATIVE_TABLE_NOT_FOUND, ciTaxCode);
        }
        return this.modelMapper.map(optionalOperativeTable.get(), TavoloOpResource.class);
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
