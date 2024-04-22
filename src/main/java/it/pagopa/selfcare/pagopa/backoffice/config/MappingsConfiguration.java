package it.pagopa.selfcare.pagopa.backoffice.config;

import it.pagopa.selfcare.pagopa.backoffice.mapper.*;
import it.pagopa.selfcare.pagopa.backoffice.model.creditorinstituions.CreditorInstitutionView;
import it.pagopa.selfcare.pagopa.backoffice.model.iban.Iban;
import it.pagopa.selfcare.pagopa.backoffice.model.iban.IbanCreateApiconfig;
import it.pagopa.selfcare.pagopa.backoffice.model.institutions.CIBrokerDelegationResource;
import it.pagopa.selfcare.pagopa.backoffice.model.institutions.CIBrokerStationResource;
import it.pagopa.selfcare.pagopa.backoffice.model.institutions.DelegationExternal;
import it.pagopa.selfcare.pagopa.backoffice.model.institutions.InstitutionDetail;
import it.pagopa.selfcare.pagopa.backoffice.model.institutions.client.Institution;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.LocalDate;
import java.time.OffsetDateTime;

@Configuration
public class MappingsConfiguration {

    @Bean
    ModelMapper modelMapper() {

        ModelMapper mapper = new ModelMapper();
        mapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);

        mapper.createTypeMap(it.pagopa.selfcare.pagopa.backoffice.model.institutions.client.InstitutionInfo.class, InstitutionDetail.class).setConverter(new ConvertInstitutionInfoToInstitutionResource());
        mapper.createTypeMap(Iban.class, IbanCreateApiconfig.class).setConverter(new ConvertIbanToIbanCreateApiconfig());
        mapper.createTypeMap(LocalDate.class, OffsetDateTime.class).setConverter(new LocalDateToOffset());
        mapper.createTypeMap(OffsetDateTime.class, LocalDate.class).setConverter(new OffsetToLocalDate());
        mapper.createTypeMap(DelegationExternal.class, CIBrokerDelegationResource.class).setConverter(new ConvertDelegationExternalToCIBrokerDelegationResource());
        mapper.createTypeMap(CreditorInstitutionView.class, CIBrokerStationResource.class).setConverter(new ConvertCreditorInstitutionViewToCIBrokerStationResource());
        mapper.createTypeMap(Institution.class, InstitutionDetail.class).setConverter(new InstitutionToInstitutionDetail());

        return mapper;
    }
}
