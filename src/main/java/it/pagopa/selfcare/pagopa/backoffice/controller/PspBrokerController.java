package it.pagopa.selfcare.pagopa.backoffice.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import it.pagopa.selfcare.pagopa.backoffice.model.ProblemJson;
import it.pagopa.selfcare.pagopa.backoffice.model.channels.BrokerPspDetailsDto;
import it.pagopa.selfcare.pagopa.backoffice.model.channels.BrokerPspDetailsResource;
import it.pagopa.selfcare.pagopa.backoffice.model.channels.BrokersPspResource;
import it.pagopa.selfcare.pagopa.backoffice.model.channels.ChannelDetailsResourceList;
import it.pagopa.selfcare.pagopa.backoffice.model.channels.PaymentServiceProvidersResource;
import it.pagopa.selfcare.pagopa.backoffice.service.PspBrokerService;
import it.pagopa.selfcare.pagopa.backoffice.util.OpenApiTableMetadata;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.constraints.NotNull;

@RestController
@RequestMapping(value = "/psp-brokers")
@Tag(name = "Payment Service Provider's Brokers")
public class PspBrokerController {

    private final PspBrokerService pspBrokerService;

    @Autowired
    public PspBrokerController(PspBrokerService pspBrokerService) {
        this.pspBrokerService = pspBrokerService;
    }

    @GetMapping(value = "", produces = {MediaType.APPLICATION_JSON_VALUE})
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Get the list of all PSP brokers", security = {@SecurityRequirement(name = "JWT")})
    @OpenApiTableMetadata
    public BrokersPspResource getBrokersPsp(
            @Parameter(description = "") @RequestParam(required = false, defaultValue = "50") Integer limit,
            @Parameter(description = "Page number. Page value starts from 0") @RequestParam Integer page,
            @Parameter(description = "Broker's code") @RequestParam(name = "brokerTaxCode") String filterByCode,
            @Parameter(description = "Broker's name") @RequestParam(required = false, name = "name") String filterByName,
            @Parameter(description = "Order by column name") @RequestParam(required = false, name = "orderby", defaultValue = "CODE") String orderBy,
            @Parameter(description = "Method of sorting") @RequestParam(required = false, value = "sorting", defaultValue = "DESC") String sorting
    ) {
        return pspBrokerService.getBrokersForPSP(limit, page, filterByCode, filterByName, orderBy, sorting);
    }

    @GetMapping(value = "/{broker-tax-code}", produces = {MediaType.APPLICATION_JSON_VALUE})
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Get the detail of a PSP broker", security = {@SecurityRequirement(name = "JWT")})
    @OpenApiTableMetadata
    public BrokerPspDetailsResource getBrokerPsp(
            @Parameter(description = "Broker's code") @PathVariable(required = false, name = "broker-tax-code") String brokerTaxCode
    ) {
        return pspBrokerService.getBrokerForPsp(brokerTaxCode);
    }

    @PostMapping(value = "", produces = {MediaType.APPLICATION_JSON_VALUE})
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create a new PSP broker", security = {@SecurityRequirement(name = "JWT")})
    @OpenApiTableMetadata
    public BrokerPspDetailsResource createBroker(@RequestBody @NotNull BrokerPspDetailsDto brokerPspDetailsDto) {
        return pspBrokerService.createBrokerForPSP(brokerPspDetailsDto);
    }

    @PutMapping(value = "/{broker-tax-code}", produces = {MediaType.APPLICATION_JSON_VALUE})
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Update Broker payment service provider", security = {@SecurityRequirement(name = "JWT")})
    @OpenApiTableMetadata
    public BrokerPspDetailsResource updateBrokerPSP(
            @Parameter(description = "Broker code")
            @PathVariable("broker-tax-code") String brokerTaxCode,
            @RequestBody @NotNull BrokerPspDetailsDto brokerPspDetailsDto
    ) {
        return pspBrokerService.updateBrokerPSP(brokerTaxCode, brokerPspDetailsDto);
    }


    @GetMapping(value = "/{broker-tax-code}/channels", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Get paginated list of PSP channels by given broker", security = {@SecurityRequirement(name = "JWT")})
    @OpenApiTableMetadata
    public ChannelDetailsResourceList getChannelListByBroker(
            @PathVariable("broker-tax-code") String brokerTaxCode,
            @RequestParam(required = false) String channelId,
            @RequestParam(required = false, defaultValue = "10") Integer limit,
            @RequestParam(required = false, defaultValue = "0") Integer page
    ) {
        return pspBrokerService.getChannelByBroker(brokerTaxCode, channelId, limit, page);
    }


    @GetMapping(value = "/{broker-tax-code}/payment-service-providers", produces = {MediaType.APPLICATION_JSON_VALUE})
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Get the list of payment service providers related to a broker", security = {@SecurityRequirement(name = "JWT")})
    @OpenApiTableMetadata
    public PaymentServiceProvidersResource getPspBrokerPsp(
            @Parameter(description = "") @RequestParam(required = false, defaultValue = "50") Integer limit,
            @Parameter(description = "Page number. Page value starts from 0") @RequestParam Integer page,
            @Parameter(description = "Broker code of a PSP") @PathVariable("broker-tax-code") String brokerTaxCode
    ) {
        return pspBrokerService.getPSPAssociatedToBroker(brokerTaxCode, limit, page);
    }

    /**
     * Deletes the Payment Service Provider's broker
     *
     * @param brokerTaxCode Tax code of the broker to delete
     */
    @DeleteMapping(value = "/{broker-tax-code}", produces = {MediaType.APPLICATION_JSON_VALUE})
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Deletes the Payment Service Provider's broker", security = {@SecurityRequirement(name = "JWT")})
    @OpenApiTableMetadata
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK", content = @Content(schema = @Schema())),
            @ApiResponse(responseCode = "400", description = "Bad Request", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ProblemJson.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content(schema = @Schema())),
            @ApiResponse(responseCode = "403", description = "Forbidden", content = @Content(schema = @Schema())),
            @ApiResponse(responseCode = "404", description = "Not Found", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ProblemJson.class))),
            @ApiResponse(responseCode = "500", description = "Service unavailable", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ProblemJson.class)))
    })
    public void deletePspBroker(
            @Parameter(description = "Broker tax code") @PathVariable("broker-tax-code") String brokerTaxCode
    ) {
        this.pspBrokerService.deletePspBroker(brokerTaxCode);
    }
}
