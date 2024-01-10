package it.pagopa.selfcare.pagopa.backoffice.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import it.pagopa.selfcare.pagopa.backoffice.model.channels.*;
import it.pagopa.selfcare.pagopa.backoffice.service.PspBrokerService;
import it.pagopa.selfcare.pagopa.backoffice.util.OpenApiTableMetadata;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotNull;

@RestController
@RequestMapping(value = "/psp-brokers")
@Tag(name = "Payment Service Provider's Brokers")
public class PspBrokerController {

    @Autowired
    private PspBrokerService pspBrokerService;

    @GetMapping(value = "", produces = {MediaType.APPLICATION_JSON_VALUE})
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Get the list of all PSP brokers", security = {@SecurityRequirement(name = "JWT")})
    @OpenApiTableMetadata
    public BrokersPspResource getBrokersPsp(@Parameter(description = "") @RequestParam(required = false, defaultValue = "50") Integer limit,
                                            @Parameter(description = "Page number. Page value starts from 0") @RequestParam Integer page,
                                            @Parameter(description = "Broker's code") @RequestParam(name = "broker-code") String filterByCode,
                                            @Parameter(description = "Broker's name") @RequestParam(required = false, name = "name") String filterByName,
                                            @Parameter(description = "Order by column name") @RequestParam(required = false, name = "orderby", defaultValue = "CODE") String orderBy,
                                            @Parameter(description = "Method of sorting") @RequestParam(required = false, value = "sorting", defaultValue = "DESC") String sorting) {

        return pspBrokerService.getBrokersForPSP(limit, page, filterByCode, filterByName, orderBy, sorting);
    }

    @GetMapping(value = "/{broker-code}", produces = {MediaType.APPLICATION_JSON_VALUE})
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Get the detail of a PSP broker", security = {@SecurityRequirement(name = "JWT")})
    @OpenApiTableMetadata
    public BrokerPspDetailsResource getBrokerPsp(@Parameter(description = "Broker's code") @PathVariable(required = false, name = "broker-code") String brokerPspCode) {

        return pspBrokerService.getBrokerForPsp(brokerPspCode);
    }

    @PostMapping(value = "", produces = {MediaType.APPLICATION_JSON_VALUE})
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create a new PSP broker", security = {@SecurityRequirement(name = "JWT")})
    @OpenApiTableMetadata
    public BrokerPspDetailsResource createBroker(@RequestBody @NotNull BrokerPspDetailsDto brokerPspDetailsDto) {

        return pspBrokerService.createBrokerForPSP(brokerPspDetailsDto);
    }

    @PutMapping(value = "/{broker-code}", produces = {MediaType.APPLICATION_JSON_VALUE})
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Update Broker payment service provider", security = {@SecurityRequirement(name = "JWT")})
    @OpenApiTableMetadata
    public BrokerPspDetailsResource updateBrokerPSP(@Parameter(description = "Broker code")
                                                    @PathVariable("broker-code") String brokerCode,
                                                    @RequestBody @NotNull BrokerPspDetailsDto brokerPspDetailsDto) {
        return pspBrokerService.updateBrokerPSP(brokerCode, brokerPspDetailsDto);
    }


    @GetMapping(value = "/{broker-code}/channels", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Get paginated list of PSP channels by given broker", security = {@SecurityRequirement(name = "JWT")})
    @OpenApiTableMetadata
    public ChannelDetailsResourceList getChannelListByBroker(@PathVariable("broker-code") String brokerCode,
                                                             @RequestParam(required = false) String channelId,
                                                             @RequestParam(required = false, defaultValue = "10") Integer limit,
                                                             @RequestParam(required = false, defaultValue = "0") Integer page) {

        return pspBrokerService.getChannelByBroker(brokerCode, channelId, limit, page);
    }


    @GetMapping(value = "/{broker-code}/payment-service-providers", produces = {MediaType.APPLICATION_JSON_VALUE})
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Get the list of payment service providers related to a broker", security = {@SecurityRequirement(name = "JWT")})
    @OpenApiTableMetadata
    public PaymentServiceProvidersResource getPspBrokerPsp(@Parameter(description = "") @RequestParam(required = false, defaultValue = "50") Integer limit,
                                                           @Parameter(description = "Page number. Page value starts from 0") @RequestParam Integer page,
                                                           @Parameter(description = "Broker code of a PSP") @PathVariable("broker-code") String brokerCode) {

        return pspBrokerService.getPSPAssociatedToBroker(brokerCode, limit, page);
    }


}
