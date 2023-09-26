package it.pagopa.selfcare.pagopa.backoffice.web.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import it.pagopa.selfcare.pagopa.backoffice.connector.model.channel.BrokerPspDetails;
import it.pagopa.selfcare.pagopa.backoffice.connector.model.channel.PaymentServiceProviderDetails;
import it.pagopa.selfcare.pagopa.backoffice.core.ApiConfigService;
import it.pagopa.selfcare.pagopa.backoffice.web.model.channels.BrokerOrPspDetailsResource;
import it.pagopa.selfcare.pagopa.backoffice.web.model.channels.BrokerPspDetailsResource;
import it.pagopa.selfcare.pagopa.backoffice.web.model.channels.PaymentServiceProviderDetailsResource;
import it.pagopa.selfcare.pagopa.backoffice.web.model.mapper.ChannelMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Slf4j
@RestController
@RequestMapping(value = "/utils", produces = MediaType.APPLICATION_JSON_VALUE)
@Api(tags = "Utils")
public class UtilsController {

    private final ApiConfigService apiConfigService;

    @Autowired
    public UtilsController(ApiConfigService apiConfigService) {
        this.apiConfigService = apiConfigService;
    }

    @GetMapping(value = "/broker-or-psp-details", produces = {MediaType.APPLICATION_JSON_VALUE})
    @ResponseStatus(HttpStatus.OK)
    @ApiOperation(value = "", notes = "${swagger.api.channels.getBrokerPsp}")
    public BrokerOrPspDetailsResource getBrokerOrPspDetails(@ApiParam("swagger.request.brokerpspcode")
                                                 @RequestParam(required = false, name = "brokerpspcode") String brokerPspCode) {
        
        

        BrokerPspDetails brokerPspDetails = apiConfigService.getBrokerPsp(brokerPspCode);
        BrokerPspDetailsResource brokerPspDetailsResource = ChannelMapper.toResource(brokerPspDetails);

        PaymentServiceProviderDetails paymentServiceProviderDetails = apiConfigService.getPSPDetails(brokerPspCode);
        PaymentServiceProviderDetailsResource paymentServiceProviderDetailsResource = ChannelMapper.toResource(paymentServiceProviderDetails);

        BrokerOrPspDetailsResource resource = new BrokerOrPspDetailsResource();
        resource.setBrokerPspDetailsResource(brokerPspDetailsResource);
        resource.setPaymentServiceProviderDetailsResource(paymentServiceProviderDetailsResource);

        
        

        return resource;
    }
}
