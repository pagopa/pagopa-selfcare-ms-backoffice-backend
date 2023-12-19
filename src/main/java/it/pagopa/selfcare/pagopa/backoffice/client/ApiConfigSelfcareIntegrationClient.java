package it.pagopa.selfcare.pagopa.backoffice.client;

import it.pagopa.selfcare.pagopa.backoffice.config.feign.ApiConfigSelfcareIntFeignConfig;
import it.pagopa.selfcare.pagopa.backoffice.model.connector.channel.ChannelDetailsList;
import it.pagopa.selfcare.pagopa.backoffice.model.connector.creditorInstitution.CreditorInstitutionAssociatedCodeList;
import it.pagopa.selfcare.pagopa.backoffice.model.connector.station.StationDetailsList;
import it.pagopa.selfcare.pagopa.backoffice.model.iban.IbansList;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@FeignClient(name = "api-config-selfcare-integration", url = "${rest-client.api-config-selfcare-integration.base-url}", configuration = ApiConfigSelfcareIntFeignConfig.class)
public interface ApiConfigSelfcareIntegrationClient {

    @GetMapping(value = "/brokers/{brokerId}/stations", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    StationDetailsList getStationsDetailsListByBroker(
            @PathVariable("brokerId") String brokerId,
            @RequestParam(required = false) String stationId,
            @RequestParam(required = false, defaultValue = "10") Integer limit,
            @RequestParam(required = false, defaultValue = "0") Integer page);


    @GetMapping(value = "/brokerspsp/{brokerId}/channels", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    ChannelDetailsList getChannelDetailsListByBroker(
            @PathVariable("brokerId") String brokerId,
            @RequestParam(required = false) String channelId,
            @RequestParam(required = false, defaultValue = "10") Integer limit,
            @RequestParam(required = false, defaultValue = "0") Integer page);

    @GetMapping(value = "/creditorinstitutions/{creditorInstitutionCode}/segregationcodes", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    CreditorInstitutionAssociatedCodeList getCreditorInstitutionSegregationcodes(
            @PathVariable("creditorInstitutionCode") String creditorInstitutionCode);


    @PostMapping(value = "/ibans", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    IbansList getIbans(
            @RequestParam(defaultValue = "10") Integer limit,
            @RequestParam(defaultValue = "0") Integer page,
            @RequestHeader(name = "X-Canary") String canary,
            @RequestBody List<String> ecList);
}
