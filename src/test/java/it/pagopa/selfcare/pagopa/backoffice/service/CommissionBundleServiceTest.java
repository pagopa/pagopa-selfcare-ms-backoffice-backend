package it.pagopa.selfcare.pagopa.backoffice.service;

import it.pagopa.selfcare.pagopa.backoffice.client.ApiConfigSelfcareIntegrationClient;
import it.pagopa.selfcare.pagopa.backoffice.client.GecClient;
import it.pagopa.selfcare.pagopa.backoffice.config.MappingsConfiguration;
import it.pagopa.selfcare.pagopa.backoffice.model.commissionbundle.Bundle;
import it.pagopa.selfcare.pagopa.backoffice.model.commissionbundle.BundleResource;
import it.pagopa.selfcare.pagopa.backoffice.model.commissionbundle.Bundles;
import it.pagopa.selfcare.pagopa.backoffice.model.commissionbundle.BundlesResource;
import it.pagopa.selfcare.pagopa.backoffice.model.commissionbundle.PublicBundleCISubscriptionsDetail;
import it.pagopa.selfcare.pagopa.backoffice.model.commissionbundle.PublicBundleCISubscriptionsResource;
import it.pagopa.selfcare.pagopa.backoffice.model.commissionbundle.PublicBundleSubscriptionStatus;
import it.pagopa.selfcare.pagopa.backoffice.model.commissionbundle.client.BundlePaymentTypesDTO;
import it.pagopa.selfcare.pagopa.backoffice.model.commissionbundle.client.BundleRequest;
import it.pagopa.selfcare.pagopa.backoffice.model.commissionbundle.client.BundleType;
import it.pagopa.selfcare.pagopa.backoffice.model.commissionbundle.client.CiBundleAttribute;
import it.pagopa.selfcare.pagopa.backoffice.model.commissionbundle.client.CiBundleDetails;
import it.pagopa.selfcare.pagopa.backoffice.model.commissionbundle.client.CiFiscalCodeList;
import it.pagopa.selfcare.pagopa.backoffice.model.commissionbundle.client.PspBundleRequest;
import it.pagopa.selfcare.pagopa.backoffice.model.commissionbundle.client.PspCiBundleAttribute;
import it.pagopa.selfcare.pagopa.backoffice.model.commissionbundle.client.PspRequests;
import it.pagopa.selfcare.pagopa.backoffice.model.commissionbundle.client.TouchpointsDTO;
import it.pagopa.selfcare.pagopa.backoffice.model.connector.PageInfo;
import it.pagopa.selfcare.pagopa.backoffice.model.institutions.client.CreditorInstitutionInfo;
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
import static org.mockito.Mockito.anyInt;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@SpringBootTest(classes = {MappingsConfiguration.class, CommissionBundleService.class})
class CommissionBundleServiceTest {

    private static final String PSP_CODE = "pspCode";
    private static final String PSP_TAX_CODE = "pspTaxCode";
    private static final String CI_TAX_CODE = "ciTaxCode";
    private static final String PSP_NAME = "pspName";
    private static final int LIMIT = 50;
    private static final int PAGE = 0;
    private static final String ID_BUNDLE = "idBundle";
    private static final String ID_BUNDLE_REQUEST = "idBundleRequest";
    private static final String ID_BUNDLE_REQUEST_2 = "idBundleRequest2";
    private static final String TRANSFER_CATEGORY = "transferCategory";
    private static final String SPECIFIC_BUILT_IN_DATA = "SpecificBuiltInData";

    @MockBean
    private GecClient gecClient;

    @Autowired
    private CommissionBundleService sut;

    @MockBean
    private TaxonomyService taxonomyService;

    @MockBean
    private LegacyPspCodeUtil legacyPspCodeUtilMock;

    @MockBean
    private ApiConfigSelfcareIntegrationClient apiConfigSelfcareIntegrationClient;

    @Test
    void getBundlesPaymentTypes() {
        when(gecClient.getPaymenttypes(LIMIT, PAGE)).thenReturn(
                new BundlePaymentTypesDTO()
        );
        assertDoesNotThrow(
                () -> sut.getBundlesPaymentTypes(LIMIT, PAGE)
        );
    }

    @Test
    void getTouchpoints() {
        when(gecClient.getTouchpoints(LIMIT, PAGE)).thenReturn(
                new TouchpointsDTO()
        );
        assertDoesNotThrow(
                () -> sut.getTouchpoints(LIMIT, PAGE)
        );
    }

    @Test
    void getBundlesByPSP() {
        when(legacyPspCodeUtilMock.retrievePspCode(PSP_TAX_CODE, false)).thenReturn(PSP_CODE);
        when(gecClient.getBundlesByPSP(any(), any(), any(), any(), any())).thenReturn(
                Bundles.builder().bundles(Collections.singletonList(
                        Bundle.builder().transferCategoryList(Collections.singletonList("test")).build())).build()
        );
        when(taxonomyService.getTaxonomiesByCodes(any())).thenReturn(
                Collections.singletonList(Taxonomy.builder().ecTypeCode("ecTypeCode").ecType("ecType").build()));
        List<BundleType> bundleTypeList = Collections.singletonList(BundleType.GLOBAL);
        assertDoesNotThrow(
                () -> sut.getBundlesByPSP(PSP_TAX_CODE, bundleTypeList, PSP_NAME, LIMIT, PAGE)
        );
        verify(gecClient).getBundlesByPSP(PSP_CODE, bundleTypeList, PSP_NAME, LIMIT, PAGE);
    }

    @Test
    void createPSPBundle() {
        when(legacyPspCodeUtilMock.retrievePspCode(PSP_TAX_CODE, false)).thenReturn(PSP_CODE);

        BundleRequest bundleRequest = new BundleRequest();
        assertDoesNotThrow(
                () -> sut.createPSPBundle(PSP_TAX_CODE, bundleRequest)
        );
        verify(gecClient).createPSPBundle(PSP_CODE, bundleRequest);
    }

    @Test
    void getBundleDetailByPSP() {
        when(legacyPspCodeUtilMock.retrievePspCode(PSP_TAX_CODE, false)).thenReturn(PSP_CODE);
        when(gecClient.getBundleDetailByPSP(any(), any())).thenReturn(
                Bundle.builder().transferCategoryList(Collections.singletonList("test")).build());
        when(taxonomyService.getTaxonomiesByCodes(any())).thenReturn(
                Collections.singletonList(Taxonomy.builder().ecTypeCode("ecTypeCode").ecType("ecType").build()));

        BundleResource bundleResource = assertDoesNotThrow(
                () -> sut.getBundleDetailByPSP(PSP_TAX_CODE, ID_BUNDLE));
        assertNotNull(bundleResource);
        assertNotNull(bundleResource.getTransferCategoryList());
        assertEquals(1, bundleResource.getTransferCategoryList().size());
        verify(gecClient).getBundleDetailByPSP(PSP_CODE, ID_BUNDLE);
    }

    @Test
    void updatePSPBundle() {
        when(legacyPspCodeUtilMock.retrievePspCode(PSP_TAX_CODE, false)).thenReturn(PSP_CODE);

        BundleRequest bundleRequest = new BundleRequest();
        assertDoesNotThrow(
                () -> sut.updatePSPBundle(PSP_TAX_CODE, ID_BUNDLE, bundleRequest)
        );
        verify(gecClient).updatePSPBundle(PSP_CODE, ID_BUNDLE, bundleRequest);
    }

    @Test
    void deletePSPBundle() {
        when(legacyPspCodeUtilMock.retrievePspCode(PSP_TAX_CODE, false)).thenReturn(PSP_CODE);

        assertDoesNotThrow(
                () -> sut.deletePSPBundle(PSP_TAX_CODE, ID_BUNDLE)
        );
        verify(gecClient).deletePSPBundle(PSP_CODE, ID_BUNDLE);
    }

    @Test
    void acceptPublicBundleSubscriptionsByPSPSuccess() {
        when(legacyPspCodeUtilMock.retrievePspCode(PSP_TAX_CODE, false)).thenReturn(PSP_CODE);
        List<String> bundleRequestIdList = new ArrayList<>();
        bundleRequestIdList.add(ID_BUNDLE_REQUEST);
        bundleRequestIdList.add(ID_BUNDLE_REQUEST_2);

        assertDoesNotThrow(() ->
                sut.acceptPublicBundleSubscriptionsByPSP(PSP_TAX_CODE, bundleRequestIdList));

        verify(gecClient).acceptPublicBundleSubscriptionsByPSP(PSP_CODE, ID_BUNDLE_REQUEST);
        verify(gecClient).acceptPublicBundleSubscriptionsByPSP(PSP_CODE, ID_BUNDLE_REQUEST_2);
    }

    @Test
    void getCIBundlesShouldReturnExpandedResultFromFilteredAPI() {
        when(gecClient.getBundlesByCI(any(), any(), any())).thenReturn(Bundles.builder()
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
                () -> sut.getCisBundles(
                        Collections.singletonList(BundleType.PRIVATE), CI_TAX_CODE, "name", 10, 0));
        assertNotNull(bundlesResource);
        assertNotNull(bundlesResource.getPageInfo());
        assertNotNull(bundlesResource.getBundles());
        assertEquals(1, bundlesResource.getBundles().get(0).getTransferCategoryList().size());
        verify(gecClient).getBundlesByCI(CI_TAX_CODE, 10, 0);
        verifyNoMoreInteractions(gecClient);
        verify(taxonomyService).getTaxonomiesByCodes(any());
    }

    @Test
    void getCIBundlesShouldReturnExpandedResultFromFilteredAPIWithoutType() {
        when(gecClient.getBundlesByCI(any(), any(), any())).thenReturn(Bundles.builder()
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
                () -> sut.getCisBundles(
                        null, CI_TAX_CODE, "name", 10, 0));
        assertNotNull(bundlesResource);
        assertNotNull(bundlesResource.getPageInfo());
        assertNotNull(bundlesResource.getBundles());
        assertEquals(1, bundlesResource.getBundles().get(0).getTransferCategoryList().size());
        verify(gecClient).getBundlesByCI(CI_TAX_CODE, 10, 0);
        verifyNoMoreInteractions(gecClient);
        verify(taxonomyService).getTaxonomiesByCodes(any());
    }

    @Test
    void getCIBundlesShouldReturnExpandedResultFromGlobalAPI() {
        when(gecClient.getBundles(any(), anyString(), anyInt(), anyInt())).thenReturn(Bundles.builder()
                .bundles(
                        Collections.singletonList(Bundle.builder()
                                .transferCategoryList(Collections.singletonList("test")).build())
                )
                .pageInfo(PageInfo.builder().build()
                ).build());
        when(taxonomyService.getTaxonomiesByCodes(any())).thenReturn(
                Collections.singletonList(Taxonomy.builder().ecTypeCode("ecTypeCode").ecType("ecType").build()));

        BundlesResource bundlesResource = assertDoesNotThrow(
                () -> sut.getCisBundles(Collections.singletonList(BundleType.GLOBAL), null, "bundleName", 10, 0));
        assertNotNull(bundlesResource);
        assertNotNull(bundlesResource.getPageInfo());
        assertNotNull(bundlesResource.getBundles());
        assertEquals(1, bundlesResource.getBundles().get(0).getTransferCategoryList().size());
        verify(gecClient).getBundles(Collections.singletonList(BundleType.GLOBAL), "bundleName", 10, 0);
        verifyNoMoreInteractions(gecClient);
        verify(taxonomyService).getTaxonomiesByCodes(any());
    }

    @Test
    void rejectPublicBundleSubscriptionByPSPSuccess() {
        when(legacyPspCodeUtilMock.retrievePspCode(PSP_TAX_CODE, false)).thenReturn(PSP_CODE);
        assertDoesNotThrow(() ->
                sut.rejectPublicBundleSubscriptionByPSP(PSP_TAX_CODE, ID_BUNDLE_REQUEST));
        verify(gecClient).rejectPublicBundleSubscriptionByPSP(PSP_CODE, ID_BUNDLE_REQUEST);
    }

    @Test
    void getPublicBundleCISubscriptionsAccepted() {
        CiFiscalCodeList codeList = CiFiscalCodeList.builder()
                .ciTaxCodeList(Collections.singletonList(CI_TAX_CODE))
                .build();
        CreditorInstitutionInfo ciInfo = buildCIInfo();

        when(legacyPspCodeUtilMock.retrievePspCode(PSP_TAX_CODE, false)).thenReturn(PSP_CODE);
        when(gecClient.getPublicBundleSubscriptionByPSP(PSP_CODE, ID_BUNDLE, null, LIMIT, PAGE))
                .thenReturn(codeList);
        when(apiConfigSelfcareIntegrationClient.getCreditorInstitutionInfo(codeList.getCiTaxCodeList()))
                .thenReturn(Collections.singletonList(ciInfo));

        PublicBundleCISubscriptionsResource result = assertDoesNotThrow(() -> sut
                .getPublicBundleCISubscriptions(
                        ID_BUNDLE,
                        PSP_TAX_CODE,
                        PublicBundleSubscriptionStatus.ACCEPTED,
                        null,
                        LIMIT,
                        PAGE)
        );

        assertNotNull(result);
        assertEquals(1, result.getCiSubscriptionInfoList().size());
        // TODO assert page

        assertEquals(CI_TAX_CODE, result.getCiSubscriptionInfoList().get(0).getCiTaxCode());
        assertEquals(ciInfo.getBusinessName(), result.getCiSubscriptionInfoList().get(0).getBusinessName());

    }

    @Test
    void getPublicBundleCISubscriptionsWaiting() {
        PspRequests pspRequests = PspRequests.builder()
                .requestsList(
                        Collections.singletonList(
                                PspBundleRequest.builder()
                                        .ciFiscalCode(CI_TAX_CODE)
                                        .build()
                        )
                )
                .pageInfo(buildPageInfo())
                .build();
        CreditorInstitutionInfo ciInfo = buildCIInfo();

        when(legacyPspCodeUtilMock.retrievePspCode(PSP_TAX_CODE, false)).thenReturn(PSP_CODE);
        when(gecClient.getPublicBundleSubscriptionRequestByPSP(PSP_CODE, null, ID_BUNDLE, LIMIT, PAGE))
                .thenReturn(pspRequests);
        when(apiConfigSelfcareIntegrationClient.getCreditorInstitutionInfo(Collections.singletonList(CI_TAX_CODE)))
                .thenReturn(Collections.singletonList(ciInfo));

        PublicBundleCISubscriptionsResource result = assertDoesNotThrow(() -> sut
                .getPublicBundleCISubscriptions(
                        ID_BUNDLE,
                        PSP_TAX_CODE,
                        PublicBundleSubscriptionStatus.WAITING,
                        null,
                        LIMIT,
                        PAGE)
        );

        assertNotNull(result);
        assertEquals(1, result.getCiSubscriptionInfoList().size());
        assertNotNull(result.getPageInfo());
        assertEquals(LIMIT, result.getPageInfo().getLimit());
        assertEquals(PAGE, result.getPageInfo().getPage());
        assertEquals(1, result.getPageInfo().getTotalPages());

        assertEquals(CI_TAX_CODE, result.getCiSubscriptionInfoList().get(0).getCiTaxCode());
        assertEquals(ciInfo.getBusinessName(), result.getCiSubscriptionInfoList().get(0).getBusinessName());
    }

    @Test
    void getPublicBundleCISubscriptionsDetailAccepted() {
        CiBundleDetails bundleDetails = CiBundleDetails.builder()
                .attributes(
                        Collections.singletonList(
                                CiBundleAttribute.builder()
                                        .maxPaymentAmount(100L)
                                        .transferCategory(TRANSFER_CATEGORY)
                                        .build()
                        )
                )
                .build();
        Taxonomy taxonomy = Taxonomy.builder()
                .serviceType(TRANSFER_CATEGORY)
                .specificBuiltInData(SPECIFIC_BUILT_IN_DATA)
                .build();

        when(legacyPspCodeUtilMock.retrievePspCode(PSP_TAX_CODE, false)).thenReturn(PSP_CODE);
        when(gecClient.getPublicBundleSubscriptionDetailByPSP(PSP_CODE, CI_TAX_CODE, ID_BUNDLE))
                .thenReturn(bundleDetails);
        when(taxonomyService.getTaxonomiesByCodes(Collections.singletonList(TRANSFER_CATEGORY)))
                .thenReturn(Collections.singletonList(taxonomy));

        PublicBundleCISubscriptionsDetail result = assertDoesNotThrow(() -> sut
                .getPublicBundleCISubscriptionsDetail(
                        ID_BUNDLE,
                        PSP_TAX_CODE,
                        CI_TAX_CODE,
                        PublicBundleSubscriptionStatus.ACCEPTED)
        );

        assertNotNull(result);
        assertEquals(1, result.getCiBundleFeeList().size());

        assertEquals(TRANSFER_CATEGORY, result.getCiBundleFeeList().get(0).getServiceType());
        assertEquals(SPECIFIC_BUILT_IN_DATA, result.getCiBundleFeeList().get(0).getSpecificBuiltInData());
        assertEquals(100L, result.getCiBundleFeeList().get(0).getPaymentAmount());

    }

    @Test
    void getPublicBundleCISubscriptionsDetailWaiting() {
        PspRequests pspRequests = PspRequests.builder()
                .requestsList(
                        Collections.singletonList(
                                PspBundleRequest.builder()
                                        .ciBundleAttributes(
                                                Collections.singletonList(
                                                        PspCiBundleAttribute.builder()
                                                                .maxPaymentAmount(100L)
                                                                .transferCategory(TRANSFER_CATEGORY)
                                                                .build()
                                                )
                                        )
                                        .id(ID_BUNDLE_REQUEST)
                                        .build()
                        )
                )
                .pageInfo(buildPageInfo())
                .build();
        Taxonomy taxonomy = Taxonomy.builder()
                .serviceType(TRANSFER_CATEGORY)
                .specificBuiltInData(SPECIFIC_BUILT_IN_DATA)
                .build();

        when(legacyPspCodeUtilMock.retrievePspCode(PSP_TAX_CODE, false)).thenReturn(PSP_CODE);
        when(gecClient.getPublicBundleSubscriptionRequestByPSP(PSP_CODE, CI_TAX_CODE, ID_BUNDLE, 1, null))
                .thenReturn(pspRequests);
        when(taxonomyService.getTaxonomiesByCodes(Collections.singletonList(TRANSFER_CATEGORY)))
                .thenReturn(Collections.singletonList(taxonomy));

        PublicBundleCISubscriptionsDetail result = assertDoesNotThrow(() -> sut
                .getPublicBundleCISubscriptionsDetail(
                        ID_BUNDLE,
                        PSP_TAX_CODE,
                        CI_TAX_CODE,
                        PublicBundleSubscriptionStatus.WAITING)
        );

        assertNotNull(result);
        assertEquals(1, result.getCiBundleFeeList().size());
        assertEquals(ID_BUNDLE_REQUEST, result.getBundleRequestId());

        assertEquals(TRANSFER_CATEGORY, result.getCiBundleFeeList().get(0).getServiceType());
        assertEquals(SPECIFIC_BUILT_IN_DATA, result.getCiBundleFeeList().get(0).getSpecificBuiltInData());
        assertEquals(100L, result.getCiBundleFeeList().get(0).getPaymentAmount());

    }

    private CreditorInstitutionInfo buildCIInfo() {
        CreditorInstitutionInfo ciInfo = CreditorInstitutionInfo.builder()
                .businessName("businessName")
                .ciTaxCode(CI_TAX_CODE)
                .build();
        return ciInfo;
    }

    private PageInfo buildPageInfo() {
        return PageInfo.builder()
                .limit(LIMIT)
                .page(PAGE)
                .totalPages(1)
                .build();
    }
}