package it.pagopa.selfcare.pagopa.backoffice.service;

import it.pagopa.selfcare.pagopa.backoffice.client.ApiConfigClient;
import it.pagopa.selfcare.pagopa.backoffice.client.AwsSesClient;
import it.pagopa.selfcare.pagopa.backoffice.client.JiraServiceManagerClient;
import it.pagopa.selfcare.pagopa.backoffice.entity.WrapperEntities;
import it.pagopa.selfcare.pagopa.backoffice.entity.WrapperEntityChannel;
import it.pagopa.selfcare.pagopa.backoffice.entity.WrapperEntityChannels;
import it.pagopa.selfcare.pagopa.backoffice.exception.AppException;
import it.pagopa.selfcare.pagopa.backoffice.mapper.ChannelMapper;
import it.pagopa.selfcare.pagopa.backoffice.model.channels.ChannelDetailsDto;
import it.pagopa.selfcare.pagopa.backoffice.model.channels.ChannelDetailsResource;
import it.pagopa.selfcare.pagopa.backoffice.model.channels.ChannelPspListResource;
import it.pagopa.selfcare.pagopa.backoffice.model.channels.PspChannelPaymentTypesResource;
import it.pagopa.selfcare.pagopa.backoffice.model.channels.WrapperChannelDetailsDto;
import it.pagopa.selfcare.pagopa.backoffice.model.channels.WrapperChannelsResource;
import it.pagopa.selfcare.pagopa.backoffice.model.connector.channel.ChannelDetails;
import it.pagopa.selfcare.pagopa.backoffice.model.connector.channel.ChannelPspList;
import it.pagopa.selfcare.pagopa.backoffice.model.connector.channel.Channels;
import it.pagopa.selfcare.pagopa.backoffice.model.connector.channel.PspChannelPaymentTypes;
import it.pagopa.selfcare.pagopa.backoffice.model.connector.channel.WrapperChannelList;
import it.pagopa.selfcare.pagopa.backoffice.model.connector.wrapper.ConfigurationStatus;
import it.pagopa.selfcare.pagopa.backoffice.model.connector.wrapper.WrapperChannel;
import it.pagopa.selfcare.pagopa.backoffice.model.connector.wrapper.WrapperChannels;
import it.pagopa.selfcare.pagopa.backoffice.model.connector.wrapper.WrapperStatus;
import it.pagopa.selfcare.pagopa.backoffice.model.email.EmailMessageDetail;
import it.pagopa.selfcare.pagopa.backoffice.model.institutions.SelfcareProductUser;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;

import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static it.pagopa.selfcare.pagopa.backoffice.service.WrapperService.getChannelWrapperEntityOperationsSortedList;

@Slf4j
@Service
public class ChannelService {

    private static final String CREATE_CHANEL_SUBJECT = "Nuovo canale attivo";
    private static final String CREATE_CHANEL_EMAIL_BODY = "Ciao, %n%n%n pagoPA ha revisionato e validato il canale %s che hai creato. Da questo momento puoi utilizzarlo per attivare i tuoi servizi.%n%n%nA presto,%n%n Back-office pagoPA";
    private static final String UPDATE_CHANEL_SUBJECT = "Modifica canale attiva";
    private static final String UPDATE_CHANEL_EMAIL_BODY = "Ciao, %n%n%n pagoPA ha revisionato e validato il canale %s che hai modificato. Da questo momento la modifica effettuata risulta attiva.%n%n%nA presto,%n%n Back-office pagoPA";
    private static final String CHANNEL_REVIEW_SUBJECT = "Modifiche richieste";
    private static final String CHANNEL_REVIEW_EMAIL_BODY = "Ciao, %n%n%n pagoPA ha richiesto delle modifiche al canale %s che hai creato.%n Puoi vedere le modifiche qui sotto oppure nel dettaglio del canale (https://selfcare%s.platform.pagopa.it/ui/channels/%s).%n Modifiche richieste %n '%s' %n%n%nA presto,%n%n Pagamenti pagoPA";

    private final String environment;

    private final ApiConfigClient apiConfigClient;

    private final WrapperService wrapperService;

    private final JiraServiceManagerClient jsmClient;

    private final AwsSesClient awsSesClient;

    @Autowired
    public ChannelService(
            @Value("${info.properties.environment}") String environment,
            ApiConfigClient apiConfigClient,
            WrapperService wrapperService,
            JiraServiceManagerClient jsmClient,
            AwsSesClient awsSesClient
    ) {
        this.environment = environment;
        this.apiConfigClient = apiConfigClient;
        this.wrapperService = wrapperService;
        this.jsmClient = jsmClient;
        this.awsSesClient = awsSesClient;
    }

    /**
     * Creates a new wrapper channel in status {@link WrapperStatus#TO_CHECK} and open a JIRA ticket for operator
     * review
     *
     * @param wrapperChannelDetailsDto detail of the new channel
     * @return the created wrapper channel
     */
    public WrapperEntities<ChannelDetails> createChannelToBeValidated(WrapperChannelDetailsDto wrapperChannelDetailsDto) {
        final String CREATE_CHANNEL_SUMMARY = "Validazione canale creazione: %s";
        final String CREATE_CHANEL_DESCRIPTION = "Il canale %s deve essere validato: %s";

        WrapperEntities<ChannelDetails> createdWrapperChannel =
                this.wrapperService.createWrapperChannel(
                        ChannelMapper.fromWrapperChannelDetailsDto(wrapperChannelDetailsDto),
                        WrapperStatus.TO_CHECK
                );
        this.jsmClient.createTicket(
                String.format(CREATE_CHANNEL_SUMMARY, wrapperChannelDetailsDto.getChannelCode()),
                String.format(CREATE_CHANEL_DESCRIPTION, wrapperChannelDetailsDto.getChannelCode(), wrapperChannelDetailsDto.getValidationUrl())
        );
        return createdWrapperChannel;
    }

    /**
     * Updated the wrapper channel with the provided code in status {@link WrapperStatus#TO_CHECK_UPDATE} and open a JIRA ticket
     * for operator review
     *
     * @param channelCode       the code of the channel to be updated
     * @param channelDetailsDto detail of the channel
     * @return the updated wrapper channel
     */
    public ChannelDetailsResource updateChannelToBeValidated(String channelCode, ChannelDetailsDto channelDetailsDto) {
        final String CREATE_CHANNEL_SUMMARY = "Validazione modifica canale: %s";
        final String CREATE_CHANEL_DESCRIPTION = "Il canale %s modificato dal broker %s deve essere validato: %s";

        WrapperEntityChannels updatedWrapperChannel =
                this.wrapperService.updateWrapperChannel(
                        channelCode,
                        ChannelMapper.fromChannelDetailsDto(channelDetailsDto)
                );
        this.jsmClient.createTicket(
                String.format(CREATE_CHANNEL_SUMMARY, channelCode),
                String.format(CREATE_CHANEL_DESCRIPTION, channelCode, channelDetailsDto.getBrokerPspCode(), channelDetailsDto.getValidationUrl())
        );
        return ChannelMapper.toResource(updatedWrapperChannel);
    }

    /**
     * Creates a validated channel and update the relative wrapper channel with status {@link WrapperStatus#APPROVED}.
     * Notify the channel owner via email.
     *
     * @param channelDetailsDto the channel details
     * @return the created channel
     */
    public ChannelDetailsResource validateChannelCreation(ChannelDetailsDto channelDetailsDto) {
        PspChannelPaymentTypes pspChannelPaymentTypes = new PspChannelPaymentTypes();
        List<String> paymentTypeList = channelDetailsDto.getPaymentTypeList();
        String channelCode = channelDetailsDto.getChannelCode();
        pspChannelPaymentTypes.setPaymentTypeList(paymentTypeList);

        ChannelDetails channelDetails = ChannelMapper.fromChannelDetailsDto(channelDetailsDto);
        this.apiConfigClient.createChannel(channelDetails);

        WrapperEntityChannels response = this.wrapperService.updateValidatedWrapperChannel(channelDetails, WrapperStatus.APPROVED);
        PspChannelPaymentTypes paymentType = this.apiConfigClient.createChannelPaymentType(pspChannelPaymentTypes, channelCode);
        ChannelDetailsResource resource =
                ChannelMapper.toResource(getChannelWrapperEntityOperationsSortedList(response).get(0).getEntity(), paymentType);

        EmailMessageDetail messageDetail = EmailMessageDetail.builder()
                .institutionTaxCode(channelDetailsDto.getBrokerPspCode())
                .subject(CREATE_CHANEL_SUBJECT)
                .textBody(String.format(CREATE_CHANEL_EMAIL_BODY, channelCode))
                .htmlBodyFileName("channelCreationValidatedEmail.html")
                .htmlBodyContext(buildChannelHtmlEmailBodyContext(channelCode, null))
                .destinationUserType(SelfcareProductUser.OPERATOR)
                .build();

        this.awsSesClient.sendEmail(messageDetail);
        return resource;
    }

    /**
     * Updates a validated channel and update the relative wrapper channel with status {@link WrapperStatus#APPROVED}.
     * Notify the channel owner via email.
     *
     * @param channelCode channel's code
     * @param channelDetailsDto the channel details
     * @return the updated channel
     */
    public ChannelDetailsResource validateChannelUpdate(String channelCode, ChannelDetailsDto channelDetailsDto) {
        ChannelDetails channelDetails = ChannelMapper.fromChannelDetailsDto(channelDetailsDto);
        ChannelDetails response = this.apiConfigClient.updateChannel(channelDetails, channelCode);
        this.wrapperService.update(channelDetails, channelDetailsDto.getNote(), WrapperStatus.APPROVED.name(), null);
        PspChannelPaymentTypes paymentTypes = PspChannelPaymentTypes.builder()
                .paymentTypeList(response.getPaymentTypeList())
                .build();
        ChannelDetailsResource resource = ChannelMapper.toResource(response, paymentTypes);

        EmailMessageDetail messageDetail = EmailMessageDetail.builder()
                .institutionTaxCode(channelDetailsDto.getBrokerPspCode())
                .subject(UPDATE_CHANEL_SUBJECT)
                .textBody(String.format(UPDATE_CHANEL_EMAIL_BODY, channelCode))
                .htmlBodyFileName("channelUpdateValidatedEmail.html")
                .htmlBodyContext(buildChannelHtmlEmailBodyContext(channelCode, null))
                .destinationUserType(SelfcareProductUser.OPERATOR)
                .build();

        this.awsSesClient.sendEmail(messageDetail);
        return resource;
    }


    /**
     * Retrieve a paginated list of channels from api-config if the provided status is {@link ConfigurationStatus#ACTIVE},
     * from wrapper otherwise. The result is filter out by channel's code and broker's code.
     *
     * @param status      channel's status
     * @param channelCode channel's code
     * @param brokerCode  broker's code
     * @param limit       page size
     * @param page        page number
     * @return the paginated list
     */
    public WrapperChannelsResource getChannels(
            ConfigurationStatus status,
            String channelCode,
            String brokerCode,
            Integer limit,
            Integer page
    ) {
        WrapperChannels response;
        if (status.equals(ConfigurationStatus.ACTIVE)) {
            Channels channels = this.apiConfigClient.getChannels(channelCode, brokerCode, "DESC", limit, page);
            response = buildEnrichedWrapperChannels(channels);
        } else {
            WrapperChannelList wrapperChannels = this.wrapperService.getWrapperChannels(channelCode, brokerCode, limit, page);
            response = ChannelMapper.toWrapperChannels(wrapperChannels);
        }

        return ChannelMapper.toWrapperChannelsResource(response);
    }

    /**
     * Retrieve the channel details from api-config if the provided status is {@link ConfigurationStatus#ACTIVE},
     * from wrapper otherwise. If the provided status is {@link ConfigurationStatus#ACTIVE} set the pending update flag
     * to false if the most recent wrapper status is {@link WrapperStatus#APPROVED}, true otherwise.
     *
     * @param channelCode channel's code
     * @param status channel's status
     * @return the detail of the channel
     */
    public ChannelDetailsResource getChannelDetails(String channelCode, ConfigurationStatus status) {
        ChannelDetailsResource channelDetailsResource;
        if (ConfigurationStatus.ACTIVE.equals(status)) {
            ChannelDetails channelDetails = this.apiConfigClient.getChannelDetails(channelCode);
            PspChannelPaymentTypes paymentTypes = this.apiConfigClient.getChannelPaymentTypes(channelCode);
            channelDetailsResource = buildActiveChannelDetails(channelCode, channelDetails, paymentTypes);
        } else {
            channelDetailsResource = findInWrapperOrElseInApiConfig(channelCode);
        }
        return channelDetailsResource;
    }

    public PspChannelPaymentTypesResource getPaymentTypesByChannel(String channelCode) {
        PspChannelPaymentTypes dto = apiConfigClient.getChannelPaymentTypes(channelCode);
        return ChannelMapper.toResource(dto);
    }

    public PspChannelPaymentTypesResource createPaymentTypeOnChannel(
            PspChannelPaymentTypes pspChannelPaymentTypes,
            String channelCode
    ) {
        PspChannelPaymentTypes dto = apiConfigClient.createChannelPaymentType(pspChannelPaymentTypes, channelCode);
        return ChannelMapper.toResource(dto);
    }

    public void deletePaymentTypeOnChannel(String channelCode, String paymentTypeCode) {
        apiConfigClient.deleteChannelPaymentType(channelCode, paymentTypeCode);
    }

    public void deleteChannel(String channelCode) {
        apiConfigClient.deleteChannel(channelCode);
    }

    public Resource getChannelsInCSVFile(HttpServletResponse response) {
        response.setContentType("text/csv");
        response.setHeader(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"channels.csv\"");
        return apiConfigClient.getChannelsCSV();
    }

    public ChannelPspListResource getPSPsByChannel(Integer limit, Integer page, String channelCode, String pspName) {
        ChannelPspList dto = apiConfigClient.getChannelPaymentServiceProviders(channelCode, limit, page, pspName);
        return ChannelMapper.toResource(dto);
    }

    /**
     * Update the wrapper channel with the operator review's note and notify the channel owner via email.
     *
     * @param channelCode   channel's code
     * @param brokerPspCode payment service provider's tax code that own the channel
     * @param note          operator review note
     * @return the updated channel wrapper
     */
    public ChannelDetailsResource updateWrapperChannelWithOperatorReview(
            String channelCode,
            String brokerPspCode,
            String note
    ) {
        WrapperEntityChannels updatedWrapper =
                this.wrapperService.updateChannelWithOperatorReview(channelCode, note);

        EmailMessageDetail messageDetail = EmailMessageDetail.builder()
                .institutionTaxCode(brokerPspCode)
                .subject(CHANNEL_REVIEW_SUBJECT)
                .textBody(String.format(CHANNEL_REVIEW_EMAIL_BODY, channelCode, getEnvParam(), channelCode, note))
                .htmlBodyFileName("channelReviewRequestedEmail.html")
                .htmlBodyContext(buildChannelHtmlEmailBodyContext(channelCode, note))
                .destinationUserType(SelfcareProductUser.OPERATOR)
                .build();
        this.awsSesClient.sendEmail(messageDetail);

        return ChannelMapper.toResource(updatedWrapper);
    }

    private Context buildChannelHtmlEmailBodyContext(String channelCode, String note) {
        // Thymeleaf Context
        Context context = new Context();

        // Properties to show up in Template after stored in Context
        Map<String, Object> properties = new HashMap<>();
        properties.put("channelCode", channelCode);
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

    private WrapperChannels buildEnrichedWrapperChannels(Channels channels) {
        WrapperChannels response;
        List<WrapperChannel> wrapperChannels = channels.getChannelList().parallelStream()
                .map(channel -> {
                    WrapperChannel wrapperChannel = ChannelMapper.toWrapperChannel(channel);
                    Optional<WrapperEntityChannels> optionalWrapperEntities =
                            this.wrapperService.findChannelByIdOptional(channel.getChannelCode());
                    if (optionalWrapperEntities.isPresent()) {
                        WrapperEntityChannels wrapperEntities = optionalWrapperEntities.get();
                        wrapperChannel.setCreatedAt(wrapperEntities.getCreatedAt());
                        wrapperChannel.setPrimitiveVersion(channel.getPrimitiveVersion());
                    }
                    return wrapperChannel;
                }).toList();
        response = WrapperChannels.builder()
                .channelList(wrapperChannels)
                .pageInfo(channels.getPageInfo())
                .build();
        return response;
    }

    private ChannelDetailsResource buildActiveChannelDetails(
            String channelCode,
            ChannelDetails channelDetails,
            PspChannelPaymentTypes paymentTypes
    ) {
        ChannelDetailsResource channelDetailsResource = ChannelMapper.toResource(channelDetails, paymentTypes);
        channelDetailsResource.setWrapperStatus(WrapperStatus.APPROVED);

        Optional<WrapperEntityChannels> optionalWrapperEntities = this.wrapperService.findChannelByIdOptional(channelCode);
        if (optionalWrapperEntities.isPresent()) {
            WrapperEntityChannels wrapperEntities = optionalWrapperEntities.get();
            channelDetailsResource.setCreatedAt(wrapperEntities.getCreatedAt());

            WrapperEntityChannel mostRecentEntity = getChannelWrapperEntityOperationsSortedList(wrapperEntities).get(0);
            channelDetailsResource.setPendingUpdate(!WrapperStatus.APPROVED.equals(mostRecentEntity.getStatus()));
        } else {
            channelDetailsResource.setPendingUpdate(false);
        }
        return channelDetailsResource;
    }

    private ChannelDetailsResource findInWrapperOrElseInApiConfig(String channelCode) {
        // handle legacy channels
        try {
            WrapperEntityChannels wrapperChannel = this.wrapperService.findChannelById(channelCode);
            return ChannelMapper.toResource(wrapperChannel);
        } catch (AppException e) {
            ChannelDetails channelDetails = this.apiConfigClient.getChannelDetails(channelCode);
            PspChannelPaymentTypes paymentTypes = this.apiConfigClient.getChannelPaymentTypes(channelCode);
            return buildActiveChannelDetails(channelCode, channelDetails, paymentTypes);
        }
    }
}
