package it.pagopa.selfcare.pagopa.backoffice.web.model.mapper;

import it.pagopa.selfcare.pagopa.backoffice.connector.model.institution.Attribute;
import it.pagopa.selfcare.pagopa.backoffice.connector.model.institution.Institution;
import it.pagopa.selfcare.pagopa.backoffice.web.model.institutions.AttributeResource;
import it.pagopa.selfcare.pagopa.backoffice.web.model.institutions.InstitutionDetailResource;

import java.util.stream.Collectors;

public class InstitutionMapper {
    
    public static InstitutionDetailResource toResource(Institution model){
        InstitutionDetailResource resource = null;
        if (model != null){
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
