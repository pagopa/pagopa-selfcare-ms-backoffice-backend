package it.pagopa.selfcare.pagopa.backoffice.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import it.pagopa.selfcare.pagopa.backoffice.model.ProblemJson;
import it.pagopa.selfcare.pagopa.backoffice.model.commissionbundle.BundlePaymentTypes;
import it.pagopa.selfcare.pagopa.backoffice.model.commissionbundle.BundleSubscriptionStatus;
import it.pagopa.selfcare.pagopa.backoffice.model.commissionbundle.CIBundleAttributeResource;
import it.pagopa.selfcare.pagopa.backoffice.model.commissionbundle.CIBundleSubscriptionsDetail;
import it.pagopa.selfcare.pagopa.backoffice.model.commissionbundle.CIBundleSubscriptionsResource;
import it.pagopa.selfcare.pagopa.backoffice.model.commissionbundle.CIBundlesResource;
import it.pagopa.selfcare.pagopa.backoffice.model.commissionbundle.PSPBundleResource;
import it.pagopa.selfcare.pagopa.backoffice.model.commissionbundle.PSPBundlesResource;
import it.pagopa.selfcare.pagopa.backoffice.model.commissionbundle.Touchpoints;
import it.pagopa.selfcare.pagopa.backoffice.model.commissionbundle.client.BundleCreateResponse;
import it.pagopa.selfcare.pagopa.backoffice.model.commissionbundle.client.BundleRequest;
import it.pagopa.selfcare.pagopa.backoffice.model.commissionbundle.client.BundleType;
import it.pagopa.selfcare.pagopa.backoffice.model.commissionbundle.client.CIBundleId;
import it.pagopa.selfcare.pagopa.backoffice.model.commissionbundle.client.CiTaxCodeList;
import it.pagopa.selfcare.pagopa.backoffice.model.commissionbundle.client.PublicBundleRequest;
import it.pagopa.selfcare.pagopa.backoffice.security.JwtSecurity;
import it.pagopa.selfcare.pagopa.backoffice.service.CommissionBundleService;
import it.pagopa.selfcare.pagopa.backoffice.util.OpenApiTableMetadata;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
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
     *
     * @param ciTaxCode optional parameter used for filter by creditor institution tax code
     * @param limit     page limit parameter
     * @param page      page number parameter
     * @return paged list of bundle resources, expanded with taxonomy data
     */
    @GetMapping("/creditor-institutions")
    @ResponseStatus(HttpStatus.OK)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = CIBundlesResource.class))),
            @ApiResponse(responseCode = "400", description = "Bad Request", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ProblemJson.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content(schema = @Schema())),
            @ApiResponse(responseCode = "404", description = "Not found", content = @Content(schema = @Schema(implementation = ProblemJson.class))),
            @ApiResponse(responseCode = "429", description = "Too many requests", content = @Content(schema = @Schema())),
            @ApiResponse(responseCode = "500", description = "Service unavailable", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ProblemJson.class)))
    })
    @Operation(summary = "Get a paginated list of bundles to be used by creditor institutions", security = {@SecurityRequirement(name = "JWT")})
    @OpenApiTableMetadata(readWriteIntense = OpenApiTableMetadata.ReadWrite.READ)
    @JwtSecurity(paramName = "ciTaxCode", skipCheckIfParamIsNull = true, checkAdminRole = true)
    public CIBundlesResource getCisBundles(
            @Parameter(description = "Commission bundle's type") @RequestParam BundleType bundleType,
            @Parameter(description = "Bundle subscription status, required in case of private bundle otherwise is not considered") @RequestParam(required = false) BundleSubscriptionStatus status,
            @Parameter(description = "Creditor institution's tax code, required in case of public/private bundle, otherwise is optional and used to filter the results") @RequestParam(required = false) String ciTaxCode,
            @Parameter(description = "Commission bundle's name, used to filter out results") @RequestParam(required = false) String name,
            @Parameter(description = "Number of elements in one page") @RequestParam(required = false, defaultValue = "50") Integer limit,
            @Parameter(description = "Page number") @RequestParam(required = false, defaultValue = "0") @Min(0) Integer page
    ) {
        return this.commissionBundleService.getCIBundles(bundleType, status, ciTaxCode, name, limit, page);
    }

    @GetMapping("/payment-types")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Get a paginated list of payment types", security = {@SecurityRequirement(name = "JWT")})
    @OpenApiTableMetadata(readWriteIntense = OpenApiTableMetadata.ReadWrite.READ)
    public BundlePaymentTypes getBundlesPaymentTypes(
            @Parameter(description = "Number of elements on one page. Default = 50") @RequestParam(required = false, defaultValue = "50") Integer limit,
            @Parameter(description = "Page number. Page value starts from 0") @RequestParam(required = false, defaultValue = "0") Integer page
    ) {
        return this.commissionBundleService.getBundlesPaymentTypes(limit, page);
    }

    @GetMapping("/touchpoints")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Get a paginated list of touchpoints", security = {@SecurityRequirement(name = "JWT")})
    @OpenApiTableMetadata(readWriteIntense = OpenApiTableMetadata.ReadWrite.READ)
    public Touchpoints getTouchpoints(
            @Parameter(description = "Number of elements on one page. Default = 10") @RequestParam(required = false, defaultValue = "10") Integer limit,
            @Parameter(description = "Page number. Page value starts from 0") @RequestParam(required = false, defaultValue = "0") Integer page
    ) {
        return this.commissionBundleService.getTouchpoints(limit, page);
    }

    @GetMapping("/payment-service-providers/{psp-tax-code}")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Get a paginated list of bundles related to PSP", security = {@SecurityRequirement(name = "JWT")})
    @OpenApiTableMetadata(readWriteIntense = OpenApiTableMetadata.ReadWrite.READ)
    @JwtSecurity(paramName = "pspTaxCode", checkAdminRole = true)
    public PSPBundlesResource getBundlesByPSP(
            @Parameter(description = "Number of elements on one page. Default = 50") @RequestParam(required = false, defaultValue = "50") Integer limit,
            @Parameter(description = "Commission bundle's type") @RequestParam(name = "bundle-type", required = false) List<BundleType> bundleType,
            @Parameter(description = "Page number. Page value starts from 0") @RequestParam(required = false, defaultValue = "0") Integer page,
            @Parameter(description = "Tax code of the payment service provider") @PathVariable("psp-tax-code") String pspTaxCode,
            @Parameter(description = "Commission bundle's name") @RequestParam(required = false) String name,
            @Parameter(description = "Order bundles by maxPaymentAmount", example = "ASC") @RequestParam(required = false) Sort.Direction maxPaymentAmountOrder,
            @Parameter(description = "Filter bundles with paymentAmount less than") @RequestParam(required = false) Long paymentAmountMinRange,
            @Parameter(description = "Filter bundles with paymentAmount more than") @RequestParam(required = false) Long paymentAmountMaxRange,
            @Parameter(description = "Validity date of bundles, used to retrieve all bundles valid before the specified date (yyyy-MM-dd)", example = "2024-05-10") @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate validBefore,
            @Parameter(description = "Validity date of bundles, used to retrieve all bundles valid after the specified date (yyyy-MM-dd)", example = "2024-05-10") @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate validAfter,
            @Parameter(description = "Validity date of bundles, used to retrieve all bundles that expire before the specified date (yyyy-MM-dd)", example = "2024-05-10") @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate expireBefore,
            @Parameter(description = "Validity date of bundles, used to retrieve all bundles that expire after the specified date (yyyy-MM-dd)", example = "2024-05-10") @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate expireAfter
    ) {
        return this.commissionBundleService.getBundlesByPSP(pspTaxCode, bundleType, name, maxPaymentAmountOrder, paymentAmountMinRange, paymentAmountMaxRange, validBefore, validAfter, expireBefore, expireAfter, limit, page);
    }

    @PostMapping(value = "/payment-service-providers/{psp-tax-code}", consumes = {MediaType.APPLICATION_JSON_VALUE}, produces = {MediaType.APPLICATION_JSON_VALUE})
    @ResponseStatus(HttpStatus.CREATED)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Created", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = BundleCreateResponse.class))),
            @ApiResponse(responseCode = "400", description = "Bad Request", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ProblemJson.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content(schema = @Schema())),
            @ApiResponse(responseCode = "404", description = "Not found", content = @Content(schema = @Schema(implementation = ProblemJson.class))),
            @ApiResponse(responseCode = "429", description = "Too many requests", content = @Content(schema = @Schema())),
            @ApiResponse(responseCode = "500", description = "Service unavailable", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ProblemJson.class)))
    })
    @Operation(summary = "Create a commission bundle related to PSP", security = {@SecurityRequirement(name = "JWT")})
    @OpenApiTableMetadata(readWriteIntense = OpenApiTableMetadata.ReadWrite.WRITE)
    @JwtSecurity(paramName = "pspTaxCode", checkAdminRole = true)
    public BundleCreateResponse createBundle(
            @Parameter(description = "Tax code of the payment service provider") @PathVariable("psp-tax-code") String pspTaxCode,
            @Parameter(description = "Commission bundle related to PSP to be created") @RequestBody @NotNull BundleRequest bundleRequest
    ) {
        return this.commissionBundleService.createPSPBundle(pspTaxCode, bundleRequest);
    }

    @GetMapping(value = "/{id-bundle}/payment-service-providers/{psp-tax-code}")
    @ResponseStatus(HttpStatus.OK)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = PSPBundleResource.class))),
            @ApiResponse(responseCode = "400", description = "Bad Request", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ProblemJson.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content(schema = @Schema())),
            @ApiResponse(responseCode = "404", description = "Not found", content = @Content(schema = @Schema(implementation = ProblemJson.class))),
            @ApiResponse(responseCode = "429", description = "Too many requests", content = @Content(schema = @Schema())),
            @ApiResponse(responseCode = "500", description = "Service unavailable", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ProblemJson.class)))
    })
    @Operation(summary = "Get a bundle by psp code and bundle id", security = {@SecurityRequirement(name = "JWT")})
    @OpenApiTableMetadata(readWriteIntense = OpenApiTableMetadata.ReadWrite.READ)
    @JwtSecurity(paramName = "pspTaxCode")
    public PSPBundleResource getBundleDetailByPSP(
            @Parameter(description = "Tax code of the payment service provider") @PathVariable("psp-tax-code") String pspTaxCode,
            @Parameter(description = "Commission bundle's id") @PathVariable("id-bundle") String idBundle
    ) {
        return this.commissionBundleService.getBundleDetailByPSP(pspTaxCode, idBundle);
    }

    @PutMapping(value = "/{id-bundle}/payment-service-providers/{psp-tax-code}")
    @ResponseStatus(HttpStatus.OK)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Created", content = @Content(schema = @Schema())),
            @ApiResponse(responseCode = "400", description = "Bad Request", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ProblemJson.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content(schema = @Schema())),
            @ApiResponse(responseCode = "404", description = "Not found", content = @Content(schema = @Schema(implementation = ProblemJson.class))),
            @ApiResponse(responseCode = "429", description = "Too many requests", content = @Content(schema = @Schema())),
            @ApiResponse(responseCode = "500", description = "Service unavailable", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ProblemJson.class)))
    })
    @Operation(summary = "Update a bundle by psp code and bundle id", security = {@SecurityRequirement(name = "JWT")})
    @OpenApiTableMetadata(readWriteIntense = OpenApiTableMetadata.ReadWrite.WRITE)
    @JwtSecurity(paramName = "pspTaxCode", checkAdminRole = true)
    public void updatePSPBundle(
            @Parameter(description = "Tax code of the payment service provider") @PathVariable("psp-tax-code") String pspTaxCode,
            @Parameter(description = "Commission bundle's id") @PathVariable("id-bundle") String idBundle,
            @Parameter(description = "Commission bundle related to PSP to be updated") @RequestBody @NotNull BundleRequest bundle
    ) {
        this.commissionBundleService.updatePSPBundle(pspTaxCode, idBundle, bundle);
    }

    @DeleteMapping(value = "/{id-bundle}/payment-service-providers/{psp-tax-code}")
    @ResponseStatus(HttpStatus.OK)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema())),
            @ApiResponse(responseCode = "400", description = "Bad Request", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ProblemJson.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content(schema = @Schema())),
            @ApiResponse(responseCode = "404", description = "Not found", content = @Content(schema = @Schema(implementation = ProblemJson.class))),
            @ApiResponse(responseCode = "429", description = "Too many requests", content = @Content(schema = @Schema())),
            @ApiResponse(responseCode = "500", description = "Service unavailable", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ProblemJson.class)))
    })
    @Operation(summary = "Delete a bundle by psp code and bundle id", security = {@SecurityRequirement(name = "JWT")})
    @OpenApiTableMetadata(readWriteIntense = OpenApiTableMetadata.ReadWrite.WRITE)
    @JwtSecurity(paramName = "pspTaxCode", checkAdminRole = true)
    public void deletePSPBundle(
            @Parameter(description = "Tax code of the payment service provider") @PathVariable("psp-tax-code") String pspTaxCode,
            @Parameter(description = "Commission bundle's id") @PathVariable("id-bundle") String idBundle,
            @Parameter(description = "Bundle's name for email notification") @RequestParam String bundleName,
            @Parameter(description = "Payment Service Provider's name for email notification") @RequestParam String pspName,
            @Parameter(description = "Commission bundle's type for email notification") @RequestParam BundleType bundleType
    ) {
        this.commissionBundleService.deletePSPBundle(pspTaxCode, idBundle, bundleName, pspName, bundleType);
    }

    /**
     * Accept a subscription requests of a CI to a public bundle
     *
     * @param pspTaxCode      payment service provider's tax code that owns the public bundle
     * @param bundleRequestId bundle request id to be accepted
     * @param ciTaxCode       creditor institution's tax code
     * @param bundleName      bundle's name
     */
    @PostMapping(value = "/payment-service-providers/{psp-tax-code}/requests/{bundle-request-id}/accept", consumes = {MediaType.APPLICATION_JSON_VALUE})
    @ResponseStatus(HttpStatus.OK)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema())),
            @ApiResponse(responseCode = "400", description = "Bad Request", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ProblemJson.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content(schema = @Schema())),
            @ApiResponse(responseCode = "404", description = "Not found", content = @Content(schema = @Schema(implementation = ProblemJson.class))),
            @ApiResponse(responseCode = "429", description = "Too many requests", content = @Content(schema = @Schema())),
            @ApiResponse(responseCode = "500", description = "Service unavailable", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ProblemJson.class)))
    })
    @Operation(summary = "Accept a subscription to a public bundle of a PSP", security = {@SecurityRequirement(name = "JWT")})
    @OpenApiTableMetadata
    @JwtSecurity(paramName = "pspTaxCode")
    public void acceptPublicBundleSubscriptions(
            @Parameter(description = "Tax code of the payment service provider") @PathVariable("psp-tax-code") String pspTaxCode,
            @Parameter(description = "Public bundle request's id") @PathVariable("bundle-request-id") String bundleRequestId,
            @Parameter(description = "Creditor institution's tax code for email notification") @RequestParam String ciTaxCode,
            @Parameter(description = "Bundle's name for email notification") @RequestParam String bundleName
    ) {
        this.commissionBundleService.acceptPublicBundleSubscriptionsByPSP(pspTaxCode, bundleRequestId, ciTaxCode, bundleName);
    }

    /**
     * Reject a subscription requests to a public bundle
     *
     * @param pspTaxCode      the tax code of the PSP that owns the public bundle
     * @param bundleRequestId the request id to be rejected
     * @param ciTaxCode       creditor institution's tax code
     * @param bundleName      bundle's name
     */
    @PostMapping(value = "/payment-service-providers/{psp-tax-code}/requests/{bundle-request-id}/reject", consumes = {MediaType.APPLICATION_JSON_VALUE})
    @ResponseStatus(HttpStatus.OK)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema())),
            @ApiResponse(responseCode = "400", description = "Bad Request", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ProblemJson.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content(schema = @Schema())),
            @ApiResponse(responseCode = "404", description = "Not found", content = @Content(schema = @Schema(implementation = ProblemJson.class))),
            @ApiResponse(responseCode = "429", description = "Too many requests", content = @Content(schema = @Schema())),
            @ApiResponse(responseCode = "500", description = "Service unavailable", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ProblemJson.class)))
    })
    @Operation(summary = "reject of subscription to a public bundle of a PSP", security = {@SecurityRequirement(name = "JWT")})
    @OpenApiTableMetadata
    @JwtSecurity(paramName = "pspTaxCode", checkAdminRole = true)
    public void rejectPublicBundleSubscription(
            @Parameter(description = "Tax code of the payment service provider") @PathVariable("psp-tax-code") String pspTaxCode,
            @Parameter(description = "Public bundle request's id") @PathVariable("bundle-request-id") String bundleRequestId,
            @Parameter(description = "Creditor institution's tax code for email notification") @RequestParam String ciTaxCode,
            @Parameter(description = "Bundle's name for email notification") @RequestParam String bundleName
    ) {
        this.commissionBundleService.rejectPublicBundleSubscriptionByPSP(pspTaxCode, bundleRequestId, ciTaxCode, bundleName);
    }

    /**
     * Retrieve a paginated list of creditor institution's subscriptions to a public/private bundle of a PSP
     *
     * @param idBundle   the id of the public/private bundle
     * @param pspTaxCode the payment service provider's tax code
     * @param bundleType the type of the bundle (Required only for subscription with status {@link BundleSubscriptionStatus#WAITING}
     * @param status     the status of the subscription
     * @param ciTaxCode  the creditor institution's tax code
     * @param limit      the size of the page
     * @param page       the page number
     * @return a paginated list of creditor institution's info
     */
    @GetMapping("/{id-bundle}/payment-service-providers/{psp-tax-code}/subscriptions")
    @ResponseStatus(HttpStatus.OK)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = CIBundleSubscriptionsResource.class))),
            @ApiResponse(responseCode = "400", description = "Bad Request", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ProblemJson.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content(schema = @Schema())),
            @ApiResponse(responseCode = "404", description = "Not found", content = @Content(schema = @Schema(implementation = ProblemJson.class))),
            @ApiResponse(responseCode = "429", description = "Too many requests", content = @Content(schema = @Schema())),
            @ApiResponse(responseCode = "500", description = "Service unavailable", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ProblemJson.class)))
    })
    @Operation(summary = "Get a paginated list of creditor institution's subscriptions to a public/private bundle of a PSP", security = {@SecurityRequirement(name = "JWT")})
    @OpenApiTableMetadata(readWriteIntense = OpenApiTableMetadata.ReadWrite.READ)
    @JwtSecurity(paramName = "pspTaxCode", checkAdminRole = true)
    public CIBundleSubscriptionsResource getBundleCISubscriptions(
            @Parameter(description = "Commission bundle's id") @PathVariable("id-bundle") String idBundle,
            @Parameter(description = "Payment Service Provider's tax code") @PathVariable("psp-tax-code") String pspTaxCode,
            @Parameter(description = "Commission bundle's type (Required only for subscription with status WAITING") @RequestParam(required = false) BundleType bundleType,
            @Parameter(description = "Subscription status") @RequestParam BundleSubscriptionStatus status,
            @Parameter(description = "Creditor Institution's tax code, used for filtering results") @RequestParam(required = false) String ciTaxCode,
            @Parameter(description = "Number of elements in one page") @RequestParam(required = false, defaultValue = "50") Integer limit,
            @Parameter(description = "Page number") @RequestParam(required = false, defaultValue = "0") Integer page
    ) {
        if (BundleSubscriptionStatus.ACCEPTED.equals(status)) {
            return this.commissionBundleService.getAcceptedBundleCISubscriptions(idBundle, pspTaxCode, ciTaxCode, limit, page);
        }
        return this.commissionBundleService.getWaitingBundleCISubscriptions(idBundle, pspTaxCode, bundleType, ciTaxCode, limit, page);
    }

    /**
     * Retrieve the detail of a creditor institution's subscription to a public/private bundle of a PSP
     *
     * @param idBundle   the id of the public/private bundle
     * @param pspTaxCode the payment service provider's tax code
     * @param ciTaxCode  the creditor institution's tax code
     * @param bundleType the type of the bundle (Required only for subscription with status {@link BundleSubscriptionStatus#WAITING}
     * @param status     the status of the subscription
     * @return the detail of a creditor institution's subscription
     */
    @GetMapping("/{id-bundle}/payment-service-providers/{psp-tax-code}/subscriptions/{ci-tax-code}")
    @ResponseStatus(HttpStatus.OK)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = CIBundleSubscriptionsDetail.class))),
            @ApiResponse(responseCode = "400", description = "Bad Request", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ProblemJson.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content(schema = @Schema())),
            @ApiResponse(responseCode = "404", description = "Not found", content = @Content(schema = @Schema(implementation = ProblemJson.class))),
            @ApiResponse(responseCode = "429", description = "Too many requests", content = @Content(schema = @Schema())),
            @ApiResponse(responseCode = "500", description = "Service unavailable", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ProblemJson.class)))
    })
    @Operation(summary = "Get the detail of a creditor institution's subscription to a public/private bundle of a PSP", security = {@SecurityRequirement(name = "JWT")})
    @OpenApiTableMetadata(readWriteIntense = OpenApiTableMetadata.ReadWrite.READ)
    @JwtSecurity(paramName = "pspTaxCode", checkAdminRole = true)
    public CIBundleSubscriptionsDetail getBundleCISubscriptionsDetail(
            @Parameter(description = "Commission bundle's id") @PathVariable("id-bundle") String idBundle,
            @Parameter(description = "Payment Service Provider's tax code") @PathVariable("psp-tax-code") String pspTaxCode,
            @Parameter(description = "Creditor institution's tax code") @PathVariable("ci-tax-code") String ciTaxCode,
            @Parameter(description = "Commission bundle's type (Required only for subscription with status WAITING") @RequestParam(required = false) BundleType bundleType,
            @Parameter(description = "Subscription status") @RequestParam BundleSubscriptionStatus status
    ) {
        if (BundleSubscriptionStatus.ACCEPTED.equals(status)) {
            return this.commissionBundleService.getAcceptedBundleCISubscriptionsDetail(idBundle, pspTaxCode, ciTaxCode);
        }
        return this.commissionBundleService.getWaitingBundleCISubscriptionsDetail(idBundle, pspTaxCode, ciTaxCode, bundleType);
    }

    /**
     * Remove the creditor institution's subscription to the specified public/private bundle and notify the Creditor Institution by email
     *
     * @param ciBundleId Subscription's id of a creditor institution to a bundle
     * @param ciTaxCode  Creditor Institution's tax code
     * @param bundleName Bundle's name, if present sends an email to notify the Creditor Institution
     */
    @DeleteMapping(value = "/{ci-bundle-id}/creditor-institutions/{ci-tax-code}")
    @ResponseStatus(HttpStatus.OK)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema())),
            @ApiResponse(responseCode = "400", description = "Bad Request", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ProblemJson.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content(schema = @Schema())),
            @ApiResponse(responseCode = "404", description = "Not found", content = @Content(schema = @Schema(implementation = ProblemJson.class))),
            @ApiResponse(responseCode = "429", description = "Too many requests", content = @Content(schema = @Schema())),
            @ApiResponse(responseCode = "500", description = "Service unavailable", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ProblemJson.class)))
    })
    @Operation(summary = "Delete a creditor institution's subscription to a public/private bundle", security = {@SecurityRequirement(name = "JWT")})
    @OpenApiTableMetadata(readWriteIntense = OpenApiTableMetadata.ReadWrite.WRITE)
    @JwtSecurity(paramName = "ciTaxCode", checkAdminRole = true)
    public void deleteCIBundleSubscription(
            @Parameter(description = "Subscription's id of a creditor institution to a bundle") @PathVariable("ci-bundle-id") String ciBundleId,
            @Parameter(description = "Creditor Institution's tax code") @PathVariable("ci-tax-code") String ciTaxCode,
            @Parameter(description = "Bundle's name, if present sends an email to notify the Creditor Institution") @RequestParam(required = false) String bundleName
    ) {
        this.commissionBundleService.deleteCIBundleSubscription(ciBundleId, ciTaxCode, bundleName);
    }

    /**
     * Remove the creditor institution's subscription request to the specified public bundle
     *
     * @param idBundleRequest Subscription request's id of a creditor institution to a bundle
     * @param ciTaxCode       Creditor Institution's tax code
     */
    @DeleteMapping(value = "/creditor-institutions/{ci-tax-code}/requests/{bundle-request-id}")
    @ResponseStatus(HttpStatus.OK)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema())),
            @ApiResponse(responseCode = "400", description = "Bad Request", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ProblemJson.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content(schema = @Schema())),
            @ApiResponse(responseCode = "404", description = "Not found", content = @Content(schema = @Schema(implementation = ProblemJson.class))),
            @ApiResponse(responseCode = "429", description = "Too many requests", content = @Content(schema = @Schema())),
            @ApiResponse(responseCode = "500", description = "Service unavailable", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ProblemJson.class)))
    })
    @Operation(summary = "Delete a creditor institution's subscription request to a public bundle", security = {@SecurityRequirement(name = "JWT")})
    @OpenApiTableMetadata(readWriteIntense = OpenApiTableMetadata.ReadWrite.WRITE)
    @JwtSecurity(paramName = "ciTaxCode", checkAdminRole = true)
    public void deleteCIBundleRequest(
            @Parameter(description = "Subscription request's id of a creditor institution to a bundle") @PathVariable("bundle-request-id") String idBundleRequest,
            @Parameter(description = "Creditor Institution's tax code") @PathVariable("ci-tax-code") String ciTaxCode
    ) {
        this.commissionBundleService.deleteCIBundleRequest(idBundleRequest, ciTaxCode);
    }

    /**
     * Create the request to subscribe to the specified bundle from the Creditor Institution and notify the Payment Service Provider by email
     *
     * @param ciTaxCode           Creditor Institution's tax code
     * @param publicBundleRequest Bundle request object with bundle and psp information
     */
    @PostMapping(value = "/creditor-institutions/{ci-tax-code}/requests")
    @ResponseStatus(HttpStatus.OK)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema())),
            @ApiResponse(responseCode = "400", description = "Bad Request", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ProblemJson.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content(schema = @Schema())),
            @ApiResponse(responseCode = "404", description = "Not found", content = @Content(schema = @Schema(implementation = ProblemJson.class))),
            @ApiResponse(responseCode = "429", description = "Too many requests", content = @Content(schema = @Schema())),
            @ApiResponse(responseCode = "500", description = "Service unavailable", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ProblemJson.class)))
    })
    @Operation(summary = "Create a creditor institution's subscription request to a public bundle", security = {@SecurityRequirement(name = "JWT")})
    @OpenApiTableMetadata(readWriteIntense = OpenApiTableMetadata.ReadWrite.WRITE)
    @JwtSecurity(paramName = "ciTaxCode", checkAdminRole = true)
    public void createCIBundleRequest(
            @Parameter(description = "Creditor Institution's tax code") @PathVariable("ci-tax-code") String ciTaxCode,
            @Parameter(description = "Bundle's name, if present sends an email to notify the Payment Service Provider") @RequestParam(required = false) String bundleName,
            @RequestBody @NotNull PublicBundleRequest publicBundleRequest
    ) {
        this.commissionBundleService.createCIBundleRequest(ciTaxCode, publicBundleRequest, bundleName);
    }

    /**
     * Delete private bundle offer
     *
     * @param idBundle      private bundle id
     * @param pspTaxCode    payment service provider's tax code
     * @param bundleOfferId id of the bundle offer
     */
    @DeleteMapping(value = "/{id-bundle}/payment-service-providers/{psp-tax-code}/offers/{bundle-offer-id}")
    @ResponseStatus(HttpStatus.OK)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema())),
            @ApiResponse(responseCode = "400", description = "Bad Request", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ProblemJson.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content(schema = @Schema())),
            @ApiResponse(responseCode = "404", description = "Not found", content = @Content(schema = @Schema(implementation = ProblemJson.class))),
            @ApiResponse(responseCode = "429", description = "Too many requests", content = @Content(schema = @Schema())),
            @ApiResponse(responseCode = "500", description = "Service unavailable", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ProblemJson.class)))
    })
    @Operation(summary = "Delete private bundle offer", security = {@SecurityRequirement(name = "JWT")})
    @OpenApiTableMetadata
    @JwtSecurity(paramName = "pspTaxCode",checkAdminRole = true)
    public void deletePrivateBundleOffer(
            @Parameter(description = "Commission bundle's id") @PathVariable("id-bundle") String idBundle,
            @Parameter(description = "Payment Service Provider's tax code") @PathVariable("psp-tax-code") String pspTaxCode,
            @Parameter(description = "Id of the private bundle offer") @PathVariable("bundle-offer-id") String bundleOfferId,
            @Parameter(description = "Creditor institution's tax code for email notification") @RequestParam String ciTaxCode,
            @Parameter(description = "Bundle's name for email notification") @RequestParam String bundleName
    ) {
        this.commissionBundleService.deletePrivateBundleOffer(idBundle, pspTaxCode, bundleOfferId, ciTaxCode, bundleName);
    }

    /**
     * Create the subscription offer for the specified private bundle
     *
     * @param idBundle      the private bundle id
     * @param pspTaxCode    Payment Service Provider's tax code
     * @param bundleName    the private bundle name
     * @param ciTaxCodeList the list tax code of creditor institutions tha will receive the offer
     */
    @PostMapping(value = "/{id-bundle}/payment-service-providers/{psp-tax-code}/offers")
    @ResponseStatus(HttpStatus.OK)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema())),
            @ApiResponse(responseCode = "400", description = "Bad Request", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ProblemJson.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content(schema = @Schema())),
            @ApiResponse(responseCode = "404", description = "Not found", content = @Content(schema = @Schema(implementation = ProblemJson.class))),
            @ApiResponse(responseCode = "429", description = "Too many requests", content = @Content(schema = @Schema())),
            @ApiResponse(responseCode = "500", description = "Service unavailable", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ProblemJson.class)))
    })
    @Operation(summary = "Create a creditor institution's subscription offer to a private bundle", security = {@SecurityRequirement(name = "JWT")})
    @OpenApiTableMetadata(readWriteIntense = OpenApiTableMetadata.ReadWrite.WRITE)
    @JwtSecurity(paramName = "pspTaxCode")
    public void createCIBundleOffers(
            @Parameter(description = "Commission bundle's id") @PathVariable("id-bundle") String idBundle,
            @Parameter(description = "Payment Service Provider's tax code") @PathVariable("psp-tax-code") String pspTaxCode,
            @Parameter(description = "Bundle's name for email notification") @RequestParam String bundleName,
            @RequestBody @NotNull CiTaxCodeList ciTaxCodeList
    ) {
        this.commissionBundleService.createCIBundleOffers(idBundle, pspTaxCode, bundleName, ciTaxCodeList);
    }

    @PostMapping(value = "/creditor-institutions/{ci-tax-code}/offers/{id-bundle-offer}/accept")
    @ResponseStatus(HttpStatus.OK)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = CIBundleId.class))),
            @ApiResponse(responseCode = "400", description = "Bad Request", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ProblemJson.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content(schema = @Schema())),
            @ApiResponse(responseCode = "404", description = "Not found", content = @Content(schema = @Schema(implementation = ProblemJson.class))),
            @ApiResponse(responseCode = "429", description = "Too many requests", content = @Content(schema = @Schema())),
            @ApiResponse(responseCode = "500", description = "Service unavailable", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ProblemJson.class)))
    })
    @Operation(summary = "Accept a private bundle offer by bundle offer id and ci tax code", security = {@SecurityRequirement(name = "JWT")})
    @OpenApiTableMetadata
    @JwtSecurity(paramName = "ciTaxCode", checkAdminRole = true)
    public CIBundleId acceptPrivateBundleOffer(
            @Parameter(description = "Tax code of the creditor institution") @PathVariable("ci-tax-code") String ciTaxCode,
            @Parameter(description = "Commission bundle offer's id") @PathVariable("id-bundle-offer") String idBundleOffer,
            @Parameter(description = "Payment Service Provider's tax code for email notification") @RequestParam String pspTaxCode,
            @Parameter(description = "Bundle's name for email notification") @RequestParam String bundleName,
            @RequestBody @NotNull CIBundleAttributeResource ciBundleAttributes
    ) {
        return this.commissionBundleService.acceptPrivateBundleOffer(ciTaxCode, idBundleOffer, pspTaxCode, bundleName, ciBundleAttributes);
    }

    @PostMapping(value = "/creditor-institutions/{ci-tax-code}/offers/{id-bundle-offer}/reject")
    @ResponseStatus(HttpStatus.OK)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema())),
            @ApiResponse(responseCode = "400", description = "Bad Request", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ProblemJson.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content(schema = @Schema())),
            @ApiResponse(responseCode = "404", description = "Not found", content = @Content(schema = @Schema(implementation = ProblemJson.class))),
            @ApiResponse(responseCode = "429", description = "Too many requests", content = @Content(schema = @Schema())),
            @ApiResponse(responseCode = "500", description = "Service unavailable", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ProblemJson.class)))
    })
    @Operation(summary = "Reject a private bundle offer by bundle offer id and ci tax code", security = {@SecurityRequirement(name = "JWT")})
    @OpenApiTableMetadata
    @JwtSecurity(paramName = "ciTaxCode", checkAdminRole = true)
    public void rejectPrivateBundleOffer(
            @Parameter(description = "Tax code of the creditor institution") @PathVariable("ci-tax-code") String ciTaxCode,
            @Parameter(description = "Commission bundle offer's id") @PathVariable("id-bundle-offer") String idBundleOffer,
            @Parameter(description = "Payment Service Provider's tax code for email notification") @RequestParam String pspTaxCode,
            @Parameter(description = "Bundle's name for email notification") @RequestParam String bundleName
    ) {
        this.commissionBundleService.rejectPrivateBundleOffer(ciTaxCode, idBundleOffer, pspTaxCode, bundleName);
    }

    @GetMapping(value = "/payment-service-providers/{psp-tax-code}/export", produces = "text/csv")
    @ResponseStatus(HttpStatus.OK)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK", content = @Content(mediaType = "text/csv", schema = @Schema(type = "string", format = "binary"))),
            @ApiResponse(responseCode = "400", description = "Bad Request", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ProblemJson.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content(schema = @Schema())),
            @ApiResponse(responseCode = "429", description = "Too many requests", content = @Content(schema = @Schema())),
            @ApiResponse(responseCode = "500", description = "Service unavailable", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ProblemJson.class)))
    })
    @Operation(summary = "Export all commission bundle related to a PSP", security = {@SecurityRequirement(name = "JWT")})
    @OpenApiTableMetadata(readWriteIntense = OpenApiTableMetadata.ReadWrite.READ, cacheable = true)
    @JwtSecurity(paramName = "pspTaxCode", checkAdminRole = true)
    public ResponseEntity<Resource> exportPSPBundleList(
            @Parameter(description = "Tax code of the payment service provider") @PathVariable("psp-tax-code") String pspTaxCode,
            @Parameter(description = "Commission bundle's type") @RequestParam List<BundleType> bundleTypeList
    ) {
        byte[] file = this.commissionBundleService.exportPSPBundleList(pspTaxCode, bundleTypeList);

        String fileNameHeader = String.format("attachment; filename=%s_%s_%s_bundle-export.csv",
                pspTaxCode, mapBundleType(bundleTypeList), LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, fileNameHeader)
                .contentType(MediaType.parseMediaType("text/csv"))
                .body(new ByteArrayResource(file));
    }

    private String mapBundleType(List<BundleType> bundleTypeList) {
        if (bundleTypeList.size() == 3) {
            return "tutti";
        }
        if (BundleType.GLOBAL.equals(bundleTypeList.get(0))) {
            return "per-tutti";
        }
        if (BundleType.PUBLIC.equals(bundleTypeList.get(0))) {
            return "su-richiesta";
        }
        if (BundleType.PRIVATE.equals(bundleTypeList.get(0))) {
            return "su-invito";
        }
        return "";
    }
}
