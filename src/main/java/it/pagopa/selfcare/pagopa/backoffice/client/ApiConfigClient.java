package it.pagopa.selfcare.pagopa.backoffice.client;

import feign.RequestLine;
import it.pagopa.selfcare.pagopa.backoffice.config.feign.ApiConfigFeignConfig;
import it.pagopa.selfcare.pagopa.backoffice.model.configuration.PaymentTypes;
import it.pagopa.selfcare.pagopa.backoffice.model.configuration.WfespPluginConfs;
import it.pagopa.selfcare.pagopa.backoffice.model.connector.broker.BrokerDetails;
import it.pagopa.selfcare.pagopa.backoffice.model.connector.broker.Brokers;
import it.pagopa.selfcare.pagopa.backoffice.model.connector.channel.*;
import it.pagopa.selfcare.pagopa.backoffice.model.connector.creditorInstitution.CreditorInstitutionDetails;
import it.pagopa.selfcare.pagopa.backoffice.model.connector.creditorInstitution.CreditorInstitutions;
import it.pagopa.selfcare.pagopa.backoffice.model.connector.station.CreditorInstitutionStationEdit;
import it.pagopa.selfcare.pagopa.backoffice.model.connector.station.CreditorInstitutionStations;
import it.pagopa.selfcare.pagopa.backoffice.model.connector.station.StationDetails;
import it.pagopa.selfcare.pagopa.backoffice.model.connector.station.Stations;
import it.pagopa.selfcare.pagopa.backoffice.model.iban.IbanCreateApiconfig;
import it.pagopa.selfcare.pagopa.backoffice.model.iban.Ibans;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotNull;

@FeignClient(name = "api-config", url = "${rest-client.api-config.base-url}", configuration = ApiConfigFeignConfig.class)
public interface ApiConfigClient {

    @PutMapping(value = "/brokers/{brokercode}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    BrokerDetails updateBrokerEc(@RequestBody BrokerDetails brokerDetails, @PathVariable("brokercode") String brokerCode);


    @GetMapping(value = "/brokerspsp", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    @RequestLine("getBrokersPsp")
    BrokersPsp getBrokersPsp(@RequestParam(required = false, defaultValue = "50") Integer limit,
                             @RequestParam(required = true) Integer page,
                             @RequestParam(required = false, name = "code") String filterByCode,
                             @RequestParam(required = false, name = "name") String filterByName,
                             @RequestParam(required = false, name = "orderby", defaultValue = "CODE") String orderBy,
                             @RequestParam(required = false, name = "ordering", defaultValue = "DESC") String sort);

    @GetMapping(value = "/brokerspsp/{brokerpspcode}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    BrokerPspDetails getBrokerPsp(@RequestParam(required = false, name = "brokerpspcode") String brokerpspcode);

    @GetMapping(value = "/channels", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    Channels getChannels(@RequestParam(required = false, defaultValue = "50") Integer limit,
                         @RequestParam(required = true) Integer page,
                         @RequestParam(required = false) String code,
                         @RequestParam(required = false) String brokercode,
                         @RequestParam(required = false, name = "ordering", defaultValue = "DESC") String sort);

    @PostMapping(value = "/channels", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    ChannelDetails createChannel(@RequestBody @NotNull ChannelDetails detailsDto);

    @PutMapping(value = "/channels/{channelcode}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    ChannelDetails updateChannel(@RequestBody ChannelDetails channelDetails,
                                 @PathVariable("channelcode") String channelCode);

    @GetMapping(value = "/paymentserviceproviders/{pspcode}/channels", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    PspChannels getPspChannels(@PathVariable("pspcode") String pspCode);

    @GetMapping(value = "/channels/{channelcode}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    ChannelDetails getChannelDetails(@PathVariable("channelcode") String channelcode);

    @PostMapping(value = "/channels/{channelcode}/paymenttypes", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    PspChannelPaymentTypes createChannelPaymentType(@RequestBody PspChannelPaymentTypes pspChannelPaymentTypes,
                                                    @PathVariable("channelcode") String channelCode);

    @GetMapping(value = "/configuration/paymenttypes", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    PaymentTypes getPaymentTypes();


    @GetMapping(value = "/channels/{channelcode}/paymenttypes", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    PspChannelPaymentTypes getChannelPaymentTypes(@PathVariable("channelcode") String channelCode);

    @DeleteMapping(value = "/channels/{channelcode}/paymenttypes/{paymenttypecode}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    void deleteChannelPaymentType(@PathVariable("channelcode") String channelCode,
                                  @PathVariable("paymenttypecode") String paymentTypeCode);

    @DeleteMapping(value = "/paymentserviceproviders/{pspcode}/channels/{channelcode}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    void deletePaymentServiceProvidersChannels(@PathVariable("pspcode") String pspCode, @PathVariable("channelcode") String channelCode);

    @PutMapping(value = "/paymentserviceproviders/{pspcode}/channels/{channelcode}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    PspChannelPaymentTypes updatePaymentServiceProvidersChannels(@PathVariable("pspcode") String pspCode, @PathVariable("channelcode") String channelCode,
                                                                 @RequestBody PspChannelPaymentTypes pspChannelPaymentTypes);

    @DeleteMapping(value = "/channels/{channelcode}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    void deleteChannel(@PathVariable("channelcode") String channelCode);

    @GetMapping(value = "/stations", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    Stations getStations(@RequestParam(required = false, defaultValue = "50") Integer limit,
                         @RequestParam(required = true) Integer page,
                         @RequestParam(required = false, name = "ordering", defaultValue = "DESC") String sort,
                         @RequestParam(name = "brokercode", required = false) String brokerCode,
                         @RequestParam(name = "creditorinstitutioncode", required = false) String creditorInstitutionCode,
                         @RequestParam(required = false) String code);

    @GetMapping(value = "/stations/{stationcode}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    StationDetails getStation(@PathVariable("stationcode") String stationCode);

    @PostMapping(value = "/stations", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    StationDetails createStation(@RequestBody @NotNull StationDetails stationDetails);

    @GetMapping(value = "/brokerspsp/{brokerpspcode}/paymentserviceproviders", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    PaymentServiceProviders getPspBrokerPsp(@RequestParam(required = false, defaultValue = "50") Integer limit,
                                            @RequestParam Integer page,
                                            @PathVariable("brokerpspcode") String brokerPspCode);

    @GetMapping(value = "/channels/csv", produces = {MediaType.TEXT_PLAIN_VALUE, MediaType.APPLICATION_JSON_VALUE})
    @ResponseBody
    Resource getChannelsCSV();


    @GetMapping(value = "/channels/{channelcode}/paymentserviceproviders", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    ChannelPspList getChannelPaymentServiceProviders(@RequestParam(required = false, defaultValue = "50") Integer limit,
                                                     @RequestParam Integer page,
                                                     @PathVariable("channelcode") String channelcode);

    @GetMapping(value = "/paymentserviceproviders", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    PaymentServiceProviders getPaymentServiceProviders(@RequestParam(required = false, defaultValue = "50") Integer limit,
                                                       @RequestParam(required = false) Integer page,
                                                       @RequestParam(required = false) String code,
                                                       @RequestParam(required = false) String name,
                                                       @RequestParam(required = false) String taxCode);

    @PostMapping(value = "/brokerspsp", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    BrokerPspDetails createBrokerPsp(@RequestBody BrokerPspDetails brokerPspDetails);

    @PostMapping(value = "/paymentserviceproviders", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    PaymentServiceProviderDetails createPaymentServiceProvider(PaymentServiceProviderDetails paymentServiceProviderDetails);


    @GetMapping(value = "/creditorinstitutions/{creditorinstitutioncode}/stations", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    CreditorInstitutionStations getEcStations(@PathVariable("creditorinstitutioncode") String ecCode);

    @PostMapping(value = "/creditorinstitutions/{creditorinstitutioncode}/stations", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    CreditorInstitutionStationEdit createCreditorInstitutionStationRelationship(@PathVariable("creditorinstitutioncode") String ecCode,
                                                                                @RequestBody CreditorInstitutionStationEdit station);

    @GetMapping(value = "/paymentserviceproviders/{pspcode}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
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

    @PostMapping(value = "/brokers", produces = MediaType.APPLICATION_JSON_VALUE)
    BrokerDetails createBroker(@RequestBody BrokerDetails request);


    @PutMapping(value = "/stations/{stationcode}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    StationDetails updateStation(@PathVariable("stationcode") String stationCode,
                                 @RequestBody StationDetails stationDetails);

    @GetMapping(value = "/configuration/wfespplugins", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    WfespPluginConfs getWfespPlugins();

    @GetMapping(value = "/stations/{stationcode}/creditorinstitutions", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    CreditorInstitutions getCreditorInstitutionsByStation(@PathVariable(required = false, name = "stationcode") String stationcode,
                                                          @RequestParam(required = false, defaultValue = "50") Integer limit,
                                                          @RequestParam Integer page);

    @DeleteMapping(value = "/creditorinstitutions/{creditorinstitutioncode}/stations/{stationcode}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    void deleteCreditorInstitutionStationRelationship(@PathVariable("creditorinstitutioncode") String ecCode,
                                                      @PathVariable(required = false, name = "stationcode") String stationcode);

    @GetMapping(value = "/creditorinstitutions/{creditorinstitutioncode}/ibans/enhanced", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    Ibans getCreditorInstitutionIbans(@PathVariable("creditorinstitutioncode") String creditorInstitutionCode,
                                      @RequestParam String label);

    @PostMapping(value = "/creditorinstitutions/{creditorinstitutioncode}/ibans", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    IbanCreateApiconfig createCreditorInstitutionIbans(@PathVariable("creditorinstitutioncode") String creditorInstitutionCode,
                                                       @RequestBody IbanCreateApiconfig ibanCreate);


    @PutMapping(value = "/creditorinstitutions/{creditorinstitutioncode}/ibans/{ibanId}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    IbanCreateApiconfig updateCreditorInstitutionIbans(@PathVariable("creditorinstitutioncode") String creditorInstitutionCode,
                                                       @PathVariable("ibanId") String ibanId,
                                                       @RequestBody IbanCreateApiconfig ibanCreate);


    @DeleteMapping(value = "/creditorinstitutions/{creditorinstitutioncode}/ibans/{ibanValue}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    void deleteCreditorInstitutionIbans(@PathVariable("creditorinstitutioncode") String creditorinstitutioncode,
                                        @PathVariable("ibanValue") String ibanValue);

    @GetMapping(value = "/brokers", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    @RequestLine("getBrokersEC")
    Brokers getBrokersEC(@RequestParam(required = false, defaultValue = "50") Integer limit,
                         @RequestParam Integer page,
                         @RequestParam(required = false) String code,
                         @RequestParam(required = false) String name,
                         @RequestParam(required = false, defaultValue = "CODE") String orderby,
                         @RequestParam(required = false, defaultValue = "DESC") String ordering);

    @PutMapping(value = "/paymentserviceproviders/{pspcode}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    PaymentServiceProviderDetails updatePSP(@PathVariable("pspcode") String pspcode,
                                            @RequestBody PaymentServiceProviderDetails paymentServiceProviderDetails);

    @PutMapping(value = "/brokerspsp/{brokerpspcode}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    BrokerPspDetails updateBrokerPSP(@PathVariable("brokerpspcode") String brokerpspcode,
                                     @RequestBody BrokerPspDetails brokerPspDetails);

}
