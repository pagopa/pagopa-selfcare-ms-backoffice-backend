package it.pagopa.selfcare.pagopa.backoffice.web.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import it.pagopa.selfcare.pagopa.backoffice.connector.model.UserSubscription;
import it.pagopa.selfcare.pagopa.backoffice.core.ApiManagementService;
import it.pagopa.selfcare.pagopa.backoffice.web.model.mapper.ApiManagerMapper;
import it.pagopa.selfcare.pagopa.backoffice.web.model.subscriptions.CreateInstitutionSubscriptionDto;
import it.pagopa.selfcare.pagopa.backoffice.web.model.subscriptions.UserSubscriptionResource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@Slf4j
@RestController
@RequestMapping(value = "/api-management", produces = MediaType.APPLICATION_JSON_VALUE)
@Api(tags = "api-management")
public class ApiManagerController {

    private final ApiManagementService apiManagementService;

    @Autowired
    public ApiManagerController(ApiManagementService apiManagementService) {
        this.apiManagementService = apiManagementService;
    }

    @GetMapping("/subscriptions/{institutionId}")
    @ResponseStatus(HttpStatus.OK)
    @ApiOperation(value = "", notes = "${swagger.api-management.api.getUserSubscriptionKeys}")
    public UserSubscriptionResource getUserSubscriptionKeys(
            @ApiParam("${swagger.institutions.model.id}")
            @PathVariable("institutionId") String institutionId) {
        UserSubscription userSubscription = apiManagementService.getUserSubscription(institutionId);
        return ApiManagerMapper.toSubscriptionResource(userSubscription);
    }

    @PostMapping("/subscriptions/{institutionId}")
    @ResponseStatus(HttpStatus.CREATED)
    @ApiOperation(value = "", notes = "${swagger.api-management.api.createUserSubscription}")
    public void createUserSubscription(@ApiParam("${swagger.institutions.model.id}")
                                       @PathVariable("institutionId") String institutionId,
                                       @RequestBody
                                       @Valid
                                               CreateInstitutionSubscriptionDto subscriptionDto){
        apiManagementService.createUserSubscription(institutionId, ApiManagerMapper.fromDto(subscriptionDto));
    }

}
