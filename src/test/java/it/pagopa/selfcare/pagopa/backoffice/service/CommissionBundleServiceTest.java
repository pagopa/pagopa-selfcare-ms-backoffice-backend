package it.pagopa.selfcare.pagopa.backoffice.service;

import it.pagopa.selfcare.pagopa.backoffice.client.GecClient;
import it.pagopa.selfcare.pagopa.backoffice.model.commissionbundle.Bundle;
import it.pagopa.selfcare.pagopa.backoffice.model.commissionbundle.BundleResource;
import it.pagopa.selfcare.pagopa.backoffice.model.commissionbundle.Bundles;
import it.pagopa.selfcare.pagopa.backoffice.model.commissionbundle.client.BundlePaymentTypesDTO;
import it.pagopa.selfcare.pagopa.backoffice.model.commissionbundle.client.BundleRequest;
import it.pagopa.selfcare.pagopa.backoffice.model.commissionbundle.client.BundleType;
import it.pagopa.selfcare.pagopa.backoffice.model.commissionbundle.client.TouchpointsDTO;
import it.pagopa.selfcare.pagopa.backoffice.model.taxonomies.Taxonomy;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest
class CommissionBundleServiceTest {

    public static final String PSP_CODE = "pspCode";
    public static final String PSP_NAME = "pspName";
    public static final int LIMIT = 50;
    public static final int PAGE = 0;
    public static final String ID_BUNDLE = "idBundle";
    @MockBean
    private GecClient client;
    @Autowired
    private CommissionBundleService service;

    @MockBean
    private  TaxonomyService taxonomyService;

    @Test
    void getBundlesPaymentTypes() {
        when(client.getPaymenttypes(LIMIT, PAGE)).thenReturn(
                new BundlePaymentTypesDTO()
        );
        Assertions.assertDoesNotThrow(
                () -> service.getBundlesPaymentTypes(LIMIT, PAGE)
        );
    }

    @Test
    void getTouchpoints() {
        when(client.getTouchpoints(LIMIT, PAGE)).thenReturn(
                new TouchpointsDTO()
        );
        Assertions.assertDoesNotThrow(
                () -> service.getTouchpoints(LIMIT, PAGE)
        );
    }

    @Test
    void getBundlesByPSP() {
        when(client.getBundlesByPSP(any(), any(), any(), any(), any())).thenReturn(
                Bundles.builder().bundles(Collections.singletonList(
                        Bundle.builder().transferCategoryList(Collections.singletonList("test")).build())).build()
        );
        when(taxonomyService.getTaxonomiesByCodes(any())).thenReturn(
                Collections.singletonList(Taxonomy.builder().ecTypeCode("ecTypeCode").ecType("ecType").build()));
        List<BundleType> bundleTypeList = Collections.singletonList(BundleType.GLOBAL);
        Assertions.assertDoesNotThrow(
                () -> service.getBundlesByPSP(PSP_CODE, bundleTypeList, PSP_NAME, LIMIT, PAGE)
        );
        verify(client).getBundlesByPSP(PSP_CODE, bundleTypeList, PSP_NAME, LIMIT, PAGE);
    }

    @Test
    void createPSPBundle() {
        BundleRequest bundleRequest = new BundleRequest();
        Assertions.assertDoesNotThrow(
                () -> service.createPSPBundle(PSP_CODE, bundleRequest)
        );
        verify(client).createPSPBundle(PSP_CODE, bundleRequest);
    }

    @Test
    void getBundleDetailByPSP() {
        when(client.getBundleDetailByPSP(any(), any())).thenReturn(
                Bundle.builder().transferCategoryList(Collections.singletonList("test")).build());
        when(taxonomyService.getTaxonomiesByCodes(any())).thenReturn(
                Collections.singletonList(Taxonomy.builder().ecTypeCode("ecTypeCode").ecType("ecType").build()));
        BundleResource bundleResource = Assertions.assertDoesNotThrow(
                () -> service.getBundleDetailByPSP(PSP_CODE, ID_BUNDLE)
        );
        assertNotNull(bundleResource);
        assertNotNull(bundleResource.getTransferCategoryList());
        assertEquals(1, bundleResource.getTransferCategoryList().size());
        verify(client).getBundleDetailByPSP(PSP_CODE, ID_BUNDLE);
    }

    @Test
    void updatePSPBundle() {
        BundleRequest bundleRequest = new BundleRequest();
        Assertions.assertDoesNotThrow(
                () -> service.updatePSPBundle(PSP_CODE, ID_BUNDLE, bundleRequest)
        );
        verify(client).updatePSPBundle(PSP_CODE, ID_BUNDLE, bundleRequest);
    }

    @Test
    void deletePSPBundle() {
        Assertions.assertDoesNotThrow(
                () -> service.deletePSPBundle(PSP_CODE, ID_BUNDLE)
        );
        verify(client).deletePSPBundle(PSP_CODE, ID_BUNDLE);
    }
}