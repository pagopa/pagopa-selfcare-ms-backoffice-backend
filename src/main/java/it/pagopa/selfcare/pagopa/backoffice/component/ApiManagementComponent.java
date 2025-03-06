package it.pagopa.selfcare.pagopa.backoffice.component;

import it.pagopa.selfcare.pagopa.backoffice.client.ExternalApiClient;
import it.pagopa.selfcare.pagopa.backoffice.exception.AppError;
import it.pagopa.selfcare.pagopa.backoffice.exception.AppException;
import it.pagopa.selfcare.pagopa.backoffice.model.institutions.AssistanceContact;
import it.pagopa.selfcare.pagopa.backoffice.model.institutions.InstitutionBase;
import it.pagopa.selfcare.pagopa.backoffice.model.institutions.InstitutionDetail;
import it.pagopa.selfcare.pagopa.backoffice.model.institutions.PspData;
import it.pagopa.selfcare.pagopa.backoffice.model.institutions.UserProductRole;
import it.pagopa.selfcare.pagopa.backoffice.model.institutions.client.Institution;
import it.pagopa.selfcare.pagopa.backoffice.model.institutions.client.InstitutionType;
import it.pagopa.selfcare.pagopa.backoffice.model.users.client.UserInstitution;
import it.pagopa.selfcare.pagopa.backoffice.model.users.client.UserInstitutionProduct;
import it.pagopa.selfcare.pagopa.backoffice.model.users.client.UserProductStatus;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

import java.util.Objects;

import static it.pagopa.selfcare.pagopa.backoffice.util.Constants.PAGOPA_BACKOFFICE_PRODUCT_ID;

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
        return externalApiClient.getInstitutionsFiltered(taxCode).getInstitutions().parallelStream()
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
        institutions.parallelStream().map(elem -> modelMapper.map(elem, InstitutionBase.class)).filter(Objects::nonNull)
                .toList();
        return institutionsBaseList;
    }

  @Cacheable(cacheNames = "getInstitutionDetailForOperator")
    public InstitutionDetail getInstitutionDetailForOperator(String institutionId) {
        Institution institution = this.externalApiClient.getInstitution(institutionId);

        return buildInstitutionDetail(institution, null);
    }

    @Cacheable(cacheNames = "getInstitutionDetail")
  public InstitutionDetail getInstitutionDetail(String institutionId, String userId) {
    Institution institution = this.externalApiClient.getInstitution(institutionId);
        List<UserInstitution> userInstitution = this.externalApiClient
                .getUserInstitution(userId, institutionId, null, null, null, null, null);

        UserInstitutionProduct backofficeProduct = getBackofficeProductIfPresentOrElseThrowUnauthorizedException(userInstitution);
    return buildInstitutionDetail(institution, backofficeProduct);
    }

    private UserInstitutionProduct getBackofficeProductIfPresentOrElseThrowUnauthorizedException(List<UserInstitution> userInstitution) {
        if (userInstitution.isEmpty() || userInstitution.get(0).getProducts() == null) {
            throw new AppException(AppError.UNAUTHORIZED);
        }
        List<UserInstitutionProduct> products = userInstitution.get(0).getProducts().parallelStream()
                .filter(item -> item.getStatus().equals(UserProductStatus.ACTIVE) && item.getProductId().equals(PAGOPA_BACKOFFICE_PRODUCT_ID))
                .toList();
        if (products.isEmpty()) {
            throw new AppException(AppError.UNAUTHORIZED);
        }
        return products.get(0);
    }

    private InstitutionDetail buildInstitutionDetail(Institution institution, UserInstitutionProduct userInstitutionProduct) {
        return InstitutionDetail.builder()
                .address(institution.getAddress())
                .id(institution.getId())
                .originId(institution.getOriginId())
                .digitalAddress(institution.getDigitalAddress())
                .address(institution.getAddress())
                .taxCode(institution.getTaxCode())
                .userProductRoles(getUserProductRole(userInstitutionProduct))
                .onboarding(institution.getOnboarding())
                .status("ACTIVE") // should be retrieved from institution.onboarding.status for prod-pagopa product
                .origin(institution.getOrigin())
                .externalId(institution.getExternalId())
                .description(institution.getDescription())
                .institutionType(InstitutionType.valueOf(institution.getInstitutionType()))
                .assistanceContacts(AssistanceContact.builder()
                        .supportEmail(institution.getSupportEmail())
                        .supportPhone(institution.getSupportPhone())
                        .build())
                .pspData(getPspData(institution))
                .build();
    }

    private List<UserProductRole> getUserProductRole(UserInstitutionProduct userInstitutionProduct) {
        UserProductRole userProductRole = UserProductRole.builder().productRole("admin").build(); // maintain old logic
        if (userInstitutionProduct != null) {
            userProductRole=  UserProductRole.builder()
                    .productRole(userInstitutionProduct.getProductRole())
                    .productRoleLabel(userInstitutionProduct.getProductRoleLabel())
                    .build();
        }
        return List.of(userProductRole);
    }

    private PspData getPspData(Institution institution) {
        if(institution.getPaymentServiceProvider() != null) {
           return PspData.builder()
                    .abiCode(institution.getPaymentServiceProvider().getAbiCode())
                    .businessRegisterNumber(institution.getPaymentServiceProvider().getBusinessRegisterNumber())
                    .vatNumberGroup(institution.getPaymentServiceProvider().getVatNumberGroup())
                    .legalRegisterName(institution.getPaymentServiceProvider().getLegalRegisterName())
                    .legalRegisterNumber(institution.getPaymentServiceProvider().getBusinessRegisterNumber())
                    .build();
        }
        return null;
    }
}