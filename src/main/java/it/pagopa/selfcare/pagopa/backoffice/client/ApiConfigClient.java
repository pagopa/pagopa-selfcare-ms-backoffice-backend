package it.pagopa.selfcare.pagopa.backoffice.client;

import feign.FeignException;
import feign.RequestLine;
import io.swagger.v3.oas.annotations.Parameter;
import it.pagopa.selfcare.pagopa.backoffice.config.feign.ApiConfigFeignConfig;
import it.pagopa.selfcare.pagopa.backoffice.model.configuration.PaymentTypes;
import it.pagopa.selfcare.pagopa.backoffice.model.configuration.WfespPluginConfs;
import it.pagopa.selfcare.pagopa.backoffice.model.connector.broker.BrokerDetails;
import it.pagopa.selfcare.pagopa.backoffice.model.connector.broker.Brokers;
import it.pagopa.selfcare.pagopa.backoffice.model.connector.channel.*;
import it.pagopa.selfcare.pagopa.backoffice.model.connector.creditorinstitution.ApiConfigCreditorInstitutionsOrderBy;
import it.pagopa.selfcare.pagopa.backoffice.model.connector.creditorinstitution.CreditorInstitutionDetails;
import it.pagopa.selfcare.pagopa.backoffice.model.connector.creditorinstitution.CreditorInstitutions;
import it.pagopa.selfcare.pagopa.backoffice.model.connector.station.CreditorInstitutionStationEdit;
import it.pagopa.selfcare.pagopa.backoffice.model.connector.station.CreditorInstitutionStations;
import it.pagopa.selfcare.pagopa.backoffice.model.connector.station.StationDetails;
import it.pagopa.selfcare.pagopa.backoffice.model.connector.station.Stations;
import it.pagopa.selfcare.pagopa.backoffice.model.creditorinstituions.CreditorInstitutionsView;
import it.pagopa.selfcare.pagopa.backoffice.model.iban.IbanCreateApiconfig;
import it.pagopa.selfcare.pagopa.backoffice.model.stationmaintenance.CreateStationMaintenance;
import it.pagopa.selfcare.pagopa.backoffice.model.stationmaintenance.StationMaintenanceResource;
import it.pagopa.selfcare.pagopa.backoffice.model.stationmaintenance.UpdateStationMaintenance;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

@FeignClient(name = "api-config", url = "${rest-client.api-config.base-url}", configuration = ApiConfigFeignConfig.class)
public interface ApiConfigClient {

    @PutMapping(value = "/brokers/{brokercode}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Valid
    BrokerDetails updateBrokerEc(@RequestBody BrokerDetails brokerDetails, @PathVariable("brokercode") String brokerCode);


    @GetMapping(value = "/brokerspsp", produces = MediaType.APPLICATION_JSON_VALUE)
    @RequestLine("getBrokersPsp")
    @Valid
    BrokersPsp getBrokersPsp(@RequestParam(required = false, defaultValue = "50") Integer limit,
                             @RequestParam(required = true) Integer page,
                             @RequestParam(required = false, name = "code") String filterByCode,
                             @RequestParam(required = false, name = "name") String filterByName,
                             @RequestParam(required = false, name = "orderby", defaultValue = "CODE") String orderBy,
                             @RequestParam(required = false, name = "ordering", defaultValue = "DESC") String sort);

    @GetMapping(value = "/brokerspsp/{brokerpspcode}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Valid
    BrokerPspDetails getBrokerPsp(@RequestParam(required = false, name = "brokerpspcode") String brokerpspcode);

    @GetMapping(value = "/channels", produces = MediaType.APPLICATION_JSON_VALUE)
    @Valid
    Channels getChannels(
            @RequestParam(required = false) String code,
            @RequestParam(required = false) String brokercode,
            @RequestParam(required = false, name = "ordering", defaultValue = "DESC") String sort,
            @RequestParam(required = false, defaultValue = "50") Integer limit,
            @RequestParam Integer page
    );

    @PostMapping(value = "/channels", produces = MediaType.APPLICATION_JSON_VALUE)
    @Valid
    ChannelDetails createChannel(@RequestBody @NotNull ChannelDetails detailsDto);

    @PutMapping(value = "/channels/{channelcode}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Valid
    ChannelDetails updateChannel(@RequestBody ChannelDetails channelDetails,
                                 @PathVariable("channelcode") String channelCode);

    @GetMapping(value = "/channels/{channelcode}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Valid
    ChannelDetails getChannelDetails(@PathVariable("channelcode") String channelcode);

    @PostMapping(value = "/channels/{channelcode}/paymenttypes", produces = MediaType.APPLICATION_JSON_VALUE)
    @Valid
    PspChannelPaymentTypes createChannelPaymentType(@RequestBody PspChannelPaymentTypes pspChannelPaymentTypes,
                                                    @PathVariable("channelcode") String channelCode);

    @GetMapping(value = "/configuration/paymenttypes", produces = MediaType.APPLICATION_JSON_VALUE)
    @Valid
    PaymentTypes getPaymentTypes();


    @GetMapping(value = "/channels/{channelcode}/paymenttypes", produces = MediaType.APPLICATION_JSON_VALUE)
    @Valid
    PspChannelPaymentTypes getChannelPaymentTypes(@PathVariable("channelcode") String channelCode);

    @DeleteMapping(value = "/channels/{channelcode}/paymenttypes/{paymenttypecode}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Valid
    void deleteChannelPaymentType(@PathVariable("channelcode") String channelCode,
                                  @PathVariable("paymenttypecode") String paymentTypeCode);

    @DeleteMapping(value = "/paymentserviceproviders/{pspcode}/channels/{channelcode}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Valid
    void deletePaymentServiceProvidersChannels(@PathVariable("pspcode") String pspCode, @PathVariable("channelcode") String channelCode);

    @PutMapping(value = "/paymentserviceproviders/{pspcode}/channels/{channelcode}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Valid
    PspChannelPaymentTypes updatePaymentServiceProvidersChannels(@PathVariable("pspcode") String pspCode, @PathVariable("channelcode") String channelCode,
                                                                 @RequestBody PspChannelPaymentTypes pspChannelPaymentTypes);

    @DeleteMapping(value = "/channels/{channelcode}", produces = MediaType.APPLICATION_JSON_VALUE)
    void deleteChannel(@PathVariable("channelcode") String channelCode);

    @GetMapping(value = "/stations", produces = MediaType.APPLICATION_JSON_VALUE)
    @Valid
    Stations getStations(@RequestParam(required = false, defaultValue = "50") Integer limit,
                         @RequestParam Integer page,
                         @RequestParam(required = false, name = "ordering", defaultValue = "DESC") String sort,
                         @RequestParam(name = "brokercode", required = false) String brokerCode,
                         @RequestParam(name = "creditorinstitutioncode", required = false) String creditorInstitutionCode,
                         @RequestParam(required = false) String code);

    @GetMapping(value = "/stations/{stationcode}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Valid
    StationDetails getStation(@PathVariable("stationcode") String stationCode);

    @PostMapping(value = "/stations", produces = MediaType.APPLICATION_JSON_VALUE)
    @Valid
    StationDetails createStation(@RequestBody @NotNull StationDetails stationDetails);

    @GetMapping(value = "/brokerspsp/{brokerpspcode}/paymentserviceproviders", produces = MediaType.APPLICATION_JSON_VALUE)
    @Valid
    PaymentServiceProviders getPspBrokerPsp(@RequestParam(required = false, defaultValue = "50") Integer limit,
                                            @RequestParam Integer page,
                                            @PathVariable("brokerpspcode") String brokerPspCode);

    @GetMapping(value = "/channels/csv", produces = {MediaType.TEXT_PLAIN_VALUE, MediaType.APPLICATION_JSON_VALUE})
    @Valid
    Resource getChannelsCSV();


    @GetMapping(value = "/channels/{channelcode}/paymentserviceproviders", produces = MediaType.APPLICATION_JSON_VALUE)
    @Valid
    ChannelPspList getChannelPaymentServiceProviders(@PathVariable("channelcode") String channelcode,
                                                     @RequestParam(required = false, defaultValue = "50") Integer limit,
                                                     @RequestParam Integer page,
                                                     @RequestParam("pspName") String pspName);

    @GetMapping(value = "/paymentserviceproviders", produces = MediaType.APPLICATION_JSON_VALUE)
    @Valid
    PaymentServiceProviders getPaymentServiceProviders(@RequestParam(required = false, defaultValue = "50") Integer limit,
                                                       @RequestParam(required = false) Integer page,
                                                       @RequestParam(required = false) String code,
                                                       @RequestParam(required = false) String name,
                                                       @RequestParam(required = false) String taxCode);

    @PostMapping(value = "/brokerspsp", produces = MediaType.APPLICATION_JSON_VALUE)
    @Valid
    BrokerPspDetails createBrokerPsp(@RequestBody BrokerPspDetails brokerPspDetails);

    @PostMapping(value = "/paymentserviceproviders", produces = MediaType.APPLICATION_JSON_VALUE)
    @Valid
    PaymentServiceProviderDetails createPaymentServiceProvider(PaymentServiceProviderDetails paymentServiceProviderDetails);


    @GetMapping(value = "/creditorinstitutions/{creditorinstitutioncode}/stations", produces = MediaType.APPLICATION_JSON_VALUE)
    @Valid
    CreditorInstitutionStations getEcStations(@PathVariable("creditorinstitutioncode") String ecCode);

    @PostMapping(value = "/creditorinstitutions/{creditorinstitutioncode}/stations", produces = MediaType.APPLICATION_JSON_VALUE)
    @Valid
    CreditorInstitutionStationEdit createCreditorInstitutionStationRelationship(@PathVariable("creditorinstitutioncode") String ecCode,
                                                                                @RequestBody CreditorInstitutionStationEdit station);

    @GetMapping(value = "/paymentserviceproviders/{pspcode}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Valid
    PaymentServiceProviderDetails getPSPDetails(@PathVariable("pspcode") String pspCode);

    @PostMapping(value = "/creditorinstitutions")
    @Valid
    CreditorInstitutionDetails createCreditorInstitution(@RequestBody CreditorInstitutionDetails request);

    @GetMapping(value = "/creditorinstitutions/{creditorinstitutioncode}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Valid
    CreditorInstitutionDetails getCreditorInstitutionDetails(@PathVariable("creditorinstitutioncode") String creditorInstitutionCode);

    @GetMapping(value = "/creditorinstitutions", produces = MediaType.APPLICATION_JSON_VALUE)
    @Valid
    CreditorInstitutions getCreditorInstitutions(
            @RequestParam(required = false, name = "code") String ciTaxCode,
            @RequestParam(required = false, name = "name") String businessName,
            @RequestParam(required = false, name = "enabled") Boolean enabled,
            @RequestParam(required = false, name = "orderby") ApiConfigCreditorInstitutionsOrderBy orderBy,
            @RequestParam(required = false, name = "ordering", defaultValue = "DESC") String sorting,
            @RequestParam(required = false, defaultValue = "50") Integer limit,
            @RequestParam Integer page
    );

    @PutMapping(value = "/creditorinstitutions/{creditorinstitutioncode}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Valid
    CreditorInstitutionDetails updateCreditorInstitutionDetails(@PathVariable("creditorinstitutioncode") String creditorInstitutionCode,
                                                                @RequestBody CreditorInstitutionDetails request);

    @GetMapping(value = "/brokers/{brokercode}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Valid
    BrokerDetails getBroker(@RequestParam(required = false, name = "brokercode") String brokerCode);

    @PostMapping(value = "/brokers", produces = MediaType.APPLICATION_JSON_VALUE)
    @Valid
    BrokerDetails createBroker(@RequestBody BrokerDetails request);


    @PutMapping(value = "/stations/{stationcode}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Valid
    StationDetails updateStation(@PathVariable("stationcode") String stationCode,
                                 @RequestBody StationDetails stationDetails);

    @GetMapping(value = "/configuration/wfespplugins", produces = MediaType.APPLICATION_JSON_VALUE)
    @Valid
    WfespPluginConfs getWfespPlugins();

    @GetMapping(value = "/stations/{stationcode}/creditorinstitutions", produces = MediaType.APPLICATION_JSON_VALUE)
    @Valid
    CreditorInstitutions getCreditorInstitutionsByStation(@PathVariable(required = false, name = "stationcode") String stationcode,
                                                          @RequestParam(required = false, defaultValue = "50") Integer limit,
                                                          @RequestParam Integer page,
                                                          @RequestParam String ciNameOrCF);

    @DeleteMapping(value = "/creditorinstitutions/{creditorinstitutioncode}/stations/{stationcode}", produces = MediaType.APPLICATION_JSON_VALUE)
    void deleteCreditorInstitutionStationRelationship(@PathVariable("creditorinstitutioncode") String ecCode,
                                                      @PathVariable(required = false, name = "stationcode") String stationcode);


    @PostMapping(value = "/creditorinstitutions/{creditorinstitutioncode}/ibans", produces = MediaType.APPLICATION_JSON_VALUE)
    @Valid
    IbanCreateApiconfig createCreditorInstitutionIbans(@PathVariable("creditorinstitutioncode") String creditorInstitutionCode,
                                                       @RequestBody IbanCreateApiconfig ibanCreate);


    @PutMapping(value = "/creditorinstitutions/{creditorinstitutioncode}/ibans/{ibanId}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Valid
    IbanCreateApiconfig updateCreditorInstitutionIbans(@PathVariable("creditorinstitutioncode") String creditorInstitutionCode,
                                                       @PathVariable("ibanId") String ibanId,
                                                       @RequestBody IbanCreateApiconfig ibanCreate);


    @DeleteMapping(value = "/creditorinstitutions/{creditorinstitutioncode}/ibans/{ibanValue}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Valid
    void deleteCreditorInstitutionIbans(@PathVariable("creditorinstitutioncode") String creditorinstitutioncode,
                                        @PathVariable("ibanValue") String ibanValue);

    @GetMapping(value = "/brokers", produces = MediaType.APPLICATION_JSON_VALUE)
    @RequestLine("getBrokersEC")
    @Retryable(
            exclude = FeignException.FeignClientException.class,
            maxAttemptsExpression = "${retry.utils.maxAttempts}",
            backoff = @Backoff(delayExpression = "${retry.utils.maxDelay}"))
    @Valid
    Brokers getBrokersEC(@RequestParam(required = false, defaultValue = "50") Integer limit,
                         @RequestParam Integer page,
                         @RequestParam(required = false) String code,
                         @RequestParam(required = false) String name,
                         @RequestParam(required = false, defaultValue = "CODE") String orderby,
                         @RequestParam(required = false, defaultValue = "DESC") String ordering);

    @PutMapping(value = "/paymentserviceproviders/{pspcode}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Valid
    PaymentServiceProviderDetails updatePSP(@PathVariable("pspcode") String pspcode,
                                            @RequestBody PaymentServiceProviderDetails paymentServiceProviderDetails);

    @PutMapping(value = "/brokerspsp/{brokerpspcode}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Valid
    BrokerPspDetails updateBrokerPSP(@PathVariable("brokerpspcode") String brokerpspcode,
                                     @RequestBody BrokerPspDetails brokerPspDetails);

    @GetMapping(value = "/creditorinstitutions/view", produces = MediaType.APPLICATION_JSON_VALUE)
    @Retryable(
            exclude = FeignException.FeignClientException.class,
            maxAttemptsExpression = "${retry.utils.maxAttempts}",
            backoff = @Backoff(delayExpression = "${retry.utils.maxDelay}"))
    @Valid
    CreditorInstitutionsView getCreditorInstitutionsAssociatedToBrokerStations(
            @RequestParam(required = false, defaultValue = "50") Integer limit,
            @RequestParam Integer page,
            @RequestParam(required = false, name = "creditorInstitutionCode") String creditorInstitutionCode,
            @RequestParam(required = false, name = "paBrokerCode") String paBrokerCode,
            @RequestParam(required = false, name = "stationCode") String stationCode,
            @RequestParam(required = false, name = "enabled") Boolean enabled,
            @RequestParam(required = false, name = "auxDigit") Long auxDigit,
            @RequestParam(required = false, name = "applicationCode") Long applicationCode,
            @RequestParam(required = false, name = "segregationCode") Long segregationCode,
            @RequestParam(required = false, name = "mod4") Boolean mod4
    );

    @DeleteMapping(value = "/brokerspsp/{brokerTaxCode}", produces = {MediaType.APPLICATION_JSON_VALUE})
    void deleteBrokerPsp(@PathVariable("brokerTaxCode") String brokerTaxCode);

    @DeleteMapping(value = "/brokers/{brokerTaxCode}", produces = {MediaType.APPLICATION_JSON_VALUE})
    void deleteCIBroker(@PathVariable("brokerTaxCode") String brokerTaxCode);

    @PostMapping(value = "/brokers/{broker-code}/station-maintenances", produces = {MediaType.APPLICATION_JSON_VALUE})
    StationMaintenanceResource createStationMaintenance(
            @PathVariable("broker-code") String brokerCode,
            @RequestBody @Valid @NotNull CreateStationMaintenance createStationMaintenance
    );

    @PutMapping(value = "/brokers/{broker-code}/station-maintenances/{maintenance-id}", produces = {MediaType.APPLICATION_JSON_VALUE})
    StationMaintenanceResource updateStationMaintenance(
            @PathVariable("broker-code") String brokerCode,
            @PathVariable("maintenance-id") Long maintenanceId,
            @RequestBody @Valid @NotNull UpdateStationMaintenance updateStationMaintenance
    );

    @GetMapping(value = "/brokers/{broker-code}/station-maintenances/{maintenance-id}", produces = {MediaType.APPLICATION_JSON_VALUE})
    StationMaintenanceResource getStationMaintenance(
            @PathVariable("broker-code") String brokerCode,
            @PathVariable("maintenance-id") Long maintenanceId
    );
}
