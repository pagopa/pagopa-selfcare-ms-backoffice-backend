package it.pagopa.selfcare.pagopa.backoffice.mapper;

import it.pagopa.selfcare.pagopa.backoffice.model.institutions.InstitutionBase;
import it.pagopa.selfcare.pagopa.backoffice.model.institutions.UserProductRole;
import it.pagopa.selfcare.pagopa.backoffice.model.users.client.UserInstitution;
import org.modelmapper.Converter;
import org.modelmapper.spi.MappingContext;

public class ConvertUserInstitutionToInstitutionBase implements Converter<UserInstitution, InstitutionBase> {

    @Override
    public InstitutionBase convert(MappingContext<UserInstitution, InstitutionBase> mappingContext) {
        UserInstitution src = mappingContext.getSource();
        return InstitutionBase.builder()
                .id(src.getInstitutionId())
                .userProductRoles(src.getProducts().stream().map(item ->
                        UserProductRole.builder()
                                .productRole(item.getProductRole())
                                .productRoleLabel(item.getProductRoleLabel())
                                .build()).toList())
                .description(src.getInstitutionDescription())
                .build();
    }

}

