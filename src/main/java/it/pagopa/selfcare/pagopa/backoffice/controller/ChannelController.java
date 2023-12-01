package it.pagopa.selfcare.pagopa.backoffice.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import it.pagopa.selfcare.pagopa.backoffice.entity.WrapperEntitiesOperations;
import it.pagopa.selfcare.pagopa.backoffice.mapper.ChannelMapper;
import it.pagopa.selfcare.pagopa.backoffice.model.channels.*;
import it.pagopa.selfcare.pagopa.backoffice.model.connector.channel.PspChannelPaymentTypes;
import it.pagopa.selfcare.pagopa.backoffice.service.ChannelService;
import it.pagopa.selfcare.pagopa.backoffice.service.WrapperService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;

@Slf4j
@RestController
@RequestMapping(value = "/channels", produces = MediaType.APPLICATION_JSON_VALUE)
@Tag(name = "Channels")
public class ChannelController {

    @Autowired
    private ChannelService channelService;

    @Autowired
    private WrapperService wrapperService;

    @GetMapping("")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Get paginated list of channels", security = {@SecurityRequirement(name = "JWT")})
    public ChannelsResource getChannels(@Parameter(description = "Number of elements on one page. Default = 50") @RequestParam(required = false, defaultValue = "50") Integer limit,
                                        @Parameter(description = "Page number. Page value starts from 0") @RequestParam(required = true) Integer page,
                                        @Parameter(description = "Filter channel by code") @RequestParam(required = false) String code,
                                        @Parameter(description = "Sort Direction ordering") @RequestParam(required = false, name = "ordering", defaultValue = "DESC") String sort) {

        return channelService.getChannels(limit, page, code, sort);
    }

    @GetMapping(value = "/{channel-code}", produces = {MediaType.APPLICATION_JSON_VALUE})
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Get channel's details", security = {@SecurityRequirement(name = "JWT")})
    public ChannelDetailsResource getChannelDetails(@Parameter(description = "Code of the payment channel") @PathVariable("channel-code") String channelCode) {

        return channelService.getChannel(channelCode);
    }

    @GetMapping(value = "/{channel-code}/payment-service-providers", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Get paginated list of PSPs associated with the channel", security = {@SecurityRequirement(name = "JWT")})
    public ChannelPspListResource getChannelPaymentServiceProviders(@Parameter(description = "") @RequestParam(required = false, defaultValue = "50") Integer limit,
                                                                    @Parameter(description = "Page number. Page value starts from 0") @RequestParam(required = true) Integer page,
                                                                    @Parameter(description = "Code of the payment channel") @PathVariable("channel-code") String channelCode) {

        return channelService.getPSPsByChannel(limit, page, channelCode);
    }

    @GetMapping(value = "/csv", produces = {MediaType.TEXT_PLAIN_VALUE, MediaType.APPLICATION_JSON_VALUE})
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Download the list of channels as CSV file", security = {@SecurityRequirement(name = "JWT")})
    public Resource getChannelsCSV(HttpServletResponse response) {

        return channelService.getChannelsInCSVFile(response);
    }

    @PostMapping(value = "", produces = {MediaType.APPLICATION_JSON_VALUE})
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create a channel, validating the creation request previously inserted by user", security = {@SecurityRequirement(name = "JWT")})
    public WrapperChannelDetailsResource createChannel(@RequestBody @NotNull ChannelDetailsDto channelDetailsDto) {

        return channelService.validateChannelCreation(channelDetailsDto);
    }

    @PutMapping(value = "/{channel-code}", produces = {MediaType.APPLICATION_JSON_VALUE})
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Update a channel, validating the update request previously inserted by user", security = {@SecurityRequirement(name = "JWT")})
    public ChannelDetailsResource updateChannel(@Parameter(description = "Channel's unique identifier") @PathVariable("channel-code") String channelCode,
                                                @RequestBody @NotNull ChannelDetailsDto channelDetailsDto) {

        return channelService.validateChannelUpdate(channelCode, channelDetailsDto);
    }

    @DeleteMapping(value = "/{channel-code}", produces = {MediaType.APPLICATION_JSON_VALUE})
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "delete channel", security = {@SecurityRequirement(name = "JWT")})
    public void deleteChannel(@Parameter(description = "Code of the payment channel") @PathVariable("channel-code") String channelCode) {

        channelService.deleteChannel(channelCode);
    }

    @GetMapping(value = "/{channel-code}/payment-types", produces = {MediaType.APPLICATION_JSON_VALUE})
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Get list of payment type of a channel", security = {@SecurityRequirement(name = "JWT")})
    public PspChannelPaymentTypesResource getChannelPaymentTypes(@Parameter(description = "Channel's unique identifier") @PathVariable("channel-code") String channelCode) {

        return channelService.getPaymentTypesByChannel(channelCode);
    }

    @PostMapping(value = "/{channel-code}/payment-types", consumes = {MediaType.APPLICATION_JSON_VALUE}, produces = {MediaType.APPLICATION_JSON_VALUE})
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create a payment types of a channel", security = {@SecurityRequirement(name = "JWT")})
    public PspChannelPaymentTypesResource createChannelPaymentType(@Parameter(description = "Channel's unique identifier") @PathVariable("channel-code") String channelCode,
                                                                   @Parameter(description = " List of payment types") @RequestBody PspChannelPaymentTypes pspChannelPaymentTypes) {

        return channelService.createPaymentTypeOnChannel(pspChannelPaymentTypes, channelCode);
    }

    @DeleteMapping(value = "/{channel-code}/payment-types/{payment-type-code}", produces = {MediaType.APPLICATION_JSON_VALUE})
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "delete payment type of a channel", security = {@SecurityRequirement(name = "JWT")})
    public void deleteChannelPaymentType(@Parameter(description = "Channel's unique identifier") @PathVariable("channel-code") String channelCode,
                                         @Parameter(description = "Code of the payment type") @PathVariable("payment-type-code") String paymentTypeCode) {

        channelService.deletePaymentTypeOnChannel(channelCode, paymentTypeCode);
    }


    @GetMapping(value = "/merged", produces = {MediaType.APPLICATION_JSON_VALUE})
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Get All Channels from cosmos db merged whit apiConfig", security = {@SecurityRequirement(name = "JWT")})
    public WrapperChannelsResource getAllChannelsMerged(@Parameter(description = "") @RequestParam(required = false, defaultValue = "50") Integer limit,
                                                        @Parameter(description = "Channel code") @RequestParam(required = false, value = "channelcodefilter") String channelcode,
                                                        @Parameter(description = "Broker code filter for search") @RequestParam(required = false, value = "brokerCode") String brokerCode,
                                                        @Parameter(description = "Page number. Page value starts from 0") @RequestParam Integer page,
                                                        @Parameter(description = "Method of sorting") @RequestParam(required = false, value = "sorting") String sorting) {

        return channelService.getAllMergedChannel(limit, channelcode, brokerCode, page, sorting);
    }

    @GetMapping(value = "/merged/{channel-code}", produces = {MediaType.APPLICATION_JSON_VALUE})
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Get channel's details from cosmos db merged whit apiConfig", security = {@SecurityRequirement(name = "JWT")})
    public ChannelDetailsResource getChannelDetail(@Parameter(description = "Code of the payment channel") @PathVariable("channel-code") String channelCode) {

        return channelService.getChannelToBeValidated(channelCode);
    }

    @GetMapping(value = "/wrapper/{channel-code}")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Get a channel that is currently in hold for operator validation", security = {@SecurityRequirement(name = "JWT")})
    public WrapperEntitiesOperations getGenericWrapperEntities(@Parameter(description = "Channel code") @PathVariable("channel-code") String channelCode) {

        return wrapperService.findById(channelCode);
    }

    @PostMapping(value = "/wrapper", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Request the creation of a channel that will be validated by an operator", security = {@SecurityRequirement(name = "JWT")})
    public WrapperEntitiesOperations createWrapperChannelDetails(@RequestBody @Valid WrapperChannelDetailsDto wrapperChannelDetailsDto) {

        return channelService.createChannelToBeValidated(wrapperChannelDetailsDto);
    }

    @PutMapping(value = "/wrapper", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Request the update of a channel that will be validated by an operator", security = {@SecurityRequirement(name = "JWT")})
    public WrapperEntitiesOperations updateWrapperChannelDetails(@RequestBody @Valid ChannelDetailsDto channelDetailsDto) {

        return channelService.updateChannelToBeValidated(channelDetailsDto);
    }

    @PutMapping(value = "/wrapper/operator", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Execute the update as operator in the channel request that must be validated", security = {@SecurityRequirement(name = "JWT")})
    public WrapperEntitiesOperations updateWrapperChannelDetailsByOpt(@RequestBody @Valid ChannelDetailsDto channelDetailsDto) {

        return wrapperService.updateByOpt(ChannelMapper.fromChannelDetailsDto(channelDetailsDto), channelDetailsDto.getNote(), channelDetailsDto.getStatus().name());
    }
}
