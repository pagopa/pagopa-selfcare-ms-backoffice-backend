package it.pagopa.selfcare.pagopa.backoffice.component;

import it.pagopa.selfcare.pagopa.backoffice.client.ExternalApiClient;
import it.pagopa.selfcare.pagopa.backoffice.model.institutions.InstitutionBase;
import it.pagopa.selfcare.pagopa.backoffice.model.institutions.InstitutionDetail;
import it.pagopa.selfcare.pagopa.backoffice.model.institutions.client.Institution;
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

    @Cacheable(cacheNames = "getInstitutionsForOperator")
    public List<InstitutionBase> getInstitutionsForOperator(String taxCode) {
        return externalApiClient.getInstitutionsFiltered(taxCode).getInstitutions().stream()
                .map(elem -> modelMapper.map(elem, InstitutionBase.class))
                .toList();
    }

    @Cacheable(cacheNames = "getInstitutions")
    public List<InstitutionBase> getInstitutions(String userIdForAuth) {
        List<InstitutionBase> institutionsBaseList;
        Collection<UserInstitution> institutions = externalApiClient.getUserInstitution(
                userIdForAuth, null, null, null, null, null, null);
        institutionsBaseList = institutions.stream()
                .map(elem -> modelMapper.map(elem, InstitutionBase.class))
                .toList();
        return institutionsBaseList;
    }

    @Cacheable(cacheNames = "getInstitutionDetail")
    public InstitutionDetail getInstitutionDetail(String institutionId) {
        Institution institution = externalApiClient.getInstitution(institutionId);
        return modelMapper.map(institution, InstitutionDetail.class);
    }

}

