package it.pagopa.selfcare.pagopa.backoffice.web.controller;

import io.swagger.annotations.Api;
import it.pagopa.selfcare.pagopa.backoffice.core.GecService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping(value = "/gec", produces = MediaType.APPLICATION_JSON_VALUE)
@Api(tags = "gec")
public class GecController {

    private final GecService gecService;



    @Autowired
    public GecController(GecService gecService) {
        this.gecService = gecService;

    }

//    @GetMapping("")
//    @ResponseStatus(HttpStatus.OK)
//    @ApiOperation(value = "", notes = "${swagger.api.channels.getChannels}")
//    public ChannelsResource getChannels(@ApiParam("${swagger.pageable.number}")
//                                        @RequestParam(required = false, defaultValue = "50") Integer limit,
//                                        @ApiParam("${swagger.pageable.start}")
//                                        @RequestParam(required = true) Integer page,
//                                        @ApiParam("${swagger.model.channel.filter}")
//                                        @RequestParam(required = false) String code,
//                                        @ApiParam("${swagger.model.sort.order}")
//                                        @RequestParam(required = false, name = "ordering", defaultValue = "DESC") String sort) {
//        log.trace("getchannels start");
//        String xRequestId = UUID.randomUUID().toString();
//        log.debug("getchannels code filter = {}, xRequestId = {}", code, xRequestId);
//        Channels channels = apiConfigService.getChannels(limit, page, code, null, sort, xRequestId);
//        ChannelsResource resource = ChannelMapper.toResource(channels);
//        log.debug(LogUtils.CONFIDENTIAL_MARKER, "getchannels result = {}", resource);
//        log.trace("getchannels end");
//        return resource;
//    }




}