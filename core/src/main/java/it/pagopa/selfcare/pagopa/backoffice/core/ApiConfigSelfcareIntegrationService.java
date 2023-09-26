package it.pagopa.selfcare.pagopa.backoffice.core;

import it.pagopa.selfcare.pagopa.backoffice.connector.model.channel.ChannelDetailsList;
import it.pagopa.selfcare.pagopa.backoffice.connector.model.creditorInstitution.CreditorInstitutionAssociatedCodeList;
import it.pagopa.selfcare.pagopa.backoffice.connector.model.station.StationDetailsList;

public interface ApiConfigSelfcareIntegrationService {

    StationDetailsList getStationsDetailsListByBroker(String brokerId, String stationId, Integer limit, Integer page);

    ChannelDetailsList getChannelsDetailsListByBroker(String brokerId, String channelId, Integer limit, Integer page);

    CreditorInstitutionAssociatedCodeList getCreditorInstitutionSegregationcodes(String ecCode);

}
