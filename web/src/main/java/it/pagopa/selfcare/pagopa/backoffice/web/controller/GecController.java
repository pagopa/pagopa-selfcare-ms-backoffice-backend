package it.pagopa.selfcare.pagopa.backoffice.web.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import it.pagopa.selfcare.pagopa.backoffice.connector.logging.LogUtils;
import it.pagopa.selfcare.pagopa.backoffice.connector.model.gec.BundleType;
import it.pagopa.selfcare.pagopa.backoffice.connector.model.gec.Bundles;
import it.pagopa.selfcare.pagopa.backoffice.connector.model.gec.Touchpoints;
import it.pagopa.selfcare.pagopa.backoffice.core.GecService;
import it.pagopa.selfcare.pagopa.backoffice.web.model.gec.BundlesResource;
import it.pagopa.selfcare.pagopa.backoffice.web.model.gec.TouchpointsResource;
import it.pagopa.selfcare.pagopa.backoffice.web.model.mapper.GecMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
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

//    @GetMapping("/ci/bundles")
//    @ResponseStatus(HttpStatus.OK)
//    @ApiOperation(value = "", notes = "${swagger.api.gec.getBundlesByCI}")
//    public BundlesResource getBundlesByCI(@ApiParam("${swagger.pageable.number}")
//                                        @RequestParam(required = false, defaultValue = "50") Integer limit,
//                                        @ApiParam("${swagger.pageable.start}")
//                                        @RequestParam(required = true) Integer page,
//                                        @ApiParam("${swagger.model.gec.cifiscalcode}")
//                                        @RequestParam(required = false) String ciFiscalcode) {
//        log.trace("getBundlesByCI start");
//        String xRequestId = UUID.randomUUID().toString();
//        log.debug("getBundlesByCI cifiscalcode = {}, xRequestId = {}", ciFiscalcode, xRequestId);
//        Bundles bundles = gecService.getBundlesByCI(ciFiscalcode, limit, page, xRequestId);
//        BundlesResource resource = GecMapper.toResource(bundles);
//        log.debug(LogUtils.CONFIDENTIAL_MARKER, "getBundlesByCI result = {}", resource);
//        log.trace("getBundlesByCI end");
//        return resource;
//    }

    @GetMapping("/bundles/touchpoints")
    @ResponseStatus(HttpStatus.OK)
    @ApiOperation(value = "", notes = "${swagger.api.gec.getBundlesByCI}")
    public TouchpointsResource getTouchpoints(@ApiParam("${swagger.pageable.number.touchpoints}")
                                          @RequestParam(required = false, defaultValue = "10") Integer limit,
                                          @ApiParam("${swagger.pageable.start}")
                                          @RequestParam(required = false, defaultValue = "0") Integer page) {
        log.trace("getTouchpoints start");
        String xRequestId = UUID.randomUUID().toString();
        log.debug("getTouchpoints xRequestId = {}", xRequestId);
        Touchpoints touchpoints = gecService.getTouchpoints(limit, page, xRequestId);
        TouchpointsResource resource = GecMapper.toResource(touchpoints);
        log.debug(LogUtils.CONFIDENTIAL_MARKER, "getTouchpoints result = {}", resource);
        log.trace("getTouchpoints end");
        return resource;
    }

    @GetMapping("/psp/{pspCode}/bundles")
    @ResponseStatus(HttpStatus.OK)
    @ApiOperation(value = "", notes = "${swagger.api.gec.getBundlesByCI}")
    public BundlesResource getBundlesByPSP(@ApiParam("${swagger.pageable.number}")
                                          @RequestParam(required = false, defaultValue = "50") Integer limit,
                                          @ApiParam("${swagger.model.gec.boundleType}")
                                          @RequestParam(required = false) ArrayList<BundleType> bundleType,
                                          @ApiParam("${swagger.pageable.start}")
                                          @RequestParam(required = false, defaultValue = "0") Integer page,
                                          @ApiParam("${swagger.model.gec.pspcode}")
                                          @PathVariable(required = true) String pspCode,
                                          @ApiParam("${swagger.model.gec.name}")
                                          @RequestParam(required = false) String name) {
        log.trace("getBundlesByPSP start");
        String xRequestId = UUID.randomUUID().toString();
        log.debug("getBundlesByPSP cifiscalcode = {}, xRequestId = {}", pspCode, xRequestId);
        Bundles bundles = gecService.getBundlesByPSP(pspCode, bundleType, name, limit, page, xRequestId);
        BundlesResource resource = GecMapper.toResource(bundles);
        log.debug(LogUtils.CONFIDENTIAL_MARKER, "getBundlesByPSP result = {}", resource);
        log.trace("getBundlesByPSP end");
        return resource;
    }


}