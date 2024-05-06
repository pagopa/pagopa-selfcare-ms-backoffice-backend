package it.pagopa.selfcare.pagopa.backoffice.client;

import feign.FeignException;
import it.pagopa.selfcare.pagopa.backoffice.config.feign.GecFeignConfig;
import it.pagopa.selfcare.pagopa.backoffice.model.commissionbundle.Bundle;
import it.pagopa.selfcare.pagopa.backoffice.model.commissionbundle.Bundles;
import it.pagopa.selfcare.pagopa.backoffice.model.commissionbundle.CIBundle;
import it.pagopa.selfcare.pagopa.backoffice.model.commissionbundle.client.*;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;

@FeignClient(name = "gec", url = "${rest-client.gec.base-url}", configuration = GecFeignConfig.class)
public interface GecClient {

    @GetMapping(value = "/cis/{ci-tax-code}/bundles", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    @Valid
    Bundles getBundlesByCI(
            @PathVariable("ci-tax-code") String ciTaxCode,
            @RequestParam(required = false) Integer limit,
            @RequestParam(required = false) Integer page
    );

    @GetMapping(value = "/touchpoints", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    @Valid
    TouchpointsDTO getTouchpoints(
            @RequestParam(required = false) Integer limit,
            @RequestParam(required = false) Integer page
    );

    @GetMapping(value = "/psps/{psp-code}/bundles", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    @Valid
    Bundles getBundlesByPSP(
            @PathVariable("psp-code") String pspCode,
            @RequestParam(required = false) List<BundleType> types,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) Integer limit,
            @RequestParam(required = false) Integer page
    );

    @PostMapping(value = "/psps/{psp-code}/bundles", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    @Valid
    BundleCreateResponse createPSPBundle(
            @PathVariable("psp-code") String pspCode,
            @RequestBody @NotNull BundleRequest bundle
    );

    @GetMapping(value = "/paymenttypes", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    @Valid
    BundlePaymentTypesDTO getPaymenttypes(
            @RequestParam(required = false) Integer limit,
            @RequestParam(required = false) Integer page
    );

    @GetMapping(value = "/psps/{psp-code}/bundles/{id-bundle}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    @Valid
    Bundle getBundleDetailByPSP(
            @PathVariable("psp-code") String pspCode,
            @PathVariable("id-bundle") String idBundle
    );

    @PutMapping(value = "/psps/{psp-code}/bundles/{id-bundle}", produces = MediaType.APPLICATION_JSON_VALUE)
    void updatePSPBundle(
            @PathVariable("psp-code") String pspCode,
            @PathVariable("id-bundle") String idBundle,
            @RequestBody @NotNull BundleRequest bundle
    );

    @DeleteMapping(value = "/psps/{psp-code}/bundles/{id-bundle}", produces = MediaType.APPLICATION_JSON_VALUE)
    void deletePSPBundle(
            @PathVariable("psp-code") String pspCode,
            @PathVariable("id-bundle") String idBundle
    );

    @PostMapping(value = "/psps/{psp-code}/requests/{id-bundle-request}/accept")
    @Retryable(
            exclude = FeignException.FeignClientException.class,
            maxAttemptsExpression = "${retry.utils.maxAttempts}",
            backoff = @Backoff(delayExpression = "${retry.utils.maxDelay}"))
    void acceptPublicBundleSubscriptionsByPSP(
            @PathVariable("psp-code") String pspCode,
            @PathVariable("id-bundle-request") String idBundleRequest
    );

    @GetMapping(value = "/bundles", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    @Valid
    Bundles getBundles(
            @RequestParam(required = false) List<BundleType> types,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) Integer limit,
            @RequestParam(required = false) Integer page
    );

    @PostMapping(value = "/psps/{psp-code}/requests/{id-bundle-request}/reject")
    @Retryable(
            exclude = FeignException.FeignClientException.class,
            maxAttemptsExpression = "${retry.utils.maxAttempts}",
            backoff = @Backoff(delayExpression = "${retry.utils.maxDelay}"))
    void rejectPublicBundleSubscriptionByPSP(
            @PathVariable("psp-code") String pspCode,
            @PathVariable("id-bundle-request") String idBundleRequest
    );

    @GetMapping(value = "/psps/{psp-code}/requests")
    @ResponseBody
    @Valid
    PublicBundleRequests getPublicBundleSubscriptionRequestByPSP(
            @PathVariable("psp-code") String pspCode,
            @RequestParam(name = "ciFiscalCode", required = false) String ciTaxCode,
            @RequestParam(required = false) String idBundle,
            @RequestParam(required = false) Integer limit,
            @RequestParam(required = false) Integer page
    );

    @GetMapping(value = "/psps/{psp-code}/bundles/{id-bundle}/creditorInstitutions")
    @ResponseBody
    @Valid
    BundleCreditorInstitutionResource getPublicBundleSubscriptionByPSP(
            @PathVariable("psp-code") String pspCode,
            @PathVariable("id-bundle") String idBundle,
            @RequestParam(name = "ciFiscalCode", required = false) String ciTaxCode,
            @RequestParam(required = false) Integer limit,
            @RequestParam(required = false) Integer page
    );

    @GetMapping(value = "/psps/{psp-code}/bundles/{id-bundle}/creditorInstitutions/{ci-tax-code}")
    @ResponseBody
    @Valid
    CiBundleDetails getPublicBundleSubscriptionDetailByPSP(
            @PathVariable("psp-code") String pspCode,
            @PathVariable("ci-tax-code") String ciTaxCode,
            @PathVariable("id-bundle") String idBundle
    );

    @DeleteMapping(value = "/cis/{ci-tax-code}/bundles/{id-bundle}")
    @ResponseBody
    @Valid
    void deleteCIBundle(
            @PathVariable("ci-tax-code") String ciTaxCode,
            @PathVariable("id-bundle") String idBundle
    );

    @GetMapping(value = "/cis/{ci-tax-code}/bundles/{id-bundle}")
    @ResponseBody
    CIBundle getCIBundle(
            @PathVariable("ci-tax-code") String ciTaxCode,
            @PathVariable("id-bundle") String idBundle
    );

    @GetMapping(value = "/cis/{ci-tax-code}/requests")
    @ResponseBody
    PublicBundleRequests getCIPublicBundleRequest(
            @PathVariable("ci-tax-code") String ciTaxCode,
            @RequestParam(required = false) String idPsp,
            @RequestParam(required = false) String idBundle,
            @RequestParam(required = false) Integer limit,
            @RequestParam(required = false) Integer page
    );

    @DeleteMapping(value = "/cis/{ci-tax-code}/requests/{id-bundle-request}")
    @ResponseBody
    void deleteCIBundleRequest(
            @PathVariable("ci-tax-code") String ciTaxCode,
            @PathVariable("id-bundle-request") String idBundleRequest
    );
}
