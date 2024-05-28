package it.pagopa.selfcare.pagopa.backoffice.mapper;

import it.pagopa.selfcare.pagopa.backoffice.model.creditorinstituions.CreditorInstitutionInfo;
import it.pagopa.selfcare.pagopa.backoffice.model.institutions.DelegationExternal;
import org.modelmapper.Converter;
import org.modelmapper.spi.MappingContext;

/**
 * Converter class that specify how to convert a {@link DelegationExternal} instance to a {@link CreditorInstitutionInfo} instance
 */
public class ConvertDelegationExternalToCreditorInstitutionInfo implements Converter<DelegationExternal, CreditorInstitutionInfo> {

    @Override
    public CreditorInstitutionInfo convert(MappingContext<DelegationExternal, CreditorInstitutionInfo> context) {
        DelegationExternal model = context.getSource();

        return CreditorInstitutionInfo.builder()
                .businessName(model.getInstitutionName())
                .ciTaxCode(model.getTaxCode())
                .build();
    }
}