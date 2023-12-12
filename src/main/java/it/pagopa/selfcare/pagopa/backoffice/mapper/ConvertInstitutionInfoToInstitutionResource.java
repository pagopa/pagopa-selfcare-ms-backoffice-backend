package it.pagopa.selfcare.pagopa.backoffice.mapper;

import it.pagopa.selfcare.pagopa.backoffice.model.institutions.*;
import it.pagopa.selfcare.pagopa.backoffice.model.institutions.client.*;
import org.modelmapper.Converter;
import org.modelmapper.spi.MappingContext;

public class ConvertInstitutionInfoToInstitutionResource implements Converter<InstitutionInfo, InstitutionDetail> {


    private static AssistanceContact convertSupportContact(SupportContact model) {
        AssistanceContact resource = null;
        if(model != null) {
            resource = new AssistanceContact();
            resource.setSupportEmail(model.getSupportEmail());
            resource.setSupportPhone(model.getSupportPhone());
        }
        return resource;
    }

    private static CompanyInformation convertBusinessData(BusinessData model) {
        CompanyInformation resource = null;
        if(model != null) {
            resource = new CompanyInformation();
            resource.setRea(model.getRea());
            resource.setShareCapital(model.getShareCapital());
            resource.setBusinessRegisterPlace(model.getBusinessRegisterPlace());
        }
        return resource;
    }

    private static PspData convertPaymentServiceProvider(PaymentServiceProvider model) {
        PspData resource = null;
        if(model != null) {
            resource = new PspData();
            resource.setBusinessRegisterNumber(model.getBusinessRegisterNumber());
            resource.setLegalRegisterName(model.getLegalRegisterName());
            resource.setLegalRegisterNumber(model.getLegalRegisterNumber());
            resource.setAbiCode(model.getAbiCode());
            resource.setVatNumberGroup(model.getVatNumberGroup());
        }
        return resource;
    }

    private static DpoData convertDataProtectionOfficer(DataProtectionOfficer model) {
        DpoData resource = null;
        if(model != null) {
            resource = new DpoData();
            resource.setAddress(model.getAddress());
            resource.setPec(model.getPec());
            resource.setEmail(model.getEmail());
        }
        return resource;
    }

    @Override
    public InstitutionDetail convert(MappingContext<InstitutionInfo, InstitutionDetail> context) {
        InstitutionInfo model = context.getSource();
        InstitutionDetail resource = null;
        if(model != null) {
            resource = new InstitutionDetail();
            resource.setId(model.getId());
            resource.setDescription(model.getDescription());
            resource.setInstitutionType(model.getInstitutionType());
            resource.setDigitalAddress(model.getDigitalAddress());
            resource.setExternalId(model.getExternalId());
            resource.setOrigin(model.getOrigin());
            resource.setOriginId(model.getOriginId());
            resource.setTaxCode(model.getTaxCode());
            resource.setUserProductRoles(model.getUserProductRoles());
            if(model.getBilling() != null) {
                resource.setRecipientCode(model.getBilling().getRecipientCode());
            }
            resource.setCompanyInformations(convertBusinessData(model.getBusinessData()));
            resource.setAssistanceContacts(convertSupportContact(model.getSupportContact()));
            resource.setPspData(convertPaymentServiceProvider(model.getPaymentServiceProvider()));
            resource.setDpoData(convertDataProtectionOfficer(model.getDataProtectionOfficer()));
            resource.setAddress(model.getAddress());
            resource.setStatus(model.getStatus());
        }
        return resource;
    }
}


