package it.pagopa.selfcare.pagopa.backoffice.client;

import feign.FeignException;
import it.pagopa.selfcare.pagopa.backoffice.config.feign.GecFeignConfig;
import it.pagopa.selfcare.pagopa.backoffice.model.commissionbundle.Bundle;
import it.pagopa.selfcare.pagopa.backoffice.model.commissionbundle.Bundles;
import it.pagopa.selfcare.pagopa.backoffice.model.commissionbundle.client.BundleCreateResponse;
import it.pagopa.selfcare.pagopa.backoffice.model.commissionbundle.client.BundlePaymentTypesDTO;
import it.pagopa.selfcare.pagopa.backoffice.model.commissionbundle.client.BundleRequest;
import it.pagopa.selfcare.pagopa.backoffice.model.commissionbundle.client.BundleType;
import it.pagopa.selfcare.pagopa.backoffice.model.commissionbundle.client.TouchpointsDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

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
                            @RequestParam(required = false) List<BundleType> types,
                            @RequestParam(required = false) String name,
                            @RequestParam(required = false) Integer limit,
                            @RequestParam(required = false) Integer page);

    @PostMapping(value = "/psps/{idpsp}/bundles", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    BundleCreateResponse createPSPBundle(@PathVariable(required = true) String idpsp,
                                         @RequestBody @NotNull BundleRequest bundle);

    @GetMapping(value = "/paymenttypes", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    BundlePaymentTypesDTO getPaymenttypes(@RequestParam(required = false) Integer limit,
                                          @RequestParam(required = false) Integer page);

    @GetMapping(value = "/psps/{idpsp}/bundles/{idbundle}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    Bundle getBundleDetailByPSP(@PathVariable() String idpsp,
                                @PathVariable() String idbundle);

    @PutMapping(value = "/psps/{idpsp}/bundles/{idbundle}", produces = MediaType.APPLICATION_JSON_VALUE)
    void updatePSPBundle(@PathVariable() String idpsp,
                         @PathVariable() String idbundle,
                         @RequestBody @NotNull BundleRequest bundle);

    @DeleteMapping(value = "/psps/{idpsp}/bundles/{idbundle}", produces = MediaType.APPLICATION_JSON_VALUE)
    void deletePSPBundle(@PathVariable() String idpsp,
                         @PathVariable() String idbundle);

    @PostMapping(value = "/psps/{psp-code}/requests/{id-bundle-request}/accept")
    @Retryable(
            exclude = FeignException.FeignClientException.class,
            maxAttemptsExpression = "${retry.utils.maxAttempts}",
            backoff = @Backoff(delayExpression = "${retry.utils.maxDelay}"))
    void acceptPublicBundleSubscriptionsByPSP(@PathVariable("psp-code") String pspCode,
                                              @PathVariable("id-bundle-request") String idBundleRequest);

    @GetMapping(value = "/bundles", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    Bundles getBundles(@RequestParam(required = false) Integer limit,
                       @RequestParam(required = false) Integer page);
}
