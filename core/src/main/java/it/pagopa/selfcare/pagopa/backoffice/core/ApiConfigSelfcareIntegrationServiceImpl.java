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
    public StationDetailsList getStationsDetailsListByBroker(String brokerId, String stationId, Integer limit, Integer page, String xRequestId) {
        log.trace("getStationsDetailsListByBroker start");
        log.debug("getStationsDetailsListByBroker  xRequestId = {}", xRequestId);
        StationDetailsList response = apiConfigSelfcareIntegrationConnector.getStationsDetailsListByBroker(brokerId, stationId, limit, page, xRequestId);
        log.debug("getStationsDetailsListByBroker result = {}", response);
        log.trace("getStationsDetailsListByBroker end");
        return response;
    }

    @Override
    public ChannelDetailsList getChannelsDetailsListByBroker(String brokerId, String channelId, Integer limit, Integer page, String xRequestId) {
        log.trace("getChannelsDetailsListByBroker start");
        log.debug("getChannelsDetailsListByBroker  xRequestId = {}", xRequestId);
        ChannelDetailsList response = apiConfigSelfcareIntegrationConnector.getChannelDetailsListByBroker(brokerId, channelId, limit, page, xRequestId);
        log.debug("getChannelsDetailsListByBroker result = {}", response);
        log.trace("getChannelsDetailsListByBroker end");
        return response;
    }

    public CreditorInstitutionAssociatedCodeList getCreditorInstitutionSegregationcodes(String ecCode, String xRequestId){
        log.trace("getCreditorInstitutionSegregationcodes start");
        log.debug("getCreditorInstitutionSegregationcodes  xRequestId = {}", xRequestId);
        CreditorInstitutionAssociatedCodeList response = apiConfigSelfcareIntegrationConnector.getCreditorInstitutionSegregationcodes(ecCode, xRequestId);
        log.debug("getChannelsDetailsListByBroker result = {}", response);
        log.trace("getChannelsDetailsListByBroker end");
        return response;
    }


}
