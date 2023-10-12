package it.pagopa.selfcare.pagopa.backoffice.connector.api;

import it.pagopa.selfcare.pagopa.backoffice.connector.model.broker.BrokerDetails;
import it.pagopa.selfcare.pagopa.backoffice.connector.model.broker.Brokers;
import it.pagopa.selfcare.pagopa.backoffice.connector.model.channel.*;
import it.pagopa.selfcare.pagopa.backoffice.connector.model.creditorInstitution.CreditorInstitutionDetails;
import it.pagopa.selfcare.pagopa.backoffice.connector.model.creditorInstitution.CreditorInstitutions;
import it.pagopa.selfcare.pagopa.backoffice.connector.model.creditorInstitution.IbanCreate;
import it.pagopa.selfcare.pagopa.backoffice.connector.model.creditorInstitution.IbansEnhanced;
import it.pagopa.selfcare.pagopa.backoffice.connector.model.station.CreditorInstitutionStationEdit;
import it.pagopa.selfcare.pagopa.backoffice.connector.model.station.CreditorInstitutionStations;
import it.pagopa.selfcare.pagopa.backoffice.connector.model.station.StationDetails;
import it.pagopa.selfcare.pagopa.backoffice.connector.model.station.Stations;
import org.springframework.core.io.Resource;

public interface ApiConfigConnector {

    BrokersPsp getBrokersPsp(Integer limit, Integer page, String filterByCode, String filterByName, String orderBy, String sorting);

    BrokerPspDetails getBrokerPsp(String brokerpspcode);

    Channels getChannels(Integer limit, Integer page, String code, String brokerCode, String sort);

    ChannelDetails createChannel(ChannelDetails channelDetails);

    ChannelDetails updateChannel(ChannelDetails channelDetails, String channelCode);

    PspChannels getPspChannels(String pspCode);

    void deleteChannelPaymentType(String channelCode, String paymentTypeCode);

    PspChannelPaymentTypes getChannelPaymentTypes(String channelCode);

    ChannelDetails getChannelDetails(String channelCode);

    PspChannelPaymentTypes createChannelPaymentType(PspChannelPaymentTypes pspChannelPaymentTypes, String channelCode);

    PaymentTypes getPaymentTypes();

    void deletePaymentServiceProvidersChannels(String pspCode, String channelCode);

    PspChannelPaymentTypes updatePaymentServiceProvidersChannels(String pspCode, String channelCode, PspChannelPaymentTypes pspChannelPaymentTypes);

    void deleteChannel(String channelCode);

    PaymentServiceProviders getPspBrokerPsp(Integer limit, Integer page, String brokerPspCode);

    Stations getStations(Integer limit, Integer page, String sort, String brokerCode, String ecCode, String stationCode);

    StationDetails getStation(String stationCode);

    StationDetails createStation(StationDetails stationDetails);

    Resource getChannelsCSV();

    ChannelPspList getChannelPaymentServiceProviders(Integer limit, Integer page, String channelCode);

    PaymentServiceProviderDetails createPaymentServiceProvider(PaymentServiceProviderDetails paymentServiceProviderDetails);

    BrokerPspDetails createBrokerPsp(BrokerPspDetails brokerPspDetails);

    CreditorInstitutionStations getEcStations(String ecCode);

    CreditorInstitutionStationEdit createCreditorInstitutionStationRelationship(String ecCode, CreditorInstitutionStationEdit station);

    PaymentServiceProviderDetails getPSPDetails(String pspCode);

    CreditorInstitutionDetails createCreditorInstitution(CreditorInstitutionDetails request);

    CreditorInstitutionDetails getCreditorInstitutionDetails(String ecCode);

    CreditorInstitutions getCreditorInstitutions(Integer limit, Integer page, String ecCode, String name, String sorting);

    CreditorInstitutionDetails updateCreditorInstitutionDetails(String creditorInstitutionCode,
                                                                CreditorInstitutionDetails request);

    BrokerDetails createBroker(BrokerDetails request);

    StationDetails updateStation(String stationCode, StationDetails stationDetails);

    WfespPluginConfs getWfespPlugins();

    CreditorInstitutions getCreditorInstitutionsByStation(String stationcode, Integer limit, Integer page);

    void deleteCreditorInstitutionStationRelationship(String ecCode, String stationcode);

    IbansEnhanced getCreditorInstitutionIbans(String ecCode, String label);

    IbanCreate createCreditorInstitutionIbans(String ecCode, IbanCreate ibanCreate);

    void deleteCreditorInstitutionIbans(String ecCode, String iban);

    IbanCreate updateCreditorInstitutionIbans(String ecCode, String ibanId, IbanCreate ibanCreate);

     Brokers getBrokersEC(Integer limit, Integer page, String code, String name, String orderby, String ordering);

    PaymentServiceProviderDetails updatePSP(String pspcode, PaymentServiceProviderDetails paymentServiceProviderDetails);

    BrokerPspDetails updateBrokerPSP(String brokercode, BrokerPspDetails brokerPspDetails);

}
