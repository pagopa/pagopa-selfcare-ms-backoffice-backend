package it.pagopa.selfcare.pagopa.backoffice.core;

import it.pagopa.selfcare.pagopa.backoffice.connector.api.ApiConfigConnector;
import it.pagopa.selfcare.pagopa.backoffice.connector.model.broker.Brokers;
import it.pagopa.selfcare.pagopa.backoffice.connector.model.channel.*;
import it.pagopa.selfcare.pagopa.backoffice.connector.model.creditorInstitution.CreditorInstitutionDetails;
import it.pagopa.selfcare.pagopa.backoffice.connector.model.creditorInstitution.CreditorInstitutions;
import it.pagopa.selfcare.pagopa.backoffice.connector.model.station.*;
import it.pagopa.selfcare.pagopa.backoffice.connector.model.wrapper.WrapperChannel;
import it.pagopa.selfcare.pagopa.backoffice.connector.model.wrapper.WrapperChannels;
import it.pagopa.selfcare.pagopa.backoffice.connector.model.wrapper.WrapperStation;
import it.pagopa.selfcare.pagopa.backoffice.connector.model.wrapper.WrapperStations;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.util.*;
import java.util.stream.Collectors;

import static it.pagopa.selfcare.pagopa.backoffice.connector.utils.StringUtils.generator;

@Slf4j
@Service
public class ApiConfigServiceImpl implements ApiConfigService {

    protected static final String CREDITOR_INSTITUTION_CODE_IS_REQUIRED = "A creditor institution code is required";
    private final ApiConfigConnector apiConfigConnector;

    @Autowired
    public ApiConfigServiceImpl(ApiConfigConnector apiConfigConnector) {
        this.apiConfigConnector = apiConfigConnector;
    }

    @Override
    public Channels getChannels(Integer limit, Integer page, String code, String sort, String xRequestId) {
        log.trace("getChannels start");
        Channels channels = apiConfigConnector.getChannels(limit, page, code, sort, xRequestId);
        log.debug("getChannels result = {}", channels);
        log.trace("getChannels end");
        return channels;
    }

    @Override
    public ChannelDetails createChannel(ChannelDetails channelDetails, String xRequestId) {
        log.trace("createChannel start");
        ChannelDetails response = apiConfigConnector.createChannel(channelDetails, xRequestId);
        log.debug("createChannel result = {}", response);
        log.trace("createChannel end");
        return response;
    }

    @Override
    public ChannelDetails updateChannel(ChannelDetails channelDetails, String channelCode, String xRequestId) {
        log.trace("updateChannel start");
        ChannelDetails response = apiConfigConnector.updateChannel(channelDetails, channelCode, xRequestId);
        log.debug("updateChannel result = {}", response);
        log.trace("updateChannel end");
        return response;
    }

    @Override
    public PspChannels getPspChannels(String pspCode, String xRequestId) {
        log.trace("getPspChannels start");
        PspChannels response = apiConfigConnector.getPspChannels(pspCode, xRequestId);
        log.debug("getPspChannels result = {}", response);
        log.trace("getPspChannels end");
        return response;
    }

    @Override
    public ChannelDetails getChannelDetails(String channelCode, String xRequestId) {
        log.trace("getChannelDetails start");
        ChannelDetails response = apiConfigConnector.getChannelDetails(channelCode, xRequestId);
        log.debug("getChannelDetails result = {}", response);
        log.trace("getChannelDetails end");
        return response;
    }

    public PspChannelPaymentTypes createChannelPaymentType(PspChannelPaymentTypes pspChannelPaymentTypes, String channelCode, String xRequestId) {
        log.trace("createChannelPaymentType start");
        PspChannelPaymentTypes response = apiConfigConnector.createChannelPaymentType(pspChannelPaymentTypes, channelCode, xRequestId);
        log.debug("createChannelPaymentType result = {}", response);
        log.trace("createChannelPaymentType end");
        return response;
    }

    @Override
    public PaymentTypes getPaymentTypes(String xRequestId) {
        log.trace("getPaymentTypes start");
        PaymentTypes response = apiConfigConnector.getPaymentTypes(xRequestId);
        log.debug("getPaymentTypes result = {}", response);
        log.trace("getPaymentTypes end");
        return response;
    }

    @Override
    public void deleteChannelPaymentType(String channelCode, String paymenTtypeCode, String xRequestId) {
        log.trace("deletePaymentTypes start");
        apiConfigConnector.deleteChannelPaymentType(channelCode, paymenTtypeCode, xRequestId);
        log.trace("deletePaymentTypes end");
    }

    @Override
    public PspChannelPaymentTypes getChannelPaymentTypes(String channelCode, String xRequestId) {
        log.trace("getChannelPaymentTypes start");
        PspChannelPaymentTypes response = apiConfigConnector.getChannelPaymentTypes(channelCode, xRequestId);
        log.debug("getChannelPaymentTypes result = {}", response);
        log.trace("getChannelPaymentTypes end");
        return response;
    }

    @Override
    public void deletePaymentServiceProvidersChannels(String pspCode, String channelCode, String xRequestId) {
        log.trace("deletePaymentServiceProvidersChannels start");
        apiConfigConnector.deletePaymentServiceProvidersChannels(pspCode, channelCode, xRequestId);
        log.trace("deletePaymentServiceProvidersChannels end");
    }

    @Override
    public PspChannelPaymentTypes updatePaymentServiceProvidersChannels(String pspCode, String channelCode, PspChannelPaymentTypes pspChannelPaymentTypes, String xRequestId) {
        log.trace("updatePaymentServiceProvidersChannels start");
        PspChannelPaymentTypes response = apiConfigConnector.updatePaymentServiceProvidersChannels(pspCode, channelCode, pspChannelPaymentTypes, xRequestId);
        log.debug("updatePaymentServiceProvidersChannels result = {}", response);
        log.trace("updatePaymentServiceProvidersChannels end");
        return response;
    }

    @Override
    public void deleteChannel(String channelCode, String xRequestId) {
        log.trace("deleteChannel start");
        apiConfigConnector.deleteChannel(channelCode, xRequestId);
        log.debug("deleteChannel with channelCode = {}", channelCode);
        log.trace("deleteChannel end");
    }

    @Override
    public PaymentServiceProviders getPspBrokerPsp(Integer limit, Integer page, String brokerPspCode, String uuid) {
        log.trace("getPspBrokerPsp start");
        PaymentServiceProviders response = apiConfigConnector.getPspBrokerPsp(limit, page, brokerPspCode, uuid);
        log.debug("getPspBrokerPsp result = {}", response);
        log.trace("getPspBrokerPsp end");
        return response;
    }

    @Override
    public Stations getStations(Integer limit, Integer page, String sort, String brokerCode, String ecCode, String stationCode, String xRequestId) {
        log.trace("getStations start");
        log.debug("getStations ecCode = {}, stationCode = {}, xRequestId = {}", ecCode, stationCode, xRequestId);
        Stations response = apiConfigConnector.getStations(limit, page, sort, brokerCode, ecCode, stationCode, xRequestId);
        log.debug("getStations result = {}", response);
        log.trace("getStations end");
        return response;
    }

    @Override
    public StationDetails getStation(String stationCode, String xRequestId) {
        log.trace("getStation start");
        log.debug("getStation stationCode = {}, xRequestId = {}", stationCode, xRequestId);
        StationDetails response = apiConfigConnector.getStation(stationCode, xRequestId);
        log.debug("getStation result = {}", response);
        log.trace("getStation end");
        return response;
    }

    @Override
    public StationDetails createStation(StationDetails stationDetails, String xRequestId) {
        log.trace("createStation start");
        log.debug("createStation stationDetail = {}, xRequestId = {}", stationDetails, xRequestId);
        StationDetails response = apiConfigConnector.createStation(stationDetails, xRequestId);
        log.debug("createStation result = {}", response);
        log.trace("createStation end");
        return response;
    }

    @Override
    public String generateChannelCode(String pspCode, String xRequestId) {
        log.trace("generateChannelCode start");
        log.debug("generateChannelCode pspCode = {}", pspCode);
        PspChannels response = apiConfigConnector.getPspChannels(pspCode, xRequestId);
        List<PspChannel> codeList = response.getChannelsList();
        List<String> codes = codeList.stream().map(PspChannel::getChannelCode)
                .filter(s -> s.matches("^\\w+_\\d+$")) // String_nn
                .collect(Collectors.toList());
        String newChannelCode = generator(codes, pspCode);
        log.debug("generateChannelCode result = {}", newChannelCode);
        log.trace("generateChannelCode end");
        return newChannelCode;
    }

    @Override
    public String generateStationCode(String ecCode, String xRequestId) {
        log.trace("generateStationCode start");
        log.debug("generateStation ecCode = {}, xRequestId = {}", ecCode, xRequestId);
        List<CreditorInstitutionStation> stationsList = apiConfigConnector.getEcStations(ecCode, xRequestId).getStationsList();
        List<String> codes = stationsList.stream().map(Station::getStationCode)
                .filter(s -> s.matches("^\\w+_\\d+$"))
                .collect(Collectors.toList());
        String newStationCode = generator(codes, ecCode);
        log.debug("generateStationCode result = {}", newStationCode);
        log.trace("generateStationCode end");
        return newStationCode;
    }


    @Override
    public PaymentServiceProviderDetails getPSPDetails(String pspCode, String xRequestId) {
        log.trace("getPSPDetails start");
        log.debug("getPSPDetails pspCode = {}", pspCode);
        PaymentServiceProviderDetails response = apiConfigConnector.getPSPDetails(pspCode, xRequestId);
        log.debug("getPSPDetails result = {}", response);
        log.trace("getPSPDetails end");
        return response;
    }

    @Override
    public CreditorInstitutionStationEdit createCreditorInstitutionStationRelation(String ecCode, CreditorInstitutionStationEdit station, String xRequestId) {
        log.trace("createCreditorInstitutionStationRelation start");
        log.debug("createCreditorInstitutionStationRelation ecCode = {}, station = {}, xRequestId = {}", ecCode, station, xRequestId);
        CreditorInstitutionStationEdit result = apiConfigConnector.createCreditorInstitutionStationRelationship(ecCode, station, xRequestId);
        log.debug("createCreditorInstitutionStationRelation result = {}", result);
        log.trace("createCreditorInstitutionStationRelation end");
        return result;
    }

    @Override
    public CreditorInstitutionDetails createCreditorInstitution(CreditorInstitutionDetails dto, String xRequestId) {
        log.trace("createCreditorInstitution start");
        log.debug("createCreditorInstitution dto = {}, xRequestId = {}", dto , xRequestId);
        CreditorInstitutionDetails result = apiConfigConnector.createCreditorInstitution(dto, xRequestId);
        log.debug("createCreditorInstitution result = {}", result);
        log.trace("createCreditorInstitution end");
        return result;
    }

    @Override
    public CreditorInstitutionDetails getCreditorInstitutionDetails(String ecCode, String xRequestId) {
        log.trace("getCreditorInstitutionDetails start");
        log.debug("getCreditorInstitutionDetails ecCode = {}, xRequestId = {}", ecCode , xRequestId);
        Assert.hasText(ecCode, CREDITOR_INSTITUTION_CODE_IS_REQUIRED);
        CreditorInstitutionDetails result = apiConfigConnector.getCreditorInstitutionDetails(ecCode, xRequestId);
        log.debug("getCreditorInstitutionDetails result = {}", result);
        log.trace("getCreditorInstitutionDetails end");
        return result;
    }

    @Override
    public CreditorInstitutionDetails updateCreditorInstitutionDetails(String creditorInstitutionCode, CreditorInstitutionDetails request, String xRequestId) {
        log.trace("updateCreditorInstitutionDetails start");
        log.debug("updateCreditorInstitutionDetails creditorInstitutionCode = {}, request = {}, xRequestId = {}", creditorInstitutionCode , request, xRequestId);
        Assert.hasText(creditorInstitutionCode, CREDITOR_INSTITUTION_CODE_IS_REQUIRED);
        CreditorInstitutionDetails result = apiConfigConnector.updateCreditorInstitutionDetails(creditorInstitutionCode, request, xRequestId);
        log.debug("updateCreditorInstitutionDetails result = {}", result);
        log.trace("updateCreditorInstitutionDetails end");
        return result;
    }

    @Override
    public StationDetails updateStation(String stationCode, StationDetails stationDetails, String xRequestId) {
        log.trace("updateStation start");
        StationDetails response = apiConfigConnector.updateStation(stationCode, stationDetails, xRequestId);
        log.debug("updateStation result = {}", response);
        log.trace("updateStation end");
        return response;
    }

    @Override
    public WfespPluginConfs getWfespPlugins(String xRequestId) {
        log.trace("getWfespPlugins start");
        log.debug("getWfespPlugins  xRequestId = {}", xRequestId);
        WfespPluginConfs response = apiConfigConnector.getWfespPlugins(xRequestId);
        log.debug("getWfespPlugins result = {}", response);
        log.trace("getWfespPlugins end");
        return response;
    }

    @Override
    public Resource getChannelsCSV(String uuid) {
        log.trace("getChannelsCSV start");
        Resource response = apiConfigConnector.getChannelsCSV(uuid);
        log.debug("getChannelsCSV result = {}", response);
        log.trace("getChannelsCSV end");
        return response;
    }

    public ChannelPspList getChannelPaymentServiceProviders(Integer limit, Integer page, String channelCode, String uuid) {
        log.trace("getChannelPaymentServiceProviders start");
        ChannelPspList response = apiConfigConnector.getChannelPaymentServiceProviders(limit, page, channelCode, uuid);
        log.debug("getChannelPaymentServiceProviders result = {}", response);
        log.trace("getChannelPaymentServiceProviders end");
        return response;
    }

    @Override
    public BrokerPspDetails createBrokerPsp(BrokerPspDetails brokerPspDetails, String xRequestId) {
        log.trace("createBrokerPsp start");
        BrokerPspDetails response = apiConfigConnector.createBrokerPsp(brokerPspDetails, xRequestId);
        log.debug("createBrokerPsp result = {}", response);
        log.trace("createBrokerPsp end");
        return response;
    }

    @Override
    public PaymentServiceProviderDetails createPaymentServiceProvider(PaymentServiceProviderDetails paymentServiceProviderDetails, String xRequestId) {
        log.trace("createPaymentServiceProvider start");
        PaymentServiceProviderDetails response = apiConfigConnector.createPaymentServiceProvider(paymentServiceProviderDetails, xRequestId);
        log.debug("createPaymentServiceProvider result = {}", response);
        log.trace("createPaymentServiceProvider end");
        return response;
    }

    public WrapperStations mergeAndSortWrapperStations(WrapperStations wrapperStationsApiConfig, WrapperStations wrapperStationsMongo, String sorting) {
        log.trace("mergeAndSortWrapperStations start");

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
        result.setPageInfo(wrapperStationsApiConfig.getPageInfo());
        log.trace("mergeAndSortWrapperStations end");
        return result;
    }

    @Override
    public WrapperChannels mergeAndSortWrapperChannels(WrapperChannels wrapperChannelsApiConfig, WrapperChannels wrapperChannelsMongo, String sorting) {
        log.trace("mergeAndSortWrapperStations start");

        List<WrapperChannel> mergedList = new ArrayList<>();
        mergedList.addAll(wrapperChannelsMongo.getChannelList());
        mergedList.addAll(
                wrapperChannelsApiConfig.getChannelList().stream()
                        .filter(obj2 -> wrapperChannelsMongo.getChannelList().stream().noneMatch(obj1 -> Objects.equals(obj1.getChannelCode(), obj2.getChannelCode())))
                        .collect(Collectors.toList())
        );

        if ("asc".equalsIgnoreCase(sorting)) {
            mergedList.sort(Comparator.comparing(WrapperChannel::getChannelCode));
        } else if ("desc".equalsIgnoreCase(sorting)) {
            mergedList.sort(Comparator.comparing(WrapperChannel::getChannelCode, Comparator.reverseOrder()));
        }
        WrapperChannels result = new WrapperChannels();
        result.setChannelList(mergedList);
        result.setPageInfo(wrapperChannelsApiConfig.getPageInfo());
        log.trace("mergeAndSortWrapperStations end");
        return result;
    }

    public CreditorInstitutions getCreditorInstitutionsByStation(String stationcode, Integer limit, Integer page, String xRequestId) {

        log.trace("getCreditorInstitutionsByStation start");
        CreditorInstitutions response = apiConfigConnector.getCreditorInstitutionsByStation(stationcode, limit, page, xRequestId);
        log.debug("getCreditorInstitutionsByStation result = {}", response);
        log.trace("getCreditorInstitutionsByStation end");

        return response;
    }

}
