package it.pagopa.selfcare.pagopa.backoffice.connector.rest.client;

import it.pagopa.selfcare.pagopa.backoffice.connector.api.ApiConfigSelfcareIntegrationConnector;
import it.pagopa.selfcare.pagopa.backoffice.connector.model.channel.ChannelDetailsList;
import it.pagopa.selfcare.pagopa.backoffice.connector.model.station.StationDetailsList;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

@FeignClient(name = "${rest-client.api-config-selfcare-integration.serviceCode}", url = "${rest-client.api-config-selfcare-integration.base-url}")
public interface ApiConfigSelfcareIntegrationRestClient extends ApiConfigSelfcareIntegrationConnector {

    @GetMapping(value = "${rest-client.api-config-selfcare-integration.getStationsDetailsFromBroker.path}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    StationDetailsList getStationsDetailsListByBroker(
            @PathVariable("brokerId") String brokerId,
            @RequestParam(required = false) String stationId,
            @RequestParam(required = false, defaultValue = "10") Integer limit,
            @RequestParam(required = false, defaultValue = "0") Integer page,
            @RequestHeader(name = "X-Request-Id", required = false) String xRequestId);


    @GetMapping(value = "${rest-client.api-config-selfcare-integration.getChannelDetailsFromBroker.path}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    ChannelDetailsList getChannelDetailsListByBroker(
            @PathVariable("brokerId") String brokerId,
            @RequestParam(required = false) String channelId,
            @RequestParam(required = false, defaultValue = "10") Integer limit,
            @RequestParam(required = false, defaultValue = "0") Integer page,
            @RequestHeader(name = "X-Request-Id", required = false) String xRequestId);

}
