package it.pagopa.selfcare.pagopa.backoffice.connector.rest.client;

import it.pagopa.selfcare.pagopa.backoffice.connector.api.ApiConfigConnector;
import it.pagopa.selfcare.pagopa.backoffice.connector.model.channel.*;
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

    @GetMapping(value = "${rest-client.api-config.getPspBrokerPsp.path}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    PaymentServiceProviders getPspBrokerPsp(@RequestParam(required = false, defaultValue = "50") Integer limit,
                                            @RequestParam Integer page,
                                            @PathVariable("brokerpspcode") String brokerPspCode,
                                            @RequestHeader(name = "X-Request-Id", required = false) String xRequestId);

    @GetMapping(value ="${rest-client.api-config.getChannelsCSV.path}", produces = {MediaType.TEXT_PLAIN_VALUE,MediaType.APPLICATION_JSON_VALUE})
    @ResponseBody
    Resource getChannelsCSV(String uuid);
}
