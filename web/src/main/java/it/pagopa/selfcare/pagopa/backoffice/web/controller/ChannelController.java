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
                                        @ApiParam("${swagger.model.sort.order}")
                                        @RequestParam(required = false, name = "ordering", defaultValue = "DESC") String sort) {
        log.trace("getchannels start");
        String xRequestId = UUID.randomUUID().toString();
        log.debug("getchannels code filter = {}, xRequestId = {}", code, xRequestId);
        Channels channels = apiConfigService.getChannels(limit, page, code, sort, xRequestId);
        ChannelsResource resource = ChannelMapper.toResource(channels);
        log.debug(LogUtils.CONFIDENTIAL_MARKER, "getchannels result = {}", resource);
        log.trace("getchannels end");
        return resource;
    }

    @PostMapping(value = "", produces = {MediaType.APPLICATION_JSON_VALUE})
    @ResponseStatus(HttpStatus.CREATED)
    @ApiOperation(value = "", notes = "${swagger.api.channels.createChannel}")
    public ChannelDetailsResource createChannel(@RequestBody @NotNull ChannelDetailsDto channelDetailsDto) {
        log.trace("createChannel start");
        String xRequestId = UUID.randomUUID().toString();
        log.debug("createChannel code channelDetailsDto = {}, xRequestId = {}", channelDetailsDto, xRequestId);

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
                                              @PathVariable("pspcode") String pspCode) {
        log.trace("getPspChannels start");
        String xRequestId = UUID.randomUUID().toString();
        log.debug("getPspChannels pspcode = {}, xRequestId = {}", pspCode, xRequestId);
        PspChannels pspChannels = apiConfigService.getPspChannels(pspCode, xRequestId);
        PspChannelsResource resource = ChannelMapper.toResource(pspChannels);
        log.debug(LogUtils.CONFIDENTIAL_MARKER, "getPspChannels result = {}", resource);
        log.trace("getPspChannels end");
        return resource;
    }

    @GetMapping(value = "/details/{channelcode}", produces = {MediaType.APPLICATION_JSON_VALUE})
    @ResponseStatus(HttpStatus.OK)
    public ChannelDetailsResource getChannelDetails(@ApiParam("${swagger.request.channelcode}")
                                                    @PathVariable("channelcode") String channelcode) {
        log.trace("getChannelDetails start");
        String xRequestId = UUID.randomUUID().toString();
        log.debug("getChannelDetails channelcode = {}, xRequestId = {}", channelcode, xRequestId);
        ChannelDetails channelDetails = apiConfigService.getChannelDetails(channelcode, xRequestId);

        PspChannelPaymentTypes ptResponse = apiConfigService.getChannelPaymentTypes(channelcode, xRequestId);
        ChannelDetailsResource resource = ChannelMapper.toResource(channelDetails, ptResponse);

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

    @PutMapping(value = "{channelcode}", produces = {MediaType.APPLICATION_JSON_VALUE})
    @ResponseStatus(HttpStatus.OK)
    @ApiOperation(value = "", notes = "${swagger.api.channels.createChannel}")
    public ChannelDetailsResource updateChannel(@RequestBody @NotNull ChannelDetailsDto channelDetailsDto,
                                                @ApiParam("${swagger.model.channel.channelCode}")
                                                @PathVariable("channelcode") String channelCode) {
        log.trace("updateChannel start");
        String uuid = UUID.randomUUID().toString();
        log.debug("updateChannel code channelDetailsDto = {} , uuid {}", channelDetailsDto, uuid);

        ChannelDetails channelDetails = ChannelMapper.fromChannelDetailsDto(channelDetailsDto);
        ChannelDetails response = apiConfigService.updateChannel(channelDetails, channelCode, uuid);

        ChannelDetailsResource resource = ChannelMapper.toResource(response, null);

        log.debug(LogUtils.CONFIDENTIAL_MARKER, "updateChannel result = {}", resource);
        log.trace("updateChannel end");
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

    @DeleteMapping(value = "/{channelcode}/{paymenttypecode}", produces = {MediaType.APPLICATION_JSON_VALUE})
    @ResponseStatus(HttpStatus.OK)
    @ApiOperation(value = "", notes = "${swagger.api.channels.deleteChannelPaymentType}")
    public void deleteChannelPaymentType(@ApiParam("${swagger.model.channel.channelCode}") @PathVariable("channelcode") String channelCode,
                                         @ApiParam("${swagger.request.paymentTypeCode}")
                                         @PathVariable("paymenttypecode") String paymentTypeCode) {
        log.trace("deleteChannelPaymentType start");
        String uuid = UUID.randomUUID().toString();
        log.debug("deleteChannelPaymentType code paymentTypeCode = {}, channel = {}, uuid {}", paymentTypeCode, channelCode, uuid);
        apiConfigService.deleteChannelPaymentType(channelCode, paymentTypeCode, uuid);
        log.trace("deleteChannelPaymentType end");
    }

    @GetMapping(value = "paymenttypes/{channelcode}", produces = {MediaType.APPLICATION_JSON_VALUE})
    @ResponseStatus(HttpStatus.OK)
    @ApiOperation(value = "", notes = "${swagger.api.channels.getChannelPaymentTypes}")
    public PspChannelPaymentTypesResource getChannelPaymentTypes(@ApiParam("${swagger.model.channel.channelCode}")
                                                                 @PathVariable("channelcode") String channelCode) {
        log.trace("getChannelPaymentTypes start");
        String uuid = UUID.randomUUID().toString();
        log.debug("getChannelPaymentTypes channelCode = {}, uuid {}", channelCode, uuid);
        PspChannelPaymentTypes response = apiConfigService.getChannelPaymentTypes(channelCode, uuid);
        PspChannelPaymentTypesResource resource = ChannelMapper.toResource(response);

        log.debug(LogUtils.CONFIDENTIAL_MARKER, "getChannelPaymentTypes result = {}", resource);
        log.trace("getChannelPaymentTypes end");

        return resource;
    }

    @DeleteMapping(value = "psp/{channelcode}/{pspcode}")
    @ResponseStatus(HttpStatus.OK)
    @ApiOperation(value = "", notes = "${swagger.api.channels.deletePaymentServiceProvidersChannels}")
    public void deletePaymentServiceProvidersChannels(@ApiParam("${swagger.model.channel.channelCode}")
                                                      @PathVariable("channelcode") String channelCode,
                                                      @ApiParam("${swagger.request.pspCode}")
                                                      @PathVariable("pspcode") String pspCode) {
        log.trace("deletePaymentServiceProvidersChannels start");
        String uuid = UUID.randomUUID().toString();
        log.debug("deletePaymentServiceProvidersChannels code pspCode = {}, channel = {}, uuid {}", pspCode, channelCode, uuid);
        apiConfigService.deletePaymentServiceProvidersChannels(pspCode, channelCode, uuid);
        log.trace("deleteChannelPaymentType end");
    }

    @PutMapping(value = "psp/{channelcode}/{pspcode}")
    @ResponseStatus(HttpStatus.OK)
    @ApiOperation(value = "", notes = "${swagger.api.channels.updatePaymentServiceProvidersChannels}")
    public PspChannelPaymentTypesResource updatePaymentServiceProvidersChannels(@ApiParam("${swagger.model.channel.channelCode}")
                                                                                @PathVariable("channelcode") String channelCode,
                                                                                @ApiParam("${swagger.request.pspCode}")
                                                                                @PathVariable("pspcode") String pspCode,
                                                                                @ApiParam("${swagger.model.PspChannelPaymentTypesResource.list}")
                                                                                @RequestBody PspChannelPaymentTypes pspChannelPaymentTypes) {
        log.trace("updatePaymentServiceProvidersChannels start");
        String uuid = UUID.randomUUID().toString();
        log.debug("updatePaymentServiceProvidersChannels code pspCode = {}, channel = {}, pspChannelPaymentTypes = {}, uuid {}", pspCode, channelCode, pspChannelPaymentTypes, uuid);
        PspChannelPaymentTypes response = apiConfigService.updatePaymentServiceProvidersChannels(pspCode, channelCode,pspChannelPaymentTypes, uuid);
        PspChannelPaymentTypesResource resource = ChannelMapper.toResource(response);
        log.debug(LogUtils.CONFIDENTIAL_MARKER, "updatePaymentServiceProvidersChannels result = {}", resource);
        log.trace("updatePaymentServiceProvidersChannels end");
        return resource;
    }

    @DeleteMapping(value = "/{channelcode}", produces = {MediaType.APPLICATION_JSON_VALUE})
    @ResponseStatus(HttpStatus.OK)
    @ApiOperation(value = "", notes = "${swagger.api.channels.deleteChannel}")
    public void deleteChannel(@ApiParam("${swagger.request.channelcode}")
                              @PathVariable("channelcode") String channelcode) {
        log.trace("deleteChannel start");
        String uuid = UUID.randomUUID().toString();
        log.debug("deleteChannel channelcode = {}, uuid = {}", channelcode, uuid);
        apiConfigService.deleteChannel(channelcode, uuid);
        log.debug(LogUtils.CONFIDENTIAL_MARKER, "deleteChannel with channelcode = {}", channelcode);
        log.trace("deleteChannel end");
    }

    @GetMapping(value = "/{brokerpspcode}/paymentserviceproviders", produces = {MediaType.APPLICATION_JSON_VALUE})
    @ResponseStatus(HttpStatus.OK)
    @ApiOperation(value = "", notes = "${swagger.api.channels.getPspBrokerPsp}")
    public PaymentServiceProvidersResource getPspBrokerPsp(@ApiParam("${swagger.request.limit}")
                                                    @RequestParam(required = false, defaultValue = "50") Integer limit,
                                                    @ApiParam("${swagger.request.page}")
                                                    @RequestParam Integer page,
                                                    @ApiParam("${swagger.request.brokerpspcode}")
                                                    @PathVariable("brokerpspcode") String brokerPspCode) {
        log.trace("getPspBrokerPsp start");
        String uuid = UUID.randomUUID().toString();
        log.debug("getPspBrokerPsp brokerPspCode = {} page = {} limit = {}, uuid {}",brokerPspCode,page, limit, uuid);
        PaymentServiceProviders response = apiConfigService.getPspBrokerPsp(limit,page,brokerPspCode, uuid);
        PaymentServiceProvidersResource resource = ChannelMapper.toResource(response);

        log.debug(LogUtils.CONFIDENTIAL_MARKER, "getPspBrokerPsp result = {}", resource);
        log.trace("getPspBrokerPsp end");

        return resource;
     }
}

