package it.pagopa.selfcare.pagopa.backoffice.web.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import it.pagopa.selfcare.pagopa.backoffice.connector.model.delegation.Delegation;
import it.pagopa.selfcare.pagopa.backoffice.connector.model.institution.Institution;
import it.pagopa.selfcare.pagopa.backoffice.connector.model.institution.InstitutionApiKeys;
import it.pagopa.selfcare.pagopa.backoffice.connector.model.institution.InstitutionInfo;
import it.pagopa.selfcare.pagopa.backoffice.connector.model.product.Product;
import it.pagopa.selfcare.pagopa.backoffice.connector.security.SelfCareUser;
import it.pagopa.selfcare.pagopa.backoffice.core.ApiManagementService;
import it.pagopa.selfcare.pagopa.backoffice.core.ExternalApiService;
import it.pagopa.selfcare.pagopa.backoffice.web.model.delegation.DelegationResource;
import it.pagopa.selfcare.pagopa.backoffice.web.model.institutions.InstitutionDetailResource;
import it.pagopa.selfcare.pagopa.backoffice.web.model.institutions.InstitutionResource;
import it.pagopa.selfcare.pagopa.backoffice.web.model.mapper.ApiManagerMapper;
import it.pagopa.selfcare.pagopa.backoffice.web.model.mapper.DelegationMapper;
import it.pagopa.selfcare.pagopa.backoffice.web.model.mapper.InstitutionMapper;
import it.pagopa.selfcare.pagopa.backoffice.web.model.mapper.ProductMapper;
import it.pagopa.selfcare.pagopa.backoffice.web.model.products.ProductsResource;
import it.pagopa.selfcare.pagopa.backoffice.web.model.subscriptions.ApiKeysResource;
import it.pagopa.selfcare.pagopa.backoffice.web.model.subscriptions.Subscription;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping(value = "/institutions", produces = MediaType.APPLICATION_JSON_VALUE)
@Api(tags = "institution")
public class InstitutionController {

    private final ApiManagementService apiManagementService;
    private final ExternalApiService externalApiService;

    @Autowired
    public InstitutionController(ApiManagementService apiManagementService, ExternalApiService externalApiService) {
        this.apiManagementService = apiManagementService;
        this.externalApiService = externalApiService;
    }

    @GetMapping("/{institutionId}/api-keys")
    @ResponseStatus(HttpStatus.OK)
    @ApiOperation(value = "", notes = "${swagger.api.institution.getInstitutionApiKeys}")
    public List<ApiKeysResource> getInstitutionApiKeys(
            @ApiParam("${swagger.model.institution.id}")
            @PathVariable("institutionId") String institutionId) {
        
        
        List<InstitutionApiKeys> institutionApiKeysList = apiManagementService.getInstitutionApiKeys(institutionId);

        return ApiManagerMapper.toApikeysResourceList(institutionApiKeysList);
    }

    @PostMapping("/{institutionId}/api-keys")
    @ResponseStatus(HttpStatus.CREATED)
    @ApiOperation(value = "", notes = "${swagger.api.institution.createInstitutionApiKeys}")
    public List<ApiKeysResource> createInstitutionApiKeys(@ApiParam("${swagger.model.institution.id}")
                                                          @PathVariable("institutionId") String institutionId,
                                                          @ApiParam("${swagger.model.subscription.code}")
                                                          @RequestParam("subscriptionCode") String subscriptionCode
    ) {
        
        
        Subscription subscriptionEnum = Subscription.valueOf(subscriptionCode);
        List<InstitutionApiKeys> institutionKeys = apiManagementService.createSubscriptionKeys(institutionId, subscriptionEnum.getScope(), subscriptionEnum.getPrefixId(), subscriptionEnum.getDisplayName());

        return ApiManagerMapper.toApikeysResourceList(institutionKeys);
    }

    @PostMapping("/{subscriptionid}/api-keys/primary/regenerate")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @ApiOperation(value = "", notes = "${swagger.api.institution.regeneratePrimaryKey}")
    public void regeneratePrimaryKey(@ApiParam("${swagger.model.subscription.id}")
                                     @PathVariable("subscriptionid") String subscriptionid) {
        
        
        apiManagementService.regeneratePrimaryKey(subscriptionid);
        
    }

    @PostMapping("/{subscriptionid}/api-keys/secondary/regenerate")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @ApiOperation(value = "", notes = "${swagger.api.institution.regenerateSecondaryKey}")
    public void regenerateSecondaryKey(@ApiParam("${swagger.model.subscription.id}")
                                       @PathVariable("subscriptionid") String subscriptionid) {
        
        
        apiManagementService.regenerateSecondaryKey(subscriptionid);
        
    }

    @GetMapping("")
    @ResponseStatus(HttpStatus.OK)
    @ApiOperation(value = "", notes = "${swagger.api.institution.getInstitutions}")
    public List<InstitutionResource> getInstitutions() {
        

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userIdForAuth = "";
        if (authentication != null && authentication.getPrincipal() instanceof SelfCareUser) {
            SelfCareUser user = (SelfCareUser) authentication.getPrincipal();
            userIdForAuth = user.getId();
        }

        Collection<InstitutionInfo> institutions = externalApiService.getInstitutions(userIdForAuth);

        return institutions.stream()
                .map(InstitutionMapper::toResource)
                .collect(Collectors.toList());
    }

    @GetMapping("/{institutionId}")
    @ResponseStatus(HttpStatus.OK)
    @ApiOperation(value = "", notes = "${swagger.api.institution.getInstitution}")
    public InstitutionDetailResource getInstitution(@ApiParam("${swagger.model.institution.id}")
                                                    @PathVariable("institutionId") String institutionId) {
        
        
        Institution institution = externalApiService.getInstitution(institutionId);

        return InstitutionMapper.toResource(institution);
    }

    @GetMapping("/{institutionId}/products")
    @ResponseStatus(HttpStatus.OK)
    @ApiOperation(value = "", notes = "${swagger.api.institution.getInstitutionProducts}")
    public List<ProductsResource> getInstitutionProducts(@ApiParam("${swagger.model.institution.id}")
                                                         @PathVariable("institutionId") String institutionId) {
        
        

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userIdForAuth = "";
        if (authentication != null && authentication.getPrincipal() instanceof SelfCareUser) {
            SelfCareUser user = (SelfCareUser) authentication.getPrincipal();
            userIdForAuth = user.getId();
        }

        List<Product> products = externalApiService.getInstitutionUserProducts(institutionId, userIdForAuth);

        return products.stream()
                .map(ProductMapper::toResource)
                .collect(Collectors.toList());
    }

    @GetMapping("/delegations")
    @ResponseStatus(HttpStatus.OK)
    @ApiOperation(value = "", notes = "${swagger.api.institution.getInstitutionProducts}")
    public List<DelegationResource> getBrokerDelegation(@ApiParam("${swagger.model.institution.id}")
                                                        @RequestParam(required = false,value = "institutionId") String institutionId,
                                                        @ApiParam("${swagger.model.broker.id}")
                                                        @RequestParam(required = false,value = "brokerId") String brokerId) {
        
        

        final String productId = "prod-pagopa";
        final String mode = "FULL";

        List<Delegation> delegations = externalApiService.getBrokerDelegation(institutionId, brokerId, productId, mode);

        return delegations.stream()
                .map(DelegationMapper::toResource)
                .collect(Collectors.toList());
    }
}
