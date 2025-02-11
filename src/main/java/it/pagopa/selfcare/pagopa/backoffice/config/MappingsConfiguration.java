package it.pagopa.selfcare.pagopa.backoffice.config;

import it.pagopa.selfcare.pagopa.backoffice.entity.CreditorInstitutionIbansEntity;
import it.pagopa.selfcare.pagopa.backoffice.entity.IbanEntity;
import it.pagopa.selfcare.pagopa.backoffice.mapper.*;
import it.pagopa.selfcare.pagopa.backoffice.model.creditorinstituions.CreditorInstitutionInfo;
import it.pagopa.selfcare.pagopa.backoffice.model.creditorinstituions.CreditorInstitutionView;
import it.pagopa.selfcare.pagopa.backoffice.model.iban.Iban;
import it.pagopa.selfcare.pagopa.backoffice.model.iban.IbanCreateApiconfig;
import it.pagopa.selfcare.pagopa.backoffice.model.iban.IbanDetails;
import it.pagopa.selfcare.pagopa.backoffice.model.institutions.*;
import it.pagopa.selfcare.pagopa.backoffice.model.institutions.client.Institution;
import it.pagopa.selfcare.pagopa.backoffice.model.users.client.UserInstitution;
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

        mapper.createTypeMap(UserInstitution.class, InstitutionBase.class).setConverter(new ConvertUserInstitutionToInstitutionBase());
        mapper.createTypeMap(Institution.class, InstitutionBase.class).setConverter(new ConvertInstitutionToInstitutionBase());
        mapper.createTypeMap(Iban.class, IbanCreateApiconfig.class).setConverter(new ConvertIbanToIbanCreateApiconfig());
        mapper.createTypeMap(LocalDate.class, OffsetDateTime.class).setConverter(new LocalDateToOffset());
        mapper.createTypeMap(OffsetDateTime.class, LocalDate.class).setConverter(new OffsetToLocalDate());
        mapper.createTypeMap(DelegationExternal.class, CIBrokerDelegationResource.class).setConverter(new ConvertDelegationExternalToCIBrokerDelegationResource());
        mapper.createTypeMap(CreditorInstitutionView.class, CIBrokerStationResource.class).setConverter(new ConvertCreditorInstitutionViewToCIBrokerStationResource());
        mapper.createTypeMap(Institution.class, InstitutionDetail.class).setConverter(new InstitutionToInstitutionDetail());
        mapper.createTypeMap(DelegationExternal.class, CreditorInstitutionInfo.class).setConverter(new ConvertDelegationExternalToCreditorInstitutionInfo());
        mapper.createTypeMap(IbanDetails.class, IbanEntity.class).setConverter(new ConvertIbanDetailsToIbanEntity());
        mapper.createTypeMap(IbanEntity.class, CreditorInstitutionIbansEntity.class).setConverter(new ConvertIbanEntityToCreditorInstitutionIbansEntity());

        return mapper;
    }
}
