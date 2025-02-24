package it.pagopa.selfcare.pagopa.backoffice.component;

import it.pagopa.selfcare.pagopa.backoffice.client.ExternalApiClient;
import it.pagopa.selfcare.pagopa.backoffice.model.institutions.InstitutionBase;
import it.pagopa.selfcare.pagopa.backoffice.model.institutions.InstitutionDetail;
import it.pagopa.selfcare.pagopa.backoffice.model.institutions.client.Institution;
import it.pagopa.selfcare.pagopa.backoffice.model.users.client.UserInstitution;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

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

    Collection<UserInstitution> institutions = new ArrayList<>();
    Collection<UserInstitution> pageInstitutions;
    int page = 0;
    do {
      pageInstitutions =
          externalApiClient.getUserInstitution(userIdForAuth, null, null, null, null, page, 100);
      institutions.addAll(pageInstitutions);
      page += 1;
    } while (!pageInstitutions.isEmpty());
    institutionsBaseList =
        institutions.stream().map(elem -> modelMapper.map(elem, InstitutionBase.class)).toList();
    return institutionsBaseList;
  }

  @Cacheable(cacheNames = "getInstitutionDetail")
  public InstitutionDetail getInstitutionDetail(String institutionId) {
    Institution institution = externalApiClient.getInstitution(institutionId);
    return modelMapper.map(institution, InstitutionDetail.class);
  }
}
