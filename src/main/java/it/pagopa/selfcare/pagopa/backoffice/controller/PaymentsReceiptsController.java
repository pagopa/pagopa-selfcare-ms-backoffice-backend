package it.pagopa.selfcare.pagopa.backoffice.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import it.pagopa.selfcare.pagopa.backoffice.model.paymentsreceipts.ReceiptsInfo;
import it.pagopa.selfcare.pagopa.backoffice.service.PaymentsReceiptsService;
import it.pagopa.selfcare.pagopa.backoffice.util.OpenApiTableMetadata;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping(value = "/payments-receipts")
@Tag(name = "Payments Receipts")
public class PaymentsReceiptsController {

    private final PaymentsReceiptsService paymentsReceiptsService;

    @Autowired
    public PaymentsReceiptsController(PaymentsReceiptsService paymentsReceiptsService) {
        this.paymentsReceiptsService = paymentsReceiptsService;
    }

    /**
     * Return the organization's receipts list
     *
     * @param organizationTaxCode Organization tax code
     * @param page Page number
     * @param limit Maximum elements for page
     * @param debtorTaxCode Debtor tax code
     * @param fromDate Filter date (after this date)
     * @param toDate Filter date (before this date)
     * @return paged list of the organization's receipts
     */
    @GetMapping(value = "/{organization-tax-code}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Get a paginated list of the organization receipts", security = {@SecurityRequirement(name = "JWT")})
    @OpenApiTableMetadata(readWriteIntense = OpenApiTableMetadata.ReadWrite.READ)
    public ReceiptsInfo getPaymentsReceipts(
            @Parameter(description = "Tax code of the organization") @PathVariable("organization-tax-code") String organizationTaxCode,
            @Parameter(description = "Page number. Page value starts from 0") @RequestParam(required = false, defaultValue = "0") Integer page,
            @Parameter(description = "Number of elements on one page. Default = 50") @RequestParam(required = false, defaultValue = "50") Integer limit,
            @Parameter(description = "Filter by debtor tax code") @RequestParam(required = false) String debtorTaxCode,
            @Parameter(description = "Filter by date, after this date") @RequestParam(required = false) String fromDate,
            @Parameter(description = "Filter by date, before this date") @RequestParam(required = false) String toDate
    ) {
        return paymentsReceiptsService.getPaymentsReceipts(organizationTaxCode, page, limit, debtorTaxCode, fromDate, toDate);
    }
}
