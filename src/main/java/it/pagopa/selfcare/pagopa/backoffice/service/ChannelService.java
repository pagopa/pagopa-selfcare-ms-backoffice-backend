package it.pagopa.selfcare.pagopa.backoffice.service;

import it.pagopa.selfcare.pagopa.backoffice.client.ApiConfigClient;
import it.pagopa.selfcare.pagopa.backoffice.client.AwsSesClient;
import it.pagopa.selfcare.pagopa.backoffice.client.JiraServiceManagerClient;
import it.pagopa.selfcare.pagopa.backoffice.entity.WrapperEntities;
import it.pagopa.selfcare.pagopa.backoffice.exception.AppException;
import it.pagopa.selfcare.pagopa.backoffice.mapper.ChannelMapper;
import it.pagopa.selfcare.pagopa.backoffice.model.channels.ChannelDetailsDto;
import it.pagopa.selfcare.pagopa.backoffice.model.channels.ChannelDetailsResource;
import it.pagopa.selfcare.pagopa.backoffice.model.channels.ChannelPspListResource;
import it.pagopa.selfcare.pagopa.backoffice.model.channels.ChannelsResource;
import it.pagopa.selfcare.pagopa.backoffice.model.channels.PspChannelPaymentTypesResource;
import it.pagopa.selfcare.pagopa.backoffice.model.channels.WrapperChannelDetailsDto;
import it.pagopa.selfcare.pagopa.backoffice.model.channels.WrapperChannelDetailsResource;
import it.pagopa.selfcare.pagopa.backoffice.model.channels.WrapperChannelsResource;
import it.pagopa.selfcare.pagopa.backoffice.model.connector.channel.ChannelDetails;
import it.pagopa.selfcare.pagopa.backoffice.model.connector.channel.ChannelPspList;
import it.pagopa.selfcare.pagopa.backoffice.model.connector.channel.Channels;
import it.pagopa.selfcare.pagopa.backoffice.model.connector.channel.PspChannelPaymentTypes;
import it.pagopa.selfcare.pagopa.backoffice.model.connector.channel.WrapperEntitiesList;
import it.pagopa.selfcare.pagopa.backoffice.model.connector.wrapper.WrapperChannels;
import it.pagopa.selfcare.pagopa.backoffice.model.connector.wrapper.WrapperStatus;
import it.pagopa.selfcare.pagopa.backoffice.model.connector.wrapper.WrapperType;
import it.pagopa.selfcare.pagopa.backoffice.model.email.EmailMessageDetail;
import it.pagopa.selfcare.pagopa.backoffice.model.institutions.SelfcareProductUser;
import it.pagopa.selfcare.pagopa.backoffice.util.Utility;
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

import static it.pagopa.selfcare.pagopa.backoffice.service.WrapperService.getWrapperEntityOperationsSortedList;

@Slf4j
@Service
public class ChannelService {

    private static final String CREATE_CHANEL_SUBJECT = "Nuovo canale attivo";
    private static final String CREATE_CHANEL_EMAIL_BODY = "Ciao, %n%n%n pagoPA ha revisionato e validato il canale %s che hai creato. Da questo momento puoi utilizzarlo per attivare i tuoi servizi.%n%n%nA presto,%n%n Back-office pagoPA";
    private static final String UPDATE_CHANEL_SUBJECT = "Modifica canale attiva";
    private static final String UPDATE_CHANEL_EMAIL_BODY = "Ciao, %n%n%n pagoPA ha revisionato e validato il canale %s che hai modificato. Da questo momento la modifica effettuata risulta attiva.%n%n%nA presto,%n%n Back-office pagoPA";

    private final ApiConfigClient apiConfigClient;

    private final WrapperService wrapperService;

    private final JiraServiceManagerClient jsmClient;

    private final AwsSesClient awsSesClient;

    @Autowired
    public ChannelService(ApiConfigClient apiConfigClient, WrapperService wrapperService, JiraServiceManagerClient jsmClient, AwsSesClient awsSesClient) {
        this.apiConfigClient = apiConfigClient;
        this.wrapperService = wrapperService;
        this.jsmClient = jsmClient;
        this.awsSesClient = awsSesClient;
    }

    public WrapperChannelsResource getAllMergedChannel(Integer limit, String channelcode, String brokerCode, Integer page, String sorting) {
        Channels channels = apiConfigClient.getChannels(limit, page, channelcode, brokerCode, sorting);
        WrapperEntitiesList mongoList = wrapperService.findByIdLikeOrTypeOrBrokerCode(channelcode, WrapperType.CHANNEL, brokerCode, page, limit);
        WrapperChannels channelsMergedAndSorted = Utility.mergeAndSortWrapperChannels(ChannelMapper.toWrapperChannels(channels), ChannelMapper.toWrapperChannels(mongoList), sorting);
        return ChannelMapper.toWrapperChannelsResource(channelsMergedAndSorted);
    }

    public WrapperEntities createChannelToBeValidated(WrapperChannelDetailsDto wrapperChannelDetailsDto) {
        final String CREATE_CHANNEL_SUMMARY = "Validazione canale creazione: %s";
        final String CREATE_CHANEL_DESCRIPTION = "Il canale %s deve essere validato: %s";
        WrapperEntities createdWrapperEntities = wrapperService.insert(
                ChannelMapper.fromWrapperChannelDetailsDto(wrapperChannelDetailsDto),
                wrapperChannelDetailsDto.getNote(),
                wrapperChannelDetailsDto.getStatus().name());
        jsmClient.createTicket(
                String.format(
                        CREATE_CHANNEL_SUMMARY,
                        wrapperChannelDetailsDto.getChannelCode()),
                String.format(
                        CREATE_CHANEL_DESCRIPTION,
                        wrapperChannelDetailsDto.getChannelCode(),
                        wrapperChannelDetailsDto.getValidationUrl()));
        return createdWrapperEntities;
    }

    public WrapperEntities updateChannelToBeValidated(ChannelDetailsDto channelDetailsDto) {
        final String CREATE_CHANNEL_SUMMARY = "Validazione modifica canale: %s";
        final String CREATE_CHANEL_DESCRIPTION = "Il canale %s modificato dal broker %s deve essere validato: %s";
        WrapperEntities createdWrapperEntities = wrapperService.update(ChannelMapper.fromChannelDetailsDto(channelDetailsDto), channelDetailsDto.getNote(), channelDetailsDto.getStatus().name(), null);
        jsmClient.createTicket(
                String.format(
                        CREATE_CHANNEL_SUMMARY,
                        channelDetailsDto.getChannelCode()),
                String.format(
                        CREATE_CHANEL_DESCRIPTION,
                        channelDetailsDto.getChannelCode(),
                        channelDetailsDto.getBrokerPspCode(),
                        channelDetailsDto.getValidationUrl()));
        return createdWrapperEntities;
    }

    public WrapperChannelDetailsResource validateChannelCreation(ChannelDetailsDto channelDetailsDto) {
        PspChannelPaymentTypes pspChannelPaymentTypes = new PspChannelPaymentTypes();
        List<String> paymentTypeList = channelDetailsDto.getPaymentTypeList();
        String channelCode = channelDetailsDto.getChannelCode();
        pspChannelPaymentTypes.setPaymentTypeList(paymentTypeList);

        ChannelDetails channelDetails = ChannelMapper.fromChannelDetailsDto(channelDetailsDto);
        this.apiConfigClient.createChannel(channelDetails);

        WrapperEntities<ChannelDetails> response = this.wrapperService
                .updateByOpt(channelDetails, channelDetailsDto.getNote(), channelDetailsDto.getStatus().name());
        PspChannelPaymentTypes paymentType = this.apiConfigClient.createChannelPaymentType(pspChannelPaymentTypes, channelCode);
        WrapperChannelDetailsResource resource = ChannelMapper.toResource(getWrapperEntityOperationsSortedList(response).get(0), paymentType);

        EmailMessageDetail messageDetail = EmailMessageDetail.builder()
                .institutionTaxCode(channelDetailsDto.getBrokerPspCode())
                .subject(CREATE_CHANEL_SUBJECT)
                .textBody(String.format(CREATE_CHANEL_EMAIL_BODY, channelCode))
                .htmlBodyFileName("channelCreationValidatedEmail.html")
                .htmlBodyContext(buildChannelHtmlEmailBodyContext(channelCode))
                .destinationUserType(SelfcareProductUser.OPERATOR)
                .build();

        this.awsSesClient.sendEmail(messageDetail);
        return resource;
    }


    public ChannelDetailsResource validateChannelUpdate(String channelCode, ChannelDetailsDto channelDetailsDto) {
        ChannelDetails channelDetails = ChannelMapper.fromChannelDetailsDto(channelDetailsDto);
        ChannelDetails response = this.apiConfigClient.updateChannel(channelDetails, channelCode);
        this.wrapperService.update(channelDetails, channelDetailsDto.getNote(), channelDetailsDto.getStatus().name(), null);
        ChannelDetailsResource resource = ChannelMapper.toResource(response, null);

        EmailMessageDetail messageDetail = EmailMessageDetail.builder()
                .institutionTaxCode(channelDetailsDto.getBrokerPspCode())
                .subject(UPDATE_CHANEL_SUBJECT)
                .textBody(String.format(UPDATE_CHANEL_EMAIL_BODY, channelCode))
                .htmlBodyFileName("channelUpdateValidatedEmail.html")
                .htmlBodyContext(buildChannelHtmlEmailBodyContext(channelCode))
                .destinationUserType(SelfcareProductUser.OPERATOR)
                .build();

        this.awsSesClient.sendEmail(messageDetail);
        return resource;
    }

    public ChannelDetailsResource getChannelToBeValidated(String channelcode) {
        ChannelDetails channelDetail;
        WrapperStatus status;
        String createdBy = "";
        String modifiedBy = "";
        PspChannelPaymentTypes ptResponse = new PspChannelPaymentTypes();
        try {
            WrapperEntities<ChannelDetails> result = wrapperService.findById(channelcode);
            createdBy = result.getCreatedBy();
            modifiedBy = result.getModifiedBy();
            channelDetail = (ChannelDetails) getWrapperEntityOperationsSortedList(result).get(0).getEntity();
            status = result.getStatus();
            ptResponse.setPaymentTypeList(channelDetail.getPaymentTypeList());
        } catch (AppException e) {
            channelDetail = apiConfigClient.getChannelDetails(channelcode);
            ptResponse = apiConfigClient.getChannelPaymentTypes(channelcode);
            status = WrapperStatus.APPROVED;
        }
        return ChannelMapper.toResource(channelDetail, ptResponse, status, createdBy, modifiedBy);
    }


    public ChannelsResource getChannels(Integer limit, Integer page, String code, String sort) {
        Channels dto = apiConfigClient.getChannels(limit, page, code, null, sort);
        return ChannelMapper.toResource(dto);
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

    public PspChannelPaymentTypesResource createPaymentTypeOnChannel(PspChannelPaymentTypes pspChannelPaymentTypes, String channelCode) {
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

    private Context buildChannelHtmlEmailBodyContext(String channelCode) {
        // Thymeleaf Context
        Context context = new Context();

        // Properties to show up in Template after stored in Context
        Map<String, Object> properties = new HashMap<>();
        properties.put("channelCode", channelCode);

        context.setVariables(properties);
        return context;
    }
}
