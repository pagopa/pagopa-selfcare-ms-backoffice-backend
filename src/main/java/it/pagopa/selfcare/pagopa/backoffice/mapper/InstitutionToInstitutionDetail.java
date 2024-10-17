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
        @NotNull Institution elem = context.getSource();

        PspData pspData = null;
        if(elem.getPaymentServiceProvider() != null) {
            pspData = PspData.builder()
                    .abiCode(elem.getPaymentServiceProvider().getAbiCode())
                    .businessRegisterNumber(elem.getPaymentServiceProvider().getBusinessRegisterNumber())
                    .vatNumberGroup(elem.getPaymentServiceProvider().getVatNumberGroup())
                    .legalRegisterName(elem.getPaymentServiceProvider().getLegalRegisterName())
                    .legalRegisterNumber(elem.getPaymentServiceProvider().getBusinessRegisterNumber())
                    .build();
        }
        return InstitutionDetail.builder()
                .address(elem.getAddress())
                .id(elem.getId())
                .originId(elem.getOriginId())
                .digitalAddress(elem.getDigitalAddress())
                .address(elem.getAddress())
                .taxCode(elem.getTaxCode())
                .userProductRoles(List.of(UserProductRole.builder().productRole("admin").build()))
                .status("ACTIVE")
                .origin(elem.getOrigin())
                .externalId(elem.getExternalId())
                .description(elem.getDescription())
                .institutionType(InstitutionType.valueOf(elem.getInstitutionType()))
                .assistanceContacts(AssistanceContact.builder()
                        .supportEmail(elem.getSupportEmail())
                        .supportPhone(elem.getSupportPhone())
                        .build())
                .pspData(pspData)
                .build();
    }
}
