package it.pagopa.selfcare.pagopa.backoffice.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import it.pagopa.selfcare.pagopa.backoffice.entity.WrapperEntities;
import it.pagopa.selfcare.pagopa.backoffice.entity.WrapperEntityOperations;
import it.pagopa.selfcare.pagopa.backoffice.model.ProblemJson;
import it.pagopa.selfcare.pagopa.backoffice.model.connector.station.StationDetails;
import it.pagopa.selfcare.pagopa.backoffice.model.connector.wrapper.ConfigurationStatus;
import it.pagopa.selfcare.pagopa.backoffice.model.creditorinstituions.CreditorInstitutionsResource;
import it.pagopa.selfcare.pagopa.backoffice.model.stations.StationCodeResource;
import it.pagopa.selfcare.pagopa.backoffice.model.stations.StationDetailResource;
import it.pagopa.selfcare.pagopa.backoffice.model.stations.StationDetailsDto;
import it.pagopa.selfcare.pagopa.backoffice.model.stations.StationTestDto;
import it.pagopa.selfcare.pagopa.backoffice.model.stations.TestStationResource;
import it.pagopa.selfcare.pagopa.backoffice.model.stations.WrapperStationDetailsDto;
import it.pagopa.selfcare.pagopa.backoffice.model.stations.WrapperStationsResource;
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
import javax.validation.constraints.Min;
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

    @PostMapping(value = "", produces = {MediaType.APPLICATION_JSON_VALUE})
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create new station", security = {@SecurityRequirement(name = "JWT")})
    @OpenApiTableMetadata
    public WrapperEntityOperations<StationDetails> createStation(@RequestBody @NotNull StationDetailsDto stationDetailsDto) {
        return stationService.createStation(stationDetailsDto);
    }

    @GetMapping("")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Get paginated list of stations", security = {@SecurityRequirement(name = "JWT")})
    @OpenApiTableMetadata
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = WrapperStationsResource.class))),
            @ApiResponse(responseCode = "400", description = "Bad Request", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ProblemJson.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content(schema = @Schema())),
            @ApiResponse(responseCode = "429", description = "Too many requests", content = @Content(schema = @Schema())),
            @ApiResponse(responseCode = "500", description = "Service unavailable", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ProblemJson.class)))
    })
    public WrapperStationsResource getStations(
            @Parameter(description = "Station's status") @RequestParam ConfigurationStatus status,
            @Parameter(description = "Station's unique identifier") @RequestParam(required = false) String stationCode,
            @Parameter(description = "Broker's code") @RequestParam("brokerCode") String brokerCode,
            @Parameter(description = "Number of elements in one page") @RequestParam(required = false, defaultValue = "50") @Positive Integer limit,
            @Parameter(description = "Page number") @RequestParam(defaultValue = "0") @PositiveOrZero Integer page
    ) {
        return stationService.getStations(status, stationCode, brokerCode, limit, page);
    }

    @GetMapping(value = "/{station-code}", produces = {MediaType.APPLICATION_JSON_VALUE})
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Get station's details", security = {@SecurityRequirement(name = "JWT")})
    @OpenApiTableMetadata
    public StationDetailResource getStation(
            @Parameter(description = "Station's unique identifier") @PathVariable("station-code") String stationCode
    ) {
        return stationService.getStation(stationCode);

    }

    @GetMapping(value = "/{station-code}/creditor-institutions")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Get a paginated list of Creditor Institutions associated to a station", security = {@SecurityRequirement(name = "JWT")})
    @OpenApiTableMetadata
    public CreditorInstitutionsResource getCreditorInstitutionsByStationCode(
            @Parameter(description = "Station Code") @PathVariable("station-code") String stationCode,
            @Parameter(description = "Number of elements in one page") @RequestParam(required = false, defaultValue = "50") Integer limit,
            @Parameter(description = "Page number") @RequestParam Integer page,
            @Parameter(description = "Filter by creditor institution name or creditor institution fiscal code") @RequestParam(required = false, name = "ci-name-or-fiscalcode") String ciNameOrFiscalCode
    ) {
        return stationService.getCreditorInstitutionsByStationCode(stationCode, limit, page, ciNameOrFiscalCode);
    }

    @PutMapping(value = "/{station-code}", produces = {MediaType.APPLICATION_JSON_VALUE})
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Update a station", security = {@SecurityRequirement(name = "JWT")})
    @OpenApiTableMetadata
    public StationDetailResource updateStation(
            @RequestBody @NotNull StationDetailsDto stationDetailsDto,
            @Parameter(description = "Station's unique identifier") @PathVariable("station-code") String stationCode
    ) {
        return stationService.updateStation(stationDetailsDto, stationCode);
    }

    @GetMapping(value = "/wrapper/{station-code}")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Get wrapper station from mongo DB", security = {@SecurityRequirement(name = "JWT")})
    @OpenApiTableMetadata
    public WrapperEntities getWrapperEntitiesStation(
            @Parameter(description = "ChannelCode or StationCode") @PathVariable("station-code") String code
    ) {
        return stationService.getWrapperEntitiesStation(code);
    }

    @GetMapping(value = "/merged", produces = {MediaType.APPLICATION_JSON_VALUE})
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Get All Stations from cosmos db merged whit apiConfig", security = {@SecurityRequirement(name = "JWT")})
    @OpenApiTableMetadata
    public WrapperStationsResource getAllStationsMerged(
            @Parameter(description = "Number of elements in one page") @RequestParam(required = false, defaultValue = "50") Integer limit,
            @Parameter(description = "Station's unique identifier") @RequestParam(required = false, value = "stationcodefilter") String stationCode,
            @Parameter(description = "Broker code filter for search") @RequestParam("brokerCode") String brokerCode,
            @Parameter(description = "Page number") @PositiveOrZero @Min(0) @RequestParam Integer page,
            @Parameter(description = "Method of sorting") @RequestParam(required = false, value = "sorting") String sorting
    ) {
        return stationService.getAllStationsMerged(limit, stationCode, brokerCode, page, sorting);
    }

    @PostMapping(value = "/wrapper", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create a WrapperChannel on Cosmodb", security = {@SecurityRequirement(name = "JWT")})
    @OpenApiTableMetadata
    public WrapperEntities<StationDetails> createWrapperStationDetails(
            @RequestBody @Valid WrapperStationDetailsDto wrapperStationDetailsDto
    ) {
        return stationService.createWrapperStationDetails(wrapperStationDetailsDto);
    }

    @GetMapping(value = "/merged/{station-code}", produces = {MediaType.APPLICATION_JSON_VALUE})
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Get station's details", security = {@SecurityRequirement(name = "JWT")})
    @OpenApiTableMetadata
    public StationDetailResource getStationDetail(
            @Parameter(description = "Station's unique identifier") @PathVariable("station-code") String stationCode
    ) {
        return stationService.getStationDetail(stationCode);
    }

    @GetMapping(value = "/station-code", produces = {MediaType.APPLICATION_JSON_VALUE})
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Generate a station code given the creditor institution's code", security = {@SecurityRequirement(name = "JWT")})
    @OpenApiTableMetadata
    public StationCodeResource getStationCode(
            @Parameter(description = "Creditor institution code") @RequestParam(value = "ec-code") String ecCode
    ) {
        return stationService.getStationCode(ecCode, false);
    }

    @GetMapping(value = "/station-code/v2", produces = {MediaType.APPLICATION_JSON_VALUE})
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Generate a station code given the creditor institution's code", security = {@SecurityRequirement(name = "JWT")})
    @OpenApiTableMetadata
    public StationCodeResource getStationCodeV2(
            @Parameter(description = "Creditor institution code") @RequestParam(value = "ec-code") String ecCode
    ) {
        return stationService.getStationCode(ecCode, true);
    }

    @PutMapping(value = "/wrapper/{station-code}", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Update WrapperStationDetails", security = {@SecurityRequirement(name = "JWT")})
    @OpenApiTableMetadata
    public WrapperEntities updateWrapperStationDetails(
            @Parameter(description = "Station code") @PathVariable(value = "station-code") String stationCode,
            @RequestBody @Valid StationDetailsDto stationDetailsDto
    ) {
        // TODO use station code
        return stationService.updateWrapperStationDetails(stationDetailsDto);
    }

    @PutMapping(value = "/wrapper/operator", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Update a WrapperStation on Cosmodb", security = {@SecurityRequirement(name = "JWT")})
    @OpenApiTableMetadata
    public WrapperEntities updateWrapperStationDetailsByOpt(@RequestBody @Valid StationDetailsDto stationDetailsDto) {
        return stationService.updateWrapperStationDetailsByOpt(stationDetailsDto);

    }

    @PostMapping(value = "/connection/test", produces = {MediaType.APPLICATION_JSON_VALUE})
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Test station connectivity", security = {@SecurityRequirement(name = "JWT")})
    @OpenApiTableMetadata
    public TestStationResource testStation(@RequestBody @Valid @NotNull StationTestDto stationTestDto) {
        return stationService.testStation(stationTestDto);
    }
}
