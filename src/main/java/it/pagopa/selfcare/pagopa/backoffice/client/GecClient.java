package it.pagopa.selfcare.pagopa.backoffice.client;

import it.pagopa.selfcare.pagopa.backoffice.config.feign.GecFeignConfig;
import it.pagopa.selfcare.pagopa.backoffice.model.commissionbundle.Bundle;
import it.pagopa.selfcare.pagopa.backoffice.model.commissionbundle.Bundles;
import it.pagopa.selfcare.pagopa.backoffice.model.commissionbundle.client.BundleCreateResponse;
import it.pagopa.selfcare.pagopa.backoffice.model.commissionbundle.client.BundlePaymentTypesDTO;
import it.pagopa.selfcare.pagopa.backoffice.model.commissionbundle.client.BundleRequest;
import it.pagopa.selfcare.pagopa.backoffice.model.commissionbundle.client.BundleType;
import it.pagopa.selfcare.pagopa.backoffice.model.commissionbundle.client.CIBundleId;
import it.pagopa.selfcare.pagopa.backoffice.model.commissionbundle.client.TouchpointsDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
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

    @PostMapping(value = "/cis/{ci-code}/offers/{id-bundle-offer}/accept", produces = MediaType.APPLICATION_JSON_VALUE)
    CIBundleId ciAcceptPrivateBundleOffer(@PathVariable("ci-code") String ciTaxCode,
                                          @PathVariable("id-bundle-offer") String idBundleOffer);

    @DeleteMapping(value = "/cis/{ci-code}/bundles/{id-bundle}")
    void removeCIBundle(@PathVariable("ci-code") String ciTaxCode,
                        @PathVariable("id-bundle") String idBundle);
}
