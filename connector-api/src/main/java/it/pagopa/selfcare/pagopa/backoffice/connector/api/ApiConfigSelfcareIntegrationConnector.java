package it.pagopa.selfcare.pagopa.backoffice.connector.api;

import it.pagopa.selfcare.pagopa.backoffice.connector.model.channel.ChannelDetailsList;
import it.pagopa.selfcare.pagopa.backoffice.connector.model.creditorInstitution.CreditorInstitutionAssociatedCodeList;
import it.pagopa.selfcare.pagopa.backoffice.connector.model.station.StationDetailsList;

public interface ApiConfigSelfcareIntegrationConnector {

    StationDetailsList getStationsDetailsListByBroker(String brokerId, String stationId, Integer limit, Integer page, String xRequestId);

    ChannelDetailsList getChannelDetailsListByBroker(String brokerId, String chennelId, Integer limit, Integer page, String xRequestId);

    CreditorInstitutionAssociatedCodeList getCreditorInstitutionSegregationcodes(String ecCode, String xRequestId);

}
