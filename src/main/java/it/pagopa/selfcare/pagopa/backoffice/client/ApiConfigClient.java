package it.pagopa.selfcare.pagopa.backoffice.client;

import feign.FeignException;
import feign.RequestLine;
import it.pagopa.selfcare.pagopa.backoffice.config.feign.ApiConfigFeignConfig;
import it.pagopa.selfcare.pagopa.backoffice.model.configuration.PaymentTypes;
import it.pagopa.selfcare.pagopa.backoffice.model.configuration.WfespPluginConfs;
import it.pagopa.selfcare.pagopa.backoffice.model.connector.broker.BrokerDetails;
import it.pagopa.selfcare.pagopa.backoffice.model.connector.broker.Brokers;
import it.pagopa.selfcare.pagopa.backoffice.model.connector.channel.BrokerPspDetails;
import it.pagopa.selfcare.pagopa.backoffice.model.connector.channel.BrokersPsp;
import it.pagopa.selfcare.pagopa.backoffice.model.connector.channel.ChannelDetails;
import it.pagopa.selfcare.pagopa.backoffice.model.connector.channel.ChannelPspList;
import it.pagopa.selfcare.pagopa.backoffice.model.connector.channel.Channels;
import it.pagopa.selfcare.pagopa.backoffice.model.connector.channel.PaymentServiceProviderDetails;
import it.pagopa.selfcare.pagopa.backoffice.model.connector.channel.PaymentServiceProviders;
import it.pagopa.selfcare.pagopa.backoffice.model.connector.channel.PspChannelPaymentTypes;
import it.pagopa.selfcare.pagopa.backoffice.model.connector.channel.PspChannels;
import it.pagopa.selfcare.pagopa.backoffice.model.connector.creditorInstitution.CreditorInstitutionDetails;
import it.pagopa.selfcare.pagopa.backoffice.model.connector.creditorInstitution.CreditorInstitutions;
import it.pagopa.selfcare.pagopa.backoffice.model.connector.station.CreditorInstitutionStationEdit;
import it.pagopa.selfcare.pagopa.backoffice.model.connector.station.CreditorInstitutionStations;
import it.pagopa.selfcare.pagopa.backoffice.model.connector.station.StationDetails;
import it.pagopa.selfcare.pagopa.backoffice.model.connector.station.Stations;
import it.pagopa.selfcare.pagopa.backoffice.model.creditorinstituions.CreditorInstitutionsView;
import it.pagopa.selfcare.pagopa.backoffice.model.iban.IbanCreateApiconfig;
import it.pagopa.selfcare.pagopa.backoffice.model.iban.Ibans;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import javax.validation.constraints.NotNull;

@FeignClient(name = "api-config", url = "${rest-client.api-config.base-url}", configuration = ApiConfigFeignConfig.class)
public interface ApiConfigClient {

    @PutMapping(value = "/brokers/{brokercode}", produces = MediaType.APPLICATION_JSON_VALUE)
    BrokerDetails updateBrokerEc(@RequestBody BrokerDetails brokerDetails, @PathVariable("brokercode") String brokerCode);


    @GetMapping(value = "/brokerspsp", produces = MediaType.APPLICATION_JSON_VALUE)
    @RequestLine("getBrokersPsp")
    BrokersPsp getBrokersPsp(@RequestParam(required = false, defaultValue = "50") Integer limit,
                             @RequestParam(required = true) Integer page,
                             @RequestParam(required = false, name = "code") String filterByCode,
                             @RequestParam(required = false, name = "name") String filterByName,
                             @RequestParam(required = false, name = "orderby", defaultValue = "CODE") String orderBy,
                             @RequestParam(required = false, name = "ordering", defaultValue = "DESC") String sort);

    @GetMapping(value = "/brokerspsp/{brokerpspcode}", produces = MediaType.APPLICATION_JSON_VALUE)
    BrokerPspDetails getBrokerPsp(@RequestParam(required = false, name = "brokerpspcode") String brokerpspcode);

    @GetMapping(value = "/channels", produces = MediaType.APPLICATION_JSON_VALUE)
    Channels getChannels(@RequestParam(required = false, defaultValue = "50") Integer limit,
                         @RequestParam(required = true) Integer page,
                         @RequestParam(required = false) String code,
                         @RequestParam(required = false) String brokercode,
                         @RequestParam(required = false, name = "ordering", defaultValue = "DESC") String sort);

    @PostMapping(value = "/channels", produces = MediaType.APPLICATION_JSON_VALUE)
    ChannelDetails createChannel(@RequestBody @NotNull ChannelDetails detailsDto);

    @PutMapping(value = "/channels/{channelcode}", produces = MediaType.APPLICATION_JSON_VALUE)
    ChannelDetails updateChannel(@RequestBody ChannelDetails channelDetails,
                                 @PathVariable("channelcode") String channelCode);

    /**
     * @deprecated (since v1.7.0, new API added in ApiConfig Selfcare Integration Client that replace this one)
     */
    @Deprecated(since="1.7.0", forRemoval=true)
    @GetMapping(value = "/paymentserviceproviders/{pspcode}/channels", produces = MediaType.APPLICATION_JSON_VALUE)
    PspChannels getPspChannels(@PathVariable("pspcode") String pspCode);

    @GetMapping(value = "/channels/{channelcode}", produces = MediaType.APPLICATION_JSON_VALUE)
    ChannelDetails getChannelDetails(@PathVariable("channelcode") String channelcode);

    @PostMapping(value = "/channels/{channelcode}/paymenttypes", produces = MediaType.APPLICATION_JSON_VALUE)
    PspChannelPaymentTypes createChannelPaymentType(@RequestBody PspChannelPaymentTypes pspChannelPaymentTypes,
                                                    @PathVariable("channelcode") String channelCode);

    @GetMapping(value = "/configuration/paymenttypes", produces = MediaType.APPLICATION_JSON_VALUE)
    PaymentTypes getPaymentTypes();


    @GetMapping(value = "/channels/{channelcode}/paymenttypes", produces = MediaType.APPLICATION_JSON_VALUE)
    PspChannelPaymentTypes getChannelPaymentTypes(@PathVariable("channelcode") String channelCode);

    @DeleteMapping(value = "/channels/{channelcode}/paymenttypes/{paymenttypecode}", produces = MediaType.APPLICATION_JSON_VALUE)
    void deleteChannelPaymentType(@PathVariable("channelcode") String channelCode,
                                  @PathVariable("paymenttypecode") String paymentTypeCode);

    @DeleteMapping(value = "/paymentserviceproviders/{pspcode}/channels/{channelcode}", produces = MediaType.APPLICATION_JSON_VALUE)
    void deletePaymentServiceProvidersChannels(@PathVariable("pspcode") String pspCode, @PathVariable("channelcode") String channelCode);

    @PutMapping(value = "/paymentserviceproviders/{pspcode}/channels/{channelcode}", produces = MediaType.APPLICATION_JSON_VALUE)
    PspChannelPaymentTypes updatePaymentServiceProvidersChannels(@PathVariable("pspcode") String pspCode, @PathVariable("channelcode") String channelCode,
                                                                 @RequestBody PspChannelPaymentTypes pspChannelPaymentTypes);

    @DeleteMapping(value = "/channels/{channelcode}", produces = MediaType.APPLICATION_JSON_VALUE)
    void deleteChannel(@PathVariable("channelcode") String channelCode);

    @GetMapping(value = "/stations", produces = MediaType.APPLICATION_JSON_VALUE)
    Stations getStations(@RequestParam(required = false, defaultValue = "50") Integer limit,
                         @RequestParam(required = true) Integer page,
                         @RequestParam(required = false, name = "ordering", defaultValue = "DESC") String sort,
                         @RequestParam(name = "brokercode", required = false) String brokerCode,
                         @RequestParam(name = "creditorinstitutioncode", required = false) String creditorInstitutionCode,
                         @RequestParam(required = false) String code);

    @GetMapping(value = "/stations/{stationcode}", produces = MediaType.APPLICATION_JSON_VALUE)
    StationDetails getStation(@PathVariable("stationcode") String stationCode);

    @PostMapping(value = "/stations", produces = MediaType.APPLICATION_JSON_VALUE)
    StationDetails createStation(@RequestBody @NotNull StationDetails stationDetails);

    @GetMapping(value = "/brokerspsp/{brokerpspcode}/paymentserviceproviders", produces = MediaType.APPLICATION_JSON_VALUE)
    PaymentServiceProviders getPspBrokerPsp(@RequestParam(required = false, defaultValue = "50") Integer limit,
                                            @RequestParam Integer page,
                                            @PathVariable("brokerpspcode") String brokerPspCode);

    @GetMapping(value = "/channels/csv", produces = {MediaType.TEXT_PLAIN_VALUE, MediaType.APPLICATION_JSON_VALUE})
    Resource getChannelsCSV();


    @GetMapping(value = "/channels/{channelcode}/paymentserviceproviders", produces = MediaType.APPLICATION_JSON_VALUE)
    ChannelPspList getChannelPaymentServiceProviders(@PathVariable("channelcode") String channelcode,
                                                     @RequestParam(required = false, defaultValue = "50") Integer limit,
                                                     @RequestParam Integer page,
                                                     @RequestParam("pspName") String pspName);

    @GetMapping(value = "/paymentserviceproviders", produces = MediaType.APPLICATION_JSON_VALUE)
    PaymentServiceProviders getPaymentServiceProviders(@RequestParam(required = false, defaultValue = "50") Integer limit,
                                                       @RequestParam(required = false) Integer page,
                                                       @RequestParam(required = false) String code,
                                                       @RequestParam(required = false) String name,
                                                       @RequestParam(required = false) String taxCode);

    @PostMapping(value = "/brokerspsp", produces = MediaType.APPLICATION_JSON_VALUE)
    BrokerPspDetails createBrokerPsp(@RequestBody BrokerPspDetails brokerPspDetails);

    @PostMapping(value = "/paymentserviceproviders", produces = MediaType.APPLICATION_JSON_VALUE)
    PaymentServiceProviderDetails createPaymentServiceProvider(PaymentServiceProviderDetails paymentServiceProviderDetails);


    @GetMapping(value = "/creditorinstitutions/{creditorinstitutioncode}/stations", produces = MediaType.APPLICATION_JSON_VALUE)
    CreditorInstitutionStations getEcStations(@PathVariable("creditorinstitutioncode") String ecCode);

    @PostMapping(value = "/creditorinstitutions/{creditorinstitutioncode}/stations", produces = MediaType.APPLICATION_JSON_VALUE)
    CreditorInstitutionStationEdit createCreditorInstitutionStationRelationship(@PathVariable("creditorinstitutioncode") String ecCode,
                                                                                @RequestBody CreditorInstitutionStationEdit station);

    @GetMapping(value = "/paymentserviceproviders/{pspcode}", produces = MediaType.APPLICATION_JSON_VALUE)
    PaymentServiceProviderDetails getPSPDetails(@PathVariable("pspcode") String pspCode);

    @PostMapping(value = "/creditorinstitutions")
    CreditorInstitutionDetails createCreditorInstitution(@RequestBody CreditorInstitutionDetails request);

    @GetMapping(value = "/creditorinstitutions/{creditorinstitutioncode}", produces = MediaType.APPLICATION_JSON_VALUE)
    CreditorInstitutionDetails getCreditorInstitutionDetails(@PathVariable("creditorinstitutioncode") String creditorInstitutionCode);

    @GetMapping(value = "/creditorinstitutions", produces = MediaType.APPLICATION_JSON_VALUE)
    CreditorInstitutions getCreditorInstitutions(@RequestParam(required = false, defaultValue = "50") Integer limit,
                                                 @RequestParam(required = true) Integer page,
                                                 @RequestParam(required = false, name = "code") String ecCode,
                                                 @RequestParam(required = false, name = "name") String name,
                                                 @RequestParam(required = false, name = "ordering", defaultValue = "DESC") String sorting);

    @PutMapping(value = "/creditorinstitutions/{creditorinstitutioncode}", produces = MediaType.APPLICATION_JSON_VALUE)
    CreditorInstitutionDetails updateCreditorInstitutionDetails(@PathVariable("creditorinstitutioncode") String creditorInstitutionCode,
                                                                @RequestBody CreditorInstitutionDetails request);

    @GetMapping(value = "/brokers/{brokercode}", produces = MediaType.APPLICATION_JSON_VALUE)
    BrokerDetails getBroker(@RequestParam(required = false, name = "brokercode") String brokerCode);

    @PostMapping(value = "/brokers", produces = MediaType.APPLICATION_JSON_VALUE)
    BrokerDetails createBroker(@RequestBody BrokerDetails request);


    @PutMapping(value = "/stations/{stationcode}", produces = MediaType.APPLICATION_JSON_VALUE)
    StationDetails updateStation(@PathVariable("stationcode") String stationCode,
                                 @RequestBody StationDetails stationDetails);

    @GetMapping(value = "/configuration/wfespplugins", produces = MediaType.APPLICATION_JSON_VALUE)
    WfespPluginConfs getWfespPlugins();

    @GetMapping(value = "/stations/{stationcode}/creditorinstitutions", produces = MediaType.APPLICATION_JSON_VALUE)
    CreditorInstitutions getCreditorInstitutionsByStation(@PathVariable(required = false, name = "stationcode") String stationcode,
                                                          @RequestParam(required = false, defaultValue = "50") Integer limit,
                                                          @RequestParam Integer page,
                                                          @RequestParam String ciNameOrCF);

    @DeleteMapping(value = "/creditorinstitutions/{creditorinstitutioncode}/stations/{stationcode}", produces = MediaType.APPLICATION_JSON_VALUE)
    void deleteCreditorInstitutionStationRelationship(@PathVariable("creditorinstitutioncode") String ecCode,
                                                      @PathVariable(required = false, name = "stationcode") String stationcode);

    @GetMapping(value = "/creditorinstitutions/{creditorinstitutioncode}/ibans/list")
    Ibans getCreditorInstitutionIbans(@PathVariable("creditorinstitutioncode") String creditorInstitutionCode,
                                      @RequestParam String label);

    @PostMapping(value = "/creditorinstitutions/{creditorinstitutioncode}/ibans", produces = MediaType.APPLICATION_JSON_VALUE)
    IbanCreateApiconfig createCreditorInstitutionIbans(@PathVariable("creditorinstitutioncode") String creditorInstitutionCode,
                                                       @RequestBody IbanCreateApiconfig ibanCreate);


    @PutMapping(value = "/creditorinstitutions/{creditorinstitutioncode}/ibans/{ibanId}", produces = MediaType.APPLICATION_JSON_VALUE)
    IbanCreateApiconfig updateCreditorInstitutionIbans(@PathVariable("creditorinstitutioncode") String creditorInstitutionCode,
                                                       @PathVariable("ibanId") String ibanId,
                                                       @RequestBody IbanCreateApiconfig ibanCreate);


    @DeleteMapping(value = "/creditorinstitutions/{creditorinstitutioncode}/ibans/{ibanValue}", produces = MediaType.APPLICATION_JSON_VALUE)
    void deleteCreditorInstitutionIbans(@PathVariable("creditorinstitutioncode") String creditorinstitutioncode,
                                        @PathVariable("ibanValue") String ibanValue);

    @GetMapping(value = "/brokers", produces = MediaType.APPLICATION_JSON_VALUE)
    @RequestLine("getBrokersEC")
    @Retryable(
            exclude = FeignException.FeignClientException.class,
            maxAttemptsExpression = "${retry.utils.maxAttempts:3}",
            backoff = @Backoff(delayExpression = "${retry.utils.maxDelay:2000}"))
    Brokers getBrokersEC(@RequestParam(required = false, defaultValue = "50") Integer limit,
                         @RequestParam Integer page,
                         @RequestParam(required = false) String code,
                         @RequestParam(required = false) String name,
                         @RequestParam(required = false, defaultValue = "CODE") String orderby,
                         @RequestParam(required = false, defaultValue = "DESC") String ordering);

    @PutMapping(value = "/paymentserviceproviders/{pspcode}", produces = MediaType.APPLICATION_JSON_VALUE)
    PaymentServiceProviderDetails updatePSP(@PathVariable("pspcode") String pspcode,
                                            @RequestBody PaymentServiceProviderDetails paymentServiceProviderDetails);

    @PutMapping(value = "/brokerspsp/{brokerpspcode}", produces = MediaType.APPLICATION_JSON_VALUE)
    BrokerPspDetails updateBrokerPSP(@PathVariable("brokerpspcode") String brokerpspcode,
                                     @RequestBody BrokerPspDetails brokerPspDetails);

    @GetMapping(value = "/creditorinstitutions/view", produces = MediaType.APPLICATION_JSON_VALUE)
    @Retryable(
            exclude = FeignException.FeignClientException.class,
            maxAttemptsExpression = "${retry.utils.maxAttempts:3}",
            backoff = @Backoff(delayExpression = "${retry.utils.maxDelay:2000}"))
    CreditorInstitutionsView getCreditorInstitutionsAssociatedToBrokerStations(@RequestParam(required = false, defaultValue = "50") Integer limit,
                                                                               @RequestParam Integer page,
                                                                               @RequestParam(required = false, name = "creditorInstitutionCode") String creditorInstitutionCode,
                                                                               @RequestParam(required = false, name = "paBrokerCode") String paBrokerCode,
                                                                               @RequestParam(required = false, name = "stationCode") String stationCode,
                                                                               @RequestParam(required = false, name = "enabled") Boolean enabled,
                                                                               @RequestParam(required = false, name = "auxDigit") Long auxDigit,
                                                                               @RequestParam(required = false, name = "applicationCode") Long applicationCode,
                                                                               @RequestParam(required = false, name = "segregationCode") Long segregationCode,
                                                                               @RequestParam(required = false, name = "mod4") Boolean mod4);
}
