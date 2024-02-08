package it.pagopa.selfcare.pagopa.backoffice.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import it.pagopa.selfcare.pagopa.backoffice.model.commissionbundle.*;
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

    @Autowired
    private CommissionBundleService commissionBundleService;

    @GetMapping("/payment-types")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Get a paginated list of payment types", security = {@SecurityRequirement(name = "JWT")})
    @OpenApiTableMetadata
    public BundlePaymentTypes getBundlesPaymentTypes(@Parameter(description = "Number of elements on one page. Default = 50") @RequestParam(required = false, defaultValue = "50") Integer limit,
                                                     @Parameter(description = "Page number. Page value starts from 0") @RequestParam(required = false, defaultValue = "0") Integer page) {

        return commissionBundleService.getBundlesPaymentTypes(limit, page);
    }

    @GetMapping("/touchpoints")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Get a paginated list of touchpoints", security = {@SecurityRequirement(name = "JWT")})
    @OpenApiTableMetadata
    public Touchpoints getTouchpoints(@Parameter(description = "Number of elements on one page. Default = 10") @RequestParam(required = false, defaultValue = "10") Integer limit,
                                      @Parameter(description = "Page number. Page value starts from 0") @RequestParam(required = false, defaultValue = "0") Integer page) {

        return commissionBundleService.getTouchpoints(limit, page);
    }

    @GetMapping("/payment-service-providers/{psp-code}")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Get a paginated list of bundles related to PSP", security = {@SecurityRequirement(name = "JWT")})
    @OpenApiTableMetadata
    public Bundles getBundlesByPSP(@Parameter(description = "Number of elements on one page. Default = 50") @RequestParam(required = false, defaultValue = "50") Integer limit,
                                   @Parameter(description = "Commissional bundle's type") @RequestParam(name = "bundle-type", required = false) List<BundleType> bundleType,
                                   @Parameter(description = "Page number. Page value starts from 0") @RequestParam(required = false, defaultValue = "0") Integer page,
                                   @Parameter(description = "Fiscal code of the payment service provider") @PathVariable("psp-code") String pspCode,
                                   @Parameter(description = "Commissional bundle's name") @RequestParam(required = false) String name) {

        return commissionBundleService.getBundlesByPSP(pspCode, bundleType, name, limit, page);
    }

    @PostMapping(value = "/payment-service-providers/{psp-code}", consumes = {MediaType.APPLICATION_JSON_VALUE}, produces = {MediaType.APPLICATION_JSON_VALUE})
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create a commissional bundle related to PSP", security = {@SecurityRequirement(name = "JWT")})
    @OpenApiTableMetadata
    public String createBundle(@Parameter(description = "Fiscal code of the payment service provider") @PathVariable("psp-code") String pspCode,
                               @Parameter(description = "Commissional bundle related to PSP to be created") @RequestBody @NotNull Bundle bundleDto) {

        return commissionBundleService.createPSPBundle(pspCode, bundleDto);
    }

}
