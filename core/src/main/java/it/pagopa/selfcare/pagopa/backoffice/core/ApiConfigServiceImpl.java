package it.pagopa.selfcare.pagopa.backoffice.core;

import it.pagopa.selfcare.pagopa.backoffice.connector.api.ApiConfigConnector;
import it.pagopa.selfcare.pagopa.backoffice.connector.model.channel.*;
import it.pagopa.selfcare.pagopa.backoffice.connector.model.station.StationDetails;
import it.pagopa.selfcare.pagopa.backoffice.connector.model.station.Stations;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Slf4j
@Service
public class ApiConfigServiceImpl implements ApiConfigService {

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
    public Stations getStations(Integer limit, Integer page, String sort, String ecCode, String stationCode, String xRequestId) {
        log.trace("getStations start");
        log.debug("getStations ecCode = {}, stationCode = {}, xRequestId = {}", ecCode, stationCode, xRequestId);
        Stations response = apiConfigConnector.getStations(limit, page, sort, null, ecCode, stationCode, xRequestId);
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
        Pattern pattern = Pattern.compile("^(.*?)(_([0-9]+))$"); // String_nn
        List<String> codes = codeList.stream().map(pspChannel -> pspChannel.getChannelCode())
                .filter(s -> s.matches("^\\w+_\\d+$")) // String_nn
                .collect(Collectors.toList());
        String newChannelCode = pspCode.concat("_").concat("01");
        if (codes.isEmpty()) return newChannelCode;
        Comparator<String> comparator = Comparator.comparingInt(s -> Integer.parseInt(s.split("_")[1]));
        Collections.sort(codes, comparator.reversed());
        String code = codes.get(0);

            Matcher matcher = pattern.matcher(code);
            if (matcher.matches()) {
                String prefix = matcher.group(1);
                String numberStr = matcher.group(3);
                int number =  Integer.parseInt(numberStr) ;
                number++;
                newChannelCode = prefix +  String.format("_%0" + numberStr.length() + "d", number);
        }
        log.debug("generateChannelCode result = {}", newChannelCode);
        return newChannelCode;
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

}
