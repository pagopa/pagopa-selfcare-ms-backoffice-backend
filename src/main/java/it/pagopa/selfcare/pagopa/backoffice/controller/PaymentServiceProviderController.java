package it.pagopa.selfcare.pagopa.backoffice.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import it.pagopa.selfcare.pagopa.backoffice.model.channels.*;
import it.pagopa.selfcare.pagopa.backoffice.model.connector.channel.PspChannelPaymentTypes;
import it.pagopa.selfcare.pagopa.backoffice.service.PaymentServiceProviderService;
import it.pagopa.selfcare.pagopa.backoffice.util.OpenApiTableMetadata;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotNull;

/**
 * Controller class that expose APIs to manage Payment Service Providers
 */
@RestController
@RequestMapping(value = "/payment-service-providers")
@Tag(name = "Payment Service Providers")
public class PaymentServiceProviderController {

    private final PaymentServiceProviderService paymentServiceProviderService;

    @Autowired
    public PaymentServiceProviderController(PaymentServiceProviderService paymentServiceProviderService) {
        this.paymentServiceProviderService = paymentServiceProviderService;
    }

    @GetMapping(value = "", produces = {MediaType.APPLICATION_JSON_VALUE})
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Get the list of payment service providers", security = {@SecurityRequirement(name = "JWT")})
    @OpenApiTableMetadata(readWriteIntense = OpenApiTableMetadata.ReadWrite.READ)
    public PaymentServiceProvidersResource getPaymentServiceProviders(@Parameter(description = "Number of elements on one page. Default = 50") @RequestParam(required = false, defaultValue = "50") Integer limit,
                                                                      @Parameter(description = "Page number. Page value starts from 0") @RequestParam Integer page,
                                                                      @Parameter(description = "Payment service provider code") @RequestParam(name = "psp-code", required = false) String pspCode,
                                                                      @Parameter(description = "Tax Code of the payment service provider") @RequestParam(name = "tax-code", required = false) String taxCode,
                                                                      @Parameter(description = "Payment service provider name") @RequestParam(required = false) String name) {

        return paymentServiceProviderService.getPaymentServiceProviders(limit, page, pspCode, taxCode, name);
    }

    @GetMapping(value = "/{tax-code}", produces = {MediaType.APPLICATION_JSON_VALUE})
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Get payment service provider's details", security = {@SecurityRequirement(name = "JWT")})
    @OpenApiTableMetadata(readWriteIntense = OpenApiTableMetadata.ReadWrite.READ)
    public BrokerOrPspDetailsResource getBrokerAndPspDetails(@Parameter(description = "Tax Code to use for retrieval of the related Code of the payment service provider") @PathVariable(required = true, name = "tax-code") String brokerTaxCode) {

        return paymentServiceProviderService.getBrokerAndPspDetails(brokerTaxCode);
    }

    @GetMapping(value = "/{tax-code}/channels", produces = {MediaType.APPLICATION_JSON_VALUE})
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Get the channels of the PSP", security = {@SecurityRequirement(name = "JWT")})
    @OpenApiTableMetadata(readWriteIntense = OpenApiTableMetadata.ReadWrite.READ)
    public PspChannelsResource getPspChannels(@Parameter(description = "Tax code of the payment service provider") @PathVariable("tax-code") String pspTaxCode) {

        return paymentServiceProviderService.getPSPChannels(pspTaxCode);
    }

    @GetMapping(value = "/{tax-code}/channels/available-code", produces = {MediaType.APPLICATION_JSON_VALUE})
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Get a valid code for the passed PSP that is not used yet for existing channels", security = {@SecurityRequirement(name = "JWT")})
    @OpenApiTableMetadata(readWriteIntense = OpenApiTableMetadata.ReadWrite.READ)
    public ChannelCodeResource getFirstValidChannelCode(@Parameter(description = "Tax Code of the payment service provider") @PathVariable("tax-code") String taxCode,
                                                        @Parameter(description = "is true if the channel is V2") @RequestParam(required = false, defaultValue = "false") Boolean v2) {

        return paymentServiceProviderService.getFirstValidChannelCode(taxCode, v2);
    }

    @PostMapping(value = "", produces = {MediaType.APPLICATION_JSON_VALUE})
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create a payment service provider", security = {@SecurityRequirement(name = "JWT")})
    @OpenApiTableMetadata(readWriteIntense = OpenApiTableMetadata.ReadWrite.WRITE)
    public PaymentServiceProviderDetailsResource createPSP(@Parameter(description = "If true the PSP will also be signed up as a broker") @RequestParam(required = false, defaultValue = "false") Boolean direct,
                                                           @RequestBody @NotNull PaymentServiceProviderDetailsDto paymentServiceProviderDetailsDto) {

        return paymentServiceProviderService.createPSP(paymentServiceProviderDetailsDto, direct);
    }

    @PutMapping(value = "/{tax-code}", produces = {MediaType.APPLICATION_JSON_VALUE})
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Update payment service provider", security = {@SecurityRequirement(name = "JWT")})
    @OpenApiTableMetadata(readWriteIntense = OpenApiTableMetadata.ReadWrite.WRITE)
    public PaymentServiceProviderDetailsResource updatePSP(
            @Parameter(description = "Tax code of the Payment Service Provider") @PathVariable("tax-code") String pspTaxCode,
            @RequestBody @NotNull PaymentServiceProviderDetailsDto paymentServiceProviderDetailsDto) {

        return this.paymentServiceProviderService.updatePSP(pspTaxCode, paymentServiceProviderDetailsDto);
    }

    @PutMapping(value = "/{tax-code}/channels/{channel-code}")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Update a relation between a PSP and a channel, using the tax code to find related PSP Code", security = {@SecurityRequirement(name = "JWT")})
    @OpenApiTableMetadata(readWriteIntense = OpenApiTableMetadata.ReadWrite.WRITE)
    public PspChannelPaymentTypesResource updatePaymentServiceProvidersChannels(@Parameter(description = "Tax Code to use for retrieval of the related Code of the payment service provider") @PathVariable("tax-code") String taxCode,
                                                                                @Parameter(description = "Channel's unique identifier") @PathVariable("channel-code") String channelCode,
                                                                                @Parameter(description = " List of payment types") @RequestBody PspChannelPaymentTypes pspChannelPaymentTypes) {

        return paymentServiceProviderService.updatePSPChannel(taxCode, channelCode, pspChannelPaymentTypes);
    }

    @DeleteMapping(value = "/{tax-code}/channels/{channel-code}")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Delete a relation between a PSP and a channel", security = {@SecurityRequirement(name = "JWT")})
    @OpenApiTableMetadata(readWriteIntense = OpenApiTableMetadata.ReadWrite.WRITE)
    public void dissociatePSPFromChannel(@Parameter(description = "Tax code of the payment service provider") @PathVariable("tax-code") String pspTaxCode,
                                         @Parameter(description = "Channel's unique identifier") @PathVariable("channel-code") String channelCode) {

        this.paymentServiceProviderService.dissociatePSPFromChannel(pspTaxCode, channelCode);
    }

}
