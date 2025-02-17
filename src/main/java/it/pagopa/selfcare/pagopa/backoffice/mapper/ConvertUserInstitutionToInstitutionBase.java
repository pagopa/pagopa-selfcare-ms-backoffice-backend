package it.pagopa.selfcare.pagopa.backoffice.mapper;

import static it.pagopa.selfcare.pagopa.backoffice.model.users.client.UserProductStatus.ACTIVE;
import static it.pagopa.selfcare.pagopa.backoffice.util.Constants.PAGOPA_BACKOFFICE_PRODUCT_ID;

import it.pagopa.selfcare.pagopa.backoffice.model.institutions.InstitutionBase;
import it.pagopa.selfcare.pagopa.backoffice.model.institutions.UserProductRole;
import it.pagopa.selfcare.pagopa.backoffice.model.users.client.UserInstitution;
import org.modelmapper.Converter;
import org.modelmapper.spi.MappingContext;

public class ConvertUserInstitutionToInstitutionBase implements Converter<UserInstitution, InstitutionBase> {

    @Override
    public InstitutionBase convert(MappingContext<UserInstitution, InstitutionBase> mappingContext) {
        UserInstitution src = mappingContext.getSource();
        InstitutionBase institution = InstitutionBase.builder()
                .id(src.getInstitutionId())
                .userProductRoles(src.getProducts().parallelStream()
                        .filter(item -> item.getStatus().equals(ACTIVE) && item.getProductId().equals(PAGOPA_BACKOFFICE_PRODUCT_ID))
                        .map(item ->
                                UserProductRole.builder()
                                        .productRole(item.getProductRole())
                                        .productRoleLabel(item.getProductRoleLabel())
                                        .build()).toList())
                .description(src.getInstitutionDescription())
                .build();
        if (institution.getUserProductRoles().isEmpty()) {
            return null;
        }
        return institution;
    }

}

