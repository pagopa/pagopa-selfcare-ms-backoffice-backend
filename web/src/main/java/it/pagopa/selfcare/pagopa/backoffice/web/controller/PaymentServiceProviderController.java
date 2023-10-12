package it.pagopa.selfcare.pagopa.backoffice.web.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import it.pagopa.selfcare.pagopa.backoffice.connector.model.channel.PaymentServiceProviderDetails;
import it.pagopa.selfcare.pagopa.backoffice.core.ApiConfigService;
import it.pagopa.selfcare.pagopa.backoffice.web.model.channels.PaymentServiceProviderDetailsDto;
import it.pagopa.selfcare.pagopa.backoffice.web.model.channels.PaymentServiceProviderDetailsResource;
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
    @ApiOperation(value = "", notes = "${swagger.api.channels.createChannel}")
    public PaymentServiceProviderDetailsResource updatePSP(@ApiParam("${swagger.request.pspCode}")
                                                           @PathVariable("pspcode") String pspcode,
                                                           @RequestBody @NotNull PaymentServiceProviderDetailsDto paymentServiceProviderDetailsDto) {


        PaymentServiceProviderDetails paymentServiceProviderDetails = ChannelMapper.fromPaymentServiceProviderDetailsDto(paymentServiceProviderDetailsDto);
        PaymentServiceProviderDetails response = apiConfigService.updatePSP(pspcode, paymentServiceProviderDetails);

        PaymentServiceProviderDetailsResource resource = ChannelMapper.toResource(response);

        return resource;
    }
}
