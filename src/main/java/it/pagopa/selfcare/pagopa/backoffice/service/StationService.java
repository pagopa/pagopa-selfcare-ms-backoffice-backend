package it.pagopa.selfcare.pagopa.backoffice.service;

import it.pagopa.selfcare.pagopa.backoffice.client.ApiConfigClient;
import it.pagopa.selfcare.pagopa.backoffice.client.AwsSesClient;
import it.pagopa.selfcare.pagopa.backoffice.client.ForwarderClient;
import it.pagopa.selfcare.pagopa.backoffice.client.JiraServiceManagerClient;
import it.pagopa.selfcare.pagopa.backoffice.entity.WrapperEntityStation;
import it.pagopa.selfcare.pagopa.backoffice.entity.WrapperEntityStations;
import it.pagopa.selfcare.pagopa.backoffice.exception.AppError;
import it.pagopa.selfcare.pagopa.backoffice.exception.AppException;
import it.pagopa.selfcare.pagopa.backoffice.mapper.CreditorInstitutionMapper;
import it.pagopa.selfcare.pagopa.backoffice.mapper.StationMapper;
import it.pagopa.selfcare.pagopa.backoffice.model.connector.channel.WrapperEntitiesList;
import it.pagopa.selfcare.pagopa.backoffice.model.connector.creditorinstitution.CreditorInstitutions;
import it.pagopa.selfcare.pagopa.backoffice.model.connector.station.Station;
import it.pagopa.selfcare.pagopa.backoffice.model.connector.station.StationDetails;
import it.pagopa.selfcare.pagopa.backoffice.model.connector.station.Stations;
import it.pagopa.selfcare.pagopa.backoffice.model.connector.station.WrapperStationList;
import it.pagopa.selfcare.pagopa.backoffice.model.connector.wrapper.*;
import it.pagopa.selfcare.pagopa.backoffice.model.creditorinstituions.CreditorInstitutionsResource;
import it.pagopa.selfcare.pagopa.backoffice.model.email.EmailMessageDetail;
import it.pagopa.selfcare.pagopa.backoffice.model.institutions.SelfcareProductUser;
import it.pagopa.selfcare.pagopa.backoffice.model.stations.*;
import org.mapstruct.factory.Mappers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

import static it.pagopa.selfcare.pagopa.backoffice.service.WrapperService.getStationWrapperEntityOperationsSortedList;
import static it.pagopa.selfcare.pagopa.backoffice.util.Constants.REGEX_GENERATE;
import static it.pagopa.selfcare.pagopa.backoffice.util.StringUtils.generator;

@Service
public class StationService {

    private static final String CREATE_STATION_SUBJECT = "Nuova stazione attiva";
    private static final String CREATE_STATION_EMAIL_BODY = "Ciao, %n%n%n pagoPA ha revisionato e validato la stazione %s che hai creato. Da questo momento puoi utilizzarla per attivare i tuoi servizi.%n%n%nA presto,%n%n Pagamenti pagoPA";
    private static final String UPDATE_STATION_SUBJECT = "Modifica stazione attiva";
    private static final String UPDATE_STATION_EMAIL_BODY = "Ciao, %n%n%n pagoPA ha revisionato e validato la stazione %s che hai modificato. Da questo momento la modifica effettuata risulta attiva.%n%n%nA presto,%n%n Pagamenti pagoPA";
    private static final String STATION_REVIEW_SUBJECT = "Modifiche richieste";
    private static final String STATION_REVIEW_EMAIL_BODY = "Ciao, %n%n%n pagoPA ha richiesto delle modifiche alla stazione %s che hai creato.%n Puoi vedere le modifiche qui sotto oppure nel dettaglio della stazione (https://selfcare%s.platform.pagopa.it/ui/stations/%s).%n Modifiche richieste %n '%s' %n%n%nA presto,%n%n Pagamenti pagoPA";

    private final String environment;

    private final CreditorInstitutionMapper creditorInstitutionMapper = Mappers.getMapper(CreditorInstitutionMapper.class);

    private final StationMapper stationMapper = Mappers.getMapper(StationMapper.class);

    private final ApiConfigClient apiConfigClient;

    private final WrapperService wrapperService;

    private final AwsSesClient awsSesClient;

    private final ForwarderClient forwarderClient;

    private final JiraServiceManagerClient jiraServiceManagerClient;

    @Autowired
    public StationService(
            @Value("${info.properties.environment}") String environment,
            ApiConfigClient apiConfigClient,
            WrapperService wrapperService,
            AwsSesClient awsSesClient,
            ForwarderClient forwarderClient,
            JiraServiceManagerClient jiraServiceManagerClient
    ) {
        this.environment = environment;
        this.apiConfigClient = apiConfigClient;
        this.wrapperService = wrapperService;
        this.awsSesClient = awsSesClient;
        this.forwarderClient = forwarderClient;
        this.jiraServiceManagerClient = jiraServiceManagerClient;
    }

    /**
     * Creates a validated station and update the relative wrapper station with status {@link WrapperStatus#APPROVED}.
     * Notify the channel owner via email.
     *
     * @param stationDetailsDto the station details
     * @return the created station
     */
    public StationDetailResource createStation(@NotNull StationDetailsDto stationDetailsDto) {
        StationDetails stationDetails = this.stationMapper.fromDto(stationDetailsDto);
        stationDetails.setActivationDate(Instant.now());
        this.apiConfigClient.createStation(stationDetails);

        WrapperEntityStations response = this.wrapperService.updateValidatedWrapperStation(stationDetails, WrapperStatus.APPROVED);
        StationDetailResource result = this.stationMapper
                .toResource(getStationWrapperEntityOperationsSortedList(response).get(0).getEntity());

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

    /**
     * Creates a new wrapper station in status {@link WrapperStatus#TO_CHECK} and open a JIRA ticket for operator
     * review
     *
     * @param wrapperStationDetailsDto detail of the new channel
     * @return the created wrapper channel
     */
    public WrapperEntityStations createWrapperStationDetails(@Valid WrapperStationDetailsDto wrapperStationDetailsDto) {
        final String CREATE_STATION_SUMMARY = " Validazione stazione - creazione: %s";
        final String CREATE_STATION_DESCRIPTION = "La stazione %s deve essere validata: %s";

        WrapperEntityStations createdWrapperEntities =
                this.wrapperService.createWrapperStation(
                        this.stationMapper.fromWrapperStationDetailsDto(wrapperStationDetailsDto),
                        WrapperStatus.TO_CHECK
                );

        String stationCode = wrapperStationDetailsDto.getStationCode();
        this.jiraServiceManagerClient.createTicket(
                String.format(CREATE_STATION_SUMMARY, stationCode),
                String.format(CREATE_STATION_DESCRIPTION, stationCode, wrapperStationDetailsDto.getValidationUrl())
        );

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
            WrapperStationList wrapperStations = this.wrapperService.getWrapperStations(stationCode, brokerCode, limit, page);
            response = this.stationMapper.toWrapperStations(wrapperStations);
        }
        return this.stationMapper.toWrapperStationsResource(response);
    }

    /**
     * Retrieve the station details from api-config if the provided status is {@link ConfigurationStatus#ACTIVE},
     * from wrapper otherwise. If the provided status is {@link ConfigurationStatus#ACTIVE} set the pending update flag
     * to false if the most recent wrapper status is {@link WrapperStatus#APPROVED}, true otherwise.
     *
     * @param stationCode station's code
     * @param status      station's status
     * @return the station details
     */
    public StationDetailResource getStationDetails(String stationCode, ConfigurationStatus status) {
        StationDetailResource stationDetailResource;
        if (status.equals(ConfigurationStatus.ACTIVE)) {
            StationDetails stationDetails = this.apiConfigClient.getStation(stationCode);
            stationDetailResource = buildActiveStationDetails(stationCode, stationDetails);
        } else {
            stationDetailResource = findInWrapperOrElseInApiConfig(stationCode);
        }
        return stationDetailResource;
    }

    public StationCodeResource getStationCode(String ecCode, Boolean v2) {
        if (Boolean.TRUE.equals(v2)) {
            return new StationCodeResource(this.wrapperService.getFirstValidCodeV2(ecCode));
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
    public StationDetailResource updateWrapperStationDetails(
            String stationCode,
            @Valid StationDetailsDto stationDetailsDto
    ) {
        final String UPDATE_STATION_SUMMARY = "Station creation validation: %s";
        final String UPDATE_STATION_DESCRIPTION = "The station %s created by broker %s needs to be validated: %s";

        WrapperEntityStations wrapperEntityStations = this.wrapperService.updateWrapperStation(
                stationCode,
                this.stationMapper.fromDto(stationDetailsDto)
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
        return this.stationMapper
                .toResource(getStationWrapperEntityOperationsSortedList(wrapperEntityStations).get(0).getEntity());
    }

    /**
     * Update the wrapper station with the operator review's note and notify the station owner via email.
     *
     * @param stationCode station's code
     * @param ciTaxCode   creditor institution's tax code that own the station
     * @param note        operator review note
     * @return the updated station wrapper
     */
    public StationDetailResource updateWrapperStationWithOperatorReview(
            String stationCode,
            String ciTaxCode,
            String note
    ) {
        WrapperEntityStations updatedWrapper = this.wrapperService.updateStationWithOperatorReview(stationCode, note);

        EmailMessageDetail messageDetail = EmailMessageDetail.builder()
                .institutionTaxCode(ciTaxCode)
                .subject(STATION_REVIEW_SUBJECT)
                .textBody(String.format(STATION_REVIEW_EMAIL_BODY, stationCode, getEnvParam(), stationCode, note))
                .htmlBodyFileName("stationReviewRequestedEmail.html")
                .htmlBodyContext(buildStationHtmlEmailBodyContext(stationCode, note))
                .destinationUserType(SelfcareProductUser.OPERATOR)
                .build();
        this.awsSesClient.sendEmail(messageDetail);

        WrapperEntityStation entityOperations = getStationWrapperEntityOperationsSortedList(updatedWrapper).get(0);
        return this.stationMapper.toResource(
                entityOperations.getEntity(),
                updatedWrapper.getStatus(),
                updatedWrapper.getCreatedBy(),
                updatedWrapper.getModifiedBy(),
                updatedWrapper.getCreatedAt(),
                entityOperations.getNote()
        );
    }

    public CreditorInstitutionsResource getCreditorInstitutionsByStationCode(
            String stationCode,
            Integer limit,
            Integer page,
            String ciNameOrFiscalCode
    ) {
        CreditorInstitutions creditorInstitutions = apiConfigClient.getCreditorInstitutionsByStation(stationCode, limit, page, ciNameOrFiscalCode);
        return creditorInstitutionMapper.toResource(creditorInstitutions);
    }

    /**
     * Updates a validated station and update the relative wrapper station with status {@link WrapperStatus#APPROVED}.
     * Notify the channel owner via email.
     *
     * @param stationCode       station's code
     * @param stationDetailsDto the station details
     * @return the updated station
     */
    public StationDetailResource updateStation(@NotNull StationDetailsDto stationDetailsDto, String stationCode) {
        StationDetails stationDetails = this.stationMapper.fromDto(stationDetailsDto);
        StationDetails response = this.apiConfigClient.updateStation(stationCode, stationDetails);
        this.wrapperService.update(stationDetails, stationDetailsDto.getNote(), WrapperStatus.APPROVED.name(), null);
        StationDetailResource resource = this.stationMapper.toResource(response);

        EmailMessageDetail messageDetail = EmailMessageDetail.builder()
                .institutionTaxCode(stationDetails.getBrokerCode())
                .subject(UPDATE_STATION_SUBJECT)
                .textBody(String.format(UPDATE_STATION_EMAIL_BODY, stationCode))
                .htmlBodyFileName("stationUpdateValidatedEmail.html")
                .htmlBodyContext(buildStationHtmlEmailBodyContext(stationCode))
                .destinationUserType(SelfcareProductUser.OPERATOR)
                .build();

        this.awsSesClient.sendEmail(messageDetail);
        return resource;
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
        properties.put("environment", getEnvParam());
        if (note != null) {
            properties.put("reviewNote", note);
        }

        context.setVariables(properties);
        return context;
    }

    private String getEnvParam() {
        if (this.environment.equals("PROD")) {
            return "";
        }
        return String.format(".%s", this.environment.toLowerCase());
    }

    /**
     * @deprecated this API invoke the old station code generation logic that can cause collision on wrapper data for PT
     */
    @Deprecated(forRemoval = true)
    private String getFirstValidStationCodeAux(String ecCode) {
        WrapperEntitiesList entitiesList = wrapperService.findByStatusAndTypeAndBrokerCodeAndIdLike(WrapperStatus.TO_CHECK, WrapperType.STATION, null, ecCode, 0, 1, "ASC");
        WrapperEntitiesList entitiesList2 = wrapperService.findByStatusAndTypeAndBrokerCodeAndIdLike(WrapperStatus.TO_FIX, WrapperType.STATION, null, ecCode, 0, 1, "ASC");
        if (!entitiesList.getWrapperEntities().isEmpty() || !entitiesList2.getWrapperEntities().isEmpty())
            throw new AppException(AppError.STATION_CONFLICT);
        return generateStationCode(ecCode);
    }

    private String generateStationCode(String ecCode) {
        Stations stations = apiConfigClient.getStations(100, 0, "ASC", null, null, ecCode);
        List<Station> stationsList = stations.getStationsList();
        Set<String> codes = stationsList.stream().map(Station::getStationCode)
                .filter(s -> s.matches(REGEX_GENERATE))
                .collect(Collectors.toSet());
        return generator(codes, ecCode);
    }


    private WrapperStations buildEnrichedWrapperStations(Stations stations) {
        WrapperStations response;
        List<WrapperStation> wrapperStations = stations.getStationsList().parallelStream()
                .map(station -> {
                    WrapperStation wrapperStation = this.stationMapper.toWrapperStation(station);
                    Optional<WrapperEntityStations> optionalWrapperEntities =
                            this.wrapperService.findStationByIdOptional(station.getStationCode());
                    if (optionalWrapperEntities.isPresent()) {
                        WrapperEntityStations wrapperEntities = optionalWrapperEntities.get();
                        wrapperStation.setCreatedAt(wrapperEntities.getCreatedAt());
                        wrapperStation.setActivationDate(wrapperEntities.getEntities().get(0).getEntity().getActivationDate());
                    }
                    return wrapperStation;
                }).toList();
        response = WrapperStations.builder()
                .stationsList(wrapperStations)
                .pageInfo(stations.getPageInfo())
                .build();
        return response;
    }

    private StationDetailResource buildActiveStationDetails(
            String stationCode,
            StationDetails stationDetails
    ) {
        StationDetailResource stationDetailResource = this.stationMapper.toResource(stationDetails);
        stationDetailResource.setWrapperStatus(WrapperStatus.APPROVED);

        Optional<WrapperEntityStations> optionalWrapperEntities = this.wrapperService.findStationByIdOptional(stationCode);
        if (optionalWrapperEntities.isPresent()) {
            WrapperEntityStations wrapperEntities = optionalWrapperEntities.get();
            stationDetailResource.setCreatedAt(wrapperEntities.getCreatedAt());

            WrapperEntityStation mostRecentEntity = getStationWrapperEntityOperationsSortedList(wrapperEntities).get(0);
            stationDetailResource.setPendingUpdate(!WrapperStatus.APPROVED.equals(mostRecentEntity.getStatus()));
            stationDetailResource.setActivationDate(wrapperEntities.getEntities().get(0).getEntity().getActivationDate());
        } else {
            stationDetailResource.setPendingUpdate(false);
        }
        return stationDetailResource;
    }

    private StationDetailResource findInWrapperOrElseInApiConfig(String stationCode) {
        // handle legacy stations
        try {
            WrapperEntityStations wrapperEntities = this.wrapperService.findStationById(stationCode);
            return this.stationMapper.toResource(wrapperEntities);
        } catch (AppException e) {
            StationDetails stationDetails = this.apiConfigClient.getStation(stationCode);
            return buildActiveStationDetails(stationCode, stationDetails);
        }
    }
}
