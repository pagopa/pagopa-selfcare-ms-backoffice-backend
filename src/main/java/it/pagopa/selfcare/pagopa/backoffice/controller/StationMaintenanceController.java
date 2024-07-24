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
import it.pagopa.selfcare.pagopa.backoffice.model.stationmaintenance.StationMaintenanceListResource;
import it.pagopa.selfcare.pagopa.backoffice.model.stationmaintenance.StationMaintenanceListState;
import it.pagopa.selfcare.pagopa.backoffice.model.stationmaintenance.StationMaintenanceResource;
import it.pagopa.selfcare.pagopa.backoffice.service.StationMaintenanceService;
import it.pagopa.selfcare.pagopa.backoffice.util.OpenApiTableMetadata;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

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
            security = {@SecurityRequirement(name = "Authorization")})
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
    @GetMapping(value = "/{brokercode}/station-maintenances", produces = {MediaType.APPLICATION_JSON_VALUE})
    public StationMaintenanceListResource getStationMaintenances(
            @Parameter(description = "Broker's tax code") @PathVariable("brokercode") String brokerCode,
            @Parameter(description = "Station's code") @RequestParam(required = false) String stationCode,
            @Parameter(description = "Maintenances' state") @RequestParam(required = false) StationMaintenanceListState state,
            @Parameter(description = "Maintenance's starting year") @RequestParam(required = false) Integer year,
            @Parameter(description = "Number of items for page") @RequestParam(required = false, defaultValue = "50") @Positive Integer limit,
            @Parameter(description = "Page number") @RequestParam(required = false, defaultValue = "0") @Min(0) @PositiveOrZero Integer page
    ) {
        return
                this.stationMaintenanceService.getStationMaintenances(
                        brokerCode,
                        stationCode,
                        state,
                        year,
                        limit,
                        page
                );
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
}
