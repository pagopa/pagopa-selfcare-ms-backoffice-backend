package it.pagopa.selfcare.pagopa.backoffice.connector.api;

import it.pagopa.selfcare.pagopa.backoffice.connector.model.channel.*;
import it.pagopa.selfcare.pagopa.backoffice.connector.model.station.CreditorInstitutionStation;
import it.pagopa.selfcare.pagopa.backoffice.connector.model.station.CreditorInstitutionStations;
import it.pagopa.selfcare.pagopa.backoffice.connector.model.station.StationDetails;
import it.pagopa.selfcare.pagopa.backoffice.connector.model.station.Stations;
import org.springframework.core.io.Resource;

public interface ApiConfigConnector {

    Channels getChannels(Integer limit, Integer page, String code, String sort, String xRequestId);

    ChannelDetails createChannel(ChannelDetails channelDetails, String xRequestId);

    ChannelDetails updateChannel(ChannelDetails channelDetails,String channelCode, String xRequestId);

    PspChannels getPspChannels(String pspCode, String xRequestId);

    void deleteChannelPaymentType(String channelCode, String paymentTypeCode, String xRequestId);

    PspChannelPaymentTypes getChannelPaymentTypes(String channelCode, String xRequestId);

    ChannelDetails getChannelDetails(String channelCode, String xRequestId);

    PspChannelPaymentTypes createChannelPaymentType(PspChannelPaymentTypes pspChannelPaymentTypes, String channelCode, String xRequestId);

    PaymentTypes getPaymentTypes(String xRequestId);

    void deletePaymentServiceProvidersChannels(String pspCode, String channelCode, String xRequestId);

    PspChannelPaymentTypes updatePaymentServiceProvidersChannels(String pspCode, String channelCode, PspChannelPaymentTypes pspChannelPaymentTypes, String xRequestId);

    void deleteChannel(String channelCode, String xRequestId);

    PaymentServiceProviders getPspBrokerPsp(Integer limit, Integer page, String brokerPspCode, String uuid);

    Stations getStations(Integer limit, Integer page, String sort, String brokerCode, String ecCode, String stationCode, String xRequestId);

    StationDetails getStation(String stationCode, String xRequestId);

    StationDetails createStation(StationDetails stationDetails, String xRequestId);

    Resource getChannelsCSV(String uuid);

    ChannelPspList getChannelPaymentServiceProviders(Integer limit, Integer page, String channelCode, String uuid);

    PaymentServiceProviderDetails createPaymentServiceProvider(PaymentServiceProviderDetails paymentServiceProviderDetails, String xRequestId);

    BrokerPspDetails createBrokerPsp(BrokerPspDetails brokerPspDetails, String xRequestId);

    CreditorInstitutionStations getEcStations(String ecCode, String xRequestId);

    CreditorInstitutionStation createCreditorInstitutionStationRelationship(String ecCode, CreditorInstitutionStation station, String xRequestId);

    PaymentServiceProviderDetails getPSPDetails(String pspCode, String xRequestId);
}
