package it.pagopa.selfcare.pagopa.backoffice.service;

import it.pagopa.selfcare.pagopa.backoffice.client.ApiConfigClient;
import it.pagopa.selfcare.pagopa.backoffice.client.AwsSesClient;
import it.pagopa.selfcare.pagopa.backoffice.client.ForwarderClient;
import it.pagopa.selfcare.pagopa.backoffice.client.JiraServiceManagerClient;
import it.pagopa.selfcare.pagopa.backoffice.entity.WrapperEntities;
import it.pagopa.selfcare.pagopa.backoffice.entity.WrapperEntityOperations;
import it.pagopa.selfcare.pagopa.backoffice.exception.AppError;
import it.pagopa.selfcare.pagopa.backoffice.exception.AppException;
import it.pagopa.selfcare.pagopa.backoffice.mapper.CreditorInstitutionMapper;
import it.pagopa.selfcare.pagopa.backoffice.mapper.StationMapper;
import it.pagopa.selfcare.pagopa.backoffice.model.connector.PageInfo;
import it.pagopa.selfcare.pagopa.backoffice.model.connector.channel.WrapperEntitiesList;
import it.pagopa.selfcare.pagopa.backoffice.model.connector.creditorinstitution.CreditorInstitutions;
import it.pagopa.selfcare.pagopa.backoffice.model.connector.station.Station;
import it.pagopa.selfcare.pagopa.backoffice.model.connector.station.StationDetails;
import it.pagopa.selfcare.pagopa.backoffice.model.connector.station.Stations;
import it.pagopa.selfcare.pagopa.backoffice.model.connector.wrapper.ConfigurationStatus;
import it.pagopa.selfcare.pagopa.backoffice.model.connector.wrapper.WrapperStation;
import it.pagopa.selfcare.pagopa.backoffice.model.connector.wrapper.WrapperStations;
import it.pagopa.selfcare.pagopa.backoffice.model.connector.wrapper.WrapperStatus;
import it.pagopa.selfcare.pagopa.backoffice.model.connector.wrapper.WrapperType;
import it.pagopa.selfcare.pagopa.backoffice.model.creditorinstituions.CreditorInstitutionsResource;
import it.pagopa.selfcare.pagopa.backoffice.model.email.EmailMessageDetail;
import it.pagopa.selfcare.pagopa.backoffice.model.institutions.SelfcareProductUser;
import it.pagopa.selfcare.pagopa.backoffice.model.stations.StationCodeResource;
import it.pagopa.selfcare.pagopa.backoffice.model.stations.StationDetailResource;
import it.pagopa.selfcare.pagopa.backoffice.model.stations.StationDetailsDto;
import it.pagopa.selfcare.pagopa.backoffice.model.stations.StationTestDto;
import it.pagopa.selfcare.pagopa.backoffice.model.stations.TestResultEnum;
import it.pagopa.selfcare.pagopa.backoffice.model.stations.TestStationResource;
import it.pagopa.selfcare.pagopa.backoffice.model.stations.WrapperStationDetailsDto;
import it.pagopa.selfcare.pagopa.backoffice.model.stations.WrapperStationsResource;
import org.mapstruct.factory.Mappers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static it.pagopa.selfcare.pagopa.backoffice.service.WrapperService.getWrapperEntityOperationsSortedList;
import static it.pagopa.selfcare.pagopa.backoffice.util.Constants.REGEX_GENERATE;
import static it.pagopa.selfcare.pagopa.backoffice.util.StringUtils.generator;

@Service
public class StationService {

    private static final String CREATE_STATION_SUBJECT = "Nuova stazione attiva";
    private static final String CREATE_STATION_EMAIL_BODY = "Ciao, %n%n%n pagoPA ha revisionato e validato la stazione %s che hai creato. Da questo momento puoi utilizzarla per attivare i tuoi servizi.%n%n%nA presto,%n%n Pagamenti pagoPA";
    private static final String UPDATE_STATION_SUBJECT = "Modifica stazione attiva";
    private static final String UPDATE_STATION_EMAIL_BODY = "Ciao, %n%n%n pagoPA ha revisionato e validato la stazione %s che hai modificato. Da questo momento la modifica effettuata risulta attiva.%n%n%nA presto,%n%n Pagamenti pagoPA";
    private static final String STATION_REVIEW_SUBJECT = "Modifiche richieste";
    private static final String STATION_REVIEW_EMAIL_BODY = "Ciao, %n%n%n pagoPA ha richiesto delle modifiche alla stazione %s che hai creato.%n Puoi vedere le modifiche qui sotto oppure nel dettaglio della stazione (https://selfcare.platform.pagopa.it/ui/stations/%s).%n Modifiche richieste %n '%s' %n%n%nA presto,%n%n Pagamenti pagoPA";

    private final CreditorInstitutionMapper creditorInstitutionMapper = Mappers.getMapper(CreditorInstitutionMapper.class);

    private final StationMapper stationMapper = Mappers.getMapper(StationMapper.class);

    private final ApiConfigClient apiConfigClient;

    private final WrapperService wrapperService;

    private final AwsSesClient awsSesClient;

    private final ForwarderClient forwarderClient;

    private final JiraServiceManagerClient jiraServiceManagerClient;

    @Autowired
    public StationService(
            ApiConfigClient apiConfigClient,
            WrapperService wrapperService,
            AwsSesClient awsSesClient,
            ForwarderClient forwarderClient,
            JiraServiceManagerClient jiraServiceManagerClient
    ) {
        this.apiConfigClient = apiConfigClient;
        this.wrapperService = wrapperService;
        this.awsSesClient = awsSesClient;
        this.forwarderClient = forwarderClient;
        this.jiraServiceManagerClient = jiraServiceManagerClient;
    }

    public WrapperEntityOperations<StationDetails> createStation(@NotNull StationDetailsDto stationDetailsDto) {
        StationDetails stationDetails = this.stationMapper.fromDto(stationDetailsDto);
        this.apiConfigClient.createStation(stationDetails);

        WrapperEntities<StationDetails> response = this.wrapperService.updateByOpt(stationDetails, stationDetailsDto.getNote(), WrapperStatus.APPROVED.name());
        WrapperEntityOperations<StationDetails> result = getWrapperEntityOperationsSortedList(response).get(0);

        EmailMessageDetail messageDetail = EmailMessageDetail.builder()
                .institutionTaxCode(stationDetails.getBrokerCode())
                .subject(CREATE_STATION_SUBJECT)
                .textBody(String.format(CREATE_STATION_EMAIL_BODY, stationDetails.getStationCode()))
                .htmlBodyFileName("stationCreationValidatedEmail.html")
                .htmlBodyContext(buildStationHtmlEmailBodyContext(stationDetails.getStationCode()))
                .destinationUserType(SelfcareProductUser.OPERATOR)
                .build();

        this.awsSesClient.sendEmail(messageDetail);
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

    /**
     * Retrieve a paginated list of stations from api-config if the provided status is {@link ConfigurationStatus#ACTIVE},
     * from wrapper otherwise. The result is filter out by station's code and broker's code.
     *
     * @param status      station's status
     * @param stationCode station's code
     * @param brokerCode  broker's code
     * @param limit       page size
     * @param page        page number
     * @return the paginated list
     */
    public WrapperStationsResource getStations(
            ConfigurationStatus status,
            String stationCode,
            String brokerCode,
            Integer limit,
            Integer page
    ) {
        WrapperStations response;
        if (status.equals(ConfigurationStatus.ACTIVE)) {
            Stations stations = this.apiConfigClient.getStations(limit, page, "DESC", brokerCode, null, stationCode);
            response = buildEnrichedWrapperStations(stations);
        } else {
            WrapperEntitiesList wrapperStations = this.wrapperService.getWrapperStations(stationCode, brokerCode, limit, page);
            response = this.stationMapper.toWrapperStations(wrapperStations);
        }
        return this.stationMapper.toWrapperStationsResource(response);
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
        String note = "";
        try {
            WrapperEntities<StationDetails> result = wrapperService.findById(stationCode);
            createdBy = result.getCreatedBy();
            createdAt = result.getCreatedAt();
            modifiedBy = result.getModifiedBy();
            status = result.getStatus();
            WrapperEntityOperations<StationDetails> wrapperEntity = getWrapperEntityOperationsSortedList(result).get(0);
            stationDetails = wrapperEntity.getEntity();
            note = wrapperEntity.getNote();
        } catch (AppException e) {

            stationDetails = apiConfigClient.getStation(stationCode);
            status = WrapperStatus.APPROVED;
        }

        return stationMapper.toResource(stationDetails, status, createdBy, modifiedBy, createdAt, note);
    }

    public StationCodeResource getStationCode(String ecCode, Boolean v2) {
        if (Boolean.TRUE.equals(v2)) {
            return new StationCodeResource(wrapperService.getFirstValidStationCodeV2(ecCode));
        } else {
            return new StationCodeResource(getFirstValidStationCodeAux(ecCode));
        }
    }

    /**
     * Update the detail of a station in the validation process. Update the details and create a JIRA ticket to notify
     * the operator for the new update.
     *
     * @param stationDetailsDto the new station details
     * @return the updated station
     */
    public WrapperEntities updateWrapperStationDetails(@Valid StationDetailsDto stationDetailsDto) {
        final String UPDATE_STATION_SUMMARY = "Station creation validation: %s";
        final String UPDATE_STATION_DESCRIPTION = "The station %s created by broker %s needs to be validated: %s";

        WrapperEntities createdWrapperEntities = this.wrapperService.upsert(
                this.stationMapper.fromDto(stationDetailsDto),
                stationDetailsDto.getNote(),
                stationDetailsDto.getStatus().name(),
                null
        );

        this.jiraServiceManagerClient.createTicket(
                String.format(UPDATE_STATION_SUMMARY, stationDetailsDto.getStationCode()),
                String.format(
                        UPDATE_STATION_DESCRIPTION,
                        stationDetailsDto.getStationCode(),
                        stationDetailsDto.getBrokerCode(),
                        stationDetailsDto.getValidationUrl()
                )
        );
        return createdWrapperEntities;
    }

    /**
     * Update the wrapper station with the operator review's note and notify the station owner via email.
     *
     * @param stationCode station's code
     * @param ciTaxCode   creditor institution's tax code that own the station
     * @param note        operator review note
     * @return the updated station wrapper
     */
    public StationDetailResource updateWrapperStationWithOperatorReview(String stationCode, String ciTaxCode, String note) {
        WrapperEntities<StationDetails> updatedWrapper = this.wrapperService.updateStationWithOperatorReview(stationCode, note);

        EmailMessageDetail messageDetail = EmailMessageDetail.builder()
                .institutionTaxCode(ciTaxCode)
                .subject(STATION_REVIEW_SUBJECT)
                .textBody(String.format(STATION_REVIEW_EMAIL_BODY, stationCode, stationCode, note))
                .htmlBodyFileName("stationReviewRequestedEmail.html")
                .htmlBodyContext(buildStationHtmlEmailBodyContext(stationCode, note))
                .destinationUserType(SelfcareProductUser.OPERATOR)
                .build();
        this.awsSesClient.sendEmail(messageDetail);

        WrapperEntityOperations<StationDetails> entityOperations = getWrapperEntityOperationsSortedList(updatedWrapper).get(0);
        return this.stationMapper.toResource(
                entityOperations.getEntity(),
                updatedWrapper.getStatus(),
                updatedWrapper.getCreatedBy(),
                updatedWrapper.getModifiedBy(),
                updatedWrapper.getCreatedAt(),
                entityOperations.getNote()
        );
    }

    public CreditorInstitutionsResource getCreditorInstitutionsByStationCode(String stationcode, Integer limit, Integer page, String ciNameOrFiscalCode) {
        CreditorInstitutions creditorInstitutions = apiConfigClient.getCreditorInstitutionsByStation(stationcode, limit, page, ciNameOrFiscalCode);
        return creditorInstitutionMapper.toResource(creditorInstitutions);
    }

    public StationDetailResource updateStation(@NotNull StationDetailsDto stationDetailsDto, String stationCode) {
        StationDetails stationDetails = this.stationMapper.fromDto(stationDetailsDto);
        StationDetails response = this.apiConfigClient.updateStation(stationCode, stationDetails);
        this.wrapperService.update(stationDetails, stationDetailsDto.getNote(), stationDetailsDto.getStatus().name(), null);
        StationDetailResource resource = this.stationMapper.toResource(response);

        EmailMessageDetail messageDetail = EmailMessageDetail.builder()
                .institutionTaxCode(stationDetails.getBrokerCode())
                .subject(UPDATE_STATION_SUBJECT)
                .textBody(String.format(UPDATE_STATION_EMAIL_BODY, stationDetails.getStationCode()))
                .htmlBodyFileName("stationUpdateValidatedEmail.html")
                .htmlBodyContext(buildStationHtmlEmailBodyContext(stationDetails.getStationCode()))
                .destinationUserType(SelfcareProductUser.OPERATOR)
                .build();

        this.awsSesClient.sendEmail(messageDetail);
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

    public TestStationResource testStation(StationTestDto stationTestDto) {
        var response = forwarderClient.testForwardConnection(
                stationTestDto.getHostProtocol(),
                stationTestDto.getHostUrl(),
                stationTestDto.getHostPort(),
                stationTestDto.getHostPath(),
                stationTestDto.getTestStationType()
        );
        if (response.getStatus() == 200) {
            return TestStationResource.builder().testResult(TestResultEnum.SUCCESS).message("OK").build();
        } else if (response.getStatus() == 401) {
            return TestStationResource.builder().testResult(TestResultEnum.CERTIFICATE_ERROR)
                    .message("Connection error due to invalid connection on the station endpoint").build();
        } else {
            return TestStationResource.builder().testResult(TestResultEnum.ERROR)
                    .message("Connection Error with status: " + response.getStatus()).build();
        }
    }

    private Context buildStationHtmlEmailBodyContext(String stationCode) {
        return buildStationHtmlEmailBodyContext(stationCode, null);
    }

    private Context buildStationHtmlEmailBodyContext(String stationCode, String note) {
        // Thymeleaf Context
        Context context = new Context();

        // Properties to show up in Template after stored in Context
        Map<String, Object> properties = new HashMap<>();
        properties.put("stationCode", stationCode);
        if (note != null) {
            properties.put("reviewNote", note);
        }

        context.setVariables(properties);
        return context;
    }

    private String getFirstValidStationCodeAux(String ecCode) {
        WrapperEntitiesList entitiesList = wrapperService.findByStatusAndTypeAndBrokerCodeAndIdLike(WrapperStatus.TO_CHECK, WrapperType.STATION, null, ecCode, 0, 1, "ASC");
        WrapperEntitiesList entitiesList2 = wrapperService.findByStatusAndTypeAndBrokerCodeAndIdLike(WrapperStatus.TO_FIX, WrapperType.STATION, null, ecCode, 0, 1, "ASC");
        if (!entitiesList.getWrapperEntities().isEmpty() || !entitiesList2.getWrapperEntities().isEmpty())
            throw new AppException(AppError.STATION_CONFLICT);
        return generateStationCode(ecCode);
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

    private WrapperStations buildEnrichedWrapperStations(Stations stations) {
        WrapperStations response;
        List<WrapperStation> wrapperStations = stations.getStationsList().parallelStream()
                .map(station -> {
                    WrapperStation wrapperStation = this.stationMapper.toWrapperStation(station);
                    Optional<WrapperEntities> optionalWrapperEntities = this.wrapperService.findByIdOptional(station.getStationCode());
                    if (optionalWrapperEntities.isPresent()) {
                        WrapperEntities<StationDetails> wrapperEntities = optionalWrapperEntities.get();
                        wrapperStation.setCreatedAt(wrapperEntities.getCreatedAt());
                    }
                    return wrapperStation;
                }).toList();
        response = WrapperStations.builder()
                .stationsList(wrapperStations)
                .pageInfo(stations.getPageInfo())
                .build();
        return response;
    }
}
