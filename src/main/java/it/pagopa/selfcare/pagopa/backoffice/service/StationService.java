package it.pagopa.selfcare.pagopa.backoffice.service;

import it.pagopa.selfcare.pagopa.backoffice.client.ApiConfigClient;
import it.pagopa.selfcare.pagopa.backoffice.client.AwsSesClient;
import it.pagopa.selfcare.pagopa.backoffice.client.JiraServiceManagerClient;
import it.pagopa.selfcare.pagopa.backoffice.entity.WrapperEntitiesOperations;
import it.pagopa.selfcare.pagopa.backoffice.entity.WrapperEntityOperations;
import it.pagopa.selfcare.pagopa.backoffice.exception.PermissionDeniedException;
import it.pagopa.selfcare.pagopa.backoffice.exception.ResourceNotFoundException;
import it.pagopa.selfcare.pagopa.backoffice.model.connector.wrapper.*;
import it.pagopa.selfcare.pagopa.backoffice.model.connector.PageInfo;
import it.pagopa.selfcare.pagopa.backoffice.model.connector.channel.WrapperEntitiesList;
import it.pagopa.selfcare.pagopa.backoffice.model.connector.creditorInstitution.CreditorInstitutions;
import it.pagopa.selfcare.pagopa.backoffice.model.connector.station.Station;
import it.pagopa.selfcare.pagopa.backoffice.model.connector.station.StationDetails;
import it.pagopa.selfcare.pagopa.backoffice.model.connector.station.Stations;
import it.pagopa.selfcare.pagopa.backoffice.model.creditorinstituions.CreditorInstitutionsResource;
import it.pagopa.selfcare.pagopa.backoffice.mapper.CreditorInstitutionMapper;
import it.pagopa.selfcare.pagopa.backoffice.mapper.StationMapper;
import it.pagopa.selfcare.pagopa.backoffice.model.stations.*;
import org.mapstruct.factory.Mappers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static it.pagopa.selfcare.pagopa.backoffice.util.StringUtils.generator;
import static it.pagopa.selfcare.pagopa.backoffice.util.Constants.REGEX_GENERATE;

@Service
public class StationService {


    private final CreditorInstitutionMapper creditorInstitutionMapper = Mappers.getMapper(CreditorInstitutionMapper.class);

    private final StationMapper stationMapper = Mappers.getMapper(StationMapper.class);

    @Autowired
    private ApiConfigClient apiConfigClient;

    @Autowired
    private WrapperService wrapperService;

    @Autowired
    private AwsSesClient awsSesClient;

    @Autowired
    private JiraServiceManagerClient jiraServiceManagerClient;


    public WrapperEntityOperations<StationDetails> createStation(@NotNull StationDetailsDto stationDetailsDto) {
        final String CREATE_STATION_SUBJECT = "Creazione Stazione";
        final String CREATE_STATION_EMAIL_BODY = String.format("Buongiorno %n%n la stazione %s è stata validata da un operatore e risulta essere attiva%n%nSaluti", stationDetailsDto.getStationCode());

        StationDetails stationDetails = stationMapper.fromDto(stationDetailsDto);
        apiConfigClient.createStation(stationDetails);

        WrapperEntitiesOperations<StationDetails> response = wrapperService.updateByOpt(stationDetails, stationDetailsDto.getNote(), WrapperStatus.APPROVED.name());
        WrapperEntityOperations<StationDetails> result = response.getWrapperEntityOperationsSortedList().get(0);
        awsSesClient.sendEmail(CREATE_STATION_SUBJECT, CREATE_STATION_EMAIL_BODY, stationDetailsDto.getEmail());
        return result;
    }

    public WrapperEntitiesOperations<StationDetails> createWrapperStationDetails(@Valid WrapperStationDetailsDto wrapperStationDetailsDto) {
        final String CREATE_STATION_SUMMARY = " Validazione stazione - creazione: %s";
        final String CREATE_STATION_DESCRIPTION = "La stazione %s deve essere validata: %s";

        WrapperEntitiesOperations<StationDetails> createdWrapperEntities = wrapperService.
                insert(stationMapper.
                        fromWrapperStationDetailsDto(wrapperStationDetailsDto), wrapperStationDetailsDto.getNote(), wrapperStationDetailsDto.getStatus().name());


        jiraServiceManagerClient.createTicket(String.format(CREATE_STATION_SUMMARY, wrapperStationDetailsDto.getStationCode()),
                String.format(CREATE_STATION_DESCRIPTION, wrapperStationDetailsDto.getStationCode(), wrapperStationDetailsDto.getValidationUrl()));

        return createdWrapperEntities;

    }


    public StationsResource getStations(Integer limit,
                                        Integer page,
                                        String stationCode,
                                        String creditorInstitutionCode,
                                        String sort) {


        Stations stations = getStations(limit, page, sort, null, creditorInstitutionCode, stationCode);
        return stationMapper.toResource(stations);
    }

    public StationDetailResource getStation(String stationCode) {

        StationDetails stationDetails = apiConfigClient.getStation(stationCode);

        return stationMapper.toResource(stationDetails);
    }

    public StationDetailResource getStationDetail(String stationCode) {

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

            stationDetails = apiConfigClient.getStation(stationCode);
            status = WrapperStatus.APPROVED;
        }

        return stationMapper.toResource(stationDetails, status, createdBy, modifiedBy);
    }

    public StationCodeResource getStationCode(String ecCode) {
        WrapperEntitiesList entitiesList = wrapperService.findByStatusAndTypeAndBrokerCodeAndIdLike(WrapperStatus.TO_CHECK, WrapperType.STATION, null, ecCode, 0, 1, "ASC");
        WrapperEntitiesList entitiesList2 = wrapperService.findByStatusAndTypeAndBrokerCodeAndIdLike(WrapperStatus.TO_FIX, WrapperType.STATION, null, ecCode, 0, 1, "ASC");
        if(!entitiesList.getWrapperEntities().isEmpty() || !entitiesList2.getWrapperEntities().isEmpty())
            throw new PermissionDeniedException("ERROR There is a Station not completed!");
        String result = generateStationCode(ecCode);

        return new StationCodeResource(result);
    }

    public StationCodeResource getStationCodeV2(String ecCode) {
        Stations stations = getStations(100, 0, "ASC", null, null, ecCode);
        WrapperStations responseApiConfig = stationMapper.toWrapperStations(stations);
        WrapperEntitiesList mongoList = wrapperService.findByIdLikeOrTypeOrBrokerCode(ecCode, WrapperType.STATION, null, 0, 100);
        WrapperStations responseMongo = stationMapper.toWrapperStations(mongoList);
        WrapperStations stationsMergedAndSorted = mergeAndSortWrapperStations(responseApiConfig, responseMongo, "ASC");
        String result = generateStationCodeV2(stationsMergedAndSorted.getStationsList(), ecCode);

        return new StationCodeResource(result);
    }

    public WrapperEntitiesOperations updateWrapperStationDetails(
            @Valid
            StationDetailsDto stationDetailsDto) {

        final String UPDATE_STATION_SUMMARY = "Station creation validation: %s";
        final String UPDATE_STATION_DESCRIPTION = "The station %s created by broker %s needs to be validated: %s";

        WrapperEntitiesOperations createdWrapperEntities = wrapperService.
                update(stationMapper.fromDto
                        (stationDetailsDto), stationDetailsDto.getNote(), stationDetailsDto.getStatus().name(), null);

        jiraServiceManagerClient.createTicket(String.format(UPDATE_STATION_SUMMARY, stationDetailsDto.getStationCode()),
                String.format(UPDATE_STATION_DESCRIPTION, stationDetailsDto.getStationCode(), stationDetailsDto.getBrokerCode(), stationDetailsDto.getValidationUrl()));

        return createdWrapperEntities;
    }

    public WrapperEntitiesOperations updateWrapperStationDetailsByOpt(
            @Valid
            StationDetailsDto stationDetailsDto) {


        return wrapperService.
                updateByOpt(stationMapper.
                        fromDto(stationDetailsDto), stationDetailsDto.getNote(), stationDetailsDto.getStatus().name());
    }

    public CreditorInstitutionsResource getCreditorInstitutionsByStationCode(String stationcode, Integer limit, Integer page) {
        CreditorInstitutions creditorInstitutions = apiConfigClient.getCreditorInstitutionsByStation(stationcode, limit, page);
        return creditorInstitutionMapper.toResource(creditorInstitutions);
    }

    public StationDetailResource updateStation(@NotNull StationDetailsDto stationDetailsDto, String stationCode) {


        final String UPDATE_STATION_SUBJECT = "Update Stazione";
        final String UPDATE_STATION_EMAIL_BODY = String.format("Buongiorno%n%n la modifica per la stazione %s è stata validata da un operatore e risulta essere attiva%n%nSaluti", stationDetailsDto.getStationCode());

        StationDetails stationDetails = stationMapper.fromDto(stationDetailsDto);
        StationDetails response = apiConfigClient.updateStation(stationCode, stationDetails);
        wrapperService.update(stationDetails, stationDetailsDto.getNote(), stationDetailsDto.getStatus().name(), null);
        StationDetailResource resource = stationMapper.toResource(response);
        awsSesClient.sendEmail(UPDATE_STATION_SUBJECT, UPDATE_STATION_EMAIL_BODY, stationDetailsDto.getEmail());

        return resource;
    }

    public WrapperEntitiesOperations getWrapperEntitiesStation(String code) {

        return wrapperService.findById(code);
    }



    public WrapperStationsResource getAllStationsMerged(Integer limit, String stationCode, String brokerCode, Integer page, String sorting) {

        Stations stations = getStations(limit, page, sorting, brokerCode, null, stationCode);
        WrapperStations responseApiConfig = stationMapper.toWrapperStations(stations);
        WrapperEntitiesList mongoList = wrapperService.findByIdLikeOrTypeOrBrokerCode(stationCode, WrapperType.STATION, brokerCode, page, limit);
        WrapperStations responseMongo = stationMapper.toWrapperStations(mongoList);
        WrapperStations stationsMergedAndSorted = mergeAndSortWrapperStations(responseApiConfig, responseMongo, sorting);

        return stationMapper.toWrapperStationsResource(stationsMergedAndSorted);
    }



    private Stations getStations(Integer limit, Integer page, String sort, String brokerCode, String ecCode, String stationCode) {
        Stations response = null;
        try {
            response = apiConfigClient.getStations(limit, page, sort, brokerCode, ecCode, stationCode);
        } catch (Exception e) {
            if (e.getMessage().contains("[404 Not Found]")) {
                response = new Stations();
                response.setStationsList(new ArrayList<>());
                PageInfo pageInfo = new PageInfo();
                pageInfo.setPage(0);
                pageInfo.setTotalPages(0);
                pageInfo.setLimit(50);
                pageInfo.setItemsFound(0);
                pageInfo.setTotalItems(0L);
                response.setPageInfo(pageInfo);
            } else {
                throw e;
            }
        }
        return response;
    }

    private String generateStationCode(String ecCode) {
        Stations stations = apiConfigClient.getStations(100, 0, "ASC", null, null, ecCode);
        List<Station> stationsList = stations.getStationsList();
        List<String> codes = stationsList.stream().map(Station::getStationCode)
                .filter(s -> s.matches(REGEX_GENERATE))
                .collect(Collectors.toList());
        return generator(codes, ecCode);
    }


    private WrapperStations mergeAndSortWrapperStations(WrapperStations wrapperStationsApiConfig, WrapperStations wrapperStationsMongo, String sorting) {

        List<WrapperStation> mergedList = new ArrayList<>();
        mergedList.addAll(wrapperStationsMongo.getStationsList());
        mergedList.addAll(
                wrapperStationsApiConfig.getStationsList().stream()
                        .filter(obj2 -> wrapperStationsMongo.getStationsList().stream().noneMatch(obj1 -> Objects.equals(obj1.getStationCode(), obj2.getStationCode())))
                        .collect(Collectors.toList())
        );

        if ("asc".equalsIgnoreCase(sorting)) {
            mergedList.sort(Comparator.comparing(WrapperStation::getStationCode));
        } else if ("desc".equalsIgnoreCase(sorting)) {
            mergedList.sort(Comparator.comparing(WrapperStation::getStationCode, Comparator.reverseOrder()));
        }
        WrapperStations result = new WrapperStations();
        result.setStationsList(mergedList);
        PageInfo pageInfo = new PageInfo();
        pageInfo.setLimit(wrapperStationsApiConfig.getPageInfo().getLimit());
        pageInfo.setTotalPages(wrapperStationsApiConfig.getPageInfo().getTotalPages());
        pageInfo.setPage(wrapperStationsApiConfig.getPageInfo().getPage());
        pageInfo.setItemsFound(mergedList.size());
        pageInfo.setTotalItems(wrapperStationsApiConfig.getPageInfo().getTotalItems());
        result.setPageInfo(pageInfo);
        return result;
    }


    private String generateStationCodeV2( List<WrapperStation> stationList, String ecCode) {
        List<String> codes = stationList.stream().map(WrapperStation::getStationCode)
                .filter(s -> s.matches(REGEX_GENERATE))
                .collect(Collectors.toList());
        return generator(codes, ecCode);
    }



}