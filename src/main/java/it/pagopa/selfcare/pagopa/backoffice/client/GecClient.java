package it.pagopa.selfcare.pagopa.backoffice.client;

import feign.FeignException;
import it.pagopa.selfcare.pagopa.backoffice.config.feign.GecFeignConfig;
import it.pagopa.selfcare.pagopa.backoffice.model.commissionbundle.CIBundleAttributeModel;
import it.pagopa.selfcare.pagopa.backoffice.model.commissionbundle.client.Bundle;
import it.pagopa.selfcare.pagopa.backoffice.model.commissionbundle.client.BundleCIOffers;
import it.pagopa.selfcare.pagopa.backoffice.model.commissionbundle.client.BundleCreateResponse;
import it.pagopa.selfcare.pagopa.backoffice.model.commissionbundle.client.BundleCreditorInstitutionResource;
import it.pagopa.selfcare.pagopa.backoffice.model.commissionbundle.client.BundleOffered;
import it.pagopa.selfcare.pagopa.backoffice.model.commissionbundle.client.BundleOffers;
import it.pagopa.selfcare.pagopa.backoffice.model.commissionbundle.client.BundlePaymentTypesDTO;
import it.pagopa.selfcare.pagopa.backoffice.model.commissionbundle.client.BundleRequest;
import it.pagopa.selfcare.pagopa.backoffice.model.commissionbundle.client.BundleRequestId;
import it.pagopa.selfcare.pagopa.backoffice.model.commissionbundle.client.BundleType;
import it.pagopa.selfcare.pagopa.backoffice.model.commissionbundle.client.Bundles;
import it.pagopa.selfcare.pagopa.backoffice.model.commissionbundle.client.CIBundleId;
import it.pagopa.selfcare.pagopa.backoffice.model.commissionbundle.client.CiBundleDetails;
import it.pagopa.selfcare.pagopa.backoffice.model.commissionbundle.client.CiBundles;
import it.pagopa.selfcare.pagopa.backoffice.model.commissionbundle.client.CiTaxCodeList;
import it.pagopa.selfcare.pagopa.backoffice.model.commissionbundle.client.PublicBundleRequest;
import it.pagopa.selfcare.pagopa.backoffice.model.commissionbundle.client.PublicBundleRequests;
import it.pagopa.selfcare.pagopa.backoffice.model.commissionbundle.client.TouchpointsDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
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

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.List;

@FeignClient(name = "gec", url = "${rest-client.gec.base-url}", configuration = GecFeignConfig.class)
public interface GecClient {

    @GetMapping(value = "/cis/{ci-tax-code}/bundles", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    @Valid
    CiBundles getBundlesByCI(
            @PathVariable("ci-tax-code") String ciTaxCode,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String bundleName,
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
            @RequestParam(required = false) Sort.Direction maxPaymentAmountOrder,
            @RequestParam(required = false) Long paymentAmountMinRange,
            @RequestParam(required = false) Long paymentAmountMaxRange,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate validBefore,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate validAfter,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate expireBefore,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate expireAfter,
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
            @RequestParam(required = false) String validFrom,
            @RequestParam(required = false) String expireAt,
            @RequestParam(required = false) Integer limit,
            @RequestParam(required = false) Integer page
    );

    @GetMapping(value = "/bundles/{id-bundle}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    @Valid
    Bundle getBundleDetail(@PathVariable("id-bundle") String idBundle);

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

    @GetMapping(value = "/psps/{psp-code}/offers")
    @ResponseBody
    @Valid
    BundleOffers getPrivateBundleOffersByPSP(
            @PathVariable("psp-code") String pspCode,
            @RequestParam(required = false) String ciTaxCode,
            @RequestParam(required = false) String idBundle,
            @RequestParam(required = false) Integer limit,
            @RequestParam(required = false) Integer page
    );

    @GetMapping(value = "/psps/{psp-code}/bundles/{id-bundle}/creditorInstitutions")
    @ResponseBody
    @Valid
    BundleCreditorInstitutionResource getBundleSubscriptionByPSP(
            @PathVariable("psp-code") String pspCode,
            @PathVariable("id-bundle") String idBundle,
            @RequestParam(name = "ciFiscalCode", required = false) String ciTaxCode,
            @RequestParam(required = false) Integer limit,
            @RequestParam(required = false) Integer page
    );

    @GetMapping(value = "/psps/{psp-code}/bundles/{id-bundle}/creditorInstitutions/{ci-tax-code}")
    @ResponseBody
    @Valid
    CiBundleDetails getBundleSubscriptionDetailByPSP(
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
    CiBundleDetails getCIBundle(
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

    @PostMapping(value = "/cis/{ci-tax-code}/requests")
    @ResponseBody
    @Valid
    BundleRequestId createCIBundleRequest(
            @PathVariable("ci-tax-code") String ciTaxCode,
            @RequestBody @NotNull PublicBundleRequest bundleRequest
    );

    @DeleteMapping(value = "/psps/{psp-code}/bundles/{id-bundle}/offers/{id-bundle-offer}")
    @ResponseBody
    void deletePrivateBundleOffer(
            @PathVariable("psp-code") String pspCode,
            @PathVariable("id-bundle") String idBundle,
            @PathVariable("id-bundle-offer") String idBundleOffer
    );

    @GetMapping(value = "/cis/{ci-tax-code}/offers")
    @ResponseBody
    BundleCIOffers getOffersByCI(
            @PathVariable("ci-tax-code") String ciTaxCode,
            @RequestParam(required = false) String idPsp,
            @RequestParam(required = false) String bundleName,
            @RequestParam(required = false) Integer limit,
            @RequestParam(required = false) Integer page
    );

    @PostMapping(value = "/psps/{psp-code}/bundles/{id-bundle}/offers")
    @ResponseBody
    List<BundleOffered> createPrivateBundleOffer(
            @PathVariable("psp-code") String pspCode,
            @PathVariable("id-bundle") String idBundle,
            @RequestBody CiTaxCodeList ciTaxCodeList
    );

    @PostMapping(value = "/cis/{ci-tax-code}/offers/{id-bundle-offer}/accept", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    CIBundleId acceptPrivateBundleOffer(
            @PathVariable("ci-tax-code") String ciTaxCode,
            @PathVariable("id-bundle-offer") String idBundleOffer,
            @RequestBody List<CIBundleAttributeModel> ciBundleAttributes
    );

    @PostMapping(value = "/cis/{ci-tax-code}/offers/{id-bundle-offer}/reject")
    void rejectPrivateBundleOffer(
            @PathVariable("ci-tax-code") String ciTaxCode,
            @PathVariable("id-bundle-offer") String idBundleOffer
    );
}
