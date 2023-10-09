package it.pagopa.selfcare.pagopa.backoffice.connector.rest.client;

import it.pagopa.selfcare.pagopa.backoffice.connector.api.ApiConfigSelfcareIntegrationConnector;
import it.pagopa.selfcare.pagopa.backoffice.connector.model.channel.ChannelDetailsList;
import it.pagopa.selfcare.pagopa.backoffice.connector.model.creditorInstitution.CreditorInstitutionAssociatedCodeList;
import it.pagopa.selfcare.pagopa.backoffice.connector.model.station.StationDetailsList;
import it.pagopa.selfcare.pagopa.backoffice.connector.rest.config.FeignConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@FeignClient(name = "${rest-client.api-config-selfcare-integration.serviceCode}", url = "${rest-client.api-config-selfcare-integration.base-url}", configuration = FeignConfig.class)
public interface ApiConfigSelfcareIntegrationRestClient extends ApiConfigSelfcareIntegrationConnector {

    @GetMapping(value = "${rest-client.api-config-selfcare-integration.getStationsDetailsFromBroker.path}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    StationDetailsList getStationsDetailsListByBroker(
            @PathVariable("brokerId") String brokerId,
            @RequestParam(required = false) String stationId,
            @RequestParam(required = false, defaultValue = "10") Integer limit,
            @RequestParam(required = false, defaultValue = "0") Integer page);


    @GetMapping(value = "${rest-client.api-config-selfcare-integration.getChannelDetailsFromBroker.path}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    ChannelDetailsList getChannelDetailsListByBroker(
            @PathVariable("brokerId") String brokerId,
            @RequestParam(required = false) String channelId,
            @RequestParam(required = false, defaultValue = "10") Integer limit,
            @RequestParam(required = false, defaultValue = "0") Integer page);

    @GetMapping(value = "${rest-client.api-config-selfcare-integration.getCreditorInstitutionSegregationcodes.path}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    CreditorInstitutionAssociatedCodeList getCreditorInstitutionSegregationcodes(
            @PathVariable("creditorInstitutionCode") String creditorInstitutionCode);

}
