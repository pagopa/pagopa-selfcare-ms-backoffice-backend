package it.pagopa.selfcare.pagopa.backoffice.connector.rest.client;

import feign.Headers;
import feign.RequestLine;
import it.pagopa.selfcare.pagopa.backoffice.connector.api.ApiConfigConnector;
import it.pagopa.selfcare.pagopa.backoffice.connector.model.broker.BrokerDetails;
import it.pagopa.selfcare.pagopa.backoffice.connector.model.broker.Brokers;
import it.pagopa.selfcare.pagopa.backoffice.connector.model.channel.*;
import it.pagopa.selfcare.pagopa.backoffice.connector.model.creditorInstitution.CreditorInstitutionDetails;
import it.pagopa.selfcare.pagopa.backoffice.connector.model.creditorInstitution.CreditorInstitutions;
import it.pagopa.selfcare.pagopa.backoffice.connector.model.creditorInstitution.IbanCreate;
import it.pagopa.selfcare.pagopa.backoffice.connector.model.creditorInstitution.IbansEnhanced;
import it.pagopa.selfcare.pagopa.backoffice.connector.model.station.CreditorInstitutionStationEdit;
import it.pagopa.selfcare.pagopa.backoffice.connector.model.station.CreditorInstitutionStations;
import it.pagopa.selfcare.pagopa.backoffice.connector.model.station.StationDetails;
import it.pagopa.selfcare.pagopa.backoffice.connector.model.station.Stations;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotNull;

@FeignClient(name = "${rest-client.api-config.serviceCode}", url = "${rest-client.api-config.base-url}")
public interface ApiConfigRestClient extends ApiConfigConnector {

    @GetMapping(value = "${rest-client.api-config.getBrokersPsp.path}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    @RequestLine("getBrokersPsp")
    BrokersPsp getBrokersPsp(@RequestParam(required = false, defaultValue = "50") Integer limit,
                             @RequestParam(required = true) Integer page,
                             @RequestParam(required = false, name = "code") String filterByCode,
                             @RequestParam(required = false, name = "name") String filterByName,
                             @RequestParam(required = false, name = "orderby", defaultValue = "CODE") String orderBy,
                             @RequestParam(required = false, name = "ordering", defaultValue = "DESC") String sort,
                             @RequestHeader(name = "X-Request-Id", required = false) String xRequestId);

    @GetMapping(value = "${rest-client.api-config.getBrokerPsp.path}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    BrokerPspDetails getBrokerPsp(@RequestParam(required = false, name = "brokerpspcode") String brokerpspcode,
                                  @RequestHeader(name = "X-Request-Id", required = false) String xRequestId);

    @GetMapping(value = "${rest-client.api-config.getChannels.path}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    Channels getChannels(@RequestParam(required = false, defaultValue = "50") Integer limit,
                         @RequestParam(required = true) Integer page,
                         @RequestParam(required = false) String code,
                         @RequestParam(required = false) String brokercode,
                         @RequestParam(required = false, name = "ordering", defaultValue = "DESC") String sort,
                         @RequestHeader(name = "X-Request-Id", required = false) String xRequestId);

    @PostMapping(value = "${rest-client.api-config.createChannel.path}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    ChannelDetails createChannel(@RequestBody @NotNull ChannelDetails detailsDto,
                                 @RequestHeader(name = "X-Request-Id", required = false) String xRequestId);

    @PutMapping(value = "${rest-client.api-config.updateChannel.path}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    ChannelDetails updateChannel(@RequestBody ChannelDetails channelDetails,
                                 @PathVariable("channelcode") String channelCode,
                                 @RequestHeader(name = "X-Request-Id", required = false) String xRequestId);

    @GetMapping(value = "${rest-client.api-config.getPspChannels.path}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    PspChannels getPspChannels(@PathVariable("pspcode") String pspCode,
                               @RequestHeader(name = "X-Request-Id", required = false) String xRequestId);

    @GetMapping(value = "${rest-client.api-config.getChannelDetails.path}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    ChannelDetails getChannelDetails(@PathVariable("channelcode") String channelcode,
                                     @RequestHeader(name = "X-Request-Id", required = false) String xRequestId);

    @PostMapping(value = "${rest-client.api-config.createChannelPaymentType.path}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    PspChannelPaymentTypes createChannelPaymentType(@RequestBody PspChannelPaymentTypes pspChannelPaymentTypes,
                                                    @PathVariable("channelcode") String channelCode,
                                                    @RequestHeader(name = "X-Request-Id", required = false) String xRequestId);

    @GetMapping(value = "${rest-client.api-config.getPaymentTypes.path}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    PaymentTypes getPaymentTypes(@RequestHeader(name = "X-Request-Id", required = false) String xRequestId);


    @GetMapping(value = "${rest-client.api-config.getChannelPaymentTypes.path}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    PspChannelPaymentTypes getChannelPaymentTypes(@PathVariable("channelcode") String channelCode,
                                                  @RequestHeader(name = "X-Request-Id", required = false) String xRequestId);

    @DeleteMapping(value = "${rest-client.api-config.deletePaymentType.path}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    void deleteChannelPaymentType(@PathVariable("channelcode") String channelCode,
                                  @PathVariable("paymenttypecode") String paymentTypeCode,
                                  @RequestHeader(name = "X-Request-Id", required = false) String xRequestId);

    @DeleteMapping(value = "${rest-client.api-config.deletePaymentServiceProvidersChannels.path}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    void deletePaymentServiceProvidersChannels(@PathVariable("pspcode") String pspCode, @PathVariable("channelcode") String channelCode,
                                               @RequestHeader(name = "X-Request-Id", required = false) String xRequestId);

    @PutMapping(value = "${rest-client.api-config.deletePaymentServiceProvidersChannels.path}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    PspChannelPaymentTypes updatePaymentServiceProvidersChannels(@PathVariable("pspcode") String pspCode, @PathVariable("channelcode") String channelCode,
                                                                 @RequestBody PspChannelPaymentTypes pspChannelPaymentTypes,
                                                                 @RequestHeader(name = "X-Request-Id", required = false) String xRequestId);

    @DeleteMapping(value = "${rest-client.api-config.deleteChannel.path}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    void deleteChannel(@PathVariable("channelcode") String channelCode, @RequestHeader(name = "X-Request-Id", required = false) String xRequestId);

    @GetMapping(value = "${rest-client.api-config.getStations.path}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    Stations getStations(@RequestParam(required = false, defaultValue = "50") Integer limit,
                         @RequestParam(required = true) Integer page,
                         @RequestParam(required = false, name = "ordering", defaultValue = "DESC") String sort,
                         @RequestParam(name = "brokercode", required = false) String brokerCode,
                         @RequestParam(name = "creditorinstitutioncode", required = false) String creditorInstitutionCode,
                         @RequestParam(required = false) String code,
                         @RequestHeader(name = "X-Request-Id", required = false) String xRequestId);

    @GetMapping(value = "${rest-client.api-config.getStation.path}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    StationDetails getStation(@PathVariable("stationcode") String stationCode,
                              @RequestHeader(name = "X-Request-Id", required = false) String xRequestId);

    @PostMapping(value = "${rest-client.api-config.createStation.path}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    StationDetails createStation(@RequestBody @NotNull StationDetails stationDetails,
                                 @RequestHeader(name = "X-Request-Id", required = false) String xRequestId);

    @GetMapping(value = "${rest-client.api-config.getPspBrokerPsp.path}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    PaymentServiceProviders getPspBrokerPsp(@RequestParam(required = false, defaultValue = "50") Integer limit,
                                            @RequestParam Integer page,
                                            @PathVariable("brokerpspcode") String brokerPspCode,
                                            @RequestHeader(name = "X-Request-Id", required = false) String xRequestId);

    @GetMapping(value = "${rest-client.api-config.getChannelsCSV.path}", produces = {MediaType.TEXT_PLAIN_VALUE, MediaType.APPLICATION_JSON_VALUE})
    @ResponseBody
    Resource getChannelsCSV(String uuid);


    @GetMapping(value = "${rest-client.api-config.getChannelPaymentServiceProviders.path}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    ChannelPspList getChannelPaymentServiceProviders(@RequestParam(required = false, defaultValue = "50") Integer limit,
                                                     @RequestParam Integer page,
                                                     @PathVariable("channelcode") String channelcode,
                                                     @RequestHeader(name = "X-Request-Id", required = false) String xRequestId);

    @PostMapping(value = "${rest-client.api-config.createBrokerPsp.path}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    BrokerPspDetails createBrokerPsp(@RequestBody BrokerPspDetails brokerPspDetails, @RequestHeader(name = "X-Request-Id", required = false) String xRequestId);

    @PostMapping(value = "${rest-client.api-config.createPaymentServiceProvider.path}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    PaymentServiceProviderDetails createPaymentServiceProvider(PaymentServiceProviderDetails paymentServiceProviderDetails, @RequestHeader(name = "X-Request-Id", required = false) String xRequestId);


    @GetMapping(value = "${rest-client.api-config.getCreditorInstitutionStations.path}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    CreditorInstitutionStations getEcStations(@PathVariable("creditorinstitutioncode") String ecCode, @RequestHeader(name = "X-Request-Id", required = false) String xRequestId);

    @PostMapping(value = "${rest-client.api-config.createCreditorInstitutionStationRelationship.path}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    CreditorInstitutionStationEdit createCreditorInstitutionStationRelationship(@PathVariable("creditorinstitutioncode") String ecCode,
                                                                                @RequestBody CreditorInstitutionStationEdit station,
                                                                                @RequestHeader(name = "X-Request-Id", required = false) String xRequestId);

    @GetMapping(value = "${rest-client.api-config.getPSPDetails.path}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    PaymentServiceProviderDetails getPSPDetails(@PathVariable("pspcode") String pspCode,
                                                @RequestHeader(name = "X-Request-Id", required = false) String xRequestId);

    @PostMapping(value = "${rest-client.api-config.createCreditorInstitution.path}")
    CreditorInstitutionDetails createCreditorInstitution(@RequestBody CreditorInstitutionDetails request,
                                                         @RequestHeader(name = "X-Request-Id", required = false) String xRequestId);

    @GetMapping(value = "${rest-client.api-config.getCreditorInstitutionDetails.path}", produces = MediaType.APPLICATION_JSON_VALUE)
    CreditorInstitutionDetails getCreditorInstitutionDetails(@PathVariable("creditorinstitutioncode") String creditorInstitutionCode,
                                                             @RequestHeader(name = "X-Request-Id", required = false) String xRequestId);

    @GetMapping(value = "${rest-client.api-config.getCreditorInstitutions.path}", produces = MediaType.APPLICATION_JSON_VALUE)
    CreditorInstitutions getCreditorInstitutions(@RequestParam(required = false, defaultValue = "50") Integer limit,
                                                 @RequestParam(required = true) Integer page,
                                                 @RequestParam(required = false, name = "code") String ecCode,
                                                 @RequestParam(required = false, name = "name") String name,
                                                 @RequestParam(required = false, name = "ordering", defaultValue = "DESC") String sorting,
                                                 @RequestHeader(required = false, name = "X-Request-Id") String xRequestId);

    @PutMapping(value = "${rest-client.api-config.updateCreditorInstitutionDetails.path}", produces = MediaType.APPLICATION_JSON_VALUE)
    CreditorInstitutionDetails updateCreditorInstitutionDetails(@PathVariable("creditorinstitutioncode") String creditorInstitutionCode,
                                                                @RequestBody CreditorInstitutionDetails request,
                                                                @RequestHeader(name = "X-Request-Id", required = false) String xRequestId);

    @PostMapping(value = "${rest-client.api-config.createBroker.path}", produces = MediaType.APPLICATION_JSON_VALUE)
    BrokerDetails createBroker(@RequestBody BrokerDetails request,
                               @RequestHeader(name = "X-Request-Id", required = false) String xRequestId);


    @PutMapping(value = "${rest-client.api-config.updateStation.path}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    StationDetails updateStation(@PathVariable("stationcode") String stationCode,
                                 @RequestBody StationDetails stationDetails,
                                 @RequestHeader(name = "X-Request-Id", required = false) String xRequestId);

    @GetMapping(value = "${rest-client.api-config.getWfespPlugins.path}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    WfespPluginConfs getWfespPlugins(@RequestHeader(name = "X-Request-Id", required = false) String xRequestId);

    @GetMapping(value = "${rest-client.api-config.getCreditorInstitutionsByStation.path}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    CreditorInstitutions getCreditorInstitutionsByStation(@PathVariable(required = false, name = "stationcode") String stationcode,
                                                          @RequestParam(required = false, defaultValue = "50") Integer limit,
                                                          @RequestParam Integer page,
                                                          @RequestHeader(name = "X-Request-Id", required = false) String xRequestId);

    @DeleteMapping(value = "${rest-client.api-config.deleteCreditorInstitutionStationRelationship.path}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    void deleteCreditorInstitutionStationRelationship(@PathVariable("creditorinstitutioncode") String ecCode,
                                                      @PathVariable(required = false, name = "stationcode") String stationcode,
                                                      @RequestHeader(name = "X-Request-Id", required = false) String xRequestId);

    @GetMapping(value = "${rest-client.api-config.getCreditorInstitutionIbans-enhanced.path}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    IbansEnhanced getCreditorInstitutionIbans(@PathVariable("creditorinstitutioncode") String creditorInstitutionCode,
                                              @RequestParam String label,
                                              @RequestHeader(name = "X-Request-Id", required = false) String xRequestId);

    @PostMapping(value = "${rest-client.api-config.createCreditorInstitutionIbans.path}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    IbanCreate createCreditorInstitutionIbans(@PathVariable("creditorinstitutioncode") String creditorInstitutionCode,
                                              @RequestBody IbanCreate ibanCreate,
                                              @RequestHeader(name = "X-Request-Id", required = false) String xRequestId);


    @PutMapping(value = "${rest-client.api-config.updateCreditorInstitutionIbans.path}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    IbanCreate updateCreditorInstitutionIbans(@PathVariable("creditorinstitutioncode")String creditorInstitutionCode,
                                           @PathVariable("ibanId")String ibanId,
                                              @RequestBody IbanCreate ibanCreate,
                                              @RequestHeader(name = "X-Request-Id", required = false)String xRequestId);


    @DeleteMapping(value = "${rest-client.api-config.deleteCreditorInstitutionIbans.path}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    void deleteCreditorInstitutionIbans(@PathVariable("creditorinstitutioncode")String creditorinstitutioncode,
                                        @PathVariable("ibanValue")String ibanValue,
                                        @RequestHeader(name = "X-Request-Id", required = false)String xRequestId);

    @GetMapping(value = "${rest-client.api-config.getStationBroker.path}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    @RequestLine("getBrokersEC")
    Brokers getBrokersEC(@RequestParam(required = false, defaultValue = "50") Integer limit,
                         @RequestParam Integer page,
                         @RequestParam(required = false) String code,
                         @RequestParam(required = false) String name,
                         @RequestParam(required = false, defaultValue = "CODE") String orderby,
                         @RequestParam(required = false, defaultValue = "DESC") String ordering,
                         @RequestHeader(name = "X-Request-Id", required = false) String xRequestId);

}
