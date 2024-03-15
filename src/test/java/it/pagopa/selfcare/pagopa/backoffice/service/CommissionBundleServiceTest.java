package it.pagopa.selfcare.pagopa.backoffice.service;

import it.pagopa.selfcare.pagopa.backoffice.client.GecClient;
import it.pagopa.selfcare.pagopa.backoffice.config.MappingsConfiguration;
import it.pagopa.selfcare.pagopa.backoffice.model.commissionbundle.Bundle;
import it.pagopa.selfcare.pagopa.backoffice.model.commissionbundle.BundleResource;
import it.pagopa.selfcare.pagopa.backoffice.model.commissionbundle.Bundles;
import it.pagopa.selfcare.pagopa.backoffice.model.commissionbundle.BundlesResource;
import it.pagopa.selfcare.pagopa.backoffice.model.commissionbundle.client.BundlePaymentTypesDTO;
import it.pagopa.selfcare.pagopa.backoffice.model.commissionbundle.client.BundleRequest;
import it.pagopa.selfcare.pagopa.backoffice.model.commissionbundle.client.BundleType;
import it.pagopa.selfcare.pagopa.backoffice.model.commissionbundle.client.TouchpointsDTO;
import it.pagopa.selfcare.pagopa.backoffice.model.connector.PageInfo;
import it.pagopa.selfcare.pagopa.backoffice.model.taxonomies.Taxonomy;
import it.pagopa.selfcare.pagopa.backoffice.util.LegacyPspCodeUtil;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest(classes = {MappingsConfiguration.class, CommissionBundleService.class})
class CommissionBundleServiceTest {

    private static final String PSP_CODE = "pspCode";
    private static final String PSP_TAX_CODE = "pspTaxCode";

    private static final String EC_TAX_CODE = "ecTaxCode";

    private static final String PSP_NAME = "pspName";
    private static final int LIMIT = 50;
    private static final int PAGE = 0;
    private static final String ID_BUNDLE = "idBundle";
    private static final String ID_BUNDLE_REQUEST = "idBundleRequest";
    private static final String ID_BUNDLE_REQUEST_2 = "idBundleRequest2";

    @MockBean
    private GecClient client;

    @Autowired
    private CommissionBundleService service;

    @MockBean
    private TaxonomyService taxonomyService;

    @MockBean
    private LegacyPspCodeUtil legacyPspCodeUtilMock;

    @Test
    void getBundlesPaymentTypes() {
        when(client.getPaymenttypes(LIMIT, PAGE)).thenReturn(
                new BundlePaymentTypesDTO()
        );
        assertDoesNotThrow(
                () -> service.getBundlesPaymentTypes(LIMIT, PAGE)
        );
    }

    @Test
    void getTouchpoints() {
        when(client.getTouchpoints(LIMIT, PAGE)).thenReturn(
                new TouchpointsDTO()
        );
        assertDoesNotThrow(
                () -> service.getTouchpoints(LIMIT, PAGE)
        );
    }

    @Test
    void getBundlesByPSP() {
        when(legacyPspCodeUtilMock.retrievePspCode(PSP_TAX_CODE, false)).thenReturn(PSP_CODE);
        when(client.getBundlesByPSP(any(), any(), any(), any(), any())).thenReturn(
                Bundles.builder().bundles(Collections.singletonList(
                        Bundle.builder().transferCategoryList(Collections.singletonList("test")).build())).build()
        );
        when(taxonomyService.getTaxonomiesByCodes(any())).thenReturn(
                Collections.singletonList(Taxonomy.builder().ecTypeCode("ecTypeCode").ecType("ecType").build()));
        List<BundleType> bundleTypeList = Collections.singletonList(BundleType.GLOBAL);
        assertDoesNotThrow(
                () -> service.getBundlesByPSP(PSP_TAX_CODE, bundleTypeList, PSP_NAME, LIMIT, PAGE)
        );
        verify(client).getBundlesByPSP(PSP_CODE, bundleTypeList, PSP_NAME, LIMIT, PAGE);
    }

    @Test
    void createPSPBundle() {
        when(legacyPspCodeUtilMock.retrievePspCode(PSP_TAX_CODE, false)).thenReturn(PSP_CODE);

        BundleRequest bundleRequest = new BundleRequest();
        assertDoesNotThrow(
                () -> service.createPSPBundle(PSP_TAX_CODE, bundleRequest)
        );
        verify(client).createPSPBundle(PSP_CODE, bundleRequest);
    }

    @Test
    void getBundleDetailByPSP() {
        when(legacyPspCodeUtilMock.retrievePspCode(PSP_TAX_CODE, false)).thenReturn(PSP_CODE);
        when(client.getBundleDetailByPSP(any(), any())).thenReturn(
                Bundle.builder().transferCategoryList(Collections.singletonList("test")).build());
        when(taxonomyService.getTaxonomiesByCodes(any())).thenReturn(
                Collections.singletonList(Taxonomy.builder().ecTypeCode("ecTypeCode").ecType("ecType").build()));

        BundleResource bundleResource = assertDoesNotThrow(
                () -> service.getBundleDetailByPSP(PSP_TAX_CODE, ID_BUNDLE));
        assertNotNull(bundleResource);
        assertNotNull(bundleResource.getTransferCategoryList());
        assertEquals(1, bundleResource.getTransferCategoryList().size());
        verify(client).getBundleDetailByPSP(PSP_CODE, ID_BUNDLE);
    }

    @Test
    void updatePSPBundle() {
        when(legacyPspCodeUtilMock.retrievePspCode(PSP_TAX_CODE, false)).thenReturn(PSP_CODE);

        BundleRequest bundleRequest = new BundleRequest();
        assertDoesNotThrow(
                () -> service.updatePSPBundle(PSP_TAX_CODE, ID_BUNDLE, bundleRequest)
        );
        verify(client).updatePSPBundle(PSP_CODE, ID_BUNDLE, bundleRequest);
    }

    @Test
    void deletePSPBundle() {
        when(legacyPspCodeUtilMock.retrievePspCode(PSP_TAX_CODE, false)).thenReturn(PSP_CODE);

        assertDoesNotThrow(
                () -> service.deletePSPBundle(PSP_TAX_CODE, ID_BUNDLE)
        );
        verify(client).deletePSPBundle(PSP_CODE, ID_BUNDLE);
    }

    @Test
    void acceptPublicBundleSubscriptionsByPSPSuccess() {
        when(legacyPspCodeUtilMock.retrievePspCode(PSP_TAX_CODE, false)).thenReturn(PSP_CODE);
        List<String> bundleRequestIdList = new ArrayList<>();
        bundleRequestIdList.add(ID_BUNDLE_REQUEST);
        bundleRequestIdList.add(ID_BUNDLE_REQUEST_2);

        assertDoesNotThrow(() ->
                service.acceptPublicBundleSubscriptionsByPSP(PSP_TAX_CODE, bundleRequestIdList));

        verify(client).acceptPublicBundleSubscriptionsByPSP(PSP_CODE, ID_BUNDLE_REQUEST);
        verify(client).acceptPublicBundleSubscriptionsByPSP(PSP_CODE, ID_BUNDLE_REQUEST_2);
    }

    @Test
    void getCIBundlesShouldReturnExpandedResultFromFilteredAPI() {
        when(client.getBundlesByCI(any(),any(), any())).thenReturn(Bundles.builder()
                .bundles(
                    Collections.singletonList(
                            Bundle.builder()
                                .name("ecName")
                                .type(BundleType.PRIVATE)
                                .transferCategoryList(Collections.singletonList("test"))
                            .build())
                )
                .pageInfo(PageInfo.builder().build()
        ).build());
        when(taxonomyService.getTaxonomiesByCodes(any())).thenReturn(
                Collections.singletonList(Taxonomy.builder().ecTypeCode("ecTypeCode").ecType("ecType").build()));

        BundlesResource bundlesResource = assertDoesNotThrow(
                () -> service.getCisBundles(
                        Collections.singletonList(BundleType.PRIVATE), EC_TAX_CODE, "name", 10, 0));
        assertNotNull(bundlesResource);
        assertNotNull(bundlesResource.getPageInfo());
        assertNotNull(bundlesResource.getBundles());
        assertEquals(1, bundlesResource.getBundles().get(0).getTransferCategoryList().size());
        verify(client).getBundlesByCI(EC_TAX_CODE, 10, 0);
        verifyNoMoreInteractions(client);
        verify(taxonomyService).getTaxonomiesByCodes(any());
    }

    @Test
    void getCIBundlesShouldReturnExpandedResultFromFilteredAPIWithoutType() {
        when(client.getBundlesByCI(any(),any(), any())).thenReturn(Bundles.builder()
                .bundles(
                        Collections.singletonList(
                                Bundle.builder()
                                        .name("ecName")
                                        .type(BundleType.PRIVATE)
                                        .transferCategoryList(Collections.singletonList("test"))
                                        .build())
                )
                .pageInfo(PageInfo.builder().build()
                ).build());
        when(taxonomyService.getTaxonomiesByCodes(any())).thenReturn(
                Collections.singletonList(Taxonomy.builder().ecTypeCode("ecTypeCode").ecType("ecType").build()));

        BundlesResource bundlesResource = assertDoesNotThrow(
                () -> service.getCisBundles(
                        null, EC_TAX_CODE, "name", 10, 0));
        assertNotNull(bundlesResource);
        assertNotNull(bundlesResource.getPageInfo());
        assertNotNull(bundlesResource.getBundles());
        assertEquals(1, bundlesResource.getBundles().get(0).getTransferCategoryList().size());
        verify(client).getBundlesByCI(EC_TAX_CODE, 10, 0);
        verifyNoMoreInteractions(client);
        verify(taxonomyService).getTaxonomiesByCodes(any());
    }

    @Test
    void getCIBundlesShouldReturnExpandedResultFromGlobalAPI() {
        when(client.getBundles(any(), any())).thenReturn(Bundles.builder()
                .bundles(
                        Collections.singletonList(Bundle.builder()
                                .transferCategoryList(Collections.singletonList("test")).build())
                )
                .pageInfo(PageInfo.builder().build()
                ).build());
        when(taxonomyService.getTaxonomiesByCodes(any())).thenReturn(
                Collections.singletonList(Taxonomy.builder().ecTypeCode("ecTypeCode").ecType("ecType").build()));

        BundlesResource bundlesResource = assertDoesNotThrow(
                () -> service.getCisBundles(null,null,null, 10, 0));
        assertNotNull(bundlesResource);
        assertNotNull(bundlesResource.getPageInfo());
        assertNotNull(bundlesResource.getBundles());
        assertEquals(1, bundlesResource.getBundles().get(0).getTransferCategoryList().size());
        verify(client).getBundles(10, 0);
        verifyNoMoreInteractions(client);
        verify(taxonomyService).getTaxonomiesByCodes(any());
    }

}