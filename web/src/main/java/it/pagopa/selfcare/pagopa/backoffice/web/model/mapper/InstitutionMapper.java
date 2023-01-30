package it.pagopa.selfcare.pagopa.backoffice.web.model.mapper;

import it.pagopa.selfcare.pagopa.backoffice.connector.model.institution.Attribute;
import it.pagopa.selfcare.pagopa.backoffice.connector.model.institution.Institution;
import it.pagopa.selfcare.pagopa.backoffice.connector.model.institution.InstitutionInfo;
import it.pagopa.selfcare.pagopa.backoffice.web.model.institutions.AttributeResource;
import it.pagopa.selfcare.pagopa.backoffice.web.model.institutions.InstitutionDetailResource;
import it.pagopa.selfcare.pagopa.backoffice.web.model.institutions.InstitutionResource;

import java.util.stream.Collectors;

public class InstitutionMapper {

    private InstitutionMapper() {
    }

    public static InstitutionDetailResource toResource(Institution model) {
        InstitutionDetailResource resource = null;
        if (model != null) {
            resource = new InstitutionDetailResource();
            resource.setId(model.getId());
            resource.setDescription(model.getDescription());
            resource.setAttributes(model.getAttributes().stream().map(InstitutionMapper::toResource).collect(Collectors.toList()));
            resource.setInstitutionType(model.getInstitutionType());
            resource.setDigitalAddress(model.getDigitalAddress());
            resource.setExternalId(model.getExternalId());
            resource.setOrigin(model.getOrigin());
            resource.setOriginId(model.getOriginId());
            resource.setTaxCode(model.getTaxCode());
            resource.setZipCode(model.getZipCode());
            resource.setAddress(model.getAddress());
        }
        return resource;
    }
    
    public static InstitutionResource toResource(InstitutionInfo model){
        InstitutionResource resource = null;
        if (model != null){
            resource = new InstitutionResource();
            resource.setId(model.getId());
            resource.setName(model.getDescription());
            resource.setInstitutionType(model.getInstitutionType());
            resource.setMailAddress(model.getDigitalAddress());
            resource.setExternalId(model.getExternalId());
            resource.setOrigin(model.getOrigin());
            resource.setOriginId(model.getOriginId());
            resource.setFiscalCode(model.getTaxCode());
            resource.setUserProductRoles(model.getUserProductRoles());
            resource.setAddress(model.getAddress());
            resource.setStatus(model.getStatus());
        }
        return resource;
    }
    
    public static AttributeResource toResource(Attribute model){
        AttributeResource resource = null;
        if (model != null){
            resource = new AttributeResource();
            resource.setCode(model.getCode());
            resource.setDescription(model.getDescription());
            resource.setOrigin(model.getOrigin());
        }
        return resource;
    }
}
