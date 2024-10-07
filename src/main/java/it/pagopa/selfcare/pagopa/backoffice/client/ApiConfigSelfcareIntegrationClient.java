package it.pagopa.selfcare.pagopa.backoffice.client;

import it.pagopa.selfcare.pagopa.backoffice.config.feign.ApiConfigSelfcareIntFeignConfig;
import it.pagopa.selfcare.pagopa.backoffice.model.connector.channel.ChannelDetailsList;
import it.pagopa.selfcare.pagopa.backoffice.model.connector.channel.PspChannels;
import it.pagopa.selfcare.pagopa.backoffice.model.connector.creditorinstitution.AvailableCodes;
import it.pagopa.selfcare.pagopa.backoffice.model.connector.creditorinstitution.BrokerCreditorInstitutionDetails;
import it.pagopa.selfcare.pagopa.backoffice.model.connector.station.StationDetailsList;
import it.pagopa.selfcare.pagopa.backoffice.model.creditorinstituions.client.CreditorInstitutionInfo;
import it.pagopa.selfcare.pagopa.backoffice.model.creditorinstituions.client.CreditorInstitutionStationSegregationCodesList;
import it.pagopa.selfcare.pagopa.backoffice.model.iban.Ibans;
import it.pagopa.selfcare.pagopa.backoffice.model.iban.IbansList;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@FeignClient(name = "api-config-selfcare-integration", url = "${rest-client.api-config-selfcare-integration.base-url}", configuration = ApiConfigSelfcareIntFeignConfig.class)
public interface ApiConfigSelfcareIntegrationClient {

    @GetMapping(value = "/brokers/{broker-tax-code}/stations", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    @Valid
    StationDetailsList getStationsDetailsListByBroker(
            @PathVariable("broker-tax-code") String brokerCode,
            @RequestParam(required = false) String stationId,
            @RequestParam(required = false) String ciTaxCode,
            @RequestParam(required = false, defaultValue = "10") Integer limit,
            @RequestParam(required = false, defaultValue = "0") Integer page);


    @GetMapping(value = "/brokerspsp/{brokerId}/channels", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    @Valid
    ChannelDetailsList getChannelDetailsListByBroker(
            @PathVariable("brokerId") String brokerId,
            @RequestParam(required = false) String channelId,
            @RequestParam(required = false, defaultValue = "10") Integer limit,
            @RequestParam(required = false, defaultValue = "0") Integer page);

    @GetMapping(value = "/creditorinstitutions/{ci-tax-code}/segregationcodes", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    @Valid
    AvailableCodes getCreditorInstitutionSegregationCodes(
            @PathVariable("ci-tax-code") String creditorInstitutionCode,
            @RequestParam String targetCITaxCode
    );


    @PostMapping(value = "/ibans", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    @Valid
    @Retryable(
            maxAttemptsExpression = "${retry.utils.maxAttempts}",
            backoff = @Backoff(delayExpression = "${retry.utils.maxDelay}"))
    IbansList getIbans(
            @RequestParam(defaultValue = "10") Integer limit,
            @RequestParam(defaultValue = "0") Integer page,
            @RequestBody List<String> ecList);

    @GetMapping(value = "/brokers/{broker-tax-code}/creditor-institutions", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    @Valid
    @Retryable(maxAttemptsExpression = "${retry.utils.maxAttempts}",
            backoff = @Backoff(delayExpression = "${retry.utils.maxDelay}"))
    BrokerCreditorInstitutionDetails getCreditorInstitutionsAssociatedToBroker(
            @RequestParam(defaultValue = "10") Integer limit,
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "true") Boolean enabled,
            @PathVariable("broker-tax-code") String brokerCode);

    @GetMapping(value = "/payment-service-providers/{psp-tax-code}/channels", produces = MediaType.APPLICATION_JSON_VALUE)
    @Valid
    PspChannels getPspChannels(@PathVariable(value = "psp-tax-code") String pspTaxCode);

    @GetMapping(value = "/creditorinstitutions", produces = MediaType.APPLICATION_JSON_VALUE)
    @Valid
    List<CreditorInstitutionInfo> getCreditorInstitutionInfo(@RequestParam List<String> taxCodeList);

    @GetMapping(value = "/creditorinstitutions/stations/{station-code}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Valid
    List<CreditorInstitutionInfo> getStationCreditorInstitutions(
            @PathVariable(value = "station-code") String stationCode,
            @RequestParam List<String> ciTaxCodeList
    );

    @GetMapping(value = "/ibans/{creditorinstitutioncode}/list")
    @Valid
    Ibans getCreditorInstitutionIbans(@PathVariable("creditorinstitutioncode") String creditorInstitutionCode,
                                      @RequestParam String label);

    @GetMapping(value = "/brokers/{broker-tax-code}/creditor-institutions/segregation-codes", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    @Valid
    CreditorInstitutionStationSegregationCodesList getCreditorInstitutionsSegregationCodeAssociatedToBroker(
            @PathVariable("broker-tax-code") String brokerCode
    );
}
