package it.pagopa.selfcare.pagopa.backoffice.web.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import it.pagopa.selfcare.pagopa.backoffice.connector.exception.PermissionDeniedException;
import it.pagopa.selfcare.pagopa.backoffice.connector.exception.ResourceNotFoundException;
import it.pagopa.selfcare.pagopa.backoffice.connector.model.broker.BrokerDetails;
import it.pagopa.selfcare.pagopa.backoffice.connector.model.broker.Brokers;
import it.pagopa.selfcare.pagopa.backoffice.connector.model.channel.WrapperEntitiesList;
import it.pagopa.selfcare.pagopa.backoffice.connector.model.creditorInstitution.CreditorInstitutions;
import it.pagopa.selfcare.pagopa.backoffice.connector.model.station.CreditorInstitutionStationEdit;
import it.pagopa.selfcare.pagopa.backoffice.connector.model.station.StationDetails;
import it.pagopa.selfcare.pagopa.backoffice.connector.model.station.StationDetailsList;
import it.pagopa.selfcare.pagopa.backoffice.connector.model.station.Stations;
import it.pagopa.selfcare.pagopa.backoffice.connector.model.wrapper.*;
import it.pagopa.selfcare.pagopa.backoffice.core.*;
import it.pagopa.selfcare.pagopa.backoffice.web.model.creditorInstituions.CreditorInstitutionStationDto;
import it.pagopa.selfcare.pagopa.backoffice.web.model.creditorInstituions.CreditorInstitutionStationEditResource;
import it.pagopa.selfcare.pagopa.backoffice.web.model.creditorInstituions.CreditorInstitutionsResource;
import it.pagopa.selfcare.pagopa.backoffice.web.model.mapper.BrokerMapper;
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

    private final AwsSesService awsSesService;
    private final ApiConfigSelfcareIntegrationService apiConfigSelfcareIntegrationService;

    private final JiraServiceManagerService jiraServiceManagerService;

    @Autowired
    public StationController(ApiConfigSelfcareIntegrationService apiConfigSelfcareIntegrationService, ApiConfigService apiConfigService, WrapperService wrapperService, AwsSesService awsSesService, JiraServiceManagerService jiraServiceManagerService) {
        this.apiConfigService = apiConfigService;
        this.wrapperService = wrapperService;
        this.apiConfigSelfcareIntegrationService = apiConfigSelfcareIntegrationService;
        this.awsSesService = awsSesService;
        this.jiraServiceManagerService = jiraServiceManagerService;
    }

    @PostMapping(value = "", produces = {MediaType.APPLICATION_JSON_VALUE})
    @ResponseStatus(HttpStatus.CREATED)
    @ApiOperation(value = "", notes = "${swagger.api.stations.createStation}")
    public WrapperEntityOperations<StationDetails> createStation(@RequestBody @NotNull StationDetailsDto stationDetailsDto) {
        
        

        final String CREATE_STATION_SUBJECT = "Creazione Stazione";
        final String CREATE_STATION_EMAIL_BODY = String.format("Buongiorno %n%n la stazione %s è stata validata da un operatore e risulta essere attiva%n%nSaluti", stationDetailsDto.getStationCode());

        StationDetails stationDetails = stationMapper.fromDto(stationDetailsDto);
        apiConfigService.createStation(stationDetails);
        
        WrapperEntitiesOperations<StationDetails> response = wrapperService.updateWrapperStationDetailsByOpt(stationDetails, stationDetailsDto.getNote(), WrapperStatus.APPROVED.name());
        WrapperEntityOperations<StationDetails> result = response.getWrapperEntityOperationsSortedList().get(0);
        awsSesService.sendEmail(CREATE_STATION_SUBJECT, CREATE_STATION_EMAIL_BODY,stationDetailsDto.getEmail());
        
        
        return result;
    }

    @PostMapping(value = "/create-wrapperStation", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    @ApiOperation(value = "", notes = "${swagger.api.channels.createWrapperStationDetails}")
    public WrapperEntitiesOperations<StationDetails> createWrapperStationDetails(@RequestBody
                                                                                 @Valid
                                                                                 WrapperStationDetailsDto wrapperStationDetailsDto) {
        
        

        final String CREATE_STATION_SUMMARY = " Validazione stazione - creazione: %s";
        final String CREATE_STATION_DESCRIPTION = "La stazione %s deve essere validata: %s";

        WrapperEntitiesOperations<StationDetails> createdWrapperEntities = wrapperService.
                createWrapperStationDetails(stationMapper.
                        fromWrapperStationDetailsDto(wrapperStationDetailsDto), wrapperStationDetailsDto.getNote(), wrapperStationDetailsDto.getStatus().name());
        
        

        jiraServiceManagerService.createTicket(String.format(CREATE_STATION_SUMMARY, wrapperStationDetailsDto.getStationCode()),
                String.format(CREATE_STATION_DESCRIPTION, wrapperStationDetailsDto.getStationCode(),wrapperStationDetailsDto.getValidationUrl()));
        
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
        
        
        Stations stations = apiConfigService.getStations(limit, page, sort, null, creditorInstitutionCode, stationCode);
        StationsResource resource = stationMapper.toResource(stations);
        
        
        return resource;
    }

    @GetMapping(value = "/details/{stationId}", produces = {MediaType.APPLICATION_JSON_VALUE})
    @ResponseStatus(HttpStatus.OK)
    @ApiOperation(value = "", notes = "${swagger.api.stations.getStation}")
    public StationDetailResource getStation(@ApiParam("${swagger.model.station.code}")
                                            @PathVariable("stationId") String stationCode) {
        
        
        StationDetails stationDetails = apiConfigService.getStation(stationCode);
        StationDetailResource resource = stationMapper.toResource(stationDetails);
        
        
        return resource;
    }

    @GetMapping(value = "/get-details/{stationId}", produces = {MediaType.APPLICATION_JSON_VALUE})
    @ResponseStatus(HttpStatus.OK)
    @ApiOperation(value = "", notes = "${swagger.api.stations.getStation}")
    public StationDetailResource getStationDetail(@ApiParam("${swagger.model.station.code}")
                                                  @PathVariable("stationId") String stationCode) {
        
        StationDetails stationDetails;
        WrapperStatus status;
        String createdBy = "";
        String modifiedBy = "";
        try {
            WrapperEntitiesOperations<StationDetails> result = wrapperService.findById(stationCode);
            createdBy = result.getCreatedBy();
            modifiedBy = result.getModifiedBy();
            stationDetails = result.getWrapperEntityOperationsSortedList().get(0).getEntity();
            status = result.getStatus();
        } catch (ResourceNotFoundException e) {
            
            stationDetails = apiConfigService.getStation(stationCode);
            status = WrapperStatus.APPROVED;
        }
        StationDetailResource resource = stationMapper.toResource(stationDetails, status, createdBy, modifiedBy);
        
        
        return resource;
    }

    @GetMapping(value = "/{ecCode}/generate", produces = {MediaType.APPLICATION_JSON_VALUE})
    @ResponseStatus(HttpStatus.OK)
    @ApiOperation(value = "", notes = "${swagger.api.stations.getStationCode}")
    public StationCodeResource getStationCode(@ApiParam("${swagger.request.ecCode}")
                                              @PathVariable("ecCode") String ecCode) {
        
        
        WrapperEntitiesList entitiesList = wrapperService.findByStatusAndTypeAndBrokerCodeAndIdLike(WrapperStatus.TO_CHECK, WrapperType.STATION, null, ecCode,  0, 1, "ASC");
        WrapperEntitiesList entitiesList2 = wrapperService.findByStatusAndTypeAndBrokerCodeAndIdLike(WrapperStatus.TO_FIX, WrapperType.STATION, null, ecCode,  0, 1, "ASC");
        if (!entitiesList.getWrapperEntities().isEmpty() || !entitiesList2.getWrapperEntities().isEmpty())
            throw new PermissionDeniedException("ERROR There is a Station not completed!");
        String result = apiConfigService.generateStationCode(ecCode);
        StationCodeResource stationCode = new StationCodeResource(result);
        
        
        return stationCode;
    }

    @GetMapping(value = "/{ecCode}/generateV2", produces = {MediaType.APPLICATION_JSON_VALUE})
    @ResponseStatus(HttpStatus.OK)
    @ApiOperation(value = "", notes = "${swagger.api.stations.getStationCode}")
    public StationCodeResource getStationCodeV2(@ApiParam("${swagger.request.ecCode}")
                                              @PathVariable("ecCode") String ecCode) {
        
        

        Stations stations = apiConfigService.getStations(100, 0, "ASC", null, null, ecCode);
        WrapperStations responseApiConfig = stationMapper.toWrapperStations(stations);
        WrapperEntitiesList mongoList = wrapperService.findByIdLikeOrTypeOrBrokerCode(ecCode, WrapperType.STATION, null, 0, 100);
        WrapperStations responseMongo = stationMapper.toWrapperStations(mongoList);
        WrapperStations stationsMergedAndSorted = apiConfigService.mergeAndSortWrapperStations(responseApiConfig, responseMongo, "ASC");
        String result = apiConfigService.generateStationCodeV2(stationsMergedAndSorted.getStationsList(), ecCode);
        StationCodeResource stationCode = new StationCodeResource(result);
        
        
        return stationCode;
    }

    @PutMapping(value = "/update-wrapperStation", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    @ApiOperation(value = "", notes = "${swagger.api.stations.updateWrapperStationDetails}")
    public WrapperEntitiesOperations updateWrapperStationDetails(@RequestBody
                                                                 @Valid
                                                                 StationDetailsDto stationDetailsDto) {
        
        final String UPDATE_STATION_SUMMARY = "Station creation validation: %s";
        final String UPDATE_STATION_DESCRIPTION = "The station %s created by broker %s needs to be validated: %s";
        
        WrapperEntitiesOperations createdWrapperEntities = wrapperService.
                updateWrapperStationDetails(stationMapper.fromDto
                        (stationDetailsDto), stationDetailsDto.getNote(), stationDetailsDto.getStatus().name(), null);
        
        jiraServiceManagerService.createTicket(String.format(UPDATE_STATION_SUMMARY, stationDetailsDto.getStationCode()),
                String.format(UPDATE_STATION_DESCRIPTION, stationDetailsDto.getStationCode(), stationDetailsDto.getBrokerCode(),stationDetailsDto.getValidationUrl()));
        
        return createdWrapperEntities;
    }

    @PutMapping(value = "/update-wrapperStationByOpt", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    @ApiOperation(value = "", notes = "${swagger.api.stations.updateWrapperStationDetailsByOpt}")
    public WrapperEntitiesOperations updateWrapperStationDetailsByOpt(@RequestBody
                                                                      @Valid
                                                                      StationDetailsDto stationDetailsDto) {
        
        
        WrapperEntitiesOperations createdWrapperEntities = wrapperService.
                updateWrapperStationDetailsByOpt(stationMapper.
                        fromDto(stationDetailsDto), stationDetailsDto.getNote(), stationDetailsDto.getStatus().name());
        
        
        return createdWrapperEntities;
    }

    @PostMapping(value = "/{ecCode}/station", produces = {MediaType.APPLICATION_JSON_VALUE})
    @ResponseStatus(HttpStatus.CREATED)
    @ApiOperation(value = "", notes = "${swagger.api.stations.associateStationToCreditorInstitution}")
    public CreditorInstitutionStationEditResource associateStationToCreditorInstitution(@ApiParam("${swagger.request.ecCode}")
                                                                                        @PathVariable("ecCode") String ecCode,
                                                                                        @RequestBody @NotNull CreditorInstitutionStationDto dto) {
        
        
        CreditorInstitutionStationEdit station = creditorInstitutionMapper.fromDto(dto);
        CreditorInstitutionStationEdit ecStation = apiConfigService.createCreditorInstitutionStationRelation(ecCode, station);
        CreditorInstitutionStationEditResource resource = creditorInstitutionMapper.toResource(ecStation);
        
        
        return resource;
    }

    @DeleteMapping(value = "/{ecCode}/station/{stationcode}", produces = {MediaType.APPLICATION_JSON_VALUE})
    @ResponseStatus(HttpStatus.OK)
    @ApiOperation(value = "", notes = "${swagger.api.stations.deleteCreditorInstitutionStationRelationship}")
    public void deleteCreditorInstitutionStationRelationship(@ApiParam("${swagger.request.ecCode}")
                                                             @PathVariable("ecCode") String ecCode,
                                                             @ApiParam("${swagger.request.code}")
                                                             @PathVariable("stationcode") String stationcode) {
        
        
        apiConfigService.deleteCreditorInstitutionStationRelationship(ecCode, stationcode);
        
    }

    @GetMapping(value = "/getCreditorInstitutions/{stationcode}")
    @ResponseStatus(HttpStatus.OK)
    @ApiOperation(value = "", notes = "${swagger.api.stations.getCreditorInstitutionsByStationCode}")
    public CreditorInstitutionsResource getCreditorInstitutionsByStationCode(@ApiParam("${swagger.request.code}") @PathVariable("stationcode") String stationcode,
                                                                             @RequestParam(required = false, defaultValue = "50") Integer limit,
                                                                             @RequestParam Integer page) {
        
        
        
        CreditorInstitutions creditorInstitutions = apiConfigService.getCreditorInstitutionsByStation(stationcode, limit, page);
        CreditorInstitutionsResource resource = creditorInstitutionMapper.toResource(creditorInstitutions);
        
        
        return resource;
    }

    @PutMapping(value = "/{stationcode}", produces = {MediaType.APPLICATION_JSON_VALUE})
    @ResponseStatus(HttpStatus.OK)
    @ApiOperation(value = "", notes = "${swagger.api.stations.updateStation}")
    public StationDetailResource updateStation(@RequestBody @NotNull StationDetailsDto stationDetailsDto,
                                               @ApiParam("${swagger.model.station.code}")
                                               @PathVariable("stationcode") String stationCode) {

        
        
        

        final String UPDATE_STATION_SUBJECT = "Update Stazione";
        final String UPDATE_STATION_EMAIL_BODY = String.format("Buongiorno%n%n la modifica per la stazione %s è stata validata da un operatore e risulta essere attiva%n%nSaluti", stationDetailsDto.getStationCode());

        StationDetails stationDetails = stationMapper.fromDto(stationDetailsDto);
        StationDetails response = apiConfigService.updateStation(stationCode, stationDetails);
        wrapperService.updateWrapperStationDetails(stationDetails, stationDetailsDto.getNote(), stationDetailsDto.getStatus().name(), null);
        StationDetailResource resource = stationMapper.toResource(response);
        awsSesService.sendEmail(UPDATE_STATION_SUBJECT, UPDATE_STATION_EMAIL_BODY,stationDetailsDto.getEmail());
        
        
        return resource;
    }

    @GetMapping(value = "/get-wrapperEntities/{code}")
    @ResponseStatus(HttpStatus.OK)
    @ApiOperation(value = "", notes = "${swagger.api.stations.getWrapperEntities}")
    public WrapperEntitiesOperations getWrapperEntitiesStation(@ApiParam("${swagger.request.code}") @PathVariable("code") String code) {
        
        
        WrapperEntitiesOperations result = wrapperService.findById(code);
        
        
        return result;
    }

    @PostMapping(value = "/create-broker")
    @ResponseStatus(HttpStatus.CREATED)
    @ApiOperation(value = "", notes = "${swagger.api.stations.createBroker}")
    public BrokerResource createBroker(@RequestBody BrokerDto brokerDto){
        
        
        BrokerDetails broker = apiConfigService.createBroker(BrokerMapper.fromDto(brokerDto));
        return BrokerMapper.toResource(broker);
    }

    @GetMapping(value = "/getAllStations", produces = {MediaType.APPLICATION_JSON_VALUE})
    @ResponseStatus(HttpStatus.OK)
    @ApiOperation(value = "", notes = "${swagger.api.stations.getAllStationsMerged}")
    public WrapperStationsResource getAllStationsMerged(@ApiParam("${swagger.request.limit}")
                                                     @RequestParam(required = false, defaultValue = "50") Integer limit,
                                                     @ApiParam("${swagger.model.station.code}")
                                                     @RequestParam(required = false, value = "stationcodefilter") String stationCode,
                                                     @ApiParam("${swagger.request.brokerCode}")
                                                     @RequestParam("brokerCode") String brokerCode,
                                                     @ApiParam("${swagger.request.page}")
                                                     @RequestParam Integer page,
                                                     @ApiParam("${swagger.request.sorting}")
                                                     @RequestParam(required = false, value = "sorting") String sorting) {
        
        
        

        Stations stations = apiConfigService.getStations(limit, page, sorting, brokerCode, null, stationCode);
        WrapperStations responseApiConfig = stationMapper.toWrapperStations(stations);
        WrapperEntitiesList mongoList = wrapperService.findByIdLikeOrTypeOrBrokerCode(stationCode, WrapperType.STATION, brokerCode, page, limit);
        WrapperStations responseMongo = stationMapper.toWrapperStations(mongoList);
        WrapperStations stationsMergedAndSorted = apiConfigService.mergeAndSortWrapperStations(responseApiConfig, responseMongo, sorting);
        WrapperStationsResource response = stationMapper.toWrapperStationsResource(stationsMergedAndSorted);
        
        

        return response;
    }

    @GetMapping(value = "{brokerId}", produces = {MediaType.APPLICATION_JSON_VALUE})
    @ResponseStatus(HttpStatus.OK)
    @ApiOperation(value = "", notes = "${swagger.api.stations.getStationsDetailsListByBroker}")
    public StationDetailsResourceList getStationsDetailsListByBroker(@PathVariable("brokerId") String brokerId,
                                                                     @RequestParam(required = false) String stationId,
                                                                     @RequestParam(required = false, defaultValue = "10") Integer limit,
                                                                     @RequestParam(required = false, defaultValue = "0") Integer page) {
        
        
        
        StationDetailsList response = apiConfigSelfcareIntegrationService.getStationsDetailsListByBroker(brokerId, stationId, limit, page);
        StationDetailsResourceList resource = stationMapper.fromStationDetailsList(response);

        
        
        return resource;
    }

    @GetMapping(value = "/brokers-EC", produces = {MediaType.APPLICATION_JSON_VALUE})
    @ResponseStatus(HttpStatus.OK)
    @ApiOperation(value = "", notes = "${swagger.api.stations.getStationBroker}")
    public BrokersResource getBrokersEC(@ApiParam("${swagger.request.limit}")
                               @RequestParam(required = false, defaultValue = "50") Integer limit,
                               @ApiParam("${swagger.request.page}")
                               @RequestParam Integer page,
                               @RequestParam(required = false) String code,
                               @RequestParam(required = false) String name,
                               @ApiParam(value = "order by name or code, default = CODE", allowableValues = "CODE,NAME")
                               @RequestParam(required = false, defaultValue = "CODE") String orderby,
                               @ApiParam(allowableValues = "ASC,DESC")
                               @RequestParam(required = false, defaultValue = "DESC") String ordering){

        
        
        
        Brokers response = apiConfigService.getBrokersEC(limit, page, code, name, orderby, ordering);
        BrokersResource resource = BrokerMapper.toResource(response);
        
        
        return resource;
    }

}
