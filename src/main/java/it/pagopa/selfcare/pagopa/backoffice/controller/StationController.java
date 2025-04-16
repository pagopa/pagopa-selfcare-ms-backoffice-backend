package it.pagopa.selfcare.pagopa.backoffice.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import it.pagopa.selfcare.pagopa.backoffice.entity.WrapperEntityStations;
import it.pagopa.selfcare.pagopa.backoffice.model.ProblemJson;
import it.pagopa.selfcare.pagopa.backoffice.model.connector.wrapper.ConfigurationStatus;
import it.pagopa.selfcare.pagopa.backoffice.model.creditorinstituions.CreditorInstitutionsResource;
import it.pagopa.selfcare.pagopa.backoffice.model.stations.OperatorStationReview;
import it.pagopa.selfcare.pagopa.backoffice.model.stations.StationCodeResource;
import it.pagopa.selfcare.pagopa.backoffice.model.stations.StationDetailResource;
import it.pagopa.selfcare.pagopa.backoffice.model.stations.StationDetailsDto;
import it.pagopa.selfcare.pagopa.backoffice.model.stations.StationTestDto;
import it.pagopa.selfcare.pagopa.backoffice.model.stations.TestStationResource;
import it.pagopa.selfcare.pagopa.backoffice.model.stations.WrapperStationDetailsDto;
import it.pagopa.selfcare.pagopa.backoffice.model.stations.WrapperStationsResource;
import it.pagopa.selfcare.pagopa.backoffice.security.JwtSecurity;
import it.pagopa.selfcare.pagopa.backoffice.service.StationService;
import it.pagopa.selfcare.pagopa.backoffice.util.OpenApiTableMetadata;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
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
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@RestController
@RequestMapping(value = "/stations")
@Tag(name = "Stations")
public class StationController {

    private final StationService stationService;

    @Autowired
    public StationController(StationService stationService) {
        this.stationService = stationService;
    }

    @GetMapping("")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Get paginated list of stations", security = {@SecurityRequirement(name = "JWT")})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = WrapperStationsResource.class))),
            @ApiResponse(responseCode = "400", description = "Bad Request", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ProblemJson.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content(schema = @Schema())),
            @ApiResponse(responseCode = "429", description = "Too many requests", content = @Content(schema = @Schema())),
            @ApiResponse(responseCode = "500", description = "Service unavailable", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ProblemJson.class)))
    })
    @OpenApiTableMetadata(readWriteIntense = OpenApiTableMetadata.ReadWrite.READ)
    @JwtSecurity(paramName = "brokerCode")
    public WrapperStationsResource getStations(
            @Parameter(description = "Station's status") @RequestParam ConfigurationStatus status,
            @Parameter(description = "Station's unique identifier") @RequestParam(required = false) String stationCode,
            @Parameter(description = "Broker's code") @RequestParam("brokerCode") String brokerCode,
            @Parameter(description = "Number of elements in one page") @RequestParam(required = false, defaultValue = "50") @Positive Integer limit,
            @Parameter(description = "Page number") @RequestParam(defaultValue = "0") @PositiveOrZero Integer page
    ) {
        return this.stationService.getStations(status, stationCode, brokerCode, limit, page);
    }

    @GetMapping(value = "/{station-code}", produces = {MediaType.APPLICATION_JSON_VALUE})
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Get station's details", security = {@SecurityRequirement(name = "JWT")})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = StationDetailResource.class))),
            @ApiResponse(responseCode = "400", description = "Bad Request", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ProblemJson.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content(schema = @Schema())),
            @ApiResponse(responseCode = "429", description = "Too many requests", content = @Content(schema = @Schema())),
            @ApiResponse(responseCode = "500", description = "Service unavailable", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ProblemJson.class)))
    })
    @OpenApiTableMetadata
    @JwtSecurity(paramName = "stationCode", removeParamSuffix = true)
    public StationDetailResource getStationDetails(
            @Parameter(description = "Station's code") @PathVariable("station-code") String stationCode,
            @Parameter(description = "Station's status") @RequestParam ConfigurationStatus status
    ) {
        return this.stationService.getStationDetails(stationCode, status);
    }

    @GetMapping(value = "/{station-code}/creditor-institutions")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Get a paginated list of Creditor Institutions associated to a station", security = {@SecurityRequirement(name = "JWT")})
    @OpenApiTableMetadata
    @JwtSecurity(paramName = "stationCode", removeParamSuffix = true)
    public CreditorInstitutionsResource getCreditorInstitutionsByStationCode(
            @Parameter(description = "Station Code") @PathVariable("station-code") String stationCode,
            @Parameter(description = "Number of elements in one page") @RequestParam(required = false, defaultValue = "50") Integer limit,
            @Parameter(description = "Page number") @RequestParam Integer page,
            @Parameter(description = "Filter by creditor institution name or creditor institution fiscal code") @RequestParam(required = false, name = "ci-name-or-fiscalcode") String ciNameOrFiscalCode
    ) {
        return this.stationService.getCreditorInstitutionsByStationCode(stationCode, limit, page, ciNameOrFiscalCode);
    }

    @PostMapping(value = "", produces = {MediaType.APPLICATION_JSON_VALUE})
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create new station", security = {@SecurityRequirement(name = "JWT")})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "OK", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = StationDetailResource.class))),
            @ApiResponse(responseCode = "400", description = "Bad Request", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ProblemJson.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content(schema = @Schema())),
            @ApiResponse(responseCode = "429", description = "Too many requests", content = @Content(schema = @Schema())),
            @ApiResponse(responseCode = "500", description = "Service unavailable", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ProblemJson.class)))
    })
    @OpenApiTableMetadata
    @JwtSecurity(paramName = "brokerCode", checkParamInsideBody = true)
    public StationDetailResource createStation(@RequestBody @NotNull StationDetailsDto stationDetailsDto) {
        return this.stationService.createStation(stationDetailsDto);
    }

    @PutMapping(value = "/{station-code}", produces = {MediaType.APPLICATION_JSON_VALUE})
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Update a station", security = {@SecurityRequirement(name = "JWT")})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = StationDetailResource.class))),
            @ApiResponse(responseCode = "400", description = "Bad Request", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ProblemJson.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content(schema = @Schema())),
            @ApiResponse(responseCode = "429", description = "Too many requests", content = @Content(schema = @Schema())),
            @ApiResponse(responseCode = "500", description = "Service unavailable", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ProblemJson.class)))
    })
    @OpenApiTableMetadata
    @JwtSecurity(paramName = "stationCode", removeParamSuffix = true)
    public StationDetailResource updateStation(
            @Parameter(description = "Station's unique identifier") @PathVariable("station-code") String stationCode,
            @RequestBody @NotNull StationDetailsDto stationDetailsDto
    ) {
        return this.stationService.updateStation(stationDetailsDto, stationCode);
    }

    @PostMapping(value = "/wrapper", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Request the creation of a station that will be validated by an operator", security = {@SecurityRequirement(name = "JWT")})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "OK", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = WrapperEntityStations.class))),
            @ApiResponse(responseCode = "400", description = "Bad Request", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ProblemJson.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content(schema = @Schema())),
            @ApiResponse(responseCode = "429", description = "Too many requests", content = @Content(schema = @Schema())),
            @ApiResponse(responseCode = "500", description = "Service unavailable", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ProblemJson.class)))
    })
    @OpenApiTableMetadata
    @JwtSecurity(paramName = "brokerCode", checkParamInsideBody = true)
    public WrapperEntityStations createWrapperStationDetails(
            @RequestBody @Valid WrapperStationDetailsDto wrapperStationDetailsDto
    ) {
        return this.stationService.createWrapperStationDetails(wrapperStationDetailsDto);
    }

    /**
     * @deprecated this API invoke the old station code generation logic that can cause collision on wrapper data for PT
     */
    @Deprecated(forRemoval = true)
    @GetMapping(value = "/station-code", produces = {MediaType.APPLICATION_JSON_VALUE})
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Generate a station code given the creditor institution's code", security = {@SecurityRequirement(name = "JWT")})
    @OpenApiTableMetadata
    @JwtSecurity(paramName = "ecCode")
    public StationCodeResource getStationCode(
            @Parameter(description = "Creditor institution code") @RequestParam(value = "ec-code") String ecCode
    ) {
        return this.stationService.getStationCode(ecCode, false);
    }

    @GetMapping(value = "/station-code/v2", produces = {MediaType.APPLICATION_JSON_VALUE})
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Generate a station code given the creditor institution's code", security = {@SecurityRequirement(name = "JWT")})
    @OpenApiTableMetadata
    @JwtSecurity(paramName = "ecCode")
    public StationCodeResource getStationCodeV2(
            @Parameter(description = "Creditor institution code") @RequestParam(value = "ec-code") String ecCode
    ) {
        return this.stationService.getStationCode(ecCode, true);
    }

    @PutMapping(value = "/wrapper/{station-code}", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Request the update of a station that will be validated by an operator", security = {@SecurityRequirement(name = "JWT")})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = StationDetailResource.class))),
            @ApiResponse(responseCode = "400", description = "Bad Request", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ProblemJson.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content(schema = @Schema())),
            @ApiResponse(responseCode = "429", description = "Too many requests", content = @Content(schema = @Schema())),
            @ApiResponse(responseCode = "500", description = "Service unavailable", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ProblemJson.class)))
    })
    @OpenApiTableMetadata
    @JwtSecurity(paramName = "stationCode", removeParamSuffix = true)
    public StationDetailResource updateWrapperStationDetails(
            @Parameter(description = "Station code") @PathVariable(value = "station-code") String stationCode,
            @RequestBody @Valid StationDetailsDto stationDetailsDto
    ) {
        return this.stationService.updateWrapperStationDetails(stationCode, stationDetailsDto);
    }

    /**
     * Updates a station wrapper with the operator review's note
     *
     * @param stationCode station's code
     * @param ciTaxCode   creditor institution's tax code that own the station
     * @param note        operator review note
     * @return the updated station wrapper
     */
    @PutMapping(value = "/wrapper/{station-code}/operator", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Update a WrapperStation with Operator review", security = {@SecurityRequirement(name = "JWT")})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = StationDetailResource.class))),
            @ApiResponse(responseCode = "400", description = "Bad Request", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ProblemJson.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content(schema = @Schema())),
            @ApiResponse(responseCode = "404", description = "Not found", content = @Content(schema = @Schema(implementation = ProblemJson.class))),
            @ApiResponse(responseCode = "429", description = "Too many requests", content = @Content(schema = @Schema())),
            @ApiResponse(responseCode = "500", description = "Service unavailable", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ProblemJson.class)))
    })
    @OpenApiTableMetadata
    @JwtSecurity(paramName = "ciTaxCode")
    public StationDetailResource updateWrapperStationWithOperatorReview(
            @Parameter(description = "Station code") @PathVariable(value = "station-code") String stationCode,
            @Parameter(description = "Creditor institution's tax code that own the station") @RequestParam String ciTaxCode,
            @RequestBody @Valid OperatorStationReview note
    ) {
        return this.stationService.updateWrapperStationWithOperatorReview(stationCode, ciTaxCode, note.getNote());

    }

    @PostMapping(value = "/connection/test", produces = {MediaType.APPLICATION_JSON_VALUE})
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Test station connectivity", security = {@SecurityRequirement(name = "JWT")})
    @OpenApiTableMetadata
    public TestStationResource testStation(@RequestBody @Valid @NotNull StationTestDto stationTestDto) {
        return this.stationService.testStation(stationTestDto);
    }
}
