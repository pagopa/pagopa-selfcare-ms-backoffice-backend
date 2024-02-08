package it.pagopa.selfcare.pagopa.backoffice.client;

import it.pagopa.selfcare.pagopa.backoffice.config.feign.GecFeignConfig;
import it.pagopa.selfcare.pagopa.backoffice.model.commissionbundle.Bundles;
import it.pagopa.selfcare.pagopa.backoffice.model.commissionbundle.Bundle;
import it.pagopa.selfcare.pagopa.backoffice.model.commissionbundle.client.*;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotNull;
import java.util.List;

@FeignClient(name = "gec", url = "${rest-client.gec.base-url}", configuration = GecFeignConfig.class)
public interface GecClient {

    @GetMapping(value = "/cis/{cifiscalcode}/bundles", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    Bundles getBundlesByCI(@PathVariable(required = true) String cifiscalcode,
                              @RequestParam(required = false) Integer limit,
                              @RequestParam(required = false) Integer page);

    @GetMapping(value = "/touchpoints", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    TouchpointsDTO getTouchpoints(@RequestParam(required = false) Integer limit,
                                  @RequestParam(required = false) Integer page);

    @GetMapping(value = "/psps/{idpsp}/bundles", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    Bundles getBundlesByPSP(@PathVariable(required = true) String idpsp,
                            @RequestParam(required = false) List<BundleType> bundleType,
                            @RequestParam(required = false) String name,
                            @RequestParam(required = false) Integer limit,
                            @RequestParam(required = false) Integer page);

    @PostMapping(value = "/psps/{idpsp}/bundles", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    String createPSPBundle(@PathVariable(required = true) String idpsp,
                           @RequestBody @NotNull Bundle bundle);

    @GetMapping(value = "/paymenttypes", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    BundlePaymentTypesDTO getPaymenttypes(@RequestParam(required = false) Integer limit,
                                          @RequestParam(required = false) Integer page);
}
