package it.pagopa.selfcare.pagopa.backoffice.web.model.mapper;

import it.pagopa.selfcare.pagopa.backoffice.connector.model.tavoloOp.TavoloOpOperations;
import it.pagopa.selfcare.pagopa.backoffice.web.model.gec.BundleResource;
import it.pagopa.selfcare.pagopa.backoffice.web.model.tavoloop.TavoloOpResource;


public class TavoloOpMapper {

    public static TavoloOpResource toResource(TavoloOpOperations tavoloOpOperations){
        if(tavoloOpOperations == null){
            return null;
        }
        TavoloOpResource response = new TavoloOpResource();

        response.setId(tavoloOpOperations.getId());
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
}
