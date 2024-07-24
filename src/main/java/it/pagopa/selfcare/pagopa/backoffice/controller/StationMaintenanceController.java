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
import it.pagopa.selfcare.pagopa.backoffice.model.stationmaintenance.CreateStationMaintenance;
import it.pagopa.selfcare.pagopa.backoffice.model.stationmaintenance.StationMaintenanceResource;
import it.pagopa.selfcare.pagopa.backoffice.service.StationMaintenanceService;
import it.pagopa.selfcare.pagopa.backoffice.util.OpenApiTableMetadata;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

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

    @PostMapping(value = "/{brokercode}/station-maintenances", produces = {MediaType.APPLICATION_JSON_VALUE})
    @ApiResponses(
            value = {
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
    public StationMaintenanceResource createStationMaintenance(
            @Parameter(description = "Broker's tax code") @PathVariable("brokercode") String brokerCode,
            @RequestBody @Valid @NotNull CreateStationMaintenance createStationMaintenance
    ) {
        return this.stationMaintenanceService.createStationMaintenance(brokerCode, createStationMaintenance);
    }

    /**
     * Recovers a station maintenance, given its brokerCode and maintenanceId.
     * If the the provided brokerCode doesnt match the one related to the persisted one for the given maintenance,
     * it will throw the maintenance not found exception
     * @param brokerCode brokerCode to be used as filter in the maintenance recovery
     * @param maintenanceId station maintentance id to be used for the detail recovery
     * @return station maintenance data, provided in an instance of StationMaintenanceResource
     * @throws AppException thrown when a maintenance, given the input data, has not been found
     */
    @Operation(summary = "Get a maintenance for the specified station, given its broker code and maintenance id",
            security = {@SecurityRequirement(name = "ApiKey"), @SecurityRequirement(name = "Authorization")})
    @ApiResponses(
            value = {
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
    @GetMapping(value = "/{brokercode}/station-maintenances/{maintenanceid}", produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<StationMaintenanceResource> getStationMaintenance(
            @Parameter(description = "Broker's tax code") @PathVariable("brokercode") String brokerCode,
            @Parameter(description = "Maintenance's id") @PathVariable("maintenanceid") Long maintenanceId
    ) {
        return ResponseEntity.ok(this.stationMaintenanceService.getStationMaintenance(brokerCode, maintenanceId));
    }

}
