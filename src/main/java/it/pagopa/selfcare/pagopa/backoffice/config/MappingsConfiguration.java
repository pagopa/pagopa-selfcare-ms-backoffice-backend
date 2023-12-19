package it.pagopa.selfcare.pagopa.backoffice.config;

import it.pagopa.selfcare.pagopa.backoffice.mapper.ConvertInstitutionInfoToInstitutionResource;
import it.pagopa.selfcare.pagopa.backoffice.mapper.LocalDateToOffset;
import it.pagopa.selfcare.pagopa.backoffice.mapper.OffsetToLocalDate;
import it.pagopa.selfcare.pagopa.backoffice.model.institutions.InstitutionDetail;
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
        mapper.createTypeMap(LocalDate.class, OffsetDateTime.class).setConverter(new LocalDateToOffset());
        mapper.createTypeMap(OffsetDateTime.class, LocalDate.class).setConverter(new OffsetToLocalDate());

        return mapper;
    }
}
