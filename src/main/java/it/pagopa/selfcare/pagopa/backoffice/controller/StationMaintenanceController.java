package it.pagopa.selfcare.pagopa.backoffice.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import it.pagopa.selfcare.pagopa.backoffice.exception.AppException;
import it.pagopa.selfcare.pagopa.backoffice.model.ProblemJson;
import it.pagopa.selfcare.pagopa.backoffice.model.stationmaintenance.CreateStationMaintenance;
import it.pagopa.selfcare.pagopa.backoffice.model.stationmaintenance.MaintenanceHoursSummaryResource;
import it.pagopa.selfcare.pagopa.backoffice.model.stationmaintenance.StationMaintenanceListResource;
import it.pagopa.selfcare.pagopa.backoffice.model.stationmaintenance.StationMaintenanceListState;
import it.pagopa.selfcare.pagopa.backoffice.model.stationmaintenance.StationMaintenanceResource;
import it.pagopa.selfcare.pagopa.backoffice.model.stationmaintenance.UpdateStationMaintenance;
import it.pagopa.selfcare.pagopa.backoffice.security.JwtSecurity;
import it.pagopa.selfcare.pagopa.backoffice.service.StationMaintenanceService;
import it.pagopa.selfcare.pagopa.backoffice.util.OpenApiTableMetadata;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
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
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import javax.validation.constraints.Size;

@Slf4j
@RestController
@RequestMapping(value = "/brokers", produces = MediaType.APPLICATION_JSON_VALUE)
@Tag(name = "Station Maintenance")
public class StationMaintenanceController {

    private final StationMaintenanceService stationMaintenanceService;

    @Autowired
    public StationMaintenanceController(StationMaintenanceService stationMaintenanceService) {
        this.stationMaintenanceService = stationMaintenanceService;
    }

    @Operation(summary = "Get a paginated list of station's maintenance for the specified broker",
            security = {@SecurityRequirement(name = "JWT")})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Ok",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = StationMaintenanceListResource.class))),
            @ApiResponse(responseCode = "400", description = "Bad Request",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ProblemJson.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content(schema = @Schema())),
            @ApiResponse(responseCode = "403", description = "Forbidden", content = @Content(schema = @Schema())),
            @ApiResponse(responseCode = "429", description = "Too many requests", content = @Content(schema = @Schema())),
            @ApiResponse(responseCode = "500", description = "Service unavailable",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ProblemJson.class)))
    })
    @GetMapping(value = "/{broker-tax-code}/station-maintenances", produces = {MediaType.APPLICATION_JSON_VALUE})
    @JwtSecurity(paramName = "brokerCode")
    public StationMaintenanceListResource getStationMaintenances(
            @Parameter(description = "Broker's tax code") @PathVariable("broker-tax-code") String brokerCode,
            @Parameter(description = "Station's code") @RequestParam(required = false) String stationCode,
            @Parameter(description = "Maintenances' state") @RequestParam(required = false) StationMaintenanceListState state,
            @Parameter(description = "Maintenance's starting year") @RequestParam(required = false) Integer year,
            @Parameter(description = "Number of items for page") @RequestParam(required = false, defaultValue = "50") @Positive Integer limit,
            @Parameter(description = "Page number") @RequestParam(required = false, defaultValue = "0") @Min(0) @PositiveOrZero Integer page
    ) {
        return this.stationMaintenanceService.getStationMaintenances(
                brokerCode,
                stationCode,
                state,
                year,
                limit,
                page
        );
    }

    @PostMapping(value = "/{broker-tax-code}/station-maintenances", produces = {MediaType.APPLICATION_JSON_VALUE})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Created",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = StationMaintenanceResource.class))),
            @ApiResponse(responseCode = "400", description = "Bad Request",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ProblemJson.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content(schema = @Schema())),
            @ApiResponse(responseCode = "403", description = "Forbidden", content = @Content(schema = @Schema())),
            @ApiResponse(responseCode = "409", description = "Conflict",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ProblemJson.class))),
            @ApiResponse(responseCode = "429", description = "Too many requests", content = @Content(schema = @Schema())),
            @ApiResponse(responseCode = "500", description = "Service unavailable",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ProblemJson.class)))
    })
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Schedule a maintenance period for a Station", security = {@SecurityRequirement(name = "JWT")})
    @OpenApiTableMetadata(readWriteIntense = OpenApiTableMetadata.ReadWrite.WRITE)
    @JwtSecurity(paramName = "brokerCode")
    public StationMaintenanceResource createStationMaintenance(
            @Parameter(description = "Broker's tax code") @PathVariable("broker-tax-code") String brokerCode,
            @RequestBody @Valid @NotNull CreateStationMaintenance createStationMaintenance
    ) {
        return this.stationMaintenanceService.createStationMaintenance(brokerCode, createStationMaintenance);
    }

    @Operation(summary = "Update a scheduled maintenance for the specified station",
            security = {@SecurityRequirement(name = "JWT")})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = StationMaintenanceResource.class))),
            @ApiResponse(responseCode = "400", description = "Bad Request",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ProblemJson.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content(schema = @Schema())),
            @ApiResponse(responseCode = "403", description = "Forbidden", content = @Content(schema = @Schema())),
            @ApiResponse(responseCode = "409", description = "Conflict",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ProblemJson.class))),
            @ApiResponse(responseCode = "429", description = "Too many requests", content = @Content(schema = @Schema())),
            @ApiResponse(responseCode = "500", description = "Service unavailable",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ProblemJson.class)))
    })
    @ResponseStatus(HttpStatus.OK)
    @PutMapping(value = "/{broker-tax-code}/station-maintenances/{maintenance-id}", produces = {MediaType.APPLICATION_JSON_VALUE})
    @OpenApiTableMetadata(readWriteIntense = OpenApiTableMetadata.ReadWrite.WRITE)
    @JwtSecurity(paramName = "brokerCode")
    public StationMaintenanceResource updateStationMaintenance(
            @Parameter(description = "Broker's tax code") @PathVariable("broker-tax-code") String brokerCode,
            @Parameter(description = "Maintenance's id") @PathVariable("maintenance-id") Long maintenanceId,
            @RequestBody @Valid @NotNull UpdateStationMaintenance updateStationMaintenance
    ) {
        return this.stationMaintenanceService.updateStationMaintenance(brokerCode, maintenanceId, updateStationMaintenance);
    }

    /**
     * Retrieves broker related station maintenance summary for the provided year
     *
     * @param brokerCode      broker id to use for summary retrieval
     * @param maintenanceYear year in format yyyy, to be used for summary retrieval
     * @return maintenance summary for the provided year and brokerCode
     */
    @Operation(summary = "Get the hours' summary of stations' maintenance for the specified broker",
            security = {@SecurityRequirement(name = "JWT")})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = MaintenanceHoursSummaryResource.class))),
            @ApiResponse(responseCode = "400", description = "Bad Request",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ProblemJson.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content(schema = @Schema())),
            @ApiResponse(responseCode = "403", description = "Forbidden", content = @Content(schema = @Schema())),
            @ApiResponse(responseCode = "404", description = "Not Found",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ProblemJson.class))),
            @ApiResponse(responseCode = "429", description = "Too many requests", content = @Content(schema = @Schema())),
            @ApiResponse(responseCode = "500", description = "Service unavailable",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ProblemJson.class)))
    })
    @GetMapping(value = "/{broker-tax-code}/station-maintenances/summary", produces = {MediaType.APPLICATION_JSON_VALUE})
    @JwtSecurity(paramName = "brokerCode")
    public MaintenanceHoursSummaryResource getBrokerMaintenancesSummary(
            @Parameter(description = "Broker's tax code") @PathVariable("broker-tax-code") String brokerCode,
            @Parameter(description = "Year of maintenance (yyyy)", example = "2024") @RequestParam @Size(min = 4, max = 4) String maintenanceYear
    ) {
        return this.stationMaintenanceService.getBrokerMaintenancesSummary(brokerCode, maintenanceYear);
    }

    /**
     * Recovers a station maintenance, given its brokerCode and maintenanceId.
     * If the provided brokerCode doesn't match the one related to the persisted one for the given maintenance,
     * it will throw the maintenance not found exception
     *
     * @param brokerCode    brokerCode to be used as filter in the maintenance recovery
     * @param maintenanceId station maintenance id to be used for the detail recovery
     * @return station maintenance data, provided in an instance of StationMaintenanceResource
     * @throws AppException thrown when a maintenance, given the input data, has not been found
     */
    @Operation(summary = "Get a maintenance for the specified station, given its broker code and maintenance id",
            security = {@SecurityRequirement(name = "JWT")})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Created",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = StationMaintenanceResource.class))),
            @ApiResponse(responseCode = "400", description = "Bad Request",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ProblemJson.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content(schema = @Schema())),
            @ApiResponse(responseCode = "403", description = "Forbidden", content = @Content(schema = @Schema())),
            @ApiResponse(responseCode = "409", description = "Conflict",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ProblemJson.class))),
            @ApiResponse(responseCode = "429", description = "Too many requests", content = @Content(schema = @Schema())),
            @ApiResponse(responseCode = "500", description = "Service unavailable",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ProblemJson.class)))
    })
    @GetMapping(value = "/{broker-tax-code}/station-maintenances/{maintenance-id}", produces = {MediaType.APPLICATION_JSON_VALUE})
    @JwtSecurity(paramName = "brokerCode")
    public StationMaintenanceResource getStationMaintenance(
            @Parameter(description = "Broker's tax code") @PathVariable("broker-tax-code") String brokerCode,
            @Parameter(description = "Maintenance's id") @PathVariable("maintenance-id") Long maintenanceId
    ) {
        return this.stationMaintenanceService.getStationMaintenance(brokerCode, maintenanceId);
    }

    @Operation(summary = "Delete a station's maintenance", security = {@SecurityRequirement(name = "JWT")})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK", content = @Content(schema = @Schema())),
            @ApiResponse(responseCode = "400", description = "Bad Request",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ProblemJson.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content(schema = @Schema())),
            @ApiResponse(responseCode = "403", description = "Forbidden", content = @Content(schema = @Schema())),
            @ApiResponse(responseCode = "404", description = "Not Found",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ProblemJson.class))),
            @ApiResponse(responseCode = "429", description = "Too many requests", content = @Content(schema = @Schema())),
            @ApiResponse(responseCode = "500", description = "Service unavailable",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ProblemJson.class)))
    })
    @DeleteMapping(value = "/{broker-tax-code}/station-maintenances/{maintenance-id}")
    @JwtSecurity(paramName = "brokerCode")
    public void deleteStationMaintenance(
            @Parameter(description = "Broker's tax code") @PathVariable("broker-tax-code") String brokerCode,
            @Parameter(description = "Maintenance's id") @PathVariable("maintenance-id") Long maintenanceId
    ) {
        this.stationMaintenanceService.deleteStationMaintenance(brokerCode, maintenanceId);
    }

    @PostMapping(value = "/{broker-tax-code}/station-maintenances/{maintenance-id}/finish", produces = {MediaType.APPLICATION_JSON_VALUE})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK", content = @Content(schema = @Schema())),
            @ApiResponse(responseCode = "400", description = "Bad Request",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ProblemJson.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content(schema = @Schema())),
            @ApiResponse(responseCode = "403", description = "Forbidden", content = @Content(schema = @Schema())),
            @ApiResponse(responseCode = "404", description = "Not found",
                    content = @Content(schema = @Schema(implementation = ProblemJson.class))),
            @ApiResponse(responseCode = "429", description = "Too many requests", content = @Content(schema = @Schema())),
            @ApiResponse(responseCode = "500", description = "Service unavailable",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ProblemJson.class)))
    })
    @Operation(summary = "Finish an in progress station's maintenance", security = {@SecurityRequirement(name = "JWT")})
    @OpenApiTableMetadata(readWriteIntense = OpenApiTableMetadata.ReadWrite.NONE)
    @JwtSecurity(paramName = "brokerCode")
    public void finishStationMaintenance(
            @Parameter(description = "Broker's tax code") @PathVariable("broker-tax-code") String brokerCode,
            @Parameter(description = "Maintenance's id") @PathVariable("maintenance-id") Long maintenanceId
    ) {
        this.stationMaintenanceService.finishStationMaintenance(brokerCode, maintenanceId);
    }
}
