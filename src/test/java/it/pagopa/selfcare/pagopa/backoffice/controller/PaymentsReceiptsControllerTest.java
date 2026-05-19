package it.pagopa.selfcare.pagopa.backoffice.controller;

import it.pagopa.selfcare.pagopa.backoffice.model.paymentsreceipts.PaymentsResult;
import it.pagopa.selfcare.pagopa.backoffice.service.PaymentsReceiptsService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@WithMockUser(username = "user1", password = "pwd", roles = "USER")
class PaymentsReceiptsControllerTest {

    public static final String ORGANIZATION_TAX_CODE = "organizationTaxCode";
    public static final String DEBTOR_TAX_CODE = "debtor tax code";
    public static final String FROM_DATE = "01/01/2020";
    public static final String TO_DATE = "05/05/2025";
    public static final String IUV = "IUV";
    @Autowired
    private MockMvc mvc;

    @MockBean
    private PaymentsReceiptsService service;

    @Test
    void getPaymentsReceiptsWithDefaultParamsOK() throws Exception {
        String url = "/payments-receipts/{organization-tax-code}";
        when(service.getPaymentsReceipts(ORGANIZATION_TAX_CODE, 0, 50, null, null, null, null)).thenReturn(
                new PaymentsResult<>()
        );
        mvc.perform(get(url, ORGANIZATION_TAX_CODE)
                ).andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE));
    }

    @Test
    void getPaymentsReceiptsWithCustomParamsOK() throws Exception {
        String url = "/payments-receipts/{organization-tax-code}";
        when(service.getPaymentsReceipts(ORGANIZATION_TAX_CODE, 5, 25, DEBTOR_TAX_CODE, FROM_DATE, TO_DATE, IUV)).thenReturn(
                new PaymentsResult<>()
        );
        mvc.perform(get(url, ORGANIZATION_TAX_CODE)
                        .param("page", String.valueOf(5))
                        .param("limit", String.valueOf(25))
                        .param("debtorTaxCode", DEBTOR_TAX_CODE)
                        .param("fromDate", FROM_DATE)
                        .param("toDate", TO_DATE)
                        .param("debtorOrIuv", IUV)
                ).andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE));
    }

    @Test
    void getPaymentsReceiptsOK() throws Exception {
        String url = "/payments-receipts/{organization-tax-code}/detail/{iuv}";
        when(service.getPaymentReceiptDetail(ORGANIZATION_TAX_CODE, IUV)).thenReturn(
                "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
        );
        mvc.perform(get(url, ORGANIZATION_TAX_CODE, IUV)
                ).andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_XML_VALUE + ";charset=UTF-8"));
    }
}