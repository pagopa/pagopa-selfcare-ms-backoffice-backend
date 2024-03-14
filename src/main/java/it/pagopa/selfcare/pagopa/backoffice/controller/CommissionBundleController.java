package it.pagopa.selfcare.pagopa.backoffice.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import it.pagopa.selfcare.pagopa.backoffice.model.commissionbundle.*;
import it.pagopa.selfcare.pagopa.backoffice.model.commissionbundle.client.BundleCreateResponse;
import it.pagopa.selfcare.pagopa.backoffice.model.commissionbundle.client.BundleRequest;
import it.pagopa.selfcare.pagopa.backoffice.model.commissionbundle.client.BundleType;
import it.pagopa.selfcare.pagopa.backoffice.service.CommissionBundleService;
import it.pagopa.selfcare.pagopa.backoffice.util.OpenApiTableMetadata;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotNull;
import java.util.List;

@Slf4j
@RestController
@RequestMapping(value = "/bundles", produces = MediaType.APPLICATION_JSON_VALUE)
@Tag(name = "Commission bundles")
public class CommissionBundleController {

    private final CommissionBundleService commissionBundleService;

    @Autowired
    public CommissionBundleController(CommissionBundleService commissionBundleService) {
        this.commissionBundleService = commissionBundleService;
    }

    /**
     * Retrieve creditor institution paged bundle list, expanded with taxonomy data
     * @param cisTaxCode optional parameter used for filter by creditor institution tax code
     * @param limit page limit parameter
     * @param page page number parameter
     * @return paged list of bundle resources, expanded with taxonomy data
     */
    @GetMapping("/cis")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Get a paginated list of bundles to be used by creditor institutions", security = {@SecurityRequirement(name = "JWT")})
    @OpenApiTableMetadata(readWriteIntense = OpenApiTableMetadata.ReadWrite.READ)
    public BundlesResource getCisBundles(
            @Parameter(description = "Creditor Institution Tax Code") @RequestParam(required = false) String cisTaxCode,
            @Parameter(description = "Number of elements on one page. Default = 50") @RequestParam(required = false, defaultValue = "50") Integer limit,
            @Parameter(description = "Page number. Page value starts from 0") @RequestParam(required = false, defaultValue = "0") Integer page
    ) {
        return commissionBundleService.getCisBundles(cisTaxCode, limit, page);
    }

    @GetMapping("/payment-types")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Get a paginated list of payment types", security = {@SecurityRequirement(name = "JWT")})
    @OpenApiTableMetadata(readWriteIntense = OpenApiTableMetadata.ReadWrite.READ)
    public BundlePaymentTypes getBundlesPaymentTypes(
            @Parameter(description = "Number of elements on one page. Default = 50") @RequestParam(required = false, defaultValue = "50") Integer limit,
            @Parameter(description = "Page number. Page value starts from 0") @RequestParam(required = false, defaultValue = "0") Integer page
    ) {
        return commissionBundleService.getBundlesPaymentTypes(limit, page);
    }

    @GetMapping("/touchpoints")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Get a paginated list of touchpoints", security = {@SecurityRequirement(name = "JWT")})
    @OpenApiTableMetadata(readWriteIntense = OpenApiTableMetadata.ReadWrite.READ)
    public Touchpoints getTouchpoints(
            @Parameter(description = "Number of elements on one page. Default = 10") @RequestParam(required = false, defaultValue = "10") Integer limit,
            @Parameter(description = "Page number. Page value starts from 0") @RequestParam(required = false, defaultValue = "0") Integer page
    ) {
        return commissionBundleService.getTouchpoints(limit, page);
    }

    @GetMapping("/payment-service-providers/{psp-tax-code}")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Get a paginated list of bundles related to PSP", security = {@SecurityRequirement(name = "JWT")})
    @OpenApiTableMetadata(readWriteIntense = OpenApiTableMetadata.ReadWrite.READ)
    public BundlesResource getBundlesByPSP(
            @Parameter(description = "Number of elements on one page. Default = 50") @RequestParam(required = false, defaultValue = "50") Integer limit,
            @Parameter(description = "Commission bundle's type") @RequestParam(name = "bundle-type", required = false) List<BundleType> bundleType,
            @Parameter(description = "Page number. Page value starts from 0") @RequestParam(required = false, defaultValue = "0") Integer page,
            @Parameter(description = "Tax code of the payment service provider") @PathVariable("psp-tax-code") String pspTaxCode,
            @Parameter(description = "Commission bundle's name") @RequestParam(required = false) String name
    ) {
        return commissionBundleService.getBundlesByPSP(pspTaxCode, bundleType, name, limit, page);
    }

    @PostMapping(value = "/payment-service-providers/{psp-tax-code}", consumes = {MediaType.APPLICATION_JSON_VALUE}, produces = {MediaType.APPLICATION_JSON_VALUE})
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create a commission bundle related to PSP", security = {@SecurityRequirement(name = "JWT")})
    @OpenApiTableMetadata(readWriteIntense = OpenApiTableMetadata.ReadWrite.WRITE)
    public BundleCreateResponse createBundle(
            @Parameter(description = "Tax code of the payment service provider") @PathVariable("psp-tax-code") String pspTaxCode,
            @Parameter(description = "Commission bundle related to PSP to be created") @RequestBody @NotNull BundleRequest bundleRequest
    ) {
        return commissionBundleService.createPSPBundle(pspTaxCode, bundleRequest);
    }

    @GetMapping(value = "/{id-bundle}/payment-service-providers/{psp-tax-code}")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Get a bundle by psp code and bundle id", security = {@SecurityRequirement(name = "JWT")})
    @OpenApiTableMetadata(readWriteIntense = OpenApiTableMetadata.ReadWrite.READ)
    public BundleResource getBundleDetailByPSP(
            @Parameter(description = "Tax code of the payment service provider") @PathVariable("psp-tax-code") String pspTaxCode,
            @Parameter(description = "Commission bundle's id") @PathVariable("id-bundle") String idBundle
    ) {
        return commissionBundleService.getBundleDetailByPSP(pspTaxCode, idBundle);
    }

    @PutMapping(value = "/{id-bundle}/payment-service-providers/{psp-tax-code}")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Update a bundle by psp code and bundle id", security = {@SecurityRequirement(name = "JWT")})
    @OpenApiTableMetadata(readWriteIntense = OpenApiTableMetadata.ReadWrite.WRITE)
    public void updatePSPBundle(
            @Parameter(description = "Tax code of the payment service provider") @PathVariable("psp-tax-code") String pspTaxCode,
            @Parameter(description = "Commission bundle's id") @PathVariable("id-bundle") String idBundle,
            @Parameter(description = "Commission bundle related to PSP to be updated") @RequestBody @NotNull BundleRequest bundle
    ) {
        commissionBundleService.updatePSPBundle(pspTaxCode, idBundle, bundle);
    }

    @DeleteMapping(value = "/{id-bundle}/payment-service-providers/{psp-tax-code}")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Delete a bundle by psp code and bundle id", security = {@SecurityRequirement(name = "JWT")})
    @OpenApiTableMetadata(readWriteIntense = OpenApiTableMetadata.ReadWrite.WRITE)
    public void deletePSPBundle(
            @Parameter(description = "Tax code of the payment service provider") @PathVariable("psp-tax-code") String pspTaxCode,
            @Parameter(description = "Commission bundle's id") @PathVariable("id-bundle") String idBundle
    ) {
        commissionBundleService.deletePSPBundle(pspTaxCode, idBundle);
    }

    /**
     * Accept a list of EC subscription requests to a public bundle
     *
     * @param pspTaxCode          the tax code of the PSP that owns the public bundle
     * @param bundleRequestIdList the list of bundle request id to be accepted
     */
    @PostMapping(value = "/requests/payment-service-providers/{psp-tax-code}/accept", consumes = {MediaType.APPLICATION_JSON_VALUE})
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Accept a list of subscription to a public bundle of a PSP", security = {@SecurityRequirement(name = "JWT")})
    @OpenApiTableMetadata
    public void acceptPublicBundleSubscriptions(
            @Parameter(description = "Tax code of the payment service provider") @PathVariable("psp-tax-code") String pspTaxCode,
            @RequestBody @NotNull List<String> bundleRequestIdList
    ) {
        commissionBundleService.acceptPublicBundleSubscriptionsByPSP(pspTaxCode, bundleRequestIdList);
    }
}
