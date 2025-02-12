package it.pagopa.selfcare.pagopa.backoffice.mapper;

import it.pagopa.selfcare.pagopa.backoffice.model.institutions.AssistanceContact;
import it.pagopa.selfcare.pagopa.backoffice.model.institutions.InstitutionDetail;
import it.pagopa.selfcare.pagopa.backoffice.model.institutions.PspData;
import it.pagopa.selfcare.pagopa.backoffice.model.institutions.UserProductRole;
import it.pagopa.selfcare.pagopa.backoffice.model.institutions.client.Institution;
import it.pagopa.selfcare.pagopa.backoffice.model.institutions.client.InstitutionType;
import org.modelmapper.Converter;
import org.modelmapper.spi.MappingContext;

import javax.validation.constraints.NotNull;
import java.util.List;


public class InstitutionToInstitutionDetail implements Converter<Institution, InstitutionDetail> {

    @Override
    public InstitutionDetail convert(MappingContext<Institution, InstitutionDetail> context) {
        @NotNull Institution institution = context.getSource();

        PspData pspData = null;
        if(institution.getPaymentServiceProvider() != null) {
            pspData = PspData.builder()
                    .abiCode(institution.getPaymentServiceProvider().getAbiCode())
                    .businessRegisterNumber(institution.getPaymentServiceProvider().getBusinessRegisterNumber())
                    .vatNumberGroup(institution.getPaymentServiceProvider().getVatNumberGroup())
                    .legalRegisterName(institution.getPaymentServiceProvider().getLegalRegisterName())
                    .legalRegisterNumber(institution.getPaymentServiceProvider().getBusinessRegisterNumber())
                    .build();
        }
        return InstitutionDetail.builder()
                .address(institution.getAddress())
                .id(institution.getId())
                .originId(institution.getOriginId())
                .digitalAddress(institution.getDigitalAddress())
                .address(institution.getAddress())
                .taxCode(institution.getTaxCode())
                .userProductRoles(List.of(UserProductRole.builder().productRole("admin").build()))
                .status("ACTIVE")
                .origin(institution.getOrigin())
                .externalId(institution.getExternalId())
                .description(institution.getDescription())
                .institutionType(InstitutionType.valueOf(institution.getInstitutionType()))
                .assistanceContacts(AssistanceContact.builder()
                        .supportEmail(institution.getSupportEmail())
                        .supportPhone(institution.getSupportPhone())
                        .build())
                .pspData(pspData)
                .build();
    }
}
