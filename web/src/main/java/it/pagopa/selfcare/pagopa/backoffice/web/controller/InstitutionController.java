package it.pagopa.selfcare.pagopa.backoffice.web.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import it.pagopa.selfcare.pagopa.backoffice.connector.model.InstitutionApiKeys;
import it.pagopa.selfcare.pagopa.backoffice.core.ApiManagementService;
import it.pagopa.selfcare.pagopa.backoffice.web.model.mapper.ApiManagerMapper;
import it.pagopa.selfcare.pagopa.backoffice.web.model.subscriptions.ApiKeysResource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping(value = "/institutions", produces = MediaType.APPLICATION_JSON_VALUE)
@Api(tags = "institution")
public class InstitutionController {

    private final ApiManagementService apiManagementService;

    @Autowired
    public InstitutionController(ApiManagementService apiManagementService) {
        this.apiManagementService = apiManagementService;
    }

    @GetMapping("/{institutionId}/api-keys")
    @ResponseStatus(HttpStatus.OK)
    @ApiOperation(value = "", notes = "${swagger.api.institution.getInstitutionApiKeys}")
    public ApiKeysResource getInstitutionApiKeys(
            @ApiParam("${swagger.model.institution.id}")
            @PathVariable("institutionId") String institutionId) {
        InstitutionApiKeys userSubscription = apiManagementService.getInstitutionApiKeys(institutionId);
        return ApiManagerMapper.toApiKeysResource(userSubscription);
    }

    @PostMapping("/{institutionId}/api-keys")
    @ResponseStatus(HttpStatus.CREATED)
    @ApiOperation(value = "", notes = "${swagger.api.institution.createInstitutionApiKeys}")
    public ApiKeysResource createInstitutionApiKeys(@ApiParam("${swagger.model.institution.id}")
                                                    @PathVariable("institutionId") String institutionId) {
        InstitutionApiKeys userSubscription = apiManagementService.createInstitutionKeys(institutionId);
        return ApiManagerMapper.toApiKeysResource(userSubscription);
    }

}
