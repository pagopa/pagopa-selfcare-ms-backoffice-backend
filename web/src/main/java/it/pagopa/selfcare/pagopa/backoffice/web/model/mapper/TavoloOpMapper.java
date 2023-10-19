package it.pagopa.selfcare.pagopa.backoffice.web.model.mapper;

import it.pagopa.selfcare.pagopa.backoffice.connector.model.tavoloOp.TavoloOp;
import it.pagopa.selfcare.pagopa.backoffice.web.model.tavoloOp.TavoloOpDto;
import it.pagopa.selfcare.pagopa.backoffice.web.model.tavoloOp.TavoloOpResource;

public class TavoloOpMapper {

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
