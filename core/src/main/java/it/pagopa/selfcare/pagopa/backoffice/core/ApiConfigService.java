package it.pagopa.selfcare.pagopa.backoffice.core;

import it.pagopa.selfcare.pagopa.backoffice.connector.model.broker.BrokerDetails;
import it.pagopa.selfcare.pagopa.backoffice.connector.model.broker.Brokers;
import it.pagopa.selfcare.pagopa.backoffice.connector.model.channel.*;
import it.pagopa.selfcare.pagopa.backoffice.connector.model.creditorInstitution.CreditorInstitutionDetails;
import it.pagopa.selfcare.pagopa.backoffice.connector.model.creditorInstitution.CreditorInstitutions;
import it.pagopa.selfcare.pagopa.backoffice.connector.model.creditorInstitution.IbanCreate;
import it.pagopa.selfcare.pagopa.backoffice.connector.model.creditorInstitution.IbansEnhanced;
import it.pagopa.selfcare.pagopa.backoffice.connector.model.station.CreditorInstitutionStationEdit;
import it.pagopa.selfcare.pagopa.backoffice.connector.model.station.StationDetails;
import it.pagopa.selfcare.pagopa.backoffice.connector.model.station.Stations;
import it.pagopa.selfcare.pagopa.backoffice.connector.model.wrapper.WrapperChannel;
import it.pagopa.selfcare.pagopa.backoffice.connector.model.wrapper.WrapperChannels;
import it.pagopa.selfcare.pagopa.backoffice.connector.model.wrapper.WrapperStation;
import it.pagopa.selfcare.pagopa.backoffice.connector.model.wrapper.WrapperStations;
import org.springframework.core.io.Resource;

import java.util.List;

public interface ApiConfigService {

    BrokerDetails updateBrokerEc(String brokerCode,BrokerDetails brokerDetails);
    BrokersPsp getBrokersPsp(Integer limit, Integer page, String filterByCode, String filterByName,String orderBy,String sorting);

    BrokerPspDetails getBrokerPsp(String brokerpspcode);

    Channels getChannels(Integer limit, Integer page, String code, String brokerCode, String sort);


    ChannelDetails createChannel(ChannelDetails channelDetails);

    ChannelDetails updateChannel(ChannelDetails channelDetails, String channelCode);

    PspChannels getPspChannels(String pspCode);

    ChannelDetails getChannelDetails(String channelCode);

    PspChannelPaymentTypes createChannelPaymentType(PspChannelPaymentTypes pspChannelPaymentTypes, String channelCode);

    PaymentTypes getPaymentTypes();

    void deleteChannelPaymentType(String channelCode, String paymentTypeCode);

    PspChannelPaymentTypes getChannelPaymentTypes(String channelCode);

    void deletePaymentServiceProvidersChannels(String pspCode, String channelCode);

    PspChannelPaymentTypes updatePaymentServiceProvidersChannels(String pspCode, String channelCode, PspChannelPaymentTypes pspChannelPaymentTypes);

    void deleteChannel(String channelCode);

    PaymentServiceProviders getPspBrokerPsp(Integer limit, Integer page, String brokerPspCode);

    Resource getChannelsCSV();

    ChannelPspList getChannelPaymentServiceProviders(Integer limit, Integer page, String channelCode);

    PaymentServiceProviders getPaymentServiceProviders(Integer limit, Integer page, String code, String name, String taxCode);

    BrokerPspDetails createBrokerPsp(BrokerPspDetails brokerPspDetails);

    BrokerDetails createBroker(BrokerDetails request);

    PaymentServiceProviderDetails createPaymentServiceProvider(PaymentServiceProviderDetails paymentServiceProviderDetails);

    Stations getStations(Integer limit, Integer page, String sort, String brokerCode, String ecCode, String stationCode);

    StationDetails getStation(String stationCode);

    StationDetails createStation(StationDetails stationDetails);

    String generateChannelCode(String pspCode);

    String generateStationCode(String ecCode);

    PaymentServiceProviderDetails getPSPDetails(String pspCode);

    CreditorInstitutionStationEdit createCreditorInstitutionStationRelation(String ecCode, CreditorInstitutionStationEdit station);

    CreditorInstitutionDetails createCreditorInstitution(CreditorInstitutionDetails dto);

    CreditorInstitutionDetails getCreditorInstitutionDetails(String ecCode);

    CreditorInstitutions getCreditorInstitutions(Integer limit, Integer page, String ecCode, String name, String sorting);

    CreditorInstitutionDetails updateCreditorInstitutionDetails(String creditorInstitutionCode,
                                                                CreditorInstitutionDetails request);
    StationDetails updateStation(String stationCode, StationDetails stationDetails);


    WrapperStations mergeAndSortWrapperStations(WrapperStations wrapperStationsApiConfig,WrapperStations wrapperStationsMongo, String sorting);

    WrapperChannels mergeAndSortWrapperChannels(WrapperChannels wrapperChannelsApiConfig, WrapperChannels wrapperChannelsMongo, String sorting);

    WfespPluginConfs getWfespPlugins();

    CreditorInstitutions getCreditorInstitutionsByStation(String stationcode, Integer limit, Integer page);

    void deleteCreditorInstitutionStationRelationship(String ecCode, String stationcode);

    IbansEnhanced getCreditorInstitutionIbans(String ecCode, String label);

    IbanCreate createCreditorInstitutionIbans(String ecCode, IbanCreate ibanCreate);

    void deleteCreditorInstitutionIbans(String ecCode, String iban);

    IbanCreate updateCreditorInstitutionIbans(String ecCode, IbanCreate ibanCreate);

    String generateStationCodeV2(List<WrapperStation> stationList, String ecCode);

    String generateChannelCodeV2(List<WrapperChannel> channelList, String ecCode);

    Brokers getBrokersEC(Integer limit, Integer page, String code, String name, String orderby, String ordering);

    PaymentServiceProviderDetails updatePSP(String pspcode, PaymentServiceProviderDetails paymentServiceProviderDetails);

    BrokerPspDetails updateBrokerPSP(String brokercode, BrokerPspDetails brokerPspDetails);

    }
