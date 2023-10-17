package it.pagopa.selfcare.pagopa.backoffice.core;

import it.pagopa.selfcare.pagopa.backoffice.connector.api.ApiConfigConnector;
import it.pagopa.selfcare.pagopa.backoffice.connector.model.PageInfo;
import it.pagopa.selfcare.pagopa.backoffice.connector.model.broker.BrokerDetails;
import it.pagopa.selfcare.pagopa.backoffice.connector.model.broker.Brokers;
import it.pagopa.selfcare.pagopa.backoffice.connector.model.channel.*;
import it.pagopa.selfcare.pagopa.backoffice.connector.model.creditorInstitution.CreditorInstitutionDetails;
import it.pagopa.selfcare.pagopa.backoffice.connector.model.creditorInstitution.CreditorInstitutions;
import it.pagopa.selfcare.pagopa.backoffice.connector.model.creditorInstitution.IbanCreate;
import it.pagopa.selfcare.pagopa.backoffice.connector.model.creditorInstitution.IbansEnhanced;
import it.pagopa.selfcare.pagopa.backoffice.connector.model.station.CreditorInstitutionStationEdit;
import it.pagopa.selfcare.pagopa.backoffice.connector.model.station.Station;
import it.pagopa.selfcare.pagopa.backoffice.connector.model.station.StationDetails;
import it.pagopa.selfcare.pagopa.backoffice.connector.model.station.Stations;
import it.pagopa.selfcare.pagopa.backoffice.connector.model.wrapper.WrapperChannel;
import it.pagopa.selfcare.pagopa.backoffice.connector.model.wrapper.WrapperChannels;
import it.pagopa.selfcare.pagopa.backoffice.connector.model.wrapper.WrapperStation;
import it.pagopa.selfcare.pagopa.backoffice.connector.model.wrapper.WrapperStations;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static it.pagopa.selfcare.pagopa.backoffice.connector.utils.StringUtils.generator;

@Slf4j
@Service
public class ApiConfigServiceImpl implements ApiConfigService {

    protected static final String CREDITOR_INSTITUTION_CODE_IS_REQUIRED = "A creditor institution code is required";
    private final ApiConfigConnector apiConfigConnector;

    private final String REGEX_GENERATE = "^\\w+_\\d+$";

    @Autowired
    public ApiConfigServiceImpl(ApiConfigConnector apiConfigConnector) {
        this.apiConfigConnector = apiConfigConnector;
    }

    @Override
    public BrokerDetails updateBrokerEc(String brokerCode, BrokerDetails brokerDetails) {
        return apiConfigConnector.updateBrokerEc(brokerDetails,brokerCode);
    }

    @Override
    public BrokersPsp getBrokersPsp(Integer limit, Integer page, String filterByCode, String filterByName, String orderBy, String sorting) {
        return apiConfigConnector.getBrokersPsp(limit, page, filterByCode, filterByName, orderBy, sorting);
    }

    @Override
    public BrokerPspDetails getBrokerPsp(String brokerpspcode) {
        return apiConfigConnector.getBrokerPsp(brokerpspcode);
    }

    @Override
    public Channels getChannels(Integer limit, Integer page, String code, String brokerCode, String sort) {
        return apiConfigConnector.getChannels(limit, page, code, brokerCode, sort);
    }

    @Override
    public ChannelDetails createChannel(ChannelDetails channelDetails) {
        return apiConfigConnector.createChannel(channelDetails);
    }

    @Override
    public ChannelDetails updateChannel(ChannelDetails channelDetails, String channelCode) {
        return apiConfigConnector.updateChannel(channelDetails, channelCode);
    }

    @Override
    public PspChannels getPspChannels(String pspCode) {
        return apiConfigConnector.getPspChannels(pspCode);
    }

    @Override
    public ChannelDetails getChannelDetails(String channelCode) {
        return apiConfigConnector.getChannelDetails(channelCode);
    }

    public PspChannelPaymentTypes createChannelPaymentType(PspChannelPaymentTypes pspChannelPaymentTypes, String channelCode) {
        return apiConfigConnector.createChannelPaymentType(pspChannelPaymentTypes, channelCode);
    }

    @Override
    public PaymentTypes getPaymentTypes() {
        return apiConfigConnector.getPaymentTypes();
    }

    @Override
    public void deleteChannelPaymentType(String channelCode, String paymenTtypeCode) {
        apiConfigConnector.deleteChannelPaymentType(channelCode, paymenTtypeCode);
    }

    @Override
    public PspChannelPaymentTypes getChannelPaymentTypes(String channelCode) {
        return apiConfigConnector.getChannelPaymentTypes(channelCode);
    }

    @Override
    public void deletePaymentServiceProvidersChannels(String pspCode, String channelCode) {
        apiConfigConnector.deletePaymentServiceProvidersChannels(pspCode, channelCode);
    }

    @Override
    public PspChannelPaymentTypes updatePaymentServiceProvidersChannels(String pspCode, String channelCode, PspChannelPaymentTypes pspChannelPaymentTypes) {
        return apiConfigConnector.updatePaymentServiceProvidersChannels(pspCode, channelCode, pspChannelPaymentTypes);
    }

    @Override
    public void deleteChannel(String channelCode) {
        apiConfigConnector.deleteChannel(channelCode);
    }

    @Override
    public PaymentServiceProviders getPspBrokerPsp(Integer limit, Integer page, String brokerPspCode) {
        return apiConfigConnector.getPspBrokerPsp(limit, page, brokerPspCode);
    }

    @Override
    public Stations getStations(Integer limit, Integer page, String sort, String brokerCode, String ecCode, String stationCode) {
        Stations response = null;
        try {
            response = apiConfigConnector.getStations(limit, page, sort, brokerCode, ecCode, stationCode);
        } catch (Exception e) {
            if (e.getMessage().contains("[404 Not Found]")) {
                response = new Stations();
                response.setStationsList(new ArrayList<>());
                PageInfo pageInfo = new PageInfo();
                pageInfo.setPage(0);
                pageInfo.setTotalPages(0);
                pageInfo.setLimit(50);
                pageInfo.setItemsFound(0);
                response.setPageInfo(pageInfo);
            } else {
                throw e;
            }
        }
        return response;
    }

    @Override
    public StationDetails getStation(String stationCode) {
        return apiConfigConnector.getStation(stationCode);
    }

    @Override
    public StationDetails createStation(StationDetails stationDetails) {
        return apiConfigConnector.createStation(stationDetails);
    }

    @Override
    public String generateChannelCode(String pspCode) {
        Channels response = apiConfigConnector.getChannels(100, 0, pspCode, null, "ASC");
        List<Channel> codeList = response.getChannelList();
        List<String> codes = codeList.stream().map(Channel::getChannelCode)
                .filter(s -> s.matches(REGEX_GENERATE)) // String_nn
                .collect(Collectors.toList());
        return generator(codes, pspCode);
    }

    @Override
    public String generateStationCode(String ecCode) {
        Stations stations = apiConfigConnector.getStations(100, 0, "ASC", null, null, ecCode);
        List<Station> stationsList = stations.getStationsList();
        List<String> codes = stationsList.stream().map(Station::getStationCode)
                .filter(s -> s.matches(REGEX_GENERATE))
                .collect(Collectors.toList());
        return generator(codes, ecCode);
    }

    @Override
    public String generateStationCodeV2( List<WrapperStation> stationList, String ecCode) {
        List<String> codes = stationList.stream().map(WrapperStation::getStationCode)
                .filter(s -> s.matches(REGEX_GENERATE))
                .collect(Collectors.toList());
        return generator(codes, ecCode);
    }

    @Override
    public String generateChannelCodeV2( List<WrapperChannel> stationList, String ecCode) {
        List<String> codes = stationList.stream().map(WrapperChannel::getChannelCode)
                .filter(s -> s.matches(REGEX_GENERATE))
                .collect(Collectors.toList());
        return generator(codes, ecCode);
    }


    @Override
    public PaymentServiceProviderDetails getPSPDetails(String pspCode) {
        return apiConfigConnector.getPSPDetails(pspCode);
    }

    @Override
    public CreditorInstitutionStationEdit createCreditorInstitutionStationRelation(String ecCode, CreditorInstitutionStationEdit station) {
        return apiConfigConnector.createCreditorInstitutionStationRelationship(ecCode, station);
    }

    @Override
    public CreditorInstitutionDetails createCreditorInstitution(CreditorInstitutionDetails dto) {
        return apiConfigConnector.createCreditorInstitution(dto);
    }

    @Override
    public CreditorInstitutionDetails getCreditorInstitutionDetails(String ecCode) {
        Assert.hasText(ecCode, CREDITOR_INSTITUTION_CODE_IS_REQUIRED);
        return apiConfigConnector.getCreditorInstitutionDetails(ecCode);
    }

    @Override
    public CreditorInstitutions getCreditorInstitutions(Integer limit, Integer page, String ecCode, String name, String sorting) {
        Assert.hasText(ecCode, CREDITOR_INSTITUTION_CODE_IS_REQUIRED);
        return apiConfigConnector.getCreditorInstitutions(limit, page, ecCode, name, sorting);
    }

    @Override
    public CreditorInstitutionDetails updateCreditorInstitutionDetails(String creditorInstitutionCode, CreditorInstitutionDetails request) {
        Assert.hasText(creditorInstitutionCode, CREDITOR_INSTITUTION_CODE_IS_REQUIRED);
        return apiConfigConnector.updateCreditorInstitutionDetails(creditorInstitutionCode, request);
    }

    @Override
    public StationDetails updateStation(String stationCode, StationDetails stationDetails) {
        return apiConfigConnector.updateStation(stationCode, stationDetails);
    }

    @Override
    public WfespPluginConfs getWfespPlugins() {
        return apiConfigConnector.getWfespPlugins();
    }

    @Override
    public Resource getChannelsCSV() {
        return apiConfigConnector.getChannelsCSV();
    }

    public ChannelPspList getChannelPaymentServiceProviders(Integer limit, Integer page, String channelCode) {
        return apiConfigConnector.getChannelPaymentServiceProviders(limit, page, channelCode);
    }

    @Override
    public BrokerPspDetails createBrokerPsp(BrokerPspDetails brokerPspDetails) {
        return apiConfigConnector.createBrokerPsp(brokerPspDetails);
    }

    @Override
    public BrokerDetails createBroker(BrokerDetails request) {
        return apiConfigConnector.createBroker(request);
    }

    @Override
    public PaymentServiceProviderDetails createPaymentServiceProvider(PaymentServiceProviderDetails paymentServiceProviderDetails) {
        return apiConfigConnector.createPaymentServiceProvider(paymentServiceProviderDetails);
    }

    public WrapperStations mergeAndSortWrapperStations(WrapperStations wrapperStationsApiConfig, WrapperStations wrapperStationsMongo, String sorting) {

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
        result.setPageInfo(pageInfo);
        return result;
    }

    @Override
    public WrapperChannels mergeAndSortWrapperChannels(WrapperChannels wrapperChannelsApiConfig, WrapperChannels wrapperChannelsMongo, String sorting) {

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
        PageInfo pageInfo = new PageInfo();
        pageInfo.setLimit(wrapperChannelsApiConfig.getPageInfo().getLimit());
        pageInfo.setTotalPages(wrapperChannelsApiConfig.getPageInfo().getTotalPages());
        pageInfo.setPage(wrapperChannelsApiConfig.getPageInfo().getPage());
        pageInfo.setItemsFound(mergedList.size());
        result.setPageInfo(pageInfo);
        return result;
    }

    public CreditorInstitutions getCreditorInstitutionsByStation(String stationcode, Integer limit, Integer page) {

        return apiConfigConnector.getCreditorInstitutionsByStation(stationcode, limit, page);
    }

    public void deleteCreditorInstitutionStationRelationship(String ecCode, String stationcode) {
        apiConfigConnector.deleteCreditorInstitutionStationRelationship(ecCode, stationcode);
    }

    public IbansEnhanced getCreditorInstitutionIbans(String ecCode, String label) {

        return apiConfigConnector.getCreditorInstitutionIbans(ecCode, label);
    }

    public IbanCreate createCreditorInstitutionIbans(String ecCode, IbanCreate ibanCreate) {

        return apiConfigConnector.createCreditorInstitutionIbans(ecCode, ibanCreate);
    }

    public void deleteCreditorInstitutionIbans(String ecCode, String iban){
        apiConfigConnector.deleteCreditorInstitutionIbans(ecCode, iban);
    }

    public IbanCreate updateCreditorInstitutionIbans(String ecCode, IbanCreate ibanCreate) {
        return apiConfigConnector.updateCreditorInstitutionIbans(ecCode, ibanCreate.getIban(), ibanCreate);
    }

    public Brokers getBrokersEC(Integer limit, Integer page, String code, String name, String orderby, String ordering){
        return apiConfigConnector.getBrokersEC(limit, page, code, name, orderby, ordering);
    }

    public PaymentServiceProviderDetails updatePSP(String pspcode, PaymentServiceProviderDetails paymentServiceProviderDetails){
        return apiConfigConnector.updatePSP(pspcode, paymentServiceProviderDetails);
    }

    public BrokerPspDetails updateBrokerPSP(String brokercode, BrokerPspDetails brokerPspDetails){
        return apiConfigConnector.updateBrokerPSP(brokercode, brokerPspDetails);
    }
}
