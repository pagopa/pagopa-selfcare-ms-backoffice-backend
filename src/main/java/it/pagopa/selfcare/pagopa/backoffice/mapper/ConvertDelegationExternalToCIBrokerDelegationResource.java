package it.pagopa.selfcare.pagopa.backoffice.mapper;

import it.pagopa.selfcare.pagopa.backoffice.model.institutions.DelegationExternal;
import it.pagopa.selfcare.pagopa.backoffice.model.institutions.CIBrokerDelegationResource;
import org.modelmapper.Converter;
import org.modelmapper.spi.MappingContext;

/**
 * Converter class that specify how to convert a {@link DelegationExternal} instance to a {@link CIBrokerDelegationResource} instance
 */
public class ConvertDelegationExternalToCIBrokerDelegationResource implements Converter<DelegationExternal, CIBrokerDelegationResource> {

    @Override
    public CIBrokerDelegationResource convert(MappingContext<DelegationExternal, CIBrokerDelegationResource> context) {
        DelegationExternal model = context.getSource();

        return CIBrokerDelegationResource.builder()
                .id(model.getId())
                .brokerId(model.getBrokerId())
                .brokerName(model.getBrokerName())
                .institutionName(model.getInstitutionName())
                .institutionId(model.getInstitutionId())
                .institutionTaxCode(model.getTaxCode())
                .institutionType(model.getInstitutionType())
                .build();
    }
}