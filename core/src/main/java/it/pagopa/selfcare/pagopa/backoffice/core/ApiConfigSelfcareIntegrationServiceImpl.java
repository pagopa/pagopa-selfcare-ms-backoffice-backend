package it.pagopa.selfcare.pagopa.backoffice.core;

import it.pagopa.selfcare.pagopa.backoffice.connector.api.ApiConfigSelfcareIntegrationConnector;
import it.pagopa.selfcare.pagopa.backoffice.connector.model.channel.ChannelDetailsList;
import it.pagopa.selfcare.pagopa.backoffice.connector.model.creditorInstitution.CreditorInstitutionAssociatedCodeList;
import it.pagopa.selfcare.pagopa.backoffice.connector.model.station.StationDetailsList;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class ApiConfigSelfcareIntegrationServiceImpl implements ApiConfigSelfcareIntegrationService {


    private final ApiConfigSelfcareIntegrationConnector apiConfigSelfcareIntegrationConnector;

    @Autowired
    public ApiConfigSelfcareIntegrationServiceImpl(ApiConfigSelfcareIntegrationConnector apiConfigConnector) {
        this.apiConfigSelfcareIntegrationConnector = apiConfigConnector;
    }


    @Override
    public StationDetailsList getStationsDetailsListByBroker(String brokerId, String stationId, Integer limit, Integer page) {
        StationDetailsList response = apiConfigSelfcareIntegrationConnector.getStationsDetailsListByBroker(brokerId, stationId, limit, page);
        return response;
    }

    @Override
    public ChannelDetailsList getChannelsDetailsListByBroker(String brokerId, String channelId, Integer limit, Integer page) {
        ChannelDetailsList response = apiConfigSelfcareIntegrationConnector.getChannelDetailsListByBroker(brokerId, channelId, limit, page);
        return response;
    }

    public CreditorInstitutionAssociatedCodeList getCreditorInstitutionSegregationcodes(String ecCode){
        CreditorInstitutionAssociatedCodeList response = apiConfigSelfcareIntegrationConnector.getCreditorInstitutionSegregationcodes(ecCode);
        return response;
    }


}
