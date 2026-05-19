package it.pagopa.selfcare.pagopa.backoffice.service;

import it.pagopa.selfcare.pagopa.backoffice.client.GpdClient;
import it.pagopa.selfcare.pagopa.backoffice.model.paymentsreceipts.PaymentsResult;
import it.pagopa.selfcare.pagopa.backoffice.model.paymentsreceipts.ReceiptModelResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class PaymentsReceiptsService {

    public static final String PAGOPA_SERVICE_CODE = "47";
    private final GpdClient gpdClient;

    @Autowired
    public PaymentsReceiptsService(GpdClient gdpClient) {
        this.gpdClient = gdpClient;
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
    public PaymentsResult<ReceiptModelResponse> getPaymentsReceipts(
            String organizationTaxCode,
            Integer page,
            Integer limit,
            String debtorTaxCode,
            String fromDate,
            String toDate,
            String debtorOrIuv
    ) {
        return this.gpdClient.getPaymentsReceipts(organizationTaxCode, page, limit, debtorTaxCode, PAGOPA_SERVICE_CODE, fromDate, toDate, debtorOrIuv);
    }

    /**
     * Retrieve payment receipt detail
     *
     * @param organizationTaxCode Organization tax code
     * @param iuv                 Receipt's iuv
     * @return receipts detail as XML
     */
    public String getPaymentReceiptDetail(
            String organizationTaxCode,
            String iuv
    ) {
        return this.gpdClient.getPaymentReceiptDetail(organizationTaxCode, iuv);
    }
}
