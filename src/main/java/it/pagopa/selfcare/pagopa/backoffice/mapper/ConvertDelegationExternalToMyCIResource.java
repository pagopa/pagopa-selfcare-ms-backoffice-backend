package it.pagopa.selfcare.pagopa.backoffice.mapper;

import it.pagopa.selfcare.pagopa.backoffice.model.institutions.DelegationExternal;
import it.pagopa.selfcare.pagopa.backoffice.model.institutions.MyCIResource;
import org.modelmapper.Converter;
import org.modelmapper.spi.MappingContext;

/**
 * Converter class that specify how to convert a {@link DelegationExternal} instance to a {@link MyCIResource} instance
 */
public class ConvertDelegationExternalToMyCIResource implements Converter<DelegationExternal, MyCIResource> {

    @Override
    public MyCIResource convert(MappingContext<DelegationExternal, MyCIResource> context) {
        DelegationExternal model = context.getSource();

        return MyCIResource.builder()
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