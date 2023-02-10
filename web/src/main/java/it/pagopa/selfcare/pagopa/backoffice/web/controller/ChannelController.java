package it.pagopa.selfcare.pagopa.backoffice.web.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import it.pagopa.selfcare.pagopa.backoffice.connector.logging.LogUtils;
import it.pagopa.selfcare.pagopa.backoffice.connector.model.channel.*;
import it.pagopa.selfcare.pagopa.backoffice.core.ApiConfigService;
import it.pagopa.selfcare.pagopa.backoffice.web.model.channels.*;
import it.pagopa.selfcare.pagopa.backoffice.web.model.mapper.ChannelMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping(value = "/channels", produces = MediaType.APPLICATION_JSON_VALUE)
@Api(tags = "channels")
public class ChannelController {

    private final ApiConfigService apiConfigService;

    @Autowired
    public ChannelController(ApiConfigService apiConfigService) {
        this.apiConfigService = apiConfigService;
    }

    @GetMapping("")
    @ResponseStatus(HttpStatus.OK)
    @ApiOperation(value = "", notes = "${swagger.api.channels.getChannels}")
    public ChannelsResource getChannels(@ApiParam("${swagger.pageable.number}")
                                        @RequestParam(required = false, defaultValue = "50") Integer limit,
                                        @ApiParam("${swagger.pageable.start}")
                                        @RequestParam(required = true) Integer page,
                                        @ApiParam("${swagger.model.channel.filter}")
                                        @RequestParam(required = false) String code,
                                        @ApiParam("${swagger.model.channel.sort.order}")
                                        @RequestParam(required = false, name = "ordering", defaultValue = "DESC") String sort,
                                        @ApiParam("${swagger.request.id}")
                                        @RequestHeader(name = "X-Request-Id", required = false) String xRequestId) {
        log.trace("getchannels start");
        log.debug("getchannels code filter = {}", code);
        Channels channels = apiConfigService.getChannels(limit, page, code, sort, xRequestId);
        ChannelsResource resource = ChannelMapper.toResource(channels);
        log.debug(LogUtils.CONFIDENTIAL_MARKER, "getchannels result = {}", resource);
        log.trace("getchannels end");
        return resource;
    }

    @PostMapping(value = "", produces = {MediaType.APPLICATION_JSON_VALUE})
    @ResponseStatus(HttpStatus.CREATED)
    @ApiOperation(value = "", notes = "${swagger.api.channels.createChannel}")
    public ChannelDetailsResource createChannel(@RequestBody @NotNull ChannelDetailsDto channelDetailsDto,
                                                @ApiParam("${swagger.request.id}")
                                                @RequestHeader(name = "X-Request-Id", required = false) String xRequestId) {
        log.trace("createChannel start");
        log.debug("createChannel code channelDetailsDto = {}", channelDetailsDto);

        PspChannelPaymentTypes pspChannelPaymentTypes = new PspChannelPaymentTypes();
        List<String> paymentTypeList = channelDetailsDto.getPaymentTypeList();
        String channelCode = channelDetailsDto.getChannelCode();
        pspChannelPaymentTypes.setPaymentTypeList(paymentTypeList);

        ChannelDetails channelDetails = ChannelMapper.fromChannelDetailsDto(channelDetailsDto);
        ChannelDetails response = apiConfigService.createChannel(channelDetails, xRequestId);

        PspChannelPaymentTypes ptResponse = apiConfigService.createChannelPaymentType(pspChannelPaymentTypes, channelCode, xRequestId);
        ChannelDetailsResource resource = ChannelMapper.toResource(response, ptResponse);

        log.debug(LogUtils.CONFIDENTIAL_MARKER, "createChannel result = {}", resource);
        log.trace("createChannel end");
        return resource;
    }

    @GetMapping(value = "/{pspcode}", produces = {MediaType.APPLICATION_JSON_VALUE})
    @ResponseStatus(HttpStatus.OK)
    public PspChannelsResource getPspChannels(@ApiParam("${swagger.request.pspCode}")
                                              @PathVariable("pspcode") String pspCode,
                                              @ApiParam("${swagger.request.id}")
                                              @RequestHeader(name = "X-Request-Id", required = false) String xRequestId
    ) {
        log.trace("getPspChannels start");
        log.debug("getPspChannels pspcode = {}", pspCode);
        PspChannels pspChannels = apiConfigService.getPspChannels(pspCode, xRequestId);
        PspChannelsResource resource = ChannelMapper.toResource(pspChannels);
        log.debug(LogUtils.CONFIDENTIAL_MARKER, "getPspChannels result = {}", resource);
        log.trace("getPspChannels end");
        return resource;
    }

    @GetMapping(value = "/details/{channelcode}", produces = {MediaType.APPLICATION_JSON_VALUE})
    @ResponseStatus(HttpStatus.OK)
    public ChannelDetailsResource getChannelDetails(@ApiParam("${swagger.request.channelcode}")
                                                    @PathVariable("channelcode") String channelcode,
                                                    @ApiParam("${swagger.request.id}")
                                                    @RequestHeader(name = "X-Request-Id", required = false) String xRequestId
    ) {
        log.trace("getChannelDetails start");
        log.debug("getChannelDetails channelcode = {}", channelcode);
        ChannelDetails channelDetails = apiConfigService.getChannelDetails(channelcode, xRequestId);
        ChannelDetailsResource resource = ChannelMapper.toResource(channelDetails);
        log.debug(LogUtils.CONFIDENTIAL_MARKER, "getChannelDetails result = {}", resource);
        log.trace("getChannelDetails end");
        return resource;
    }

    @PostMapping(value = "/{channelcode}/paymenttypes", consumes = {MediaType.APPLICATION_JSON_VALUE}, produces = {MediaType.APPLICATION_JSON_VALUE})
    @ResponseStatus(HttpStatus.CREATED)
    @ApiOperation(value = "", notes = "${swagger.api.channels.createChannelPaymentType}")
    public PspChannelPaymentTypesResource createChannelPaymentType(@ApiParam("${swagger.model.channel.channelCode}") @PathVariable("channelcode") String channelCode,
                                                                   @ApiParam("${swagger.model.PspChannelPaymentTypesResource.list}")
                                                                   @RequestBody PspChannelPaymentTypes pspChannelPaymentTypes) {
        log.trace("createChannelPaymentType start");
        String uuid = UUID.randomUUID().toString();
        log.debug("createChannelPaymentType code pspChannelPaymentTypes = {}, uuid {}", pspChannelPaymentTypes, uuid);
        PspChannelPaymentTypes response = apiConfigService.createChannelPaymentType(pspChannelPaymentTypes, channelCode, uuid);
        PspChannelPaymentTypesResource resource = ChannelMapper.toResource(response);
        log.debug(LogUtils.CONFIDENTIAL_MARKER, "createChannelPaymentType result = {}", resource);
        log.trace("createChannelPaymentType end");
        return resource;
    }

    @GetMapping(value = "configuration/paymenttypes", produces = {MediaType.APPLICATION_JSON_VALUE})
    @ResponseStatus(HttpStatus.OK)
    @ApiOperation(value = "", notes = "${swagger.api.channels.getPaymentTypes}")
    public PaymentTypesResource getPaymentTypes() {
        log.trace("getPaymentTypes start");

        log.debug("getPaymentTypes code getPaymentTypes");
        String uuid = UUID.randomUUID().toString();
        log.debug("getPaymentTypes uuid {}", uuid);
        PaymentTypes response = apiConfigService.getPaymentTypes(uuid);
        PaymentTypesResource resource = ChannelMapper.toResource(response);

        log.debug(LogUtils.CONFIDENTIAL_MARKER, "getPaymentTypes result = {}", resource);
        log.trace("getPaymentTypes end");
        return resource;
    }

    @DeleteMapping(value = "/{channelcode}", produces = {MediaType.APPLICATION_JSON_VALUE})
    @ResponseStatus(HttpStatus.OK)
    public void deleteChannel(@ApiParam("${swagger.request.channelcode}")
                              @PathVariable("channelcode") String channelcode) {
        log.trace("deleteChannel start");
        String uuid = UUID.randomUUID().toString();
        log.debug("deleteChannel channelcode = {}, uuid = {}", channelcode, uuid);
        apiConfigService.deleteChannel(channelcode, uuid);
        log.debug(LogUtils.CONFIDENTIAL_MARKER, "deleteChannel with channelcode = {}", channelcode);
        log.trace("getChannelDetails end");
    }

    @DeleteMapping(value = "/{channelcode}/paymenttypes/{paymenttypecode}", produces = {MediaType.APPLICATION_JSON_VALUE})
    @ResponseStatus(HttpStatus.OK)
    public PspChannelPaymentTypesResource deleteChannelPaymentType(@ApiParam("${swagger.request.channelcode}")
                                                                   @PathVariable("channelcode") String channelcode,
                                                                   @ApiParam("${swagger.request.paymentTypeCode}")
                                                                   @PathVariable("paymenttypecode") String paymentTypeCode) {
        log.trace("deleteChannelPaymentType start");
        String uuid = UUID.randomUUID().toString();
        log.debug("deleteChannelPaymentType paymentTypeCode = {}, channelcode = {}, uuid = {}", paymentTypeCode, channelcode, uuid);
        PspChannelPaymentTypes response = apiConfigService.deleteChannelPaymentType(channelcode,paymentTypeCode,uuid);
        PspChannelPaymentTypesResource resource = ChannelMapper.toResource(response);
        log.debug(LogUtils.CONFIDENTIAL_MARKER, "deleteChannelPaymentType paymentTypeCode = {}, channelcode = {}", paymentTypeCode, channelcode);
        log.trace("deleteChannelPaymentType end");
        return resource;
    }
}

