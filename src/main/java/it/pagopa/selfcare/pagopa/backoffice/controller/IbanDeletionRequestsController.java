package it.pagopa.selfcare.pagopa.backoffice.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import it.pagopa.selfcare.pagopa.backoffice.model.iban.*;
import it.pagopa.selfcare.pagopa.backoffice.security.JwtSecurity;
import it.pagopa.selfcare.pagopa.backoffice.service.IbanDeletionRequestsService;
import it.pagopa.selfcare.pagopa.backoffice.util.OpenApiTableMetadata;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

import java.time.LocalDate;

import static it.pagopa.selfcare.pagopa.backoffice.model.institutions.ProductRole.ADMIN;

@Slf4j
@RestController
@RequestMapping(value = "/creditor-institutions/{ci-code}/iban-deletion-requests", produces = MediaType.APPLICATION_JSON_VALUE)
@Tag(name = "IbanDeletionRequest")
public class IbanDeletionRequestsController {

    private final IbanDeletionRequestsService ibanDeletionRequestsService;

    @Autowired
    public IbanDeletionRequestsController(IbanDeletionRequestsService ibanDeletionRequestsService) {
        this.ibanDeletionRequestsService = ibanDeletionRequestsService;
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    @Operation(
            summary = "Get IBAN deletion requests",
            description = "Retrieves all IBAN deletion requests for the creditor institution, optionally filtered by IBAN value.",
            security = {@SecurityRequirement(name = "JWT")}
    )
    @OpenApiTableMetadata
    @JwtSecurity(paramName = "ciCode", allowedProductRole = ADMIN)
    public IbanDeletionRequestResponse getIbanDeletionRequest(
            @Parameter(description = "Creditor institution code") @PathVariable("ci-code") String ciCode,
            @Parameter(description = "Filter by IBAN value") @RequestParam(value = "ibanValue", required = true) String ibanValue) {

        return ibanDeletionRequestsService.getIbanDeletionRequest(ciCode, ibanValue);
    }

    @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(
            summary = "Request deletion of a specific IBAN owned by creditor institution",
            description = "Creates a scheduled deletion request for the specified IBAN to be executed at the provided date.",
            security = {@SecurityRequirement(name = "JWT")}
    )
    @OpenApiTableMetadata
    @JwtSecurity(paramName = "ciCode", allowedProductRole = ADMIN)
    public IbanDeletionRequestResponse createIbanDeletionRequest(
            @Parameter(description = "Creditor institution code") @PathVariable("ci-code") String ciCode,
            @Valid @RequestBody IbanDeletionRequest request) {

        return ibanDeletionRequestsService.createIbanDeletionRequest(ciCode, request.getIbanValue(), LocalDate.parse(request.getScheduledExecutionDate()));
    }

    @DeleteMapping(value = "/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(
            summary = "Cancel an IBAN deletion request",
            description = "Cancels a pending IBAN deletion request by setting its status to CANCELED.",
            security = {@SecurityRequirement(name = "JWT")}
    )
    @OpenApiTableMetadata
    @JwtSecurity(paramName = "ciCode", allowedProductRole = ADMIN)
    public void cancelIbanDeletionRequest(
            @Parameter(description = "Creditor institution code") @PathVariable("ci-code") String ciCode,
            @Parameter(description = "Deletion request ID") @PathVariable("id") String id) {

        ibanDeletionRequestsService.cancelIbanDeletionRequest(ciCode, id);
    }

    @PatchMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    @Operation(
            summary = "Update the scheduled execution date of an IBAN deletion request",
            description = "Updates the scheduled execution date for a pending IBAN deletion request.",
            security = {@SecurityRequirement(name = "JWT")}
    )
    @OpenApiTableMetadata
    @JwtSecurity(paramName = "ciCode", allowedProductRole = ADMIN)
    public IbanDeletionRequestResponse updateIbanDeletionRequestSchedule(
            @Parameter(description = "Creditor institution code") @PathVariable("ci-code") String ciCode,
            @Parameter(description = "Deletion request ID") @PathVariable("id") String id,
            @Valid @RequestBody IbanDeletionRequestUpdateRequest request) {

        return ibanDeletionRequestsService.updateIbanDeletionRequestSchedule(ciCode, id, request.getScheduledExecutionDate());
    }
}