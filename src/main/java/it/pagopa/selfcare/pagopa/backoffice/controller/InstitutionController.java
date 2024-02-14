package it.pagopa.selfcare.pagopa.backoffice.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import it.pagopa.selfcare.pagopa.backoffice.model.institutions.*;
import it.pagopa.selfcare.pagopa.backoffice.model.institutions.client.InstitutionApiKeys;
import it.pagopa.selfcare.pagopa.backoffice.service.ApiManagementService;
import it.pagopa.selfcare.pagopa.backoffice.util.OpenApiTableMetadata;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotBlank;
import java.util.List;

@Slf4j
@RestController
@RequestMapping(value = "/institutions", produces = MediaType.APPLICATION_JSON_VALUE)
@Tag(name = "Institutions")
public class InstitutionController {

    @Autowired
    private ApiManagementService apiManagementService;


    @GetMapping("")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Retrieves all the onboarded institutions related to the logged user", security = {@SecurityRequirement(name = "JWT")})
    @OpenApiTableMetadata
    public List<InstitutionDetail> getInstitutions() {

        return apiManagementService.getInstitutions();
    }

    @GetMapping("/delegations")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Retrieve all active delegations for given institution broker and logged user", security = {@SecurityRequirement(name = "JWT")})
    @OpenApiTableMetadata
    public List<Delegation> getBrokerDelegation(@Parameter(description = "Institution's unique internal identifier") @RequestParam(required = false, value = "institution-id") String institutionId,
                                                @Parameter(description = "Broker's unique id") @RequestParam(required = false, value = "brokerId") String brokerId,
                                                @Parameter(description = "Broker's role to consider when filtering institution types (PSP,EC,PT_EC,PT_PSP,PT_ALL)") @RequestParam(required = false, value = "role") List<RoleType> role
    ) {
        return apiManagementService.getBrokerDelegation(institutionId, brokerId, role);
    }

    @GetMapping("/{institution-id}")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Retrieves the details of an institution", security = {@SecurityRequirement(name = "JWT")})
    @OpenApiTableMetadata
    public Institution getInstitution(@Parameter(description = "Institution's unique internal identifier") @PathVariable("institution-id") @NotBlank String institutionId) {

        return apiManagementService.getInstitution(institutionId);
    }

    @GetMapping("/{institution-id}/products")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Retrieve all active products for given institution and logged user", security = {@SecurityRequirement(name = "JWT")})
    @OpenApiTableMetadata
    public List<Product> getInstitutionProducts(@Parameter(description = "Institution's unique internal identifier") @PathVariable("institution-id") @NotBlank String institutionId) {

        return apiManagementService.getInstitutionProducts(institutionId);
    }

    @GetMapping("/{institution-id}/api-keys")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Retrieve an institution's key pair, including primary and secondary keys", security = {@SecurityRequirement(name = "JWT")})
    @OpenApiTableMetadata
    public List<InstitutionApiKeys> getInstitutionApiKeys(@Parameter(description = "Institution's unique internal identifier") @PathVariable("institution-id") @NotBlank String institutionId) {

        return apiManagementService.getInstitutionApiKeys(institutionId);
    }

    @PostMapping("/{institution-id}/api-keys")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Creates a new subscription for a given Institution and returns its primary and secondary keys", security = {@SecurityRequirement(name = "JWT")})
    @OpenApiTableMetadata
    public List<InstitutionApiKeys> createInstitutionApiKeys(@Parameter(description = "Institution's unique internal identifier") @PathVariable("institution-id") @NotBlank String institutionId,
                                                             @Parameter(description = "Subscription's unique internal identifier") @RequestParam("subscription-code") Subscription subscriptionCode) {

        return apiManagementService.createSubscriptionKeys(institutionId, subscriptionCode);
    }

    @PostMapping("/{subscription-id}/api-keys/primary/regenerate")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Regenerates the subscription's primary key", security = {@SecurityRequirement(name = "JWT")})
    @OpenApiTableMetadata
    public void regeneratePrimaryKey(@Parameter(description = "Institution's subscription id") @PathVariable("subscription-id") @NotBlank String subscriptionId) {

        apiManagementService.regeneratePrimaryKey(subscriptionId);
    }

    @PostMapping("/{subscription-id}/api-keys/secondary/regenerate")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Regenerates the subscription's secondary key", security = {@SecurityRequirement(name = "JWT")})
    @OpenApiTableMetadata
    public void regenerateSecondaryKey(@Parameter(description = "Institution's subscription id") @PathVariable("subscription-id") String subscriptionId) {

        apiManagementService.regenerateSecondaryKey(subscriptionId);
    }
}
