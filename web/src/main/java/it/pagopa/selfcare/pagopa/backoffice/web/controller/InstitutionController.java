package it.pagopa.selfcare.pagopa.backoffice.web.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import it.pagopa.selfcare.pagopa.backoffice.connector.logging.LogUtils;
import it.pagopa.selfcare.pagopa.backoffice.connector.model.institution.Institution;
import it.pagopa.selfcare.pagopa.backoffice.connector.model.institution.InstitutionApiKeys;
import it.pagopa.selfcare.pagopa.backoffice.connector.model.institution.InstitutionInfo;
import it.pagopa.selfcare.pagopa.backoffice.connector.model.product.Product;
import it.pagopa.selfcare.pagopa.backoffice.core.ApiManagementService;
import it.pagopa.selfcare.pagopa.backoffice.core.ExternalApiService;
import it.pagopa.selfcare.pagopa.backoffice.web.model.institutions.InstitutionDetailResource;
import it.pagopa.selfcare.pagopa.backoffice.web.model.institutions.InstitutionResource;
import it.pagopa.selfcare.pagopa.backoffice.web.model.mapper.ApiManagerMapper;
import it.pagopa.selfcare.pagopa.backoffice.web.model.mapper.InstitutionMapper;
import it.pagopa.selfcare.pagopa.backoffice.web.model.mapper.ProductMapper;
import it.pagopa.selfcare.pagopa.backoffice.web.model.products.ProductsResource;
import it.pagopa.selfcare.pagopa.backoffice.web.model.subscriptions.ApiKeysResource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
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
        log.trace("getInstitutionApiKeys start");
        log.debug("getInstitutionApiKeys institutionId = {}", institutionId);
        List<InstitutionApiKeys> institutionApiKeysList = apiManagementService.getInstitutionApiKeys(institutionId);
        List<ApiKeysResource> apiKeysResource = ApiManagerMapper.toApikeysResourceList(institutionApiKeysList);
        log.debug(LogUtils.CONFIDENTIAL_MARKER, "getInstitutionApiKeys result = {}", apiKeysResource);
        log.trace("getInstitutionApiKeys end");
        return apiKeysResource;
    }

    @PostMapping("/{institutionId}/api-keys")
    @ResponseStatus(HttpStatus.CREATED)
    @ApiOperation(value = "", notes = "${swagger.api.institution.createInstitutionApiKeys}")
    public List<ApiKeysResource> createInstitutionApiKeys(@ApiParam("${swagger.model.institution.id}")
                                                          @PathVariable("institutionId") String institutionId) {
        log.trace("createInstitutionApiKeys start");
        log.debug("createInstitutionApiKeys institutionId = {}", institutionId);
        List<InstitutionApiKeys> institutionKeys = apiManagementService.createSubscriptionKeysList(institutionId);
        List<ApiKeysResource> apiKeysResourceList = ApiManagerMapper.toApikeysResourceList(institutionKeys);
        log.debug(LogUtils.CONFIDENTIAL_MARKER, "createInstitutionApiKeys result = {}", apiKeysResourceList);
        log.trace("createInstitutionApiKeys end");
        return apiKeysResourceList;
    }

    @PostMapping("/{subscriptionid}/api-keys/primary/regenerate")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @ApiOperation(value = "", notes = "${swagger.api.institution.regeneratePrimaryKey}")
    public void regeneratePrimaryKey(@ApiParam("${swagger.model.institution.subscription.id}")
                                     @PathVariable("subscriptionid") String subscriptionid) {
        log.trace("regeneratePrimaryKey start");
        log.debug("regeneratePrimaryKey institutionId = {}", subscriptionid);
        apiManagementService.regeneratePrimaryKey(subscriptionid);
        log.trace("regeneratePrimaryKey end");
    }

    @PostMapping("/{subscriptionid}/api-keys/secondary/regenerate")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @ApiOperation(value = "", notes = "${swagger.api.institution.regenerateSecondaryKey}")
    public void regenerateSecondaryKey(@ApiParam("${swagger.model.institution.subscription.id}")
                                       @PathVariable("subscriptionid") String subscriptionid) {
        log.trace("regenerateSecondaryKey start");
        log.debug("regenerateSecondaryKey institutionId = {}", subscriptionid);
        apiManagementService.regenerateSecondaryKey(subscriptionid);
        log.trace("regenerateSecondaryKey end");
    }
    
    @GetMapping("")
    @ResponseStatus(HttpStatus.OK)
    @ApiOperation(value = "", notes = "${swagger.api.institution.getInstitutions}")
    public List<InstitutionResource> getInstitutions(){

        log.trace("getInstitutions start");
        Collection<InstitutionInfo> institutions = externalApiService.getInstitutions();
        List<InstitutionResource> resources = institutions.stream()
                .map(InstitutionMapper::toResource)
                .collect(Collectors.toList());
        log.debug("getInstitutions result = {}", resources);
        log.trace("getInstitutions end");
        return resources;
    }
    
    @GetMapping("/{institutionId}")
    @ResponseStatus(HttpStatus.OK)
    @ApiOperation(value = "", notes = "${swagger.api.institution.getInstitution}")
    public InstitutionDetailResource getInstitution(@ApiParam("${swagger.model.institution.id}")
                                              @PathVariable("institutionId")String institutionId){
        log.trace("getInstitution start");
        log.debug("getInstitution institutionId = {}", institutionId);
        Institution institution = externalApiService.getInstitution(institutionId);
        InstitutionDetailResource resource = InstitutionMapper.toResource(institution);
        log.debug("getInstitution result = {}", resource);
        log.trace("getInstitution end");
        return resource;
    }
    
    @GetMapping("/{institutionId}/products")
    @ResponseStatus(HttpStatus.OK)
    @ApiOperation(value = "", notes = "${swagger.api.institution.getInstitutionProducts}")
    public List<ProductsResource> getInstitutionProducts(@ApiParam("${swagger.model.institution.id}")
                                                         @PathVariable("institutionId")String institutionId){
        log.trace("getInstitutionProducts start");
        log.debug("getInstitutionProducts institutionId = {}", institutionId);
        List<Product> products = externalApiService.getInstitutionUserProducts(institutionId);
        List<ProductsResource> resource = products.stream()
                .map(ProductMapper::toResource)
                .collect(Collectors.toList());
        log.debug("getInstitutionProducts result = {}", resource);
        log.trace("getInstitutionProducts end");
        return resource;
    }

}
