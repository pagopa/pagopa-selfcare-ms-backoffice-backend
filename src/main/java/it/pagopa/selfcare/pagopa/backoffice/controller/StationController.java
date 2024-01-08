package it.pagopa.selfcare.pagopa.backoffice.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import it.pagopa.selfcare.pagopa.backoffice.entity.WrapperEntities;
import it.pagopa.selfcare.pagopa.backoffice.entity.WrapperEntityOperations;
import it.pagopa.selfcare.pagopa.backoffice.model.connector.station.StationDetails;
import it.pagopa.selfcare.pagopa.backoffice.model.creditorinstituions.CreditorInstitutionsResource;
import it.pagopa.selfcare.pagopa.backoffice.model.stations.*;
import it.pagopa.selfcare.pagopa.backoffice.service.StationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

@RestController
@RequestMapping(value = "/stations")
@Tag(name = "Stations")
public class StationController {

    @Autowired
    private StationService stationService;

    @PostMapping(value = "", produces = {MediaType.APPLICATION_JSON_VALUE})
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create new station", security = {@SecurityRequirement(name = "JWT")})
    public WrapperEntityOperations<StationDetails> createStation(@RequestBody @NotNull StationDetailsDto stationDetailsDto) {
        return stationService.createStation(stationDetailsDto);
    }


    @GetMapping("")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Get paginated list of stations", security = {@SecurityRequirement(name = "JWT")})
    public StationsResource getStations(@Parameter(description = "") @RequestParam(required = false, defaultValue = "50") Integer limit,
                                        @Parameter(description = "Page number. Page value starts from 0") @RequestParam(required = true) Integer page,
                                        @Parameter(description = "Station's unique identifier") @RequestParam(required = false) String stationCode,
                                        @Parameter(description = "Creditor institution associated to given station") @RequestParam(required = false) String creditorInstitutionCode,
                                        @Parameter(description = "Sort Direction ordering") @RequestParam(required = false, name = "ordering", defaultValue = "DESC") String sort) {
        return stationService.getStations(limit, page, stationCode, creditorInstitutionCode, sort);
    }

    @GetMapping(value = "/{station-code}", produces = {MediaType.APPLICATION_JSON_VALUE})
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Get station's details", security = {@SecurityRequirement(name = "JWT")})
    public StationDetailResource getStation(@Parameter(description = "Station's unique identifier")
                                            @PathVariable("station-code") String stationCode) {
        return stationService.getStation(stationCode);

    }


    @GetMapping(value = "/{station-code}/creditor-institutions")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Get Creditor Institutions By Station Code", security = {@SecurityRequirement(name = "JWT")})
    public CreditorInstitutionsResource getCreditorInstitutionsByStationCode(@Parameter(description = "Station Code") @PathVariable("station-code") String stationCode,
                                                                             @RequestParam(required = false, defaultValue = "50") Integer limit,
                                                                             @RequestParam Integer page,
                                                                             @Parameter(description = "Filter by creditor institution name") @RequestParam(required = false, name = "ci-name") String ciName) {
        return stationService.getCreditorInstitutionsByStationCode(stationCode, limit, page, ciName);
    }

    @PutMapping(value = "/{station-code}", produces = {MediaType.APPLICATION_JSON_VALUE})
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Update a station", security = {@SecurityRequirement(name = "JWT")})
    public StationDetailResource updateStation(@RequestBody @NotNull StationDetailsDto stationDetailsDto,
                                               @Parameter(description = "Station's unique identifier")
                                               @PathVariable("station-code") String stationCode) {
        return stationService.updateStation(stationDetailsDto, stationCode);
    }

    @GetMapping(value = "/wrapper/{station-code}")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Get wrapper station from mongo DB", security = {@SecurityRequirement(name = "JWT")})
    public WrapperEntities getWrapperEntitiesStation(@Parameter(description = "Channlecode or StationCode") @PathVariable("station-code") String code) {
        return stationService.getWrapperEntitiesStation(code);
    }


    @GetMapping(value = "/merged", produces = {MediaType.APPLICATION_JSON_VALUE})
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Get All Stations from cosmos db merged whit apiConfig", security = {@SecurityRequirement(name = "JWT")})
    public WrapperStationsResource getAllStationsMerged(@Parameter(description = "")
                                                        @RequestParam(required = false, defaultValue = "50") Integer limit,
                                                        @Parameter(description = "Station's unique identifier")
                                                        @RequestParam(required = false, value = "stationcodefilter") String stationCode,
                                                        @Parameter(description = "Broker code filter for search")
                                                        @RequestParam("brokerCode") String brokerCode,
                                                        @Parameter(description = "Page number. Page value starts from 0")
                                                        @RequestParam Integer page,
                                                        @Parameter(description = "Method of sorting")
                                                        @RequestParam(required = false, value = "sorting") String sorting) {
        return stationService.getAllStationsMerged(limit, stationCode, brokerCode, page, sorting);
    }


    @PostMapping(value = "/wrapper", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create a WrapperChannel on Cosmodb", security = {@SecurityRequirement(name = "JWT")})
    public WrapperEntities<StationDetails> createWrapperStationDetails(@RequestBody
                                                                       @Valid
                                                                       WrapperStationDetailsDto wrapperStationDetailsDto) {
        return stationService.createWrapperStationDetails(wrapperStationDetailsDto);
    }

    @GetMapping(value = "/merged/{station-code}", produces = {MediaType.APPLICATION_JSON_VALUE})
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Get station's details", security = {@SecurityRequirement(name = "JWT")})
    public StationDetailResource getStationDetail(@Parameter(description = "Station's unique identifier")
                                                  @PathVariable("station-code") String stationCode) {
        return stationService.getStationDetail(stationCode);
    }

    @GetMapping(value = "/station-code", produces = {MediaType.APPLICATION_JSON_VALUE})
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Generate a station code given the creditor institution's code", security = {@SecurityRequirement(name = "JWT")})
    public StationCodeResource getStationCode(@Parameter(description = "Creditor institution code")
                                              @RequestParam(value = "ec-code") String ecCode) {
        return stationService.getStationCode(ecCode, false);
    }

    @GetMapping(value = "/station-code/v2", produces = {MediaType.APPLICATION_JSON_VALUE})
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Generate a station code given the creditor institution's code", security = {@SecurityRequirement(name = "JWT")})
    public StationCodeResource getStationCodeV2(@Parameter(description = "Creditor institution code") @RequestParam(value = "ec-code") String ecCode) {
        return stationService.getStationCode(ecCode, true);
    }

    @PutMapping(value = "/wrapper/{station-code}", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Update WrapperStationDetails", security = {@SecurityRequirement(name = "JWT")})
    public WrapperEntities updateWrapperStationDetails(@Parameter(description = "Station code") @PathVariable(value = "station-code") String stationCode,
                                                       @RequestBody @Valid StationDetailsDto stationDetailsDto) {

        // TODO use station code
        return stationService.updateWrapperStationDetails(stationDetailsDto);

    }


    @PutMapping(value = "/wrapper/operator", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Update a WrapperStation on Cosmodb", security = {@SecurityRequirement(name = "JWT")})
    public WrapperEntities updateWrapperStationDetailsByOpt(@RequestBody @Valid StationDetailsDto stationDetailsDto) {
        return stationService.updateWrapperStationDetailsByOpt(stationDetailsDto);

    }


}
