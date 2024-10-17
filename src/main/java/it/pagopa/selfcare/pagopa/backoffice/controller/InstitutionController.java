package it.pagopa.selfcare.pagopa.backoffice.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import it.pagopa.selfcare.pagopa.backoffice.model.institutions.*;
import it.pagopa.selfcare.pagopa.backoffice.service.ApiManagementService;
import it.pagopa.selfcare.pagopa.backoffice.util.OpenApiTableMetadata;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import java.util.List;

/**
 * Controller that expose the API for manage Institution entity
 */
@Slf4j
@RestController
@RequestMapping(value = "/institutions", produces = MediaType.APPLICATION_JSON_VALUE)
@Tag(name = "Institutions")
public class InstitutionController {

    private final ApiManagementService apiManagementService;

    @Autowired
    public InstitutionController(ApiManagementService apiManagementService) {
        this.apiManagementService = apiManagementService;
    }

    @GetMapping("")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Retrieves all the onboarded institutions related to the logged user", security = {@SecurityRequirement(name = "JWT")})
    @OpenApiTableMetadata
    public @Valid InstitutionBaseResources getInstitutions(
            @Parameter(description = "filter by the tax code of the Creditor Institution") @RequestParam(required = false, value = "tax-code") String taxCode
    ) {
        return this.apiManagementService.getInstitutions(taxCode);
    }

    @GetMapping("/delegations")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Retrieve all active delegations for given institution broker and logged user", security = {@SecurityRequirement(name = "JWT")})
    @OpenApiTableMetadata
    public @Valid DelegationResource getBrokerDelegation(
            @Parameter(description = "Institution's unique internal identifier") @RequestParam(required = false, value = "institution-id") String institutionId,
            @Parameter(description = "Broker's unique id") @RequestParam(required = false, value = "brokerId") String brokerId,
            @Parameter(description = "Broker's role to consider when filtering institution types") @RequestParam(required = false, value = "roles") List<RoleType> roles
    ) {
        return this.apiManagementService.getBrokerDelegation(institutionId, brokerId, roles);
    }

    @GetMapping("/{institution-id}")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Retrieves the details of an institution", security = {@SecurityRequirement(name = "JWT")})
    @OpenApiTableMetadata
    public Institution getInstitution(
            @Parameter(description = "Institution's unique internal identifier") @PathVariable("institution-id") @NotBlank String institutionId
    ) {
        return this.apiManagementService.getInstitution(institutionId);
    }

    @GetMapping("/{institution-id}/full-detail")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Retrieves the full detail of an institution", security = {@SecurityRequirement(name = "JWT")})
    @OpenApiTableMetadata
    public InstitutionDetail getInstitutionFullDetail(
            @Parameter(description = "Institution's unique internal identifier") @PathVariable("institution-id") @NotBlank String institutionId
    ) {
        return this.apiManagementService.getInstitutionFullDetail(institutionId);
    }

    @GetMapping("/{institution-id}/products")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Retrieve all active products for given institution and logged user", security = {@SecurityRequirement(name = "JWT")})
    @OpenApiTableMetadata
    public @Valid ProductResource getInstitutionProducts(
            @Parameter(description = "Institution's unique internal identifier") @PathVariable("institution-id") @NotBlank String institutionId
    ) {
        return this.apiManagementService.getInstitutionProducts(institutionId);
    }

    @GetMapping("/{institution-id}/api-keys")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Retrieve an institution's key pair, including primary and secondary keys", security = {@SecurityRequirement(name = "JWT")})
    @OpenApiTableMetadata
    public @Valid InstitutionApiKeysResource getInstitutionApiKeys(
            @Parameter(description = "Institution's unique internal identifier") @PathVariable("institution-id") @NotBlank String institutionId
    ) {
        return this.apiManagementService.getInstitutionApiKeys(institutionId);
    }

    @PostMapping("/{institution-id}/api-keys")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Creates a new subscription for a given Institution and returns its primary and secondary keys", security = {@SecurityRequirement(name = "JWT")})
    @OpenApiTableMetadata
    public @Valid InstitutionApiKeysResource createInstitutionApiKeys(
            @Parameter(description = "Institution's unique internal identifier") @PathVariable("institution-id") @NotBlank String institutionId,
            @Parameter(description = "Subscription's unique internal identifier") @RequestParam("subscription-code") Subscription subscriptionCode
    ) {
        return this.apiManagementService.createSubscriptionKeys(institutionId, subscriptionCode);
    }

    @PostMapping("/{institution-id}/api-keys/{subscription-id}/primary/regenerate")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Regenerates the subscription's primary key", security = {@SecurityRequirement(name = "JWT")})
    @OpenApiTableMetadata
    public void regeneratePrimaryKey(
            @Parameter(description = "Institution's unique internal identifier") @PathVariable("institution-id") @NotBlank String institutionId,
            @Parameter(description = "Institution's subscription id") @PathVariable("subscription-id") @NotBlank String subscriptionId
    ) {
        this.apiManagementService.regeneratePrimaryKey(institutionId, subscriptionId);
    }

    @PostMapping("/{institution-id}/api-keys/{subscription-id}/secondary/regenerate")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Regenerates the subscription's secondary key", security = {@SecurityRequirement(name = "JWT")})
    @OpenApiTableMetadata
    public void regenerateSecondaryKey(
            @Parameter(description = "Institution's unique internal identifier") @PathVariable("institution-id") @NotBlank String institutionId,
            @Parameter(description = "Institution's subscription id") @PathVariable("subscription-id") String subscriptionId
    ) {
        this.apiManagementService.regenerateSecondaryKey(institutionId, subscriptionId);
    }
}
