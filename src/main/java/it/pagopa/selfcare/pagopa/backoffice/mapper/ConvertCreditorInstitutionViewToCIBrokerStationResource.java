package it.pagopa.selfcare.pagopa.backoffice.mapper;

import it.pagopa.selfcare.pagopa.backoffice.model.creditorinstituions.CreditorInstitutionView;
import it.pagopa.selfcare.pagopa.backoffice.model.institutions.CIBrokerStationResource;
import org.modelmapper.Converter;
import org.modelmapper.spi.MappingContext;

/**
 * Converter class that specify how to convert a {@link CreditorInstitutionView} instance to a {@link CIBrokerStationResource} instance
 */
public class ConvertCreditorInstitutionViewToCIBrokerStationResource implements Converter<CreditorInstitutionView, CIBrokerStationResource> {

    @Override
    public CIBrokerStationResource convert(MappingContext<CreditorInstitutionView, CIBrokerStationResource> context) {
        CreditorInstitutionView model = context.getSource();

        return CIBrokerStationResource.builder()
                .brokerTaxCode(model.getIdIntermediarioPa())
                .ciTaxCode(model.getIdDominio())
                .stationCode(model.getIdStazione())
                .auxDigit(model.getAuxDigit())
                .segregationCode(model.getSegregazione())
                .applicationCode(model.getProgressivo())
                .stationEnabled(model.getStationEnabled())
                .build();
    }
}