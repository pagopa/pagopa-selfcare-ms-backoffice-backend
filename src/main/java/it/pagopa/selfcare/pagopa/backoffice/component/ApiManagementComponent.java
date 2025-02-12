package it.pagopa.selfcare.pagopa.backoffice.component;

import it.pagopa.selfcare.pagopa.backoffice.client.ExternalApiClient;
import it.pagopa.selfcare.pagopa.backoffice.model.institutions.AssistanceContact;
import it.pagopa.selfcare.pagopa.backoffice.model.institutions.InstitutionBase;
import it.pagopa.selfcare.pagopa.backoffice.model.institutions.InstitutionDetail;
import it.pagopa.selfcare.pagopa.backoffice.model.institutions.PspData;
import it.pagopa.selfcare.pagopa.backoffice.model.institutions.UserProductRole;
import it.pagopa.selfcare.pagopa.backoffice.model.institutions.client.Institution;
import it.pagopa.selfcare.pagopa.backoffice.model.institutions.client.InstitutionType;
import it.pagopa.selfcare.pagopa.backoffice.model.users.client.UserInstitution;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

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
    public InstitutionDetail getInstitutionDetail(String institutionId, String userId) {
        Institution institution = this.externalApiClient.getInstitution(institutionId);
        List<UserInstitution> userInstitution =
                this.externalApiClient.getUserInstitution(userId, institutionId, null, null, null, null, null);

        return buildInstitutionDetail(institution, userInstitution.get(0)); // TODO add check on list size?
    }

    private InstitutionDetail buildInstitutionDetail(Institution institution, UserInstitution userInstitution) {
        PspData pspData = null;
        if(institution.getPaymentServiceProvider() != null) {
            pspData = PspData.builder()
                    .abiCode(institution.getPaymentServiceProvider().getAbiCode())
                    .businessRegisterNumber(institution.getPaymentServiceProvider().getBusinessRegisterNumber())
                    .vatNumberGroup(institution.getPaymentServiceProvider().getVatNumberGroup())
                    .legalRegisterName(institution.getPaymentServiceProvider().getLegalRegisterName())
                    .legalRegisterNumber(institution.getPaymentServiceProvider().getBusinessRegisterNumber())
                    .build();
        }

        UserProductRole userProductRole = UserProductRole.builder().productRole("admin").build(); // maintain old logic
        if (userInstitution.getProducts() != null && !userInstitution.getProducts().isEmpty()) {
            userProductRole = UserProductRole.builder()
                    .productRole(userInstitution.getProducts().get(0).getProductRole())
                    .productRoleLabel(userInstitution.getProducts().get(0).getProductRoleLabel())
                    .build();
        }

        return InstitutionDetail.builder()
                .address(institution.getAddress())
                .id(institution.getId())
                .originId(institution.getOriginId())
                .digitalAddress(institution.getDigitalAddress())
                .address(institution.getAddress())
                .taxCode(institution.getTaxCode())
                .userProductRoles(List.of(userProductRole))
                .status("ACTIVE") // TODO take status form institution.onboarding ?
                .origin(institution.getOrigin())
                .externalId(institution.getExternalId())
                .description(institution.getDescription())
                .institutionType(InstitutionType.valueOf(institution.getInstitutionType()))
                .assistanceContacts(AssistanceContact.builder()
                        .supportEmail(institution.getSupportEmail())
                        .supportPhone(institution.getSupportPhone())
                        .build())
                .pspData(pspData)
                .build();
    }

}

