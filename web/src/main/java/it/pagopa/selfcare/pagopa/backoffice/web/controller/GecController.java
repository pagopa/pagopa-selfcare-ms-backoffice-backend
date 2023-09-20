package it.pagopa.selfcare.pagopa.backoffice.web.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import it.pagopa.selfcare.pagopa.backoffice.connector.logging.LogUtils;
import it.pagopa.selfcare.pagopa.backoffice.connector.model.gec.Bundles;
import it.pagopa.selfcare.pagopa.backoffice.core.GecService;
import it.pagopa.selfcare.pagopa.backoffice.web.model.channels.ChannelsResource;
import it.pagopa.selfcare.pagopa.backoffice.web.model.gec.BundlesResource;
import it.pagopa.selfcare.pagopa.backoffice.web.model.mapper.GecMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

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

    @GetMapping("")
    @ResponseStatus(HttpStatus.OK)
    @ApiOperation(value = "", notes = "${swagger.api.gec.getBundlesByCI}")
    public BundlesResource getBundlesByCI(@ApiParam("${swagger.pageable.number}")
                                        @RequestParam(required = false, defaultValue = "50") Integer limit,
                                        @ApiParam("${swagger.pageable.start}")
                                        @RequestParam(required = true) Integer page,
                                        @ApiParam("${swagger.model.gec.cifiscalcode}")
                                        @RequestParam(required = false) String ciFiscalcode,
                                        @ApiParam("${swagger.model.sort.order}")
                                        @RequestParam(required = false, name = "ordering", defaultValue = "DESC") String sort) {
        log.trace("getchannels start");
        String xRequestId = UUID.randomUUID().toString();
        log.debug("getchannels code filter = {}, xRequestId = {}", ciFiscalcode, xRequestId);
        Bundles bundles = gecService.getBundlesByCI(ciFiscalcode, limit, page, xRequestId);
        BundlesResource resource = GecMapper.toResource(bundles);
        log.debug(LogUtils.CONFIDENTIAL_MARKER, "getchannels result = {}", resource);
        log.trace("getchannels end");
        return resource;
    }




}