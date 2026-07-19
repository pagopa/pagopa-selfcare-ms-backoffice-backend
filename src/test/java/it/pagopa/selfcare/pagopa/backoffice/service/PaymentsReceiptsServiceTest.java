package it.pagopa.selfcare.pagopa.backoffice.service;

import it.pagopa.selfcare.pagopa.backoffice.client.GpdClient;
import it.pagopa.selfcare.pagopa.backoffice.model.paymentsreceipts.PaymentsResult;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest(classes = {PaymentsReceiptsService.class})
class PaymentsReceiptsServiceTest {
    private static final String ORGANIZATION_TAX_CODE = "organizationTaxCode";
    private static final String DEBTOR_TAX_CODE = "debtor tax code";
    private static final String FROM_DATE = "01/01/2020";
    private static final String TO_DATE = "05/05/2025";
    private static final String PAGOPA_SERVICE_CODE = "47";
    private static final String CIE_SERVICE_CODE = "99";

    private static final String IUV = "IUV";

    @MockBean
    private GpdClient client;
    @Autowired
    private PaymentsReceiptsService service;
    @Test
    void getPaymentsReceipts() {
        when(client.getPaymentsReceipts(ORGANIZATION_TAX_CODE, 0, 50, DEBTOR_TAX_CODE, PAGOPA_SERVICE_CODE, FROM_DATE, TO_DATE, IUV)).thenReturn(
                new PaymentsResult<>());
        assertDoesNotThrow(
                () -> service.getPaymentsReceipts(ORGANIZATION_TAX_CODE, 0, 50, DEBTOR_TAX_CODE, FROM_DATE, TO_DATE, IUV));
        verify(client).getPaymentsReceipts(ORGANIZATION_TAX_CODE, 0, 50, DEBTOR_TAX_CODE, PAGOPA_SERVICE_CODE, FROM_DATE, TO_DATE, IUV);
    }
    @Test
    void getPaymentReceiptDetail() {
        when(client.getPaymentReceiptDetail(ORGANIZATION_TAX_CODE, IUV)).thenReturn(
                "<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
        assertDoesNotThrow(
                () -> service.getPaymentReceiptDetail(ORGANIZATION_TAX_CODE, IUV));
        verify(client).getPaymentReceiptDetail(ORGANIZATION_TAX_CODE, IUV);
    }
    @Test
    void getCIEPaymentsReceipts() {
        when(client.getPaymentsReceipts(ORGANIZATION_TAX_CODE, 0, 50, DEBTOR_TAX_CODE, CIE_SERVICE_CODE, FROM_DATE, TO_DATE, IUV)).thenReturn(
                new PaymentsResult<>());
        assertDoesNotThrow(
                () -> service.getCIEPaymentsReceipts(ORGANIZATION_TAX_CODE, 0, 50, DEBTOR_TAX_CODE, FROM_DATE, TO_DATE, IUV));
        verify(client).getPaymentsReceipts(ORGANIZATION_TAX_CODE, 0, 50, DEBTOR_TAX_CODE, CIE_SERVICE_CODE, FROM_DATE, TO_DATE, IUV);
    }
}