package it.pagopa.selfcare.pagopa.backoffice.web.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import it.pagopa.selfcare.pagopa.backoffice.connector.logging.LogUtils;
import it.pagopa.selfcare.pagopa.backoffice.connector.model.channel.ChannelDetails;
import it.pagopa.selfcare.pagopa.backoffice.connector.model.channel.Channels;
import it.pagopa.selfcare.pagopa.backoffice.core.ApiConfigService;
import it.pagopa.selfcare.pagopa.backoffice.web.model.channels.ChannelDetailsDto;
import it.pagopa.selfcare.pagopa.backoffice.web.model.channels.ChannelDetailsResource;
import it.pagopa.selfcare.pagopa.backoffice.web.model.channels.ChannelsResource;
import it.pagopa.selfcare.pagopa.backoffice.web.model.mapper.ChannelMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotNull;

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

        ChannelDetails channelDetails = ChannelMapper.fromChannelDetailsDto(channelDetailsDto);
        ChannelDetails response = apiConfigService.createChannel(channelDetails,xRequestId);
        ChannelDetailsResource resource = ChannelMapper.toResource(response);
        log.debug(LogUtils.CONFIDENTIAL_MARKER, "createChannel result = {}", resource);
        log.trace("createChannel end");
        return resource;
    }
}
