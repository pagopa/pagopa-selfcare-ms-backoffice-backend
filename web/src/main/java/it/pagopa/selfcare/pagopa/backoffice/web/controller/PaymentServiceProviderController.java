package it.pagopa.selfcare.pagopa.backoffice.web.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import it.pagopa.selfcare.pagopa.backoffice.connector.model.channel.BrokerPspDetails;
import it.pagopa.selfcare.pagopa.backoffice.connector.model.channel.PaymentServiceProviderDetails;
import it.pagopa.selfcare.pagopa.backoffice.connector.model.channel.PaymentServiceProviders;
import it.pagopa.selfcare.pagopa.backoffice.core.ApiConfigService;
import it.pagopa.selfcare.pagopa.backoffice.web.model.channels.*;
import it.pagopa.selfcare.pagopa.backoffice.web.model.mapper.ChannelMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotNull;

@Slf4j
@RestController
@RequestMapping(value = "/payment-service-provider", produces = MediaType.APPLICATION_JSON_VALUE)
@Api(tags = "PSP")
public class PaymentServiceProviderController {

    private final ApiConfigService apiConfigService;

    @Autowired
    public PaymentServiceProviderController(ApiConfigService apiConfigService){
        this.apiConfigService = apiConfigService;
    }

    @PutMapping(value = "/{pspcode}", produces = {MediaType.APPLICATION_JSON_VALUE})
    @ResponseStatus(HttpStatus.OK)
    @ApiOperation(value = "", notes = "${swagger.api.payment-service-provider.updatePSP}")
    public PaymentServiceProviderDetailsResource updatePSP(@ApiParam("${swagger.request.pspCode}")
                                                           @PathVariable("pspcode") String pspcode,
                                                           @RequestBody @NotNull PaymentServiceProviderDetailsDto paymentServiceProviderDetailsDto) {

        PaymentServiceProviderDetails paymentServiceProviderDetails = ChannelMapper.fromPaymentServiceProviderDetailsDto(paymentServiceProviderDetailsDto);
        PaymentServiceProviderDetails response = apiConfigService.updatePSP(pspcode, paymentServiceProviderDetails);
        return ChannelMapper.toResource(response);
    }

    @PutMapping(value = "/brokerpsp/{brokercode}", produces = {MediaType.APPLICATION_JSON_VALUE})
    @ResponseStatus(HttpStatus.OK)
    @ApiOperation(value = "", notes = "${swagger.api.payment-service-provider.updateBrokerPSP}")
    public BrokerPspDetailsResource updateBrokerPSP(@ApiParam("${swagger.request.brokercode}")
                                                           @PathVariable("brokercode") String brokercode,
                                                           @RequestBody @NotNull BrokerPspDetailsDto brokerPspDetailsDto) {

        BrokerPspDetails brokerPspDetails = ChannelMapper.fromBrokerPspDetailsDto(brokerPspDetailsDto);
        BrokerPspDetails response = apiConfigService.updateBrokerPSP(brokercode, brokerPspDetails);
        return ChannelMapper.toResource(response);
    }

    @GetMapping(value = "/paymentserviceproviders", produces = {MediaType.APPLICATION_JSON_VALUE})
    @ResponseStatus(HttpStatus.OK)
    @ApiOperation(value = "", notes = "${swagger.api.channels.getPspBrokerPsp}")
    public PaymentServiceProvidersResource getPaymentServiceProviders(@ApiParam("${swagger.request.limit}")
                                                           @RequestParam(required = false, defaultValue = "50") Integer limit,
                                                           @ApiParam("${swagger.request.page}")
                                                           @RequestParam Integer page,
                                                           @ApiParam("${swagger.request.pspCode}")
                                                           @RequestParam(required = false) String pspCode,
                                                           @ApiParam("${swagger.request.taxCode}")
                                                           @RequestParam(required = false) String taxCode,
                                                           @ApiParam("${swagger.request.name}")
                                                           @RequestParam(required = false) String name) {

        PaymentServiceProviders response = apiConfigService.getPaymentServiceProviders(limit, page, pspCode, name, taxCode);

        return ChannelMapper.toResource(response);
    }

}
