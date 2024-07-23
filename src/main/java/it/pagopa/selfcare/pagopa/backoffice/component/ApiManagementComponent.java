package it.pagopa.selfcare.pagopa.backoffice.component;

import it.pagopa.selfcare.pagopa.backoffice.client.ExternalApiClient;
import it.pagopa.selfcare.pagopa.backoffice.model.institutions.InstitutionDetail;
import it.pagopa.selfcare.pagopa.backoffice.model.users.client.UserInstitution;
import it.pagopa.selfcare.pagopa.backoffice.model.users.client.UserInstitutionProduct;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Slf4j
@Component
public class ApiManagementComponent {

    private final ExternalApiClient externalApiClient;

    private final ModelMapper modelMapper;

    public ApiManagementComponent(ExternalApiClient externalApiClient, ModelMapper modelMapper) {
        this.externalApiClient = externalApiClient;
        this.modelMapper = modelMapper;
    }

    @Cacheable(cacheNames = "getInstitutionDetailsForOperator")
    public List<InstitutionDetail> getInstitutionDetailsForOperator(String taxCode) {
        return externalApiClient.getInstitutionsFiltered(taxCode).getInstitutions().stream()
                .map(elem -> modelMapper.map(elem, InstitutionDetail.class))
                .toList();
    }

    @Cacheable(cacheNames = "getInstitutionDetails")
    public List<InstitutionDetail> getInstitutionDetails(String userIdForAuth) {
        List<InstitutionDetail> institutionDetails;
        Collection<UserInstitution> institutions = externalApiClient.getUserInstitution(
                userIdForAuth, null, null, null, List.of("ACTIVE"), null, null);
        institutionDetails = institutions.stream()
                .map(userInstitution -> Pair.of(userInstitution, externalApiClient.getInstitution(
                        userInstitution.getInstitutionId())))
                .map(pair -> {
                    UserInstitution userInstitution = pair.getFirst();
                    InstitutionDetail institutionDetail = modelMapper.map(pair.getSecond(), InstitutionDetail.class);
                    institutionDetail.setUserProductRoles(userInstitution.getProducts() != null ?
                            userInstitution.getProducts().stream().map(
                                    UserInstitutionProduct::getProductRole).toList() : new ArrayList<>());
                    return institutionDetail;
                })
                .toList();
        return institutionDetails;
    }
}

