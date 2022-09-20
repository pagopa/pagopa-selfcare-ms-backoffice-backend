package it.pagopa.selfcare.pagopa.backoffice.web.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import it.pagopa.selfcare.pagopa.backoffice.connector.logging.LogUtils;
import it.pagopa.selfcare.pagopa.backoffice.connector.model.institution.Institution;
import it.pagopa.selfcare.pagopa.backoffice.connector.model.institution.InstitutionApiKeys;
import it.pagopa.selfcare.pagopa.backoffice.connector.model.institution.InstitutionInfo;
import it.pagopa.selfcare.pagopa.backoffice.core.ApiManagementService;
import it.pagopa.selfcare.pagopa.backoffice.core.ExternalApiService;
import it.pagopa.selfcare.pagopa.backoffice.web.model.institutions.InstitutionDetailResource;
import it.pagopa.selfcare.pagopa.backoffice.web.model.institutions.InstitutionResource;
import it.pagopa.selfcare.pagopa.backoffice.web.model.mapper.ApiManagerMapper;
import it.pagopa.selfcare.pagopa.backoffice.web.model.mapper.InstitutionMapper;
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
    public ApiKeysResource getInstitutionApiKeys(
            @ApiParam("${swagger.model.institution.id}")
            @PathVariable("institutionId") String institutionId) {
        log.trace("getInstitutionApiKeys start");
        log.debug("getInstitutionApiKeys institutionId = {}", institutionId);
        InstitutionApiKeys institutionApiKeys = apiManagementService.getInstitutionApiKeys(institutionId);
        ApiKeysResource apiKeysResource = ApiManagerMapper.toApiKeysResource(institutionApiKeys);
        log.debug(LogUtils.CONFIDENTIAL_MARKER, "getInstitutionApiKeys result = {}", apiKeysResource);
        log.trace("getInstitutionApiKeys end");
        return apiKeysResource;
    }

    @PostMapping("/{institutionId}/api-keys")
    @ResponseStatus(HttpStatus.CREATED)
    @ApiOperation(value = "", notes = "${swagger.api.institution.createInstitutionApiKeys}")
    public ApiKeysResource createInstitutionApiKeys(@ApiParam("${swagger.model.institution.id}")
                                                    @PathVariable("institutionId") String institutionId) {
        log.trace("createInstitutionApiKeys start");
        log.debug("createInstitutionApiKeys institutionId = {}", institutionId);
        InstitutionApiKeys institutionKeys = apiManagementService.createInstitutionKeys(institutionId);
        ApiKeysResource apiKeysResource = ApiManagerMapper.toApiKeysResource(institutionKeys);
        log.debug(LogUtils.CONFIDENTIAL_MARKER, "createInstitutionApiKeys result = {}", apiKeysResource);
        log.trace("createInstitutionApiKeys end");
        return apiKeysResource;
    }
    
    @PostMapping("/{institutionId}/api-keys/primary/regenerate")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @ApiOperation(value = "", notes = "${swagger.api.institution.regeneratePrimaryKey}")
    public void regeneratePrimaryKey(@ApiParam("${swagger.model.institution.id}")
                                     @PathVariable("institutionId")String institutionId){
        log.trace("regeneratePrimaryKey start");
        log.debug("regeneratePrimaryKey institutionId = {}", institutionId);
        apiManagementService.regeneratePrimaryKey(institutionId);
        log.trace("regeneratePrimaryKey end");
    }

    @PostMapping("/{institutionId}/api-keys/secondary/regenerate")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @ApiOperation(value = "", notes = "${swagger.api.institution.regenerateSecondaryKey}")
    public void regenerateSecondaryKey(@ApiParam("${swagger.model.institution.id}")
                                     @PathVariable("institutionId")String institutionId){
        log.trace("regenerateSecondaryKey start");
        log.debug("regenerateSecondaryKey institutionId = {}", institutionId);
        apiManagementService.regenerateSecondaryKey(institutionId);
        log.trace("regenerateSecondaryKey end");
    }
    
    @GetMapping("")
    @ResponseStatus(HttpStatus.OK)
    @ApiOperation(value = "", notes = "${swagger.api.institution.getInstitutions}")
    public List<InstitutionResource> getInstitutions(@RequestParam("productId")String productId){

        log.trace("getInstitutions start");
        log.debug("getInstitutions productId = {}", productId);
        Collection<InstitutionInfo> institutions = externalApiService.getInstitutions(productId);
        List<InstitutionResource> resources = institutions.stream()
                .map(InstitutionMapper::toResource).collect(Collectors.toList());
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
        return null;
    }

}
