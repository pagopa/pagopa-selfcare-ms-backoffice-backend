package it.pagopa.selfcare.pagopa.backoffice.service;

import it.pagopa.selfcare.pagopa.backoffice.client.GpdClient;
import it.pagopa.selfcare.pagopa.backoffice.model.paymentsreceipts.ReceiptsInfo;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@SpringBootTest(classes = {PaymentsReceiptsService.class})
class PaymentsReceiptsServiceTest {
    public static final String ORGANIZATION_TAX_CODE = "organizationTaxCode";
    public static final String DEBTOR_TAX_CODE = "debtor tax code";
    public static final String FROM_DATE = "01/01/2020";
    public static final String TO_DATE = "05/05/2025";
    public static final String SERVICE_CODE = "serviceCode";

    @MockBean
    private GpdClient client;
    @Autowired
    private PaymentsReceiptsService service;
    @Test
    void getPaymentsReceipts() {
        when(client.getPaymentsReceipts(ORGANIZATION_TAX_CODE, 0, 50, DEBTOR_TAX_CODE, "47", FROM_DATE, TO_DATE)).thenReturn(
                new ReceiptsInfo());
        assertDoesNotThrow(
                () -> service.getPaymentsReceipts(ORGANIZATION_TAX_CODE, 0, 50, DEBTOR_TAX_CODE, FROM_DATE, TO_DATE));
    }
}