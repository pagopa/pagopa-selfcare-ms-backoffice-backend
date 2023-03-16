package it.pagopa.selfcare.pagopa.backoffice.web.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import it.pagopa.selfcare.pagopa.backoffice.connector.logging.LogUtils;
import it.pagopa.selfcare.pagopa.backoffice.connector.model.station.StationDetails;
import it.pagopa.selfcare.pagopa.backoffice.connector.model.station.Stations;
import it.pagopa.selfcare.pagopa.backoffice.core.ApiConfigService;
import it.pagopa.selfcare.pagopa.backoffice.web.model.mapper.StationMapper;
import it.pagopa.selfcare.pagopa.backoffice.web.model.stations.StationDetailResource;
import it.pagopa.selfcare.pagopa.backoffice.web.model.stations.StationDetailsDto;
import it.pagopa.selfcare.pagopa.backoffice.web.model.stations.StationsResource;
import lombok.extern.slf4j.Slf4j;
import org.mapstruct.factory.Mappers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotNull;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping(value = "/stations", produces = MediaType.APPLICATION_JSON_VALUE)
@Api(tags = "stations")
public class StationController {

    private StationMapper mapper = Mappers.getMapper(StationMapper.class);
    private final ApiConfigService apiConfigService;

    @Autowired
    public StationController(ApiConfigService apiConfigService) {
        this.apiConfigService = apiConfigService;
    }

    @PostMapping(value = "", produces = {MediaType.APPLICATION_JSON_VALUE})
    @ResponseStatus(HttpStatus.CREATED)
    @ApiOperation(value = "", notes = "${swagger.api.stations.createStation}")
    public StationDetailResource createStation(@RequestBody @NotNull StationDetailsDto stationDetailsDto) {
        log.trace("createStation start");
        String xRequestId = UUID.randomUUID().toString();
        log.debug(LogUtils.CONFIDENTIAL_MARKER, "createStation dto = {}, xRequestId = {}", stationDetailsDto, xRequestId);
        StationDetails stationDetails = mapper.fromDto(stationDetailsDto);
        StationDetails response = apiConfigService.createStation(stationDetails, xRequestId);
        StationDetailResource resource = mapper.toResource(response);
        log.debug("createStation result = {}", resource);
        log.trace("createStation end");
        return resource;
    }

    @GetMapping("")
    @ResponseStatus(HttpStatus.OK)
    @ApiOperation(value = "", notes = "${swagger.api.stations.getStations}")
    public StationsResource getStations(@ApiParam("${swagger.pageable.number}")
                                        @RequestParam(required = false, defaultValue = "50") Integer limit,
                                        @ApiParam("${swagger.pageable.start}")
                                        @RequestParam(required = true) Integer page,
                                        @ApiParam("${swagger.model.station.code}")
                                        @RequestParam(required = false) String stationCode,
                                        @ApiParam("${swagger.model.stations.ecCode}")
                                        @RequestParam(required = false) String creditorInstitutionCode,
                                        @ApiParam("${swagger.model.sort.order}")
                                        @RequestParam(required = false, name = "ordering", defaultValue = "DESC") String sort) {
        log.trace("getStations start");
        String uuid = UUID.randomUUID().toString();
        log.debug("getStations ecCode = {}, stationCode = {}, X-Request-Id = {}", creditorInstitutionCode, stationCode, uuid);
        Stations stations = apiConfigService.getStations(limit, page, sort, creditorInstitutionCode, stationCode, uuid);
        StationsResource resource = mapper.toResource(stations);
        log.debug(LogUtils.CONFIDENTIAL_MARKER, "StationController result = {}", resource);
        log.trace("getStations end");
        return resource;
    }

    @GetMapping(value = "/details/{stationId}", produces = {MediaType.APPLICATION_JSON_VALUE})
    @ResponseStatus(HttpStatus.OK)
    @ApiOperation(value = "", notes = "${swagger.api.stations.getStation}")
    public StationDetailResource getStation(@ApiParam("${swagger.model.station.code}")
                                            @PathVariable("stationId") String stationCode) {
        log.trace("getStation start");
        String uuid = UUID.randomUUID().toString();
        log.debug("getStation stationCode = {}, X-Request-Id = {}", stationCode, uuid);
        StationDetails stationDetails = apiConfigService.getStation(stationCode, uuid);
        StationDetailResource resource = mapper.toResource(stationDetails);
        log.debug(LogUtils.CONFIDENTIAL_MARKER, "getStation result = {}", resource);
        log.trace("getStation end");
        return resource;
    }


}
