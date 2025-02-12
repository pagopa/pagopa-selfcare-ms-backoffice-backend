package it.pagopa.selfcare.pagopa.backoffice.mapper;

import it.pagopa.selfcare.pagopa.backoffice.model.institutions.InstitutionBase;
import it.pagopa.selfcare.pagopa.backoffice.model.institutions.client.Institution;
import org.modelmapper.Converter;
import org.modelmapper.spi.MappingContext;

import java.util.Collections;

public class ConvertInstitutionToInstitutionBase implements Converter<Institution, InstitutionBase> {
    @Override
    public InstitutionBase convert(MappingContext<Institution, InstitutionBase> mappingContext) {
        Institution src = mappingContext.getSource();
        return InstitutionBase.builder()
                .id(src.getId())
                .userProductRoles(Collections.emptyList())
                .description(src.getDescription())
                .build();
    }
}

