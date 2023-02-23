package it.pagopa.selfcare.pagopa.backoffice.web.model.mapper;

import it.pagopa.selfcare.pagopa.backoffice.connector.model.institution.*;
import it.pagopa.selfcare.pagopa.backoffice.web.model.institutions.*;

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
            if (model.getBilling() != null)
                resource.setRecipientCode(model.getBilling().getRecipientCode());
            resource.setCompanyInformations(toResource(model.getBusinessData()));
            resource.setAssistanceContacts(toResource(model.getSupportContact()));
            resource.setPspData(toResource(model.getPaymentServiceProvider()));
            resource.setDpoData(toResource(model.getDataProtectionOfficer()));
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
    public static AssistanceContactsResource toResource(SupportContact model) {
        AssistanceContactsResource resource = null;
        if (model != null) {
            resource = new AssistanceContactsResource();
            resource.setSupportEmail(model.getSupportEmail());
            resource.setSupportPhone(model.getSupportPhone());
        }
        return resource;
    }

    public static CompanyInformationsResource toResource(BusinessData model) {
        CompanyInformationsResource resource = null;
        if (model != null) {
            resource = new CompanyInformationsResource();
            resource.setRea(model.getRea());
            resource.setShareCapital(model.getShareCapital());
            resource.setBusinessRegisterPlace(model.getBusinessRegisterPlace());
        }
        return resource;
    }

    public static PspDataResource toResource(PaymentServiceProvider model) {
        PspDataResource resource = null;
        if (model != null) {
            resource = new PspDataResource();
            resource.setBusinessRegisterNumber(model.getBusinessRegisterNumber());
            resource.setLegalRegisterName(model.getLegalRegisterName());
            resource.setLegalRegisterNumber(model.getLegalRegisterNumber());
            resource.setAbiCode(model.getAbiCode());
            resource.setVatNumberGroup(model.getVatNumberGroup());
        }
        return resource;
    }

    public static DpoDataResource toResource(DataProtectionOfficer model) {
        DpoDataResource resource = null;
        if (model != null) {
            resource = new DpoDataResource();
            resource.setAddress(model.getAddress());
            resource.setPec(model.getPec());
            resource.setEmail(model.getEmail());
        }
        return resource;
    }
}
