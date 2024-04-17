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
import it.pagopa.selfcare.pagopa.backoffice.model.paymentsreceipts.PaymentsResult;
import it.pagopa.selfcare.pagopa.backoffice.model.paymentsreceipts.ReceiptModelResponse;
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
     * @param page                Page number
     * @param limit               Maximum elements for page
     * @param debtorTaxCode       Debtor tax code
     * @param fromDate            Filter date (after this date)
     * @param toDate              Filter date (before this date)
     * @param debtorOrIuv         Dynamic filter by debtor tax code or iuv
     * @return paged list of the organization's receipts
     */
    @GetMapping(value = "/{organization-tax-code}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Get a paginated list of the organization receipts", security = {@SecurityRequirement(name = "JWT")})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = PaymentsResult.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content(schema = @Schema())),
            @ApiResponse(responseCode = "404", description = "Not found", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ProblemJson.class))),
            @ApiResponse(responseCode = "500", description = "Service unavailable", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ProblemJson.class)))
    })
    @OpenApiTableMetadata(readWriteIntense = OpenApiTableMetadata.ReadWrite.READ)
    public PaymentsResult<ReceiptModelResponse> getPaymentsReceipts(
            @Parameter(description = "Tax code of the organization") @PathVariable("organization-tax-code") String organizationTaxCode,
            @Parameter(description = "Page number. Page value starts from 0") @RequestParam(required = false, defaultValue = "0") Integer page,
            @Parameter(description = "Number of elements on one page. Default = 50") @RequestParam(required = false, defaultValue = "50") Integer limit,
            @Parameter(description = "Filter by debtor tax code") @RequestParam(required = false) String debtorTaxCode,
            @Parameter(description = "Filter by date, after this date") @RequestParam(required = false) String fromDate,
            @Parameter(description = "Filter by date, before this date") @RequestParam(required = false) String toDate,
            @Parameter(description = "Dynamic filter for both IUV and debtor tax code") @RequestParam(required = false) String debtorOrIuv
    ) {
        return paymentsReceiptsService.getPaymentsReceipts(organizationTaxCode, page, limit, debtorTaxCode, fromDate, toDate, debtorOrIuv);
    }

    /**
     * Return receipt's details
     *
     * @param organizationTaxCode Organization tax code
     * @param iuv                 Receipt's IUV
     * @return receipt's details as XML
     */
    @GetMapping(value = "/{organization-tax-code}/detail/{iuv}", produces = {MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE})
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Get the payment receipt's details", security = {@SecurityRequirement(name = "JWT")})
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200", description = "Obtained receipt details.",
                    content = @Content(mediaType = MediaType.APPLICATION_XML_VALUE,
                            schema = @Schema(name = "ReceiptResponse", implementation = String.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content(schema = @Schema())),
            @ApiResponse(responseCode = "404", description = "Not found", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ProblemJson.class))),
            @ApiResponse(responseCode = "500", description = "Service unavailable", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ProblemJson.class)))
    })
    @OpenApiTableMetadata(readWriteIntense = OpenApiTableMetadata.ReadWrite.READ)
    public String getPaymentReceiptDetail(
            @Parameter(description = "Tax code of the organization") @PathVariable("organization-tax-code") String organizationTaxCode,
            @Parameter(description = "Receipt's IUV") @PathVariable("iuv") String iuv
    ) {
        return paymentsReceiptsService.getPaymentReceiptDetail(organizationTaxCode, iuv);
    }
}
