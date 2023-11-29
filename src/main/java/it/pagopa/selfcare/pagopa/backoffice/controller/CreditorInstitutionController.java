package it.pagopa.selfcare.pagopa.backoffice.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import it.pagopa.selfcare.pagopa.backoffice.model.connector.creditorInstitution.CreditorInstitutionAssociatedCodeList;
import it.pagopa.selfcare.pagopa.backoffice.model.creditorinstituions.*;
import it.pagopa.selfcare.pagopa.backoffice.model.stations.BrokerAndEcDetailsResource;
import it.pagopa.selfcare.pagopa.backoffice.service.CreditorInstitutionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Slf4j
@RestController
@RequestMapping(value = "/creditor-institutions", produces = MediaType.APPLICATION_JSON_VALUE)
@Tag(name = "Creditor institutions")
public class CreditorInstitutionController {

    @Autowired
    private CreditorInstitutionService ciService;


    @GetMapping(value = "", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Get paginated list of creditor institutions", security = {@SecurityRequirement(name = "JWT")})
    public CreditorInstitutionsResource getCreditorInstitutions(@Parameter(description = "Number of elements on one page. Default = 50") @RequestParam(required = false, defaultValue = "50") Integer limit,
                                                                @Parameter(description = "Page number. Page value starts from 0") @RequestParam Integer page,
                                                                @Parameter(description = "Creditor institution code") @RequestParam(required = false, value = "ci-code") @NotBlank String ciCode,
                                                                @Parameter(description = "Creditor institution name") @RequestParam(required = false, value = "name") String name,
                                                                @Parameter(description = "Sorting method for paginated result") @RequestParam(required = false, value = "sorting") String sorting) {

        return ciService.getCreditorInstitutions(limit, page, ciCode, name, sorting);
    }

    @GetMapping(value = "/{ci-code}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Get the detail of specific creditor institution", security = {@SecurityRequirement(name = "JWT")})
    public CreditorInstitutionDetailsResource getCreditorInstitutionDetails(@Parameter(description = "Creditor institution code") @PathVariable("ci-code") @NotBlank String ciCode) {

        return ciService.getCreditorInstitutionDetails(ciCode);
    }

    @GetMapping(value = "/{ci-code}/segregation-codes", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Get segregation code associations with creditor institution", security = {@SecurityRequirement(name = "JWT")})
    public CreditorInstitutionAssociatedCodeList getCreditorInstitutionSegregationcodes(@Parameter(description = "Creditor institution code") @PathVariable("ci-code") @NotBlank String ciCode) {

        return ciService.getCreditorInstitutionSegregationcodes(ciCode);
    }

    @PostMapping(value = "/{ci-code}/station", produces = {MediaType.APPLICATION_JSON_VALUE})
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Creates the relationship between the created station and the creditorInstitution", security = {@SecurityRequirement(name = "JWT")})
    public CreditorInstitutionStationEditResource associateStationToCreditorInstitution(@Parameter(description = "Creditor institution code") @PathVariable("ci-code") String ciCode,
                                                                                        @RequestBody @NotNull CreditorInstitutionStationDto dto) {
        return ciService.associateStationToCreditorInstitution(ciCode, dto);
    }


    @DeleteMapping(value = "/{ci-code}/station/{station-code}", produces = {MediaType.APPLICATION_JSON_VALUE})
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "delete the relationship between the created station and the creditorInstitution", security = {@SecurityRequirement(name = "JWT")})
    public void deleteCreditorInstitutionStationRelationship(@Parameter(description = "Creditor institution code") @PathVariable("ci-code") String ciCode,
                                                             @Parameter(description = "Channlecode or StationCode") @PathVariable("station-code") String stationCode) {

        ciService.deleteCreditorInstitutionStationRelationship(ciCode, stationCode);
    }

    @PostMapping(value = "", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create a new creditor institution", security = {@SecurityRequirement(name = "JWT")})
    public CreditorInstitutionDetailsResource createCreditorInstitution(@RequestBody @NotNull CreditorInstitutionDto dto) {

        return ciService.createCreditorInstitution(dto);
    }

    @PostMapping(value = "/{ci-code}/full", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create a new creditor institution that can intermediate as a broker", security = {@SecurityRequirement(name = "JWT")})
    public CreditorInstitutionDetailsResource createCreditorInstitutionAndBroker(@Parameter(description = "Creditor institution code") @PathVariable("ci-code") @NotBlank String ciCode,
                                                                                 @RequestBody @NotNull CreditorInstitutionAndBrokerDto dto) {

        return ciService.createCIAndBroker(dto);
    }


    @PutMapping(value = "/{ci-code}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Update an existing creditor institution", security = {@SecurityRequirement(name = "JWT")})
    public CreditorInstitutionDetailsResource updateCreditorInstitutionDetails(@Parameter(description = "Creditor institution code") @PathVariable("ci-code") @NotBlank String ciCode,
                                                                               @RequestBody @Valid UpdateCreditorInstitutionDto dto) {

        return ciService.updateCreditorInstitutionDetails(ciCode, dto);
    }

    @GetMapping(value = "/{ci-code}/full", produces = {MediaType.APPLICATION_JSON_VALUE})
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Get PSP broker details", security = {@SecurityRequirement(name = "JWT")})
    public BrokerAndEcDetailsResource getBrokerAndEcDetails(@Parameter(description = "Broker code of an EC") @PathVariable(required = true, name = "ci-code") String brokerEcCode) {

        return ciService.getBrokerAndEcDetails(brokerEcCode);

    }

}
