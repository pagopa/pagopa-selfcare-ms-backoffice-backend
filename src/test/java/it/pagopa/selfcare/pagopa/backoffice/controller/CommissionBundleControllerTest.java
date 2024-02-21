package it.pagopa.selfcare.pagopa.backoffice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import it.pagopa.selfcare.pagopa.backoffice.model.commissionbundle.Bundle;
import it.pagopa.selfcare.pagopa.backoffice.model.commissionbundle.BundlePaymentTypes;
import it.pagopa.selfcare.pagopa.backoffice.model.commissionbundle.Bundles;
import it.pagopa.selfcare.pagopa.backoffice.model.commissionbundle.Touchpoints;
import it.pagopa.selfcare.pagopa.backoffice.model.commissionbundle.client.BundleCreateResponse;
import it.pagopa.selfcare.pagopa.backoffice.model.commissionbundle.client.BundleRequest;
import it.pagopa.selfcare.pagopa.backoffice.model.commissionbundle.client.BundleType;
import it.pagopa.selfcare.pagopa.backoffice.service.CommissionBundleService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;
import java.util.List;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@WithMockUser(username = "user1", password = "pwd", roles = "USER")
class CommissionBundleControllerTest {
    private static final String PSP_CODE = "pspCode";
    public static final String BUNDLE_ID = "bundleId";
    private final ObjectMapper mapper = new ObjectMapper();
    @Autowired
    private MockMvc mvc;

    @MockBean
    private CommissionBundleService service;

    @Test
    void getBundlesPaymentTypesWithDefaultParamsOK() throws Exception {
        String url = "/bundles/payment-types";
        when(service.getBundlesPaymentTypes(50, 0)).thenReturn(
                new BundlePaymentTypes()
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
                new BundlePaymentTypes()
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
        when(service.getBundlesByPSP(PSP_CODE, null, null, 50, 0)).thenReturn(
                new Bundles()
        );
        mvc.perform(get(url, PSP_CODE)
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
        when(service.getBundlesByPSP(PSP_CODE, bundleTypeList, name, limit, page)).thenReturn(
                new Bundles()
        );
        mvc.perform(get(url, PSP_CODE)
                        .param("limit", String.valueOf(limit))
                        .param("page", String.valueOf(page))
                        .param("name", name)
                        .param("bundle-type", BundleType.PRIVATE.name())
                ).andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE));
    }

    @Test
    void createPSPBundleOK() throws Exception {
        String url = "/bundles/payment-service-providers/{psp-code}";
        BundleRequest bundleRequest = new BundleRequest();
        when(service.createPSPBundle(PSP_CODE, bundleRequest)).thenReturn(new BundleCreateResponse());

        mvc.perform(post(url, PSP_CODE)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(mapper.writeValueAsString(bundleRequest))
                ).andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE));
    }

    @Test
    void createPSPBundleNoBundleKO() throws Exception {
        String url = "/bundles/payment-service-providers/{psp-code}";

        mvc.perform(post(url, PSP_CODE)
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().is4xxClientError());
    }

    @Test
    void getBundleDetailByPSPOK() throws Exception {
        String url = "/bundles/{id-bundle}/payment-service-providers/{psp-code}";
        when(service.getBundleDetailByPSP(PSP_CODE, BUNDLE_ID)).thenReturn(new Bundle());

        mvc.perform(get(url, BUNDLE_ID, PSP_CODE))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE));
        verify(service).getBundleDetailByPSP(PSP_CODE, BUNDLE_ID);
    }

    @Test
    void updatePSPBundleOK() throws Exception {
        String url = "/bundles/{id-bundle}/payment-service-providers/{psp-code}";
        BundleRequest bundleRequest = new BundleRequest();
        mvc.perform(put(url, BUNDLE_ID, PSP_CODE)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(bundleRequest))
                )
                .andExpect(status().isOk());
        verify(service).updatePSPBundle(PSP_CODE, BUNDLE_ID, bundleRequest);
    }

    @Test
    void deletePSPBundleOK() throws Exception {
        String url = "/bundles/{id-bundle}/payment-service-providers/{psp-code}";

        mvc.perform(delete(url, BUNDLE_ID, PSP_CODE))
                .andExpect(status().isOk());
        verify(service).deletePSPBundle(PSP_CODE, BUNDLE_ID);
    }
}
