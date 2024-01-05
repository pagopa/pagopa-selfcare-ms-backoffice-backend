package it.pagopa.selfcare.pagopa.backoffice.service;

import it.pagopa.selfcare.pagopa.backoffice.client.ApiConfigClient;
import it.pagopa.selfcare.pagopa.backoffice.client.AwsSesClient;
import it.pagopa.selfcare.pagopa.backoffice.client.JiraServiceManagerClient;
import it.pagopa.selfcare.pagopa.backoffice.entity.WrapperEntities;
import it.pagopa.selfcare.pagopa.backoffice.exception.AppException;
import it.pagopa.selfcare.pagopa.backoffice.mapper.ChannelMapper;
import it.pagopa.selfcare.pagopa.backoffice.model.channels.*;
import it.pagopa.selfcare.pagopa.backoffice.model.connector.channel.*;
import it.pagopa.selfcare.pagopa.backoffice.model.connector.wrapper.WrapperChannels;
import it.pagopa.selfcare.pagopa.backoffice.model.connector.wrapper.WrapperStatus;
import it.pagopa.selfcare.pagopa.backoffice.model.connector.wrapper.WrapperType;
import it.pagopa.selfcare.pagopa.backoffice.util.Utility;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

import static it.pagopa.selfcare.pagopa.backoffice.service.WrapperService.getWrapperEntityOperationsSortedList;

@Slf4j
@Service
public class ChannelService {

    @Autowired
    private ApiConfigClient apiConfigClient;

    @Autowired
    private WrapperService wrapperService;

    @Autowired
    private JiraServiceManagerClient jsmClient;

    @Autowired
    private AwsSesClient awsSesClient;

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
        final String CREATE_CHANEL_SUBJECT = "Creazione Canale";
        final String CREATE_CHANEL_EMAIL_BODY = String.format("Buongiorno %n%n Il canale %s è stato validato da un operatore e risulta essere attivo%n%nSaluti", channelDetailsDto.getChannelCode());

        PspChannelPaymentTypes pspChannelPaymentTypes = new PspChannelPaymentTypes();
        List<String> paymentTypeList = channelDetailsDto.getPaymentTypeList();
        String channelCode = channelDetailsDto.getChannelCode();
        pspChannelPaymentTypes.setPaymentTypeList(paymentTypeList);

        ChannelDetails channelDetails = ChannelMapper.fromChannelDetailsDto(channelDetailsDto);
        apiConfigClient.createChannel(channelDetails);

        WrapperEntities<ChannelDetails> response = wrapperService.updateByOpt(channelDetails, channelDetailsDto.getNote(), channelDetailsDto.getStatus().name());
        PspChannelPaymentTypes paymentType = apiConfigClient.createChannelPaymentType(pspChannelPaymentTypes, channelCode);
        WrapperChannelDetailsResource resource = ChannelMapper.toResource(getWrapperEntityOperationsSortedList(response).get(0), paymentType);

        awsSesClient.sendEmail(CREATE_CHANEL_SUBJECT, CREATE_CHANEL_EMAIL_BODY, channelDetailsDto.getEmail());
        return resource;
    }


    public ChannelDetailsResource validateChannelUpdate(String channelCode, ChannelDetailsDto channelDetailsDto) {
        final String UPDATE_CHANEL_SUBJECT = "Update Canale";
        final String UPDATE_CHANEL_EMAIL_BODY = String.format("Buongiorno%n%n la modifica per Il canale %s è stata validata da un operatore e risulta essere attiva%n%nSaluti", channelDetailsDto.getChannelCode());

        ChannelDetails channelDetails = ChannelMapper.fromChannelDetailsDto(channelDetailsDto);
        ChannelDetails response = apiConfigClient.updateChannel(channelDetails, channelCode);
        wrapperService.update(channelDetails, channelDetailsDto.getNote(), channelDetailsDto.getStatus().name(), null);
        ChannelDetailsResource resource = ChannelMapper.toResource(response, null);
        awsSesClient.sendEmail(UPDATE_CHANEL_SUBJECT, UPDATE_CHANEL_EMAIL_BODY, channelDetailsDto.getEmail());
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

    public ChannelPspListResource getPSPsByChannel(Integer limit, Integer page, String channelCode) {
        ChannelPspList dto = apiConfigClient.getChannelPaymentServiceProviders(limit, page, channelCode);
        return ChannelMapper.toResource(dto);
    }

}
