package it.pagopa.selfcare.pagopa.backoffice.connector.rest.client;

import it.pagopa.selfcare.pagopa.backoffice.connector.api.ApiConfigConnector;
import it.pagopa.selfcare.pagopa.backoffice.connector.model.channel.*;
import it.pagopa.selfcare.pagopa.backoffice.connector.model.station.StationDetail;
import it.pagopa.selfcare.pagopa.backoffice.connector.model.station.Stations;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotNull;

@FeignClient(name = "${rest-client.api-config.serviceCode}", url = "${rest-client.api-config.base-url}")
public interface ApiConfigRestClient extends ApiConfigConnector {

    @GetMapping(value = "${rest-client.api-config.getChannels.path}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    Channels getChannels(@RequestParam(required = false, defaultValue = "50") Integer limit,
                         @RequestParam(required = true) Integer page,
                         @RequestParam(required = false) String code,
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
                         @RequestParam(name = "brokerCode", required = false) String brokerCode,
                         @RequestParam(name = "creditorInstitutionCode", required = false) String creditorInstitutionCode,
                         @RequestParam(required = false) String code,
                         @RequestHeader(name = "X-Request-Id", required = false) String xRequestId);

    @GetMapping(value = "${rest-client.api-config.getStation.path}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    StationDetail getStation(@PathVariable("stationcode") String stationCode,
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

    @GetMapping(value = "${rest-client.api-config.getPSPDetails.path}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    PaymentServiceProviderDetails getPSPDetails(@PathVariable("pspcode") String pspCode,
                                                @RequestHeader(name = "X-Request-Id", required = false) String xRequestId);
}
