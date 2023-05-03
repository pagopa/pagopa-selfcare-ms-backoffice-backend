package it.pagopa.selfcare.pagopa.backoffice.web.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import it.pagopa.selfcare.pagopa.backoffice.connector.logging.LogUtils;
import it.pagopa.selfcare.pagopa.backoffice.connector.model.channel.ChannelDetails;
import it.pagopa.selfcare.pagopa.backoffice.connector.model.station.CreditorInstitutionStationEdit;
import it.pagopa.selfcare.pagopa.backoffice.connector.model.station.StationDetails;
import it.pagopa.selfcare.pagopa.backoffice.connector.model.station.Stations;
import it.pagopa.selfcare.pagopa.backoffice.connector.model.wrapper.WrapperEntitiesOperations;
import it.pagopa.selfcare.pagopa.backoffice.connector.model.wrapper.WrapperEntityOperations;
import it.pagopa.selfcare.pagopa.backoffice.core.ApiConfigService;
import it.pagopa.selfcare.pagopa.backoffice.core.WrapperService;
import it.pagopa.selfcare.pagopa.backoffice.web.model.channels.ChannelDetailsDto;
import it.pagopa.selfcare.pagopa.backoffice.web.model.channels.ChannelDetailsResource;
import it.pagopa.selfcare.pagopa.backoffice.web.model.creditorInstituions.CreditorInstitutionStationDto;
import it.pagopa.selfcare.pagopa.backoffice.web.model.creditorInstituions.CreditorInstitutionStationEditResource;
import it.pagopa.selfcare.pagopa.backoffice.web.model.mapper.ChannelMapper;
import it.pagopa.selfcare.pagopa.backoffice.web.model.mapper.CreditorInstitutionMapper;
import it.pagopa.selfcare.pagopa.backoffice.web.model.mapper.StationMapper;
import it.pagopa.selfcare.pagopa.backoffice.web.model.stations.*;
import lombok.extern.slf4j.Slf4j;
import org.mapstruct.factory.Mappers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.io.BufferedInputStream;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping(value = "/stations", produces = MediaType.APPLICATION_JSON_VALUE)
@Api(tags = "stations")
public class StationController {

    private CreditorInstitutionMapper creditorInstitutionMapper = Mappers.getMapper(CreditorInstitutionMapper.class);

    private StationMapper stationMapper = Mappers.getMapper(StationMapper.class);
    private final ApiConfigService apiConfigService;

    private final WrapperService wrapperService;

    @Autowired
    public StationController(ApiConfigService apiConfigService, WrapperService wrapperService) {
        this.apiConfigService = apiConfigService;
        this.wrapperService = wrapperService;
    }

    @PostMapping(value = "", produces = {MediaType.APPLICATION_JSON_VALUE})
    @ResponseStatus(HttpStatus.CREATED)
    @ApiOperation(value = "", notes = "${swagger.api.stations.createStation}")
    public WrapperEntityOperations<StationDetails> createStation(@RequestBody @NotNull StationDetailsDto stationDetailsDto) {
        log.trace("createStation start");
        String xRequestId = UUID.randomUUID().toString();
        log.debug(LogUtils.CONFIDENTIAL_MARKER, "createStation dto = {}, xRequestId = {}", stationDetailsDto, xRequestId);
        StationDetails stationDetails = stationMapper.fromDto(stationDetailsDto);
        apiConfigService.createStation(stationDetails, xRequestId);
        log.trace("created station in apiConfig");
        WrapperEntitiesOperations<StationDetails> response = wrapperService.updateWrapperStationDetailsByOpt(stationDetails, stationDetailsDto.getNote(), stationDetailsDto.getStatus().name());
        WrapperEntityOperations<StationDetails> result = response.getWrapperEntityOperationsSortedList().get(0);
        log.debug("createStation result = {}", result);
        log.trace("createStation end");
        return result;
    }

    @PostMapping(value = "/create-wrapperStation", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    @ApiOperation(value = "", notes = "${swagger.api.channels.createWrapperStationDetails}")
    public WrapperEntitiesOperations<StationDetails> createWrapperStationDetails(@RequestBody
                                                                 @Valid
                                                                 WrapperStationDetailsDto wrapperStationDetailsDto) {
        log.trace("createWrapperStationDetails start");
        log.debug("createWrapperStationDetails channelDetailsDto = {}", wrapperStationDetailsDto);
        WrapperEntitiesOperations<StationDetails> createdWrapperEntities = wrapperService.
                createWrapperStationDetails(stationMapper.
                        fromWrapperStationDetailsDto(wrapperStationDetailsDto), wrapperStationDetailsDto.getNote(), wrapperStationDetailsDto.getStatus().name());
        log.debug("createWrapperStationDetails result = {}", createdWrapperEntities);
        log.trace("createWrapperStationDetails end");
        return createdWrapperEntities;
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
        StationsResource resource = stationMapper.toResource(stations);
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
        StationDetailResource resource = stationMapper.toResource(stationDetails);
        log.debug(LogUtils.CONFIDENTIAL_MARKER, "getStation result = {}", resource);
        log.trace("getStation end");
        return resource;
    }

    @GetMapping(value = "/{ecCode}/generate", produces = {MediaType.APPLICATION_JSON_VALUE})
    @ResponseStatus(HttpStatus.OK)
    @ApiOperation(value = "", notes = "${swagger.api.stations.getStationCode}")
    public StationCodeResource getStationCode(@ApiParam("${swagger.request.ecCode}")
                                              @PathVariable("ecCode") String ecCode) {
        log.trace("getStationCode start");
        String xRequestId = UUID.randomUUID().toString();
        log.debug("getStationCode ecCode = {}, xRequestId = {}", ecCode, xRequestId);
        String result = apiConfigService.generateStationCode(ecCode, xRequestId);
        StationCodeResource stationCode = new StationCodeResource(result);
        log.debug(LogUtils.CONFIDENTIAL_MARKER, "getStationCode result = {}", stationCode);
        log.trace("getStationCode end");
        return stationCode;
    }

    @PutMapping(value = "/update-wrapperStation", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    @ApiOperation(value = "", notes = "${swagger.api.stations.updateWrapperStationDetails}")
    public WrapperEntitiesOperations updateWrapperStationDetails(@RequestBody
                                                                 @Valid
                                                                     StationDetailsDto stationDetailsDto) {
        log.trace("updateWrapperStationDetails start");
        log.debug("updateWrapperStationDetails stationDetailsDto = {}", stationDetailsDto);
        WrapperEntitiesOperations createdWrapperEntities = wrapperService.
                updateWrapperStationDetails(stationMapper.fromDto
                        (stationDetailsDto), stationDetailsDto.getNote(), stationDetailsDto.getStatus().name());
        log.debug("updateWrapperStationDetails result = {}", createdWrapperEntities);
        log.trace("updateWrapperStationDetails end");
        return createdWrapperEntities;
    }

    @PutMapping(value = "/update-wrapperStationByOpt", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    @ApiOperation(value = "", notes = "${swagger.api.stations.updateWrapperStationDetailsByOpt}")
    public WrapperEntitiesOperations updateWrapperStationDetailsByOpt(@RequestBody
                                                                      @Valid
                                                                      StationDetailsDto stationDetailsDto) {
        log.trace("updateWrapperStationDetailsByOpt start");
        log.debug("updateWrapperStationDetailsByOpt stationDetailsDto = {}", stationDetailsDto);
        WrapperEntitiesOperations createdWrapperEntities = wrapperService.
                updateWrapperStationDetailsByOpt(stationMapper.
                        fromDto(stationDetailsDto), stationDetailsDto.getNote(), stationDetailsDto.getStatus().name());
        log.debug("updateWrapperStationDetailsByOpt result = {}", createdWrapperEntities);
        log.trace("updateWrapperStationDetailsByOpt end");
        return createdWrapperEntities;
    }
    @PostMapping(value = "/{ecCode}/station", produces = {MediaType.APPLICATION_JSON_VALUE})
    @ResponseStatus(HttpStatus.CREATED)
    @ApiOperation(value = "", notes = "${swagger.api.stations.associateStationToCreditorInstitution}")
    public CreditorInstitutionStationEditResource associateStationToCreditorInstitution(@ApiParam("${swagger.request.ecCode}")
                                                                                    @PathVariable("ecCode") String ecCode,
                                                                                    @RequestBody @NotNull CreditorInstitutionStationDto dto) {
        log.trace("associateStationToCreditorInstitution start");
        String xRequestId = UUID.randomUUID().toString();
        log.debug("associateStationToCreditorInstitution ecCode ={}, dto = {}, xRequestId = {}", ecCode, dto, xRequestId);
        CreditorInstitutionStationEdit station = creditorInstitutionMapper.fromDto(dto);
        CreditorInstitutionStationEdit ecStation = apiConfigService.createCreditorInstitutionStationRelation(ecCode, station, xRequestId);
        CreditorInstitutionStationEditResource resource = creditorInstitutionMapper.toResource(ecStation);
        log.debug(LogUtils.CONFIDENTIAL_MARKER, "associateStationToCreditorInstitution result = {}", resource);
        log.trace("associateStationToCreditorInstitution end");
        return resource;
    }

    @PutMapping(value = "/{stationcode}", produces = {MediaType.APPLICATION_JSON_VALUE})
    @ResponseStatus(HttpStatus.OK)
    @ApiOperation(value = "", notes = "${swagger.api.stations.updateStation}")
    public StationDetailResource updateStation(@RequestBody @NotNull StationDetailsDto stationDetailsDto,
                                                @ApiParam("${swagger.model.station.code}")
                                                @PathVariable("stationcode") String stationCode) {

        log.trace("updateStation start");
        String uuid = UUID.randomUUID().toString();
        log.debug("updateStation code stationDetailsDto = {} , uuid {}", stationDetailsDto, uuid);
        StationDetails stationDetails = stationMapper.fromDto(stationDetailsDto);
        StationDetails response = apiConfigService.updateStation(stationCode, stationDetails, uuid);
        wrapperService.updateWrapperStationDetails(stationDetails, stationDetailsDto.getNote(), stationDetailsDto.getStatus().name());
        StationDetailResource resource = stationMapper.toResource(response);

        log.debug(LogUtils.CONFIDENTIAL_MARKER, "updateStation result = {}", resource);
        log.trace("updateStation end");
        return resource;
    }

    @GetMapping(value = "/get-wrapperEntities/{code}")
    @ResponseStatus(HttpStatus.OK)
    @ApiOperation(value = "", notes = "${swagger.api.stations.getWrapperEntities}")
    public WrapperEntitiesOperations getWrapperEntities(@ApiParam("${swagger.request.code}") @PathVariable("code") String code) {
        log.trace("getWrapperEntities start");
        log.debug("getWrapperEntities cCode = {}", code);
        WrapperEntitiesOperations result = wrapperService.findById(code);
        log.debug("getWrapperEntities result = {}", result);
        log.trace("getWrapperEntities end");
        return result;
    }
}
