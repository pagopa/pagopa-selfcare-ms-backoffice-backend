package it.pagopa.selfcare.pagopa.backoffice.service;

import it.pagopa.selfcare.pagopa.backoffice.client.ApiConfigClient;
import it.pagopa.selfcare.pagopa.backoffice.client.AwsSesClient;
import it.pagopa.selfcare.pagopa.backoffice.client.JiraServiceManagerClient;
import it.pagopa.selfcare.pagopa.backoffice.entity.WrapperEntities;
import it.pagopa.selfcare.pagopa.backoffice.entity.WrapperEntityChannel;
import it.pagopa.selfcare.pagopa.backoffice.entity.WrapperEntityChannels;
import it.pagopa.selfcare.pagopa.backoffice.entity.WrapperEntityOperations;
import it.pagopa.selfcare.pagopa.backoffice.exception.AppException;
import it.pagopa.selfcare.pagopa.backoffice.mapper.ChannelMapper;
import it.pagopa.selfcare.pagopa.backoffice.model.channels.ChannelDetailsDto;
import it.pagopa.selfcare.pagopa.backoffice.model.channels.ChannelDetailsResource;
import it.pagopa.selfcare.pagopa.backoffice.model.channels.ChannelPspListResource;
import it.pagopa.selfcare.pagopa.backoffice.model.channels.PspChannelPaymentTypesResource;
import it.pagopa.selfcare.pagopa.backoffice.model.channels.WrapperChannelDetailsDto;
import it.pagopa.selfcare.pagopa.backoffice.model.channels.WrapperChannelDetailsResource;
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
import static it.pagopa.selfcare.pagopa.backoffice.service.WrapperService.getWrapperEntityOperationsSortedList;

@Slf4j
@Service
public class ChannelService {

    private static final String CREATE_CHANEL_SUBJECT = "Nuovo canale attivo";
    private static final String CREATE_CHANEL_EMAIL_BODY = "Ciao, %n%n%n pagoPA ha revisionato e validato il canale %s che hai creato. Da questo momento puoi utilizzarlo per attivare i tuoi servizi.%n%n%nA presto,%n%n Back-office pagoPA";
    private static final String UPDATE_CHANEL_SUBJECT = "Modifica canale attiva";
    private static final String UPDATE_CHANEL_EMAIL_BODY = "Ciao, %n%n%n pagoPA ha revisionato e validato il canale %s che hai modificato. Da questo momento la modifica effettuata risulta attiva.%n%n%nA presto,%n%n Back-office pagoPA";
    private static final String CHANNEL_REVIEW_SUBJECT = "Modifiche richieste";
    private static final String CHANNEL_REVIEW_EMAIL_BODY = "Ciao, %n%n%n pagoPA ha richiesto delle modifiche al canale %s che hai creato.%n Puoi vedere le modifiche qui sotto oppure nel dettaglio del canale (https://selfcare.platform.pagopa.it/ui/channels/%s).%n Modifiche richieste %n '%s' %n%n%nA presto,%n%n Pagamenti pagoPA";

    private final ApiConfigClient apiConfigClient;

    private final WrapperService wrapperService;

    private final JiraServiceManagerClient jsmClient;

    private final AwsSesClient awsSesClient;

    @Autowired
    public ChannelService(
            ApiConfigClient apiConfigClient,
            WrapperService wrapperService,
            JiraServiceManagerClient jsmClient,
            AwsSesClient awsSesClient
    ) {
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
     * @param channelCode the code of the channel to be updated
     * @param channelDetailsDto detail of the channel
     * @return the updated wrapper channel
     */
    public WrapperEntities<ChannelDetails> updateChannelToBeValidated(String channelCode, ChannelDetailsDto channelDetailsDto) {
        final String CREATE_CHANNEL_SUMMARY = "Validazione modifica canale: %s";
        final String CREATE_CHANEL_DESCRIPTION = "Il canale %s modificato dal broker %s deve essere validato: %s";

        WrapperEntities<ChannelDetails> updatedWrapperChannel =
                this.wrapperService.updateWrapperChannel(
                        channelCode,
                        ChannelMapper.fromChannelDetailsDto(channelDetailsDto)
                );
        this.jsmClient.createTicket(
                String.format(CREATE_CHANNEL_SUMMARY, channelCode),
                String.format(CREATE_CHANEL_DESCRIPTION, channelCode, channelDetailsDto.getBrokerPspCode(), channelDetailsDto.getValidationUrl())
        );
        return updatedWrapperChannel;
    }

    /**
     * Creates a validated channel and update the relative wrapper channel with status {@link WrapperStatus#APPROVED}.
     * Notify the channel owner via email.
     *
     * @param channelDetailsDto the channel details
     * @return the created channel
     */
    public WrapperChannelDetailsResource validateChannelCreation(ChannelDetailsDto channelDetailsDto) {
        PspChannelPaymentTypes pspChannelPaymentTypes = new PspChannelPaymentTypes();
        List<String> paymentTypeList = channelDetailsDto.getPaymentTypeList();
        String channelCode = channelDetailsDto.getChannelCode();
        pspChannelPaymentTypes.setPaymentTypeList(paymentTypeList);

        ChannelDetails channelDetails = ChannelMapper.fromChannelDetailsDto(channelDetailsDto);
        this.apiConfigClient.createChannel(channelDetails);

        WrapperEntities<ChannelDetails> response = this.wrapperService.updateValidatedWrapperChannel(channelDetails, WrapperStatus.APPROVED);
        PspChannelPaymentTypes paymentType = this.apiConfigClient.createChannelPaymentType(pspChannelPaymentTypes, channelCode);
        WrapperChannelDetailsResource resource = ChannelMapper.toResource(getWrapperEntityOperationsSortedList(response).get(0), paymentType);

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
     * @param channelDetailsDto the channel details
     * @return the updated channel
     */
    public ChannelDetailsResource validateChannelUpdate(String channelCode, ChannelDetailsDto channelDetailsDto) {
        ChannelDetails channelDetails = ChannelMapper.fromChannelDetailsDto(channelDetailsDto);
        ChannelDetails response = this.apiConfigClient.updateChannel(channelDetails, channelCode);
        this.wrapperService.update(channelDetails, channelDetailsDto.getNote(), WrapperStatus.APPROVED.name(), null);
        ChannelDetailsResource resource = ChannelMapper.toResource(response, null);

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
     * Retrieve the channel details from Wrapper and if not found from Api-Config
     *
     * @param channelCode channel's code
     * @return the channel details
     */
    public ChannelDetailsResource getChannelToBeValidated(String channelCode) {
        ChannelDetails channelDetail;
        WrapperStatus status;
        String createdBy = "";
        String modifiedBy = "";
        String note = "";
        PspChannelPaymentTypes ptResponse = new PspChannelPaymentTypes();
        try {
            WrapperEntityChannels result = this.wrapperService.findChannelById(channelCode);
            createdBy = result.getCreatedBy();
            modifiedBy = result.getModifiedBy();
            status = result.getStatus();
            WrapperEntityChannel wrapperEntity = getChannelWrapperEntityOperationsSortedList(result).get(0);
            note = wrapperEntity.getNote();
            channelDetail = wrapperEntity.getEntity();
            ptResponse.setPaymentTypeList(channelDetail.getPaymentTypeList());
        } catch (AppException e) {
            channelDetail = this.apiConfigClient.getChannelDetails(channelCode);
            ptResponse = this.apiConfigClient.getChannelPaymentTypes(channelCode);
            status = WrapperStatus.APPROVED;
        }
        return ChannelMapper.toResource(channelDetail, ptResponse, status, createdBy, modifiedBy, note);
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

    public ChannelDetailsResource getChannel(String channelCode) {
        ChannelDetails channelDetails = apiConfigClient.getChannelDetails(channelCode);
        PspChannelPaymentTypes paymentTypes = apiConfigClient.getChannelPaymentTypes(channelCode);
        return ChannelMapper.toResource(channelDetails, paymentTypes);
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
        WrapperEntities<ChannelDetails> updatedWrapper =
                this.wrapperService.updateChannelWithOperatorReview(channelCode, note);

        EmailMessageDetail messageDetail = EmailMessageDetail.builder()
                .institutionTaxCode(brokerPspCode)
                .subject(CHANNEL_REVIEW_SUBJECT)
                .textBody(String.format(CHANNEL_REVIEW_EMAIL_BODY, channelCode, channelCode, note))
                .htmlBodyFileName("channelReviewRequestedEmail.html")
                .htmlBodyContext(buildChannelHtmlEmailBodyContext(channelCode, note))
                .destinationUserType(SelfcareProductUser.OPERATOR)
                .build();
        this.awsSesClient.sendEmail(messageDetail);

        WrapperEntityOperations<ChannelDetails> entityOperations =
                getWrapperEntityOperationsSortedList(updatedWrapper).get(0);
        PspChannelPaymentTypes pspChannelPaymentTypes = new PspChannelPaymentTypes();
        List<String> paymentTypeList = entityOperations.getEntity().getPaymentTypeList();
        pspChannelPaymentTypes.setPaymentTypeList(paymentTypeList);

        return ChannelMapper.toResource(
                entityOperations.getEntity(),
                pspChannelPaymentTypes,
                updatedWrapper.getStatus(),
                updatedWrapper.getCreatedBy(),
                updatedWrapper.getModifiedBy(),
                entityOperations.getNote()
        );
    }

    private Context buildChannelHtmlEmailBodyContext(String channelCode, String note) {
        // Thymeleaf Context
        Context context = new Context();

        // Properties to show up in Template after stored in Context
        Map<String, Object> properties = new HashMap<>();
        properties.put("channelCode", channelCode);
        if (note != null) {
            properties.put("reviewNote", note);
        }

        context.setVariables(properties);
        return context;
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
                    }
                    return wrapperChannel;
                }).toList();
        response = WrapperChannels.builder()
                .channelList(wrapperChannels)
                .pageInfo(channels.getPageInfo())
                .build();
        return response;
    }
}
