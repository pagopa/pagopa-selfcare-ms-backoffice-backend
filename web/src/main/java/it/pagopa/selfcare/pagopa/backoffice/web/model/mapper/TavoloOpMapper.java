package it.pagopa.selfcare.pagopa.backoffice.web.model.mapper;

import it.pagopa.selfcare.pagopa.backoffice.connector.model.tavoloop.TavoloOp;
import it.pagopa.selfcare.pagopa.backoffice.connector.model.tavoloop.TavoloOpEntitiesList;
import it.pagopa.selfcare.pagopa.backoffice.connector.model.tavoloop.TavoloOpOperations;
import it.pagopa.selfcare.pagopa.backoffice.web.model.tavoloop.TavoloOpDto;
import it.pagopa.selfcare.pagopa.backoffice.web.model.tavoloop.TavoloOpResource;
import it.pagopa.selfcare.pagopa.backoffice.web.model.tavoloop.TavoloOpResourceList;

import java.util.stream.Collectors;

public class TavoloOpMapper {

    private TavoloOpMapper(){}

    public static TavoloOpResource toResource(TavoloOpOperations tavoloOpOperations) {
        if (tavoloOpOperations == null) {
            return null;
        }
        TavoloOpResource response = new TavoloOpResource();

        response.setName(tavoloOpOperations.getName());
        response.setEmail(tavoloOpOperations.getEmail());
        response.setCreatedBy(tavoloOpOperations.getCreatedBy());
        response.setCreatedAt(tavoloOpOperations.getCreatedAt());
        response.setReferent(tavoloOpOperations.getReferent());
        response.setModifiedAt(tavoloOpOperations.getModifiedAt());
        response.setModifiedBy(tavoloOpOperations.getModifiedBy());
        response.setTelephone(tavoloOpOperations.getTelephone());
        response.setTaxCode(tavoloOpOperations.getTaxCode());

        return response;
    }

    public static TavoloOpResourceList toResource(TavoloOpEntitiesList tavoloOpOperations) {
        if (tavoloOpOperations == null) {
            return null;
        }
        TavoloOpResourceList response = new TavoloOpResourceList();

        response.setTavoloOpResourceList(tavoloOpOperations.getTavoloOpOperationsList().stream()
                .map(TavoloOpMapper::toResource)
                .collect(Collectors.toList()));
        //response.setPageInfo(model.getPageInfo());

        return response;
    }

    public static TavoloOp fromDto(TavoloOpDto dto) {
        TavoloOp tavoloOp = null;
        if (dto == null)
            return null;
        tavoloOp = new TavoloOp();
        tavoloOp.setName(dto.getName());
        tavoloOp.setReferent(dto.getReferent());
        tavoloOp.setTelephone(dto.getTelephone());
        tavoloOp.setTaxCode(dto.getTelephone());
        tavoloOp.setEmail(dto.getEmail());
        return tavoloOp;
    }

    public static TavoloOpResource toResource(TavoloOp model) {
        TavoloOpResource resource = null;
        if (model == null)
            return null;
        resource = new TavoloOpResource();
        resource.setName(model.getName());
        resource.setReferent(model.getReferent());
        resource.setTelephone(model.getTelephone());
        resource.setTaxCode(model.getTelephone());
        resource.setEmail(model.getEmail());
        resource.setCreatedAt(model.getCreatedAt());
        resource.setCreatedBy(model.getCreatedBy());
        resource.setModifiedAt(model.getModifiedAt());
        return resource;
    }
}
