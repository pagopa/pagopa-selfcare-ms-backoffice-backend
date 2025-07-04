package it.pagopa.selfcare.pagopa.backoffice.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import it.pagopa.selfcare.pagopa.backoffice.model.ProblemJson;
import it.pagopa.selfcare.pagopa.backoffice.model.connector.creditorinstitution.ApiConfigCreditorInstitutionsOrderBy;
import it.pagopa.selfcare.pagopa.backoffice.model.connector.creditorinstitution.AvailableCodes;
import it.pagopa.selfcare.pagopa.backoffice.model.creditorinstituions.CreditorInstitutionAndBrokerDto;
import it.pagopa.selfcare.pagopa.backoffice.model.creditorinstituions.CreditorInstitutionContactsResource;
import it.pagopa.selfcare.pagopa.backoffice.model.creditorinstituions.CreditorInstitutionDetailsResource;
import it.pagopa.selfcare.pagopa.backoffice.model.creditorinstituions.CreditorInstitutionDto;
import it.pagopa.selfcare.pagopa.backoffice.model.creditorinstituions.CreditorInstitutionInfoResource;
import it.pagopa.selfcare.pagopa.backoffice.model.creditorinstituions.CreditorInstitutionStationDto;
import it.pagopa.selfcare.pagopa.backoffice.model.creditorinstituions.CreditorInstitutionStationEditResource;
import it.pagopa.selfcare.pagopa.backoffice.model.creditorinstituions.CreditorInstitutionsResource;
import it.pagopa.selfcare.pagopa.backoffice.model.creditorinstituions.UpdateCreditorInstitutionDto;
import it.pagopa.selfcare.pagopa.backoffice.model.stations.BrokerAndEcDetailsResource;
import it.pagopa.selfcare.pagopa.backoffice.security.JwtSecurity;
import it.pagopa.selfcare.pagopa.backoffice.service.CreditorInstitutionService;
import it.pagopa.selfcare.pagopa.backoffice.util.OpenApiTableMetadata;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@RestController
@RequestMapping(value = "/creditor-institutions", produces = MediaType.APPLICATION_JSON_VALUE)
@Tag(name = "Creditor institutions")
public class CreditorInstitutionController {

    private final CreditorInstitutionService ciService;

    @Autowired
    public CreditorInstitutionController(CreditorInstitutionService ciService) {
        this.ciService = ciService;
    }

    @GetMapping(value = "", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = CreditorInstitutionsResource.class))),
            @ApiResponse(responseCode = "400", description = "Bad Request", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ProblemJson.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content(schema = @Schema())),
            @ApiResponse(responseCode = "403", description = "Forbidden", content = @Content(schema = @Schema())),
            @ApiResponse(responseCode = "429", description = "Too many requests", content = @Content(schema = @Schema())),
            @ApiResponse(responseCode = "500", description = "Service unavailable", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ProblemJson.class)))
    })
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Get paginated list of creditor institutions", security = {@SecurityRequirement(name = "JWT")})
    @OpenApiTableMetadata
    public CreditorInstitutionsResource getCreditorInstitutions(
            @Parameter(description = "Creditor institution's tax code, used to filter out results") @RequestParam(required = false) String ciTaxCode,
            @Parameter(description = "Creditor institution's name, used to filter out results") @RequestParam(required = false) String ciName,
            @Parameter(description = "Flag that describe if the creditor institution is enabled, used to filter out results") @RequestParam(required = false) Boolean enabled,
            @Parameter(description = "Order by creditor institution's tax code or business name") @RequestParam(required = false, defaultValue = "NAME") ApiConfigCreditorInstitutionsOrderBy orderBy,
            @Parameter(description = "Sorting method for paginated result") @RequestParam(required = false, defaultValue = "DESC") Sort.Direction sorting,
            @Parameter(description = "Number of elements on one page") @RequestParam(required = false, defaultValue = "50") @Positive Integer limit,
            @Parameter(description = "Page number") @RequestParam @PositiveOrZero @Min(0) Integer page
    ) {
        return this.ciService.getCreditorInstitutions(ciTaxCode, ciName, enabled, orderBy, sorting, limit, page);
    }

    @GetMapping(value = "/{ci-tax-code}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = CreditorInstitutionDetailsResource.class))),
            @ApiResponse(responseCode = "400", description = "Bad Request", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ProblemJson.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content(schema = @Schema())),
            @ApiResponse(responseCode = "403", description = "Forbidden", content = @Content(schema = @Schema())),
            @ApiResponse(responseCode = "404", description = "Not found", content = @Content(schema = @Schema(implementation = ProblemJson.class))),
            @ApiResponse(responseCode = "429", description = "Too many requests", content = @Content(schema = @Schema())),
            @ApiResponse(responseCode = "500", description = "Service unavailable", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ProblemJson.class)))
    })
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Get the detail of specific creditor institution", security = {@SecurityRequirement(name = "JWT")})
    @OpenApiTableMetadata
    public CreditorInstitutionDetailsResource getCreditorInstitutionDetails(
            @Parameter(description = "Creditor institution's tax code") @PathVariable("ci-tax-code") @NotBlank String ciTaxCode
    ) {
        return ciService.getCreditorInstitutionDetails(ciTaxCode);
    }

    @GetMapping(value = "/{ci-tax-code}/segregation-codes", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = AvailableCodes.class))),
            @ApiResponse(responseCode = "400", description = "Bad Request", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ProblemJson.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content(schema = @Schema())),
            @ApiResponse(responseCode = "403", description = "Forbidden", content = @Content(schema = @Schema())),
            @ApiResponse(responseCode = "404", description = "Not found", content = @Content(schema = @Schema(implementation = ProblemJson.class))),
            @ApiResponse(responseCode = "429", description = "Too many requests", content = @Content(schema = @Schema())),
            @ApiResponse(responseCode = "500", description = "Service unavailable", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ProblemJson.class)))
    })
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Get the available creditor institution's segregation code", security = {@SecurityRequirement(name = "JWT")})
    @OpenApiTableMetadata
    @JwtSecurity(paramName = "ciTaxCode")
    public AvailableCodes getCreditorInstitutionSegregationCodes(
            @Parameter(description = "Creditor institution's tax code that own the station") @PathVariable("ci-tax-code") String ciTaxCode,
            @Parameter(description = "Tax code of the creditor institution that will be associated to the station") @RequestParam @NotBlank String targetCITaxCode
    ) {
        return this.ciService.getCreditorInstitutionSegregationCodes(ciTaxCode, targetCITaxCode);
    }

    @PostMapping(value = "/{ci-tax-code}/station", produces = {MediaType.APPLICATION_JSON_VALUE})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Created", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = CreditorInstitutionStationEditResource.class))),
            @ApiResponse(responseCode = "400", description = "Bad Request", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ProblemJson.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content(schema = @Schema())),
            @ApiResponse(responseCode = "403", description = "Forbidden", content = @Content(schema = @Schema())),
            @ApiResponse(responseCode = "404", description = "Not found", content = @Content(schema = @Schema(implementation = ProblemJson.class))),
            @ApiResponse(responseCode = "409", description = "Conflict", content = @Content(schema = @Schema(implementation = ProblemJson.class))),
            @ApiResponse(responseCode = "429", description = "Too many requests", content = @Content(schema = @Schema())),
            @ApiResponse(responseCode = "500", description = "Service unavailable", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ProblemJson.class)))
    })
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Creates the relationship between the created station and the creditorInstitution", security = {@SecurityRequirement(name = "JWT")})
    @OpenApiTableMetadata
    @JwtSecurity(paramName = "brokerTaxCode")
    public CreditorInstitutionStationEditResource associateStationToCreditorInstitution(
            @Parameter(description = "Creditor institution's tax code") @PathVariable("ci-tax-code") String ciTaxCode,
            @Parameter(description = "Broker's institution id") @RequestParam("institutionId") String institutionId,
            @Parameter(description = "Broker's tax code") @RequestParam("brokerTaxCode") String brokerTaxCode,
            @RequestBody @NotNull CreditorInstitutionStationDto dto
    ) {
        return this.ciService.associateStationToCreditorInstitution(ciTaxCode, institutionId, brokerTaxCode, dto);
    }

    @PutMapping(value = "/{ci-tax-code}/station", produces = {MediaType.APPLICATION_JSON_VALUE})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Updated", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = CreditorInstitutionStationEditResource.class))),
            @ApiResponse(responseCode = "400", description = "Bad Request", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ProblemJson.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content(schema = @Schema())),
            @ApiResponse(responseCode = "403", description = "Forbidden", content = @Content(schema = @Schema())),
            @ApiResponse(responseCode = "404", description = "Not found", content = @Content(schema = @Schema(implementation = ProblemJson.class))),
            @ApiResponse(responseCode = "409", description = "Conflict", content = @Content(schema = @Schema(implementation = ProblemJson.class))),
            @ApiResponse(responseCode = "429", description = "Too many requests", content = @Content(schema = @Schema())),
            @ApiResponse(responseCode = "500", description = "Service unavailable", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ProblemJson.class)))
    })
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Updates the relationship between the created station and the creditorInstitution", security = {@SecurityRequirement(name = "JWT")})
    @OpenApiTableMetadata
    @JwtSecurity(paramName = "stationCode", checkParamInsideBody = true, removeParamSuffix = true)
    public CreditorInstitutionStationEditResource updateStationAssociationToCreditorInstitution(
            @Parameter(description = "Creditor institution's tax code") @PathVariable("ci-tax-code") String ciTaxCode,
            @RequestBody @NotNull CreditorInstitutionStationDto dto
    ) {
        return this.ciService.updateStationAssociationToCreditorInstitution(ciTaxCode, dto);
    }


    @DeleteMapping(value = "/{ci-tax-code}/station/{station-code}", produces = {MediaType.APPLICATION_JSON_VALUE})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema())),
            @ApiResponse(responseCode = "204", description = "No content", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema())),
            @ApiResponse(responseCode = "400", description = "Bad Request", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ProblemJson.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content(schema = @Schema())),
            @ApiResponse(responseCode = "403", description = "Forbidden", content = @Content(schema = @Schema())),
            @ApiResponse(responseCode = "404", description = "Not found", content = @Content(schema = @Schema(implementation = ProblemJson.class))),
            @ApiResponse(responseCode = "429", description = "Too many requests", content = @Content(schema = @Schema())),
            @ApiResponse(responseCode = "500", description = "Service unavailable", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ProblemJson.class)))
    })
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "delete the relationship between the created station and the creditorInstitution", security = {@SecurityRequirement(name = "JWT")})
    @OpenApiTableMetadata
    @JwtSecurity(paramName = "brokerTaxCode")
    public void deleteCreditorInstitutionStationRelationship(
            @Parameter(description = "Creditor institution's code") @PathVariable("ci-tax-code") String ciTaxCode,
            @Parameter(description = "Broker's institution id") @RequestParam("institutionId") String institutionId,
            @Parameter(description = "Broker's tax code") @RequestParam("brokerTaxCode") String brokerTaxCode,
            @Parameter(description = "Station's code") @PathVariable("station-code") String stationCode
    ) {
        this.ciService.deleteCreditorInstitutionStationRelationship(ciTaxCode, stationCode, institutionId, brokerTaxCode);
    }

    @PostMapping(value = "", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Created", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = CreditorInstitutionDetailsResource.class))),
            @ApiResponse(responseCode = "400", description = "Bad Request", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ProblemJson.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content(schema = @Schema())),
            @ApiResponse(responseCode = "403", description = "Forbidden", content = @Content(schema = @Schema())),
            @ApiResponse(responseCode = "409", description = "Conflict", content = @Content(schema = @Schema())),
            @ApiResponse(responseCode = "429", description = "Too many requests", content = @Content(schema = @Schema())),
            @ApiResponse(responseCode = "500", description = "Service unavailable", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ProblemJson.class)))
    })
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create a new creditor institution", security = {@SecurityRequirement(name = "JWT")})
    @OpenApiTableMetadata
    public CreditorInstitutionDetailsResource createCreditorInstitution(@RequestBody @NotNull CreditorInstitutionDto dto) {

        return ciService.createCreditorInstitution(dto);
    }

    @PostMapping(value = "/{ci-tax-code}/full", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Created", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = CreditorInstitutionDetailsResource.class))),
            @ApiResponse(responseCode = "400", description = "Bad Request", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ProblemJson.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content(schema = @Schema())),
            @ApiResponse(responseCode = "403", description = "Forbidden", content = @Content(schema = @Schema())),
            @ApiResponse(responseCode = "409", description = "Conflict", content = @Content(schema = @Schema())),
            @ApiResponse(responseCode = "429", description = "Too many requests", content = @Content(schema = @Schema())),
            @ApiResponse(responseCode = "500", description = "Service unavailable", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ProblemJson.class)))
    })
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create a new creditor institution that can intermediate as a broker", security = {@SecurityRequirement(name = "JWT")})
    @OpenApiTableMetadata
    public CreditorInstitutionDetailsResource createCreditorInstitutionAndBroker(
            @Parameter(description = "Creditor institution's tax code") @PathVariable("ci-tax-code") @NotBlank String ciTaxCode,
            @RequestBody @NotNull CreditorInstitutionAndBrokerDto dto
    ) {
        return ciService.createCIAndBroker(dto);
    }


    @PutMapping(value = "/{ci-tax-code}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = CreditorInstitutionDetailsResource.class))),
            @ApiResponse(responseCode = "400", description = "Bad Request", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ProblemJson.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content(schema = @Schema())),
            @ApiResponse(responseCode = "403", description = "Forbidden", content = @Content(schema = @Schema())),
            @ApiResponse(responseCode = "404", description = "Not found", content = @Content(schema = @Schema(implementation = ProblemJson.class))),
            @ApiResponse(responseCode = "429", description = "Too many requests", content = @Content(schema = @Schema())),
            @ApiResponse(responseCode = "500", description = "Service unavailable", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ProblemJson.class)))
    })
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Update an existing creditor institution", security = {@SecurityRequirement(name = "JWT")})
    @OpenApiTableMetadata
    public CreditorInstitutionDetailsResource updateCreditorInstitutionDetails(
            @Parameter(description = "Creditor institution's tax code") @PathVariable("ci-tax-code") @NotBlank String ciTaxCode,
            @RequestBody @Valid UpdateCreditorInstitutionDto dto
    ) {
        return ciService.updateCreditorInstitutionDetails(ciTaxCode, dto);
    }

    @GetMapping(value = "/{ci-tax-code}/full", produces = {MediaType.APPLICATION_JSON_VALUE})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = BrokerAndEcDetailsResource.class))),
            @ApiResponse(responseCode = "400", description = "Bad Request", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ProblemJson.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content(schema = @Schema())),
            @ApiResponse(responseCode = "404", description = "Not found", content = @Content(schema = @Schema(implementation = ProblemJson.class))),
            @ApiResponse(responseCode = "429", description = "Too many requests", content = @Content(schema = @Schema())),
            @ApiResponse(responseCode = "500", description = "Service unavailable", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ProblemJson.class)))
    })
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Get PSP broker details", security = {@SecurityRequirement(name = "JWT")})
    @OpenApiTableMetadata
    public BrokerAndEcDetailsResource getBrokerAndEcDetails(
            @Parameter(description = "Broker's tax code of an EC") @PathVariable(name = "ci-tax-code") String brokerEcCode
    ) {
        return ciService.getBrokerAndEcDetails(brokerEcCode);

    }

    /**
     * Retrieve the operative table and the payment contacts list of the creditor institution with the provided
     * tax code and institution's id
     *
     * @param ciTaxCode     creditor institution's tax code
     * @param institutionId creditor institution's identifier
     * @return the creditor institution's contacts
     */
    @GetMapping(value = "/{ci-tax-code}/contacts", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = CreditorInstitutionContactsResource.class))),
            @ApiResponse(responseCode = "400", description = "Bad Request", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ProblemJson.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content(schema = @Schema())),
            @ApiResponse(responseCode = "403", description = "Forbidden", content = @Content(schema = @Schema())),
            @ApiResponse(responseCode = "404", description = "Not found", content = @Content(schema = @Schema(implementation = ProblemJson.class))),
            @ApiResponse(responseCode = "429", description = "Too many requests", content = @Content(schema = @Schema())),
            @ApiResponse(responseCode = "500", description = "Service unavailable", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ProblemJson.class)))
    })
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Get creditor institution's payment contacts and operative table", security = {@SecurityRequirement(name = "JWT")})
    @OpenApiTableMetadata(readWriteIntense = OpenApiTableMetadata.ReadWrite.READ)
    public CreditorInstitutionContactsResource getCreditorInstitutionContacts(
            @Parameter(description = "Creditor institution code") @PathVariable("ci-tax-code") @NotBlank String ciTaxCode,
            @Parameter(description = "Institution's identifier") @RequestParam String institutionId
    ) {
        return ciService.getCreditorInstitutionContacts(ciTaxCode, institutionId);
    }


    /**
     * Retrieve the list of creditor institutions that can be associated to the specified station of the specified broker
     *
     * @param stationCode station's code
     * @param brokerId    identifier of the broker that own the station
     * @return the list of creditor institution's
     */
    @GetMapping(value = "/stations/{station-code}")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = CreditorInstitutionInfoResource.class))),
            @ApiResponse(responseCode = "400", description = "Bad Request",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ProblemJson.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content(schema = @Schema())),
            @ApiResponse(responseCode = "403", description = "Forbidden", content = @Content(schema = @Schema())),
            @ApiResponse(responseCode = "404", description = "Not found", content = @Content(schema = @Schema(implementation = ProblemJson.class))),
            @ApiResponse(responseCode = "429", description = "Too many requests", content = @Content(schema = @Schema())),
            @ApiResponse(responseCode = "500", description = "Service unavailable",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ProblemJson.class)))
    })
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Get the list of Creditor Institutions that can be associated to the station", security = {@SecurityRequirement(name = "JWT")})
    @OpenApiTableMetadata(readWriteIntense = OpenApiTableMetadata.ReadWrite.READ)
    @JwtSecurity(paramName = "stationCode", removeParamSuffix = true)
    public CreditorInstitutionInfoResource getAvailableCreditorInstitutionsForStation(
            @Parameter(description = "Station's code") @PathVariable("station-code") String stationCode,
            @Parameter(description = "Broker's unique id") @RequestParam String brokerId,
            @Parameter(description = "Creditor institution's name, used to filter out results") @RequestParam(required = false) String ciName
    ) {
        return this.ciService.getAvailableCreditorInstitutionsForStation(stationCode, brokerId, ciName);
    }
}
