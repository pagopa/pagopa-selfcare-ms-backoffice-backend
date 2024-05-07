package it.pagopa.selfcare.pagopa.backoffice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import it.pagopa.selfcare.pagopa.backoffice.model.commissionbundle.BundlePaymentTypes;
import it.pagopa.selfcare.pagopa.backoffice.model.commissionbundle.BundleResource;
import it.pagopa.selfcare.pagopa.backoffice.model.commissionbundle.BundlesResource;
import it.pagopa.selfcare.pagopa.backoffice.model.commissionbundle.PublicBundleSubscriptionStatus;
import it.pagopa.selfcare.pagopa.backoffice.model.commissionbundle.Touchpoints;
import it.pagopa.selfcare.pagopa.backoffice.model.commissionbundle.client.BundleCreateResponse;
import it.pagopa.selfcare.pagopa.backoffice.model.commissionbundle.client.BundleRequest;
import it.pagopa.selfcare.pagopa.backoffice.model.commissionbundle.client.BundleType;
import it.pagopa.selfcare.pagopa.backoffice.model.commissionbundle.client.PublicBundleRequest;
import it.pagopa.selfcare.pagopa.backoffice.model.connector.PageInfo;
import it.pagopa.selfcare.pagopa.backoffice.service.CommissionBundleService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@WithMockUser(username = "user1", password = "pwd", roles = "USER")
class CommissionBundleControllerTest {

    private static final String PSP_TAX_CODE = "pspTaxCode";
    private static final String CI_TAX_CODE = "ciTaxCode";
    public static final String BUNDLE_ID = "bundleId";
    private static final String CI_BUNDLE_ID = "ciBundleId";
    public static final String ID_BUNDLE_REQUEST = "idBundleRequest";
    public static final String BUNDLE_NAME = "bundleName";

    private final ObjectMapper mapper = new ObjectMapper();

    @Autowired
    private MockMvc mvc;

    @MockBean
    private CommissionBundleService service;

    @Test
    void getBundlesPaymentTypesWithDefaultParamsOK() throws Exception {
        String url = "/bundles/payment-types";
        when(service.getBundlesPaymentTypes(50, 0)).thenReturn(
                BundlePaymentTypes.builder()
                        .paymentTypes(new ArrayList<>())
                        .pageInfo(PageInfo.builder().build())
                        .build()
        );
        mvc.perform(get(url).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE));
    }

    @Test
    void getBundlesPaymentTypesWithParamsOK() throws Exception {
        String url = "/bundles/payment-types";
        int limit = 25;
        int page = 2;
        when(service.getBundlesPaymentTypes(limit, page)).thenReturn(
                BundlePaymentTypes.builder()
                        .paymentTypes(new ArrayList<>())
                        .pageInfo(PageInfo.builder().build())
                        .build()
        );
        mvc.perform(get(url)
                        .param("limit", String.valueOf(limit))
                        .param("page", String.valueOf(page)).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE));
    }

    @Test
    void getTouchpointsWithDefaultParamsOK() throws Exception {
        String url = "/bundles/touchpoints";
        when(service.getTouchpoints(10, 0)).thenReturn(
                new Touchpoints()
        );
        mvc.perform(get(url).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE));
    }

    @Test
    void getTouchpointsWithParamsOK() throws Exception {
        String url = "/bundles/touchpoints";
        int limit = 25;
        int page = 2;
        when(service.getTouchpoints(limit, page)).thenReturn(
                new Touchpoints()
        );
        mvc.perform(get(url)
                        .param("limit", String.valueOf(limit))
                        .param("page", String.valueOf(page)).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE));
    }

    @Test
    void getBundleByPSPWithDefaultParamsOK() throws Exception {
        String url = "/bundles/payment-service-providers/{psp-code}";
        when(service.getBundlesByPSP(PSP_TAX_CODE, null, null, 50, 0)).thenReturn(
                new BundlesResource()
        );
        mvc.perform(get(url, PSP_TAX_CODE)
                ).andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE));
    }

    @Test
    void getBundleByPSPWithParamsOK() throws Exception {
        String url = "/bundles/payment-service-providers/{psp-code}";
        Integer limit = 25;
        List<BundleType> bundleTypeList = Collections.singletonList(BundleType.PRIVATE);
        Integer page = 2;
        String name = "pspName";
        when(service.getBundlesByPSP(PSP_TAX_CODE, bundleTypeList, name, limit, page)).thenReturn(
                new BundlesResource()
        );
        mvc.perform(get(url, PSP_TAX_CODE)
                        .param("limit", String.valueOf(limit))
                        .param("page", String.valueOf(page))
                        .param("name", name)
                        .param("bundle-type", BundleType.PRIVATE.name())
                ).andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE));
        verify(service).getBundlesByPSP(PSP_TAX_CODE, bundleTypeList, name, limit, page);
    }

    @Test
    void createPSPBundleOK() throws Exception {
        String url = "/bundles/payment-service-providers/{psp-code}";
        BundleRequest bundleRequest = new BundleRequest();
        when(service.createPSPBundle(PSP_TAX_CODE, bundleRequest)).thenReturn(new BundleCreateResponse());

        mvc.perform(post(url, PSP_TAX_CODE)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(mapper.writeValueAsString(bundleRequest))
                ).andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE));
    }

    @Test
    void createPSPBundleNoBundleKO() throws Exception {
        String url = "/bundles/payment-service-providers/{psp-code}";

        mvc.perform(post(url, PSP_TAX_CODE)
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().is4xxClientError());
    }

    @Test
    void getBundleDetailByPSPOK() throws Exception {
        String url = "/bundles/{id-bundle}/payment-service-providers/{psp-code}";
        when(service.getBundleDetailByPSP(PSP_TAX_CODE, BUNDLE_ID)).thenReturn(new BundleResource());

        mvc.perform(get(url, BUNDLE_ID, PSP_TAX_CODE))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE));
        verify(service).getBundleDetailByPSP(PSP_TAX_CODE, BUNDLE_ID);
    }

    @Test
    void updatePSPBundleOK() throws Exception {
        String url = "/bundles/{id-bundle}/payment-service-providers/{psp-code}";
        BundleRequest bundleRequest = new BundleRequest();
        mvc.perform(put(url, BUNDLE_ID, PSP_TAX_CODE)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(bundleRequest))
                )
                .andExpect(status().isOk());
        verify(service).updatePSPBundle(PSP_TAX_CODE, BUNDLE_ID, bundleRequest);
    }

    @Test
    void deletePSPBundleOK() throws Exception {
        String url = "/bundles/{id-bundle}/payment-service-providers/{psp-code}";

        mvc.perform(delete(url, BUNDLE_ID, PSP_TAX_CODE))
                .andExpect(status().isOk());
        verify(service).deletePSPBundle(PSP_TAX_CODE, BUNDLE_ID);
    }

    @Test
    void acceptPublicBundleSubscriptionsOK() throws Exception {
        String url = "/bundles/payment-service-providers/{tax-code}/requests/{bundle-request-id}/accept";

        mvc.perform(post(url, PSP_TAX_CODE, ID_BUNDLE_REQUEST)
                        .param(CI_TAX_CODE, CI_TAX_CODE)
                        .param(BUNDLE_NAME, BUNDLE_NAME)
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk());
        verify(service).acceptPublicBundleSubscriptionsByPSP(PSP_TAX_CODE, ID_BUNDLE_REQUEST, CI_TAX_CODE, BUNDLE_NAME);
    }

    @Test
    void getCiBundlesWithTaxCodeOK() throws Exception {
        String url = "/bundles/creditor-institutions";
        int limit = 25;
        int page = 2;
        when(service.getCIBundles(BundleType.PRIVATE,
                CI_TAX_CODE, "name", limit, page)).thenReturn(
                new BundlesResource()
        );
        mvc.perform(get(url)
                        .param("name", "name")
                        .param("bundleType", BundleType.PRIVATE.name())
                        .param("ciTaxCode", CI_TAX_CODE)
                        .param("limit", String.valueOf(limit))
                        .param("page", String.valueOf(page)).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE));
    }

    @Test
    void getCiBundlesWithoutTaxCodeOK() throws Exception {
        String url = "/bundles/creditor-institutions";
        int limit = 25;
        int page = 2;
        when(service.getCIBundles(BundleType.PRIVATE, null, null, limit, page)).thenReturn(
                new BundlesResource()
        );
        mvc.perform(get(url)
                        .param("bundleType", BundleType.PRIVATE.name())
                        .param("limit", String.valueOf(limit))
                        .param("page", String.valueOf(page)).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE));
    }

    @Test
    void rejectPublicBundleSubscriptionsOK() throws Exception {
        String url = "/bundles/payment-service-providers/{psp-tax-code}/requests/{bundle-request-id}/reject";
        mvc.perform(post(url, PSP_TAX_CODE, ID_BUNDLE_REQUEST)
                        .param(CI_TAX_CODE, CI_TAX_CODE)
                        .param(BUNDLE_NAME, BUNDLE_NAME)
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk());
        verify(service).rejectPublicBundleSubscriptionByPSP(PSP_TAX_CODE, ID_BUNDLE_REQUEST, CI_TAX_CODE, BUNDLE_NAME);
    }

    @Test
    void getPublicBundleCISubscriptions() throws Exception {
        String url = "/bundles/{id-bundle}/payment-service-providers/{psp-tax-code}/subscriptions";
        mvc.perform(get(url, BUNDLE_ID, PSP_TAX_CODE)
                        .param("status", PublicBundleSubscriptionStatus.ACCEPTED.name())
                        .param("limit", "10")
                        .param("page", "0")
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk());
        verify(service).getPublicBundleCISubscriptions(BUNDLE_ID, PSP_TAX_CODE, PublicBundleSubscriptionStatus.ACCEPTED, null, 10, 0);
    }

    @Test
    void getPublicBundleCISubscriptionsDetail() throws Exception {
        String url = "/bundles/{id-bundle}/payment-service-providers/{psp-tax-code}/subscriptions/{ci-tax-code}/detail";
        mvc.perform(get(url, BUNDLE_ID, PSP_TAX_CODE, CI_TAX_CODE)
                        .param("status", PublicBundleSubscriptionStatus.ACCEPTED.name())
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk());
        verify(service).getPublicBundleCISubscriptionsDetail(BUNDLE_ID, PSP_TAX_CODE, CI_TAX_CODE, PublicBundleSubscriptionStatus.ACCEPTED);
    }

    @Test
    void deleteCIBundleSubscriptionOK() throws Exception {
        String url = "/bundles/{ci-bundle-id}/creditor-institutions/{ci-tax-code}";
        mvc.perform(delete(url, CI_BUNDLE_ID, CI_TAX_CODE)
                        .param(BUNDLE_NAME, BUNDLE_NAME)
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk());
        verify(service).deleteCIBundleSubscription(CI_BUNDLE_ID, CI_TAX_CODE, BUNDLE_NAME);
    }

    @Test
    void deleteCIBundleRequestOK() throws Exception {
        String url = "/bundles/creditor-institutions/{ci-tax-code}/requests/{bundle-request-id}";
        mvc.perform(delete(url, CI_TAX_CODE, ID_BUNDLE_REQUEST)
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk());
        verify(service).deleteCIBundleRequest(ID_BUNDLE_REQUEST, CI_TAX_CODE);
    }

    @Test
    void createCIBundleRequestOK() throws Exception {
        PublicBundleRequest bundleRequest = new PublicBundleRequest();

        String url = "/bundles/creditor-institutions/{ci-tax-code}";
        mvc.perform(post(url, CI_TAX_CODE)
                        .param(BUNDLE_NAME, BUNDLE_NAME)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(mapper.writeValueAsString(bundleRequest))
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk());
        verify(service).createCIBundleRequest(CI_TAX_CODE, bundleRequest, BUNDLE_NAME);
    }
}
