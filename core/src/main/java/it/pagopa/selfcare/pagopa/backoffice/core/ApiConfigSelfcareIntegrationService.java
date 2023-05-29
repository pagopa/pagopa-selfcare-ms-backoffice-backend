package it.pagopa.selfcare.pagopa.backoffice.core;

import it.pagopa.selfcare.pagopa.backoffice.connector.model.channel.*;
import it.pagopa.selfcare.pagopa.backoffice.connector.model.creditorInstitution.CreditorInstitutionDetails;
import it.pagopa.selfcare.pagopa.backoffice.connector.model.station.CreditorInstitutionStationEdit;
import it.pagopa.selfcare.pagopa.backoffice.connector.model.station.StationDetails;
import it.pagopa.selfcare.pagopa.backoffice.connector.model.station.StationDetailsList;
import it.pagopa.selfcare.pagopa.backoffice.connector.model.station.Stations;
import org.springframework.core.io.Resource;

public interface ApiConfigSelfcareIntegrationService {

    StationDetailsList getStationsDetailsListByBroker(String brokerId, String stationId, Integer limit, Integer page, String xRequestId);

    ChannelDetailsList getChannelsDetailsListByBroker(String brokerId, String channelId, Integer limit, Integer page, String xRequestId);

}
