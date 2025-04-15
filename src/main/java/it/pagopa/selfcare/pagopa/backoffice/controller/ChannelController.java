package it.pagopa.selfcare.pagopa.backoffice.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import it.pagopa.selfcare.pagopa.backoffice.entity.WrapperEntities;
import it.pagopa.selfcare.pagopa.backoffice.model.ProblemJson;
import it.pagopa.selfcare.pagopa.backoffice.model.channels.*;
import it.pagopa.selfcare.pagopa.backoffice.model.connector.channel.ChannelDetails;
import it.pagopa.selfcare.pagopa.backoffice.model.connector.channel.PspChannelPaymentTypes;
import it.pagopa.selfcare.pagopa.backoffice.model.connector.wrapper.ConfigurationStatus;
import it.pagopa.selfcare.pagopa.backoffice.security.JwtSecurity;
import it.pagopa.selfcare.pagopa.backoffice.service.ChannelService;
import it.pagopa.selfcare.pagopa.backoffice.util.OpenApiTableMetadata;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@Slf4j
@RestController
@RequestMapping(value = "/channels", produces = MediaType.APPLICATION_JSON_VALUE)
@Tag(name = "Channels")
public class ChannelController {

    private final ChannelService channelService;

    @Autowired
    public ChannelController(ChannelService channelService) {
        this.channelService = channelService;
    }

    @GetMapping("")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Get paginated list of channels", security = {@SecurityRequirement(name = "JWT")})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = WrapperChannelsResource.class))),
            @ApiResponse(responseCode = "400", description = "Bad Request", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ProblemJson.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content(schema = @Schema())),
            @ApiResponse(responseCode = "429", description = "Too many requests", content = @Content(schema = @Schema())),
            @ApiResponse(responseCode = "500", description = "Service unavailable", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ProblemJson.class)))
    })
    @OpenApiTableMetadata(readWriteIntense = OpenApiTableMetadata.ReadWrite.READ)
    public WrapperChannelsResource getChannels(
            @Parameter(description = "Channel's status") @RequestParam ConfigurationStatus status,
            @Parameter(description = "Channel's 'code, to filter out result") @RequestParam(required = false) String channelCode,
            @Parameter(description = "Broker code, to filter out result") @RequestParam(required = false, value = "brokerCode") String brokerCode,
            @Parameter(description = "Number of elements on one page") @RequestParam(required = false, defaultValue = "50") @Positive Integer limit,
            @Parameter(description = "Page number") @RequestParam(required = false, defaultValue = "0") @PositiveOrZero Integer page
    ) {
        return this.channelService.getChannels(status, channelCode, brokerCode, limit, page);
    }

    @GetMapping(value = "/{channel-code}", produces = {MediaType.APPLICATION_JSON_VALUE})
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Get channel's details", security = {@SecurityRequirement(name = "JWT")})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ChannelDetailsResource.class))),
            @ApiResponse(responseCode = "400", description = "Bad Request", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ProblemJson.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content(schema = @Schema())),
            @ApiResponse(responseCode = "429", description = "Too many requests", content = @Content(schema = @Schema())),
            @ApiResponse(responseCode = "500", description = "Service unavailable", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ProblemJson.class)))
    })
    @OpenApiTableMetadata(readWriteIntense = OpenApiTableMetadata.ReadWrite.READ)
    @JwtSecurity(paramName = "channelCode", removeParamSuffix = true)
    public ChannelDetailsResource getChannelDetails(
            @Parameter(description = "Channel's code") @PathVariable("channel-code") String channelCode,
            @Parameter(description = "Channel's status") @RequestParam ConfigurationStatus status
    ) {
        return this.channelService.getChannelDetails(channelCode, status);
    }

    @GetMapping(value = "/{channel-code}/payment-service-providers", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Get paginated list of PSPs associated with the channel", security = {@SecurityRequirement(name = "JWT")})
    @OpenApiTableMetadata(readWriteIntense = OpenApiTableMetadata.ReadWrite.READ)
    @JwtSecurity(paramName = "channelCode", removeParamSuffix = true)
    public ChannelPspListResource getChannelPaymentServiceProviders(
            @Parameter(description = "Code of the payment channel") @PathVariable("channel-code") String channelCode,
            @Parameter(description = "Filter by PSP name") @RequestParam(name = "psp-name") String pspName,
            @Parameter(description = "Number of elements on one page") @RequestParam(required = false, defaultValue = "50") @Positive Integer limit,
            @Parameter(description = "Page number") @RequestParam(required = false, defaultValue = "0") @PositiveOrZero Integer page
    ) {
        return this.channelService.getPSPsByChannel(limit, page, channelCode, pspName);
    }

    @GetMapping(value = "/csv", produces = {MediaType.TEXT_PLAIN_VALUE, MediaType.APPLICATION_JSON_VALUE})
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Download the list of channels as CSV file", security = {@SecurityRequirement(name = "JWT")})
    @OpenApiTableMetadata(readWriteIntense = OpenApiTableMetadata.ReadWrite.READ)
    public Resource getChannelsCSV(HttpServletResponse response) {

        return this.channelService.getChannelsInCSVFile(response);
    }

    @PostMapping(value = "", produces = {MediaType.APPLICATION_JSON_VALUE})
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create a channel, validating the creation request previously inserted by user", security = {@SecurityRequirement(name = "JWT")})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "OK", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ChannelDetailsResource.class))),
            @ApiResponse(responseCode = "400", description = "Bad Request", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ProblemJson.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content(schema = @Schema())),
            @ApiResponse(responseCode = "429", description = "Too many requests", content = @Content(schema = @Schema())),
            @ApiResponse(responseCode = "500", description = "Service unavailable", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ProblemJson.class)))
    })
    @OpenApiTableMetadata(readWriteIntense = OpenApiTableMetadata.ReadWrite.WRITE)
    public ChannelDetailsResource createChannel(@RequestBody @NotNull ChannelDetailsDto channelDetailsDto) {
        return this.channelService.validateChannelCreation(channelDetailsDto);
    }

    @PutMapping(value = "/{channel-code}", produces = {MediaType.APPLICATION_JSON_VALUE})
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Update a channel, validating the update request previously inserted by user", security = {@SecurityRequirement(name = "JWT")})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ChannelDetailsResource.class))),
            @ApiResponse(responseCode = "400", description = "Bad Request", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ProblemJson.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content(schema = @Schema())),
            @ApiResponse(responseCode = "429", description = "Too many requests", content = @Content(schema = @Schema())),
            @ApiResponse(responseCode = "500", description = "Service unavailable", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ProblemJson.class)))
    })
    @OpenApiTableMetadata(readWriteIntense = OpenApiTableMetadata.ReadWrite.WRITE)
    @JwtSecurity(paramName = "channelCode", removeParamSuffix = true)
    public ChannelDetailsResource updateChannel(
            @Parameter(description = "Channel's unique identifier") @PathVariable("channel-code") String channelCode,
            @RequestBody @NotNull ChannelDetailsDto channelDetailsDto
    ) {
        return this.channelService.validateChannelUpdate(channelCode, channelDetailsDto);
    }

    @DeleteMapping(value = "/{channel-code}", produces = {MediaType.APPLICATION_JSON_VALUE})
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "delete channel", security = {@SecurityRequirement(name = "JWT")})
    @OpenApiTableMetadata
    @JwtSecurity(paramName = "channelCode", removeParamSuffix = true)
    public void deleteChannel(@Parameter(description = "Code of the payment channel") @PathVariable("channel-code") String channelCode) {
        this.channelService.deleteChannel(channelCode);
    }

    @GetMapping(value = "/{channel-code}/payment-types", produces = {MediaType.APPLICATION_JSON_VALUE})
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Get list of payment type of a channel", security = {@SecurityRequirement(name = "JWT")})
    @OpenApiTableMetadata
    @JwtSecurity(paramName = "channelCode", removeParamSuffix = true)
    public PspChannelPaymentTypesResource getChannelPaymentTypes(
            @Parameter(description = "Channel's unique identifier") @PathVariable("channel-code") String channelCode
    ) {
        return this.channelService.getPaymentTypesByChannel(channelCode);
    }

    @PostMapping(value = "/{channel-code}/payment-types", consumes = {MediaType.APPLICATION_JSON_VALUE}, produces = {MediaType.APPLICATION_JSON_VALUE})
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create a payment types of a channel", security = {@SecurityRequirement(name = "JWT")})
    @OpenApiTableMetadata
    @JwtSecurity(paramName = "channelCode", removeParamSuffix = true)
    public PspChannelPaymentTypesResource createChannelPaymentType(
            @Parameter(description = "Channel's unique identifier") @PathVariable("channel-code") String channelCode,
            @Parameter(description = " List of payment types") @RequestBody PspChannelPaymentTypes pspChannelPaymentTypes
    ) {
        return this.channelService.createPaymentTypeOnChannel(pspChannelPaymentTypes, channelCode);
    }

    @DeleteMapping(value = "/{channel-code}/payment-types/{payment-type-code}", produces = {MediaType.APPLICATION_JSON_VALUE})
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "delete payment type of a channel", security = {@SecurityRequirement(name = "JWT")})
    @OpenApiTableMetadata
    @JwtSecurity(paramName = "channelCode", removeParamSuffix = true)
    public void deleteChannelPaymentType(
            @Parameter(description = "Channel's unique identifier") @PathVariable("channel-code") String channelCode,
            @Parameter(description = "Code of the payment type") @PathVariable("payment-type-code") String paymentTypeCode
    ) {
        this.channelService.deletePaymentTypeOnChannel(channelCode, paymentTypeCode);
    }

    @PostMapping(value = "/wrapper", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Request the creation of a channel that will be validated by an operator", security = {@SecurityRequirement(name = "JWT")})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "OK", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = WrapperEntities.class))),
            @ApiResponse(responseCode = "400", description = "Bad Request", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ProblemJson.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content(schema = @Schema())),
            @ApiResponse(responseCode = "429", description = "Too many requests", content = @Content(schema = @Schema())),
            @ApiResponse(responseCode = "500", description = "Service unavailable", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ProblemJson.class)))
    })
    @OpenApiTableMetadata
    public WrapperEntities<ChannelDetails> createWrapperChannelDetails(@RequestBody @Valid WrapperChannelDetailsDto wrapperChannelDetailsDto) {
        return this.channelService.createChannelToBeValidated(wrapperChannelDetailsDto);
    }

    @PutMapping(value = "/wrapper/{channel-code}", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Request the update of a channel that will be validated by an operator", security = {@SecurityRequirement(name = "JWT")})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ChannelDetailsResource.class))),
            @ApiResponse(responseCode = "400", description = "Bad Request", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ProblemJson.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content(schema = @Schema())),
            @ApiResponse(responseCode = "429", description = "Too many requests", content = @Content(schema = @Schema())),
            @ApiResponse(responseCode = "500", description = "Service unavailable", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ProblemJson.class)))
    })
    @OpenApiTableMetadata
    @JwtSecurity(paramName = "channelCode", removeParamSuffix = true)
    public ChannelDetailsResource updateWrapperChannelDetails(
            @Parameter(description = "Channel's code") @PathVariable("channel-code") String channelCode,
            @RequestBody @Valid ChannelDetailsDto channelDetailsDto
    ) {
        return this.channelService.updateChannelToBeValidated(channelCode, channelDetailsDto);
    }

    /**
     * Updates a station wrapper with the operator review's note
     *
     * @param channelCode   channel identifier
     * @param brokerPspCode broker code related to the channel
     * @param note          operator review note
     * @return the updated channel wrapper
     */
    @PutMapping(value = "/wrapper/{channel-code}/operator", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Update a WrapperChannel with Operator review", security = {@SecurityRequirement(name = "JWT")})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ChannelDetailsResource.class))),
            @ApiResponse(responseCode = "400", description = "Bad Request", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ProblemJson.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content(schema = @Schema())),
            @ApiResponse(responseCode = "404", description = "Not found", content = @Content(schema = @Schema(implementation = ProblemJson.class))),
            @ApiResponse(responseCode = "429", description = "Too many requests", content = @Content(schema = @Schema())),
            @ApiResponse(responseCode = "500", description = "Service unavailable", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ProblemJson.class)))
    })
    @OpenApiTableMetadata
    @JwtSecurity(paramName = "channelCode", removeParamSuffix = true)
    public ChannelDetailsResource updateWrapperChannelWithOperatorReview(
            @Parameter(description = "Channel's code") @PathVariable("channel-code") String channelCode,
            @Parameter(description = "Broker Code related to the channel") @RequestParam String brokerPspCode,
            @RequestBody @Valid OperatorChannelReview note
    ) {
        return this.channelService.updateWrapperChannelWithOperatorReview(channelCode, brokerPspCode, note.getNote());
    }
}
