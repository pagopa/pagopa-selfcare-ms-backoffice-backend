package it.pagopa.selfcare.pagopa.backoffice.service;

import it.pagopa.selfcare.pagopa.backoffice.client.ApiConfigClient;
import it.pagopa.selfcare.pagopa.backoffice.client.AwsSesClient;
import it.pagopa.selfcare.pagopa.backoffice.client.JiraServiceManagerClient;
import it.pagopa.selfcare.pagopa.backoffice.entity.WrapperEntities;
import it.pagopa.selfcare.pagopa.backoffice.entity.WrapperEntityOperations;
import it.pagopa.selfcare.pagopa.backoffice.exception.AppError;
import it.pagopa.selfcare.pagopa.backoffice.exception.AppException;
import it.pagopa.selfcare.pagopa.backoffice.mapper.CreditorInstitutionMapper;
import it.pagopa.selfcare.pagopa.backoffice.mapper.StationMapper;
import it.pagopa.selfcare.pagopa.backoffice.model.connector.PageInfo;
import it.pagopa.selfcare.pagopa.backoffice.model.connector.channel.WrapperEntitiesList;
import it.pagopa.selfcare.pagopa.backoffice.model.connector.creditorInstitution.CreditorInstitutions;
import it.pagopa.selfcare.pagopa.backoffice.model.connector.station.Station;
import it.pagopa.selfcare.pagopa.backoffice.model.connector.station.StationDetails;
import it.pagopa.selfcare.pagopa.backoffice.model.connector.station.Stations;
import it.pagopa.selfcare.pagopa.backoffice.model.connector.wrapper.WrapperStation;
import it.pagopa.selfcare.pagopa.backoffice.model.connector.wrapper.WrapperStations;
import it.pagopa.selfcare.pagopa.backoffice.model.connector.wrapper.WrapperStatus;
import it.pagopa.selfcare.pagopa.backoffice.model.connector.wrapper.WrapperType;
import it.pagopa.selfcare.pagopa.backoffice.model.creditorinstituions.CreditorInstitutionsResource;
import it.pagopa.selfcare.pagopa.backoffice.model.stations.*;
import org.mapstruct.factory.Mappers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

import static it.pagopa.selfcare.pagopa.backoffice.service.WrapperService.getWrapperEntityOperationsSortedList;
import static it.pagopa.selfcare.pagopa.backoffice.util.Constants.REGEX_GENERATE;
import static it.pagopa.selfcare.pagopa.backoffice.util.StringUtils.generator;

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

        WrapperEntities<StationDetails> response = wrapperService.updateByOpt(stationDetails, stationDetailsDto.getNote(), WrapperStatus.APPROVED.name());
        WrapperEntityOperations<StationDetails> result = getWrapperEntityOperationsSortedList(response).get(0);
        awsSesClient.sendEmail(CREATE_STATION_SUBJECT, CREATE_STATION_EMAIL_BODY, stationDetailsDto.getEmail());
        return result;
    }

    public WrapperEntities<StationDetails> createWrapperStationDetails(@Valid WrapperStationDetailsDto wrapperStationDetailsDto) {
        final String CREATE_STATION_SUMMARY = " Validazione stazione - creazione: %s";
        final String CREATE_STATION_DESCRIPTION = "La stazione %s deve essere validata: %s";

        WrapperEntities<StationDetails> createdWrapperEntities = wrapperService.
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
        Instant createdAt = null;
        String modifiedBy = "";
        try {
            WrapperEntities<StationDetails> result = wrapperService.findById(stationCode);
            createdBy = result.getCreatedBy();
            createdAt = result.getCreatedAt();
            modifiedBy = result.getModifiedBy();
            stationDetails = (StationDetails) getWrapperEntityOperationsSortedList(result).get(0).getEntity();
            status = result.getStatus();
        } catch (AppException e) {

            stationDetails = apiConfigClient.getStation(stationCode);
            status = WrapperStatus.APPROVED;
        }

        return stationMapper.toResource(stationDetails, status, createdBy, modifiedBy, createdAt);
    }

    public StationCodeResource getStationCode(String ecCode, Boolean v2) {
        if(Boolean.TRUE.equals(v2)) {
            return new StationCodeResource(wrapperService.getFirstValidCodeV2(ecCode, ecCode));
        } else {
            return new StationCodeResource(getFirstValidStationCodeAux(ecCode));
        }
    }

    private String getFirstValidStationCodeAux(String ecCode) {
        WrapperEntitiesList entitiesList = wrapperService.findByStatusAndTypeAndBrokerCodeAndIdLike(WrapperStatus.TO_CHECK, WrapperType.STATION, null, ecCode, 0, 1, "ASC");
        WrapperEntitiesList entitiesList2 = wrapperService.findByStatusAndTypeAndBrokerCodeAndIdLike(WrapperStatus.TO_FIX, WrapperType.STATION, null, ecCode, 0, 1, "ASC");
        if(!entitiesList.getWrapperEntities().isEmpty() || !entitiesList2.getWrapperEntities().isEmpty())
            throw new AppException(AppError.STATION_CONFLICT);
        return generateStationCode(ecCode);
    }


    public WrapperEntities updateWrapperStationDetails(@Valid StationDetailsDto stationDetailsDto) {
        final String UPDATE_STATION_SUMMARY = "Station creation validation: %s";
        final String UPDATE_STATION_DESCRIPTION = "The station %s created by broker %s needs to be validated: %s";


        apiConfigClient.getStation(stationDetailsDto.getStationCode());

        WrapperEntities createdWrapperEntities = wrapperService.upsert(stationMapper.fromDto(stationDetailsDto), stationDetailsDto.getNote(), stationDetailsDto.getStatus().name(), null);

        jiraServiceManagerClient.createTicket(String.format(UPDATE_STATION_SUMMARY, stationDetailsDto.getStationCode()),
                String.format(UPDATE_STATION_DESCRIPTION, stationDetailsDto.getStationCode(), stationDetailsDto.getBrokerCode(), stationDetailsDto.getValidationUrl()));

        return createdWrapperEntities;
    }

    public WrapperEntities updateWrapperStationDetailsByOpt(
            @Valid
            StationDetailsDto stationDetailsDto) {


        return wrapperService.
                updateByOpt(stationMapper.
                        fromDto(stationDetailsDto), stationDetailsDto.getNote(), stationDetailsDto.getStatus().name());
    }

    public CreditorInstitutionsResource getCreditorInstitutionsByStationCode(String stationcode, Integer limit, Integer page, String ciNameOrFiscalCode) {
        CreditorInstitutions creditorInstitutions = apiConfigClient.getCreditorInstitutionsByStation(stationcode, limit, page, ciNameOrFiscalCode);
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

    public WrapperEntities getWrapperEntitiesStation(String code) {
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
            if(e.getMessage().contains("[404 Not Found]")) {
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
        Set<String> codes = stationsList.stream().map(Station::getStationCode)
                .filter(s -> s.matches(REGEX_GENERATE))
                .collect(Collectors.toSet());
        return generator(codes, ecCode);
    }


    private WrapperStations mergeAndSortWrapperStations(WrapperStations wrapperStationsApiConfig, WrapperStations wrapperStationsMongo, String sorting) {

        List<WrapperStation> mergedList = new ArrayList<>();
        mergedList.addAll(wrapperStationsMongo.getStationsList());
        mergedList.addAll(
                wrapperStationsApiConfig.getStationsList().stream()
                        .filter(obj2 -> wrapperStationsMongo.getStationsList().stream().noneMatch(obj1 -> Objects.equals(obj1.getStationCode(), obj2.getStationCode())))
                        .toList()
        );

        if("asc".equalsIgnoreCase(sorting)) {
            mergedList.sort(Comparator.comparing(WrapperStation::getStationCode));
        } else if("desc".equalsIgnoreCase(sorting)) {
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

}
