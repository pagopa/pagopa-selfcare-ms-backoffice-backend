package it.pagopa.selfcare.pagopa.backoffice.config;

import it.pagopa.selfcare.pagopa.backoffice.mapper.ConvertInstitutionInfoToInstitutionResource;
import it.pagopa.selfcare.pagopa.backoffice.model.institutions.InstitutionDetail;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MappingsConfiguration {

  @Bean
  ModelMapper modelMapper() {

    ModelMapper mapper = new ModelMapper();
    mapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);

    mapper.createTypeMap(it.pagopa.selfcare.pagopa.backoffice.model.institutions.client.InstitutionInfo.class, InstitutionDetail.class).setConverter(new ConvertInstitutionInfoToInstitutionResource());

    return mapper;
  }
}
