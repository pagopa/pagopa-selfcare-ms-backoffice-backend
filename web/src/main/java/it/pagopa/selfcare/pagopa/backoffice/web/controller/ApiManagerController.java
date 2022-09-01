package it.pagopa.selfcare.pagopa.backoffice.web.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import it.pagopa.selfcare.pagopa.backoffice.connector.model.UserSubscription;
import it.pagopa.selfcare.pagopa.backoffice.core.ApiManagementService;
import it.pagopa.selfcare.pagopa.backoffice.web.model.UserSubscriptionResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api-management")
@Api(tags = "api-management")
public class ApiManagerController {

    private final ApiManagementService apiManagementService;

    @Autowired
    public ApiManagerController(ApiManagementService apiManagementService) {
        this.apiManagementService = apiManagementService;
    }

    @GetMapping("/subscriptions/{institutionId}")
    @ResponseStatus(HttpStatus.OK)
    @ApiOperation("${}")
    public UserSubscriptionResource getUserSubscriptionKeys(
            @ApiParam("${swagger.pagopa.backoffice.institutions.model.id}")
            @PathVariable("institutionId")String institutionId){
        UserSubscription userSubscription = apiManagementService.getUserSubscription(institutionId);
        return null;
    }

}
