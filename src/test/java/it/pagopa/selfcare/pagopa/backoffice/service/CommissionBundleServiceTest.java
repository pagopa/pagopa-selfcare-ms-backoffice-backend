package it.pagopa.selfcare.pagopa.backoffice.service;

import feign.FeignException;
import it.pagopa.selfcare.pagopa.backoffice.client.ApiConfigSelfcareIntegrationClient;
import it.pagopa.selfcare.pagopa.backoffice.client.AwsSesClient;
import it.pagopa.selfcare.pagopa.backoffice.client.GecClient;
import it.pagopa.selfcare.pagopa.backoffice.config.MappingsConfiguration;
import it.pagopa.selfcare.pagopa.backoffice.exception.AppException;
import it.pagopa.selfcare.pagopa.backoffice.model.commissionbundle.*;
import it.pagopa.selfcare.pagopa.backoffice.model.commissionbundle.client.*;
import it.pagopa.selfcare.pagopa.backoffice.model.connector.PageInfo;
import it.pagopa.selfcare.pagopa.backoffice.model.institutions.client.CreditorInstitutionInfo;
import it.pagopa.selfcare.pagopa.backoffice.model.taxonomies.Taxonomy;
import it.pagopa.selfcare.pagopa.backoffice.util.LegacyPspCodeUtil;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.anyInt;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.*;

@SpringBootTest(classes = {MappingsConfiguration.class, CommissionBundleService.class})
class CommissionBundleServiceTest {

    private static final String PSP_CODE = "pspCode";
    private static final String PSP_TAX_CODE = "pspTaxCode";
    private static final String CI_TAX_CODE = "ciTaxCode";
    private static final String PSP_NAME = "pspName";
    private static final int LIMIT = 50;
    private static final int PAGE = 0;
    private static final String ID_BUNDLE = "idBundle";
    private static final String CI_BUNDLE_ID = "ciBundleId";
    private static final String ID_BUNDLE_REQUEST = "idBundleRequest";
    private static final String TRANSFER_CATEGORY = "9/0105107TS/";
    private static final String SERVICE_TYPE = "Diritti Pratiche SUAP e SUE";
    public static final String BUNDLE_NAME = "bundleName";

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

    @MockBean
    private AwsSesClient awsSesClient;

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
        when(legacyPspCodeUtilMock.retrievePspCode(PSP_TAX_CODE, true)).thenReturn(PSP_CODE);
        when(gecClient.getBundlesByPSP(any(), any(), any(), any(), any())).thenReturn(
                Bundles.builder().bundleList(Collections.singletonList(
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
        when(legacyPspCodeUtilMock.retrievePspCode(PSP_TAX_CODE, true)).thenReturn(PSP_CODE);

        BundleRequest bundleRequest = new BundleRequest();
        assertDoesNotThrow(
                () -> sut.createPSPBundle(PSP_TAX_CODE, bundleRequest)
        );
        verify(gecClient).createPSPBundle(PSP_CODE, bundleRequest);
    }

    @Test
    void getBundleDetailByPSP() {
        when(legacyPspCodeUtilMock.retrievePspCode(PSP_TAX_CODE, true)).thenReturn(PSP_CODE);
        when(gecClient.getBundleDetailByPSP(any(), any())).thenReturn(
                Bundle.builder().transferCategoryList(Collections.singletonList("test")).build());
        when(taxonomyService.getTaxonomiesByCodes(any())).thenReturn(
                Collections.singletonList(Taxonomy.builder().ecTypeCode("ecTypeCode").ecType("ecType").build()));

        PSPBundleResource bundleResource = assertDoesNotThrow(
                () -> sut.getBundleDetailByPSP(PSP_TAX_CODE, ID_BUNDLE));
        assertNotNull(bundleResource);
        assertNotNull(bundleResource.getBundleTaxonomies());
        assertEquals(1, bundleResource.getBundleTaxonomies().size());
        verify(gecClient).getBundleDetailByPSP(PSP_CODE, ID_BUNDLE);
    }

    @Test
    void updatePSPBundle() {
        when(legacyPspCodeUtilMock.retrievePspCode(PSP_TAX_CODE, true)).thenReturn(PSP_CODE);

        BundleRequest bundleRequest = new BundleRequest();
        assertDoesNotThrow(
                () -> sut.updatePSPBundle(PSP_TAX_CODE, ID_BUNDLE, bundleRequest)
        );
        verify(gecClient).updatePSPBundle(PSP_CODE, ID_BUNDLE, bundleRequest);
    }

    @Test
    void deletePSPBundle() {
        when(legacyPspCodeUtilMock.retrievePspCode(PSP_TAX_CODE, true)).thenReturn(PSP_CODE);

        assertDoesNotThrow(
                () -> sut.deletePSPBundle(PSP_TAX_CODE, ID_BUNDLE)
        );
        verify(gecClient).deletePSPBundle(PSP_CODE, ID_BUNDLE);
    }

    @Test
    void acceptPublicBundleSubscriptionsByPSPSuccess() {
        when(legacyPspCodeUtilMock.retrievePspCode(PSP_TAX_CODE, true)).thenReturn(PSP_CODE);

        assertDoesNotThrow(() ->
                sut.acceptPublicBundleSubscriptionsByPSP(PSP_TAX_CODE, ID_BUNDLE_REQUEST, CI_TAX_CODE, BUNDLE_NAME));

        verify(gecClient).acceptPublicBundleSubscriptionsByPSP(PSP_CODE, ID_BUNDLE_REQUEST);
    }

    @Test
    void getCIBundlesPrivateSuccess() {
        List<String> transferCategoryList = Collections.singletonList(TRANSFER_CATEGORY);
        Bundles bundles = buildBundles(transferCategoryList, BundleType.PRIVATE);

        when(gecClient.getBundles(any(), eq(null), eq(null), anyInt(), anyInt())).thenReturn(bundles);
        when(taxonomyService.getTaxonomiesByCodes(transferCategoryList)).thenReturn(buildTaxonomyList());

        CIBundlesResource bundlesResource = assertDoesNotThrow(
                () -> sut.getCIBundles(BundleType.PRIVATE, CI_TAX_CODE, null, 10, 0));

        assertNotNull(bundlesResource);
        assertNotNull(bundlesResource.getPageInfo());
        assertNotNull(bundlesResource.getBundles());
        assertEquals(1, bundlesResource.getBundles().size());
        assertEquals(1, bundlesResource.getBundles().get(0).getCiBundleFeeList().size());

        verify(gecClient).getBundles(any(), eq(null), eq(null), anyInt(), anyInt());
        verifyNoMoreInteractions(gecClient);
        verify(taxonomyService).getTaxonomiesByCodes(transferCategoryList);
    }

    @Test
    void getCIBundlesShouldReturnExpandedResultFromGlobalAPI() {
        List<String> transferCategoryList = Collections.singletonList(TRANSFER_CATEGORY);
        Bundles bundles = buildBundles(transferCategoryList, BundleType.GLOBAL);

        when(gecClient.getBundles(any(), anyString(), eq(null), anyInt(), anyInt())).thenReturn(bundles);
        when(taxonomyService.getTaxonomiesByCodes(transferCategoryList)).thenReturn(buildTaxonomyList());

        CIBundlesResource bundlesResource = assertDoesNotThrow(
                () -> sut.getCIBundles(BundleType.GLOBAL, null, BUNDLE_NAME, 10, 0));

        assertNotNull(bundlesResource);
        assertNotNull(bundlesResource.getPageInfo());
        assertNotNull(bundlesResource.getBundles());
        assertEquals(1, bundlesResource.getBundles().get(0).getCiBundleFeeList().size());

        verify(gecClient).getBundles(Collections.singletonList(BundleType.GLOBAL), BUNDLE_NAME, null, 10, 0);
        verifyNoMoreInteractions(gecClient);
        verify(taxonomyService).getTaxonomiesByCodes(any());
    }

    @Test
    void getCIBundlesPublicErrorNoCITaxCodeSpecified() {
        AppException e = assertThrows(AppException.class,
                () -> sut.getCIBundles(BundleType.PUBLIC, null, null, 10, 0));

        assertNotNull(e);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, e.getHttpStatus());

        verify(gecClient, never()).getBundles(any(), eq(null), anyString(), anyInt(), anyInt());
        verify(gecClient, never()).getCIBundle(CI_TAX_CODE, ID_BUNDLE);
        verify(gecClient, never()).getCIPublicBundleRequest(CI_TAX_CODE, null, ID_BUNDLE, 1, 0);
        verify(taxonomyService, never()).getTaxonomiesByCodes(any());
    }

    @Test
    void getCIBundlesPublicWithAvailableBundleSuccess() {
        List<String> transferCategoryList = Collections.singletonList(TRANSFER_CATEGORY);
        Bundles bundles = buildBundles(transferCategoryList, BundleType.PUBLIC);
        PublicBundleRequests requests = new PublicBundleRequests();
        requests.setPageInfo(PageInfo.builder().totalItems(0L).build());

        when(gecClient.getBundles(any(), eq(null), anyString(), anyInt(), anyInt())).thenReturn(bundles);
        when(gecClient.getCIBundle(CI_TAX_CODE, ID_BUNDLE)).thenThrow(FeignException.NotFound.class);
        when(gecClient.getCIPublicBundleRequest(CI_TAX_CODE, null, ID_BUNDLE, 1, 0)).thenReturn(requests);
        when(taxonomyService.getTaxonomiesByCodes(transferCategoryList)).thenReturn(buildTaxonomyList());

        CIBundlesResource bundlesResource = assertDoesNotThrow(
                () -> sut.getCIBundles(BundleType.PUBLIC, CI_TAX_CODE, null, 10, 0));

        assertNotNull(bundlesResource);
        assertNotNull(bundlesResource.getPageInfo());
        assertNotNull(bundlesResource.getBundles());
        assertEquals(1, bundlesResource.getBundles().size());
        assertEquals(1, bundlesResource.getBundles().get(0).getCiBundleFeeList().size());
        assertEquals(CIBundleStatus.AVAILABLE, bundlesResource.getBundles().get(0).getCiBundleStatus());

        verify(gecClient).getBundles(any(), eq(null), anyString(), anyInt(), anyInt());
        verify(gecClient).getCIBundle(CI_TAX_CODE, ID_BUNDLE);
        verify(gecClient).getCIPublicBundleRequest(CI_TAX_CODE, null, ID_BUNDLE, 1, 0);
        verifyNoMoreInteractions(gecClient);
        verify(taxonomyService).getTaxonomiesByCodes(transferCategoryList);
    }

    @Test
    void getCIBundlesPublicWithRequestedBundleSuccess() {
        List<String> transferCategoryList = Collections.singletonList(TRANSFER_CATEGORY);
        Bundles bundles = buildBundles(transferCategoryList, BundleType.PUBLIC);
        PublicBundleRequests requests = new PublicBundleRequests();
        requests.setRequestsList(Collections.singletonList(
                PublicBundleRequest.builder()
                        .id(ID_BUNDLE_REQUEST)
                        .ciBundleAttributes(Collections.singletonList(buildCIBundleAttribute()))
                        .build())
        );
        requests.setPageInfo(PageInfo.builder().totalItems(1L).build());

        when(gecClient.getBundles(any(), eq(null), anyString(), anyInt(), anyInt())).thenReturn(bundles);
        when(gecClient.getCIBundle(CI_TAX_CODE, ID_BUNDLE)).thenThrow(FeignException.NotFound.class);
        when(gecClient.getCIPublicBundleRequest(CI_TAX_CODE, null, ID_BUNDLE, 1, 0)).thenReturn(requests);
        when(taxonomyService.getTaxonomiesByCodes(transferCategoryList)).thenReturn(buildTaxonomyList());

        CIBundlesResource bundlesResource = assertDoesNotThrow(
                () -> sut.getCIBundles(BundleType.PUBLIC, CI_TAX_CODE, null, 10, 0));

        assertNotNull(bundlesResource);
        assertNotNull(bundlesResource.getPageInfo());
        assertNotNull(bundlesResource.getBundles());
        assertEquals(1, bundlesResource.getBundles().size());
        assertEquals(1, bundlesResource.getBundles().get(0).getCiBundleFeeList().size());
        assertEquals(CIBundleStatus.REQUESTED, bundlesResource.getBundles().get(0).getCiBundleStatus());

        verify(gecClient).getBundles(any(), eq(null), anyString(), anyInt(), anyInt());
        verify(gecClient).getCIBundle(CI_TAX_CODE, ID_BUNDLE);
        verify(gecClient).getCIPublicBundleRequest(CI_TAX_CODE, null, ID_BUNDLE, 1, 0);
        verifyNoMoreInteractions(gecClient);
        verify(taxonomyService).getTaxonomiesByCodes(transferCategoryList);
    }

    @Test
    void getCIBundlesPublicWithOnRemovalBundleSuccess() {
        List<String> transferCategoryList = Collections.singletonList(TRANSFER_CATEGORY);
        Bundles bundles = buildBundles(transferCategoryList, BundleType.PUBLIC);
        CiBundleDetails ciBundle = new CiBundleDetails();
        ciBundle.setValidityDateTo(LocalDate.now());
        ciBundle.setAttributes(Collections.singletonList(buildCIBundleAttribute()));

        when(gecClient.getBundles(any(), eq(null), anyString(), anyInt(), anyInt())).thenReturn(bundles);
        when(gecClient.getCIBundle(CI_TAX_CODE, ID_BUNDLE)).thenReturn(ciBundle);
        when(taxonomyService.getTaxonomiesByCodes(transferCategoryList)).thenReturn(buildTaxonomyList());

        CIBundlesResource bundlesResource = assertDoesNotThrow(
                () -> sut.getCIBundles(BundleType.PUBLIC, CI_TAX_CODE, null, 10, 0));

        assertNotNull(bundlesResource);
        assertNotNull(bundlesResource.getPageInfo());
        assertNotNull(bundlesResource.getBundles());
        assertEquals(1, bundlesResource.getBundles().size());
        assertEquals(1, bundlesResource.getBundles().get(0).getCiBundleFeeList().size());
        assertEquals(CIBundleStatus.ON_REMOVAL, bundlesResource.getBundles().get(0).getCiBundleStatus());

        verify(gecClient).getBundles(any(), eq(null), anyString(), anyInt(), anyInt());
        verify(gecClient).getCIBundle(CI_TAX_CODE, ID_BUNDLE);
        verify(gecClient, never()).getCIPublicBundleRequest(CI_TAX_CODE, null, ID_BUNDLE, 1, 0);
        verifyNoMoreInteractions(gecClient);
        verify(taxonomyService).getTaxonomiesByCodes(transferCategoryList);
    }

    @Test
    void getCIBundlesPublicWithEnabledBundleSuccess() {
        List<String> transferCategoryList = Collections.singletonList(TRANSFER_CATEGORY);
        Bundles bundles = buildBundles(transferCategoryList, BundleType.PUBLIC);
        CiBundleDetails ciBundle = new CiBundleDetails();
        ciBundle.setValidityDateTo(LocalDate.now().plusDays(1));
        ciBundle.setAttributes(Collections.singletonList(buildCIBundleAttribute()));

        when(gecClient.getBundles(any(), eq(null), anyString(), anyInt(), anyInt())).thenReturn(bundles);
        when(gecClient.getCIBundle(CI_TAX_CODE, ID_BUNDLE)).thenReturn(ciBundle);
        when(taxonomyService.getTaxonomiesByCodes(transferCategoryList)).thenReturn(buildTaxonomyList());

        CIBundlesResource bundlesResource = assertDoesNotThrow(
                () -> sut.getCIBundles(BundleType.PUBLIC, CI_TAX_CODE, null, 10, 0));

        assertNotNull(bundlesResource);
        assertNotNull(bundlesResource.getPageInfo());
        assertNotNull(bundlesResource.getBundles());
        assertEquals(1, bundlesResource.getBundles().size());
        assertEquals(1, bundlesResource.getBundles().get(0).getCiBundleFeeList().size());
        assertEquals(CIBundleStatus.ENABLED, bundlesResource.getBundles().get(0).getCiBundleStatus());

        verify(gecClient).getBundles(any(), eq(null), anyString(), anyInt(), anyInt());
        verify(gecClient).getCIBundle(CI_TAX_CODE, ID_BUNDLE);
        verify(gecClient, never()).getCIPublicBundleRequest(CI_TAX_CODE, null, ID_BUNDLE, 1, 0);
        verifyNoMoreInteractions(gecClient);
        verify(taxonomyService).getTaxonomiesByCodes(transferCategoryList);
    }

    @Test
    void rejectPublicBundleSubscriptionByPSPSuccess() {
        when(legacyPspCodeUtilMock.retrievePspCode(PSP_TAX_CODE, true)).thenReturn(PSP_CODE);
        assertDoesNotThrow(() ->
                sut.rejectPublicBundleSubscriptionByPSP(PSP_TAX_CODE, ID_BUNDLE_REQUEST, CI_TAX_CODE, BUNDLE_NAME));
        verify(gecClient).rejectPublicBundleSubscriptionByPSP(PSP_CODE, ID_BUNDLE_REQUEST);
    }

    @Test
    void getPublicBundleCISubscriptionsAccepted() {
        BundleCreditorInstitutionResource codeList = BundleCreditorInstitutionResource.builder()
                .ciBundleDetails(Collections.singletonList(
                        CiBundleDetails.builder()
                                .ciTaxCode(CI_TAX_CODE)
                                .validityDateTo(LocalDate.now().plusDays(1))
                                .build()
                ))
                .pageInfo(buildPageInfo())
                .build();
        CreditorInstitutionInfo ciInfo = buildCIInfo();

        when(legacyPspCodeUtilMock.retrievePspCode(PSP_TAX_CODE, true)).thenReturn(PSP_CODE);
        when(gecClient.getPublicBundleSubscriptionByPSP(PSP_CODE, ID_BUNDLE, null, LIMIT, PAGE))
                .thenReturn(codeList);
        when(apiConfigSelfcareIntegrationClient.getCreditorInstitutionInfo(Collections.singletonList(CI_TAX_CODE)))
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
        assertNotNull(result.getPageInfo());
        assertEquals(LIMIT, result.getPageInfo().getLimit());
        assertEquals(PAGE, result.getPageInfo().getPage());
        assertEquals(1, result.getPageInfo().getTotalPages());

        assertEquals(CI_TAX_CODE, result.getCiSubscriptionInfoList().get(0).getCiTaxCode());
        assertFalse(result.getCiSubscriptionInfoList().get(0).getOnRemoval());
        assertEquals(ciInfo.getBusinessName(), result.getCiSubscriptionInfoList().get(0).getBusinessName());
    }

    @Test
    void getPublicBundleCISubscriptionsAcceptedOnRemovalToday() {
        BundleCreditorInstitutionResource codeList = BundleCreditorInstitutionResource.builder()
                .ciBundleDetails(Collections.singletonList(
                        CiBundleDetails.builder()
                                .ciTaxCode(CI_TAX_CODE)
                                .validityDateTo(LocalDate.now())
                                .build()
                ))
                .pageInfo(buildPageInfo())
                .build();
        CreditorInstitutionInfo ciInfo = buildCIInfo();

        when(legacyPspCodeUtilMock.retrievePspCode(PSP_TAX_CODE, true)).thenReturn(PSP_CODE);
        when(gecClient.getPublicBundleSubscriptionByPSP(PSP_CODE, ID_BUNDLE, null, LIMIT, PAGE))
                .thenReturn(codeList);
        when(apiConfigSelfcareIntegrationClient.getCreditorInstitutionInfo(Collections.singletonList(CI_TAX_CODE)))
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
        assertNotNull(result.getPageInfo());
        assertEquals(LIMIT, result.getPageInfo().getLimit());
        assertEquals(PAGE, result.getPageInfo().getPage());
        assertEquals(1, result.getPageInfo().getTotalPages());

        assertEquals(CI_TAX_CODE, result.getCiSubscriptionInfoList().get(0).getCiTaxCode());
        assertTrue(result.getCiSubscriptionInfoList().get(0).getOnRemoval());
        assertEquals(ciInfo.getBusinessName(), result.getCiSubscriptionInfoList().get(0).getBusinessName());
    }

    @Test
    void getPublicBundleCISubscriptionsAcceptedOnRemovalYesterday() {
        BundleCreditorInstitutionResource codeList = BundleCreditorInstitutionResource.builder()
                .ciBundleDetails(Collections.singletonList(
                        CiBundleDetails.builder()
                                .ciTaxCode(CI_TAX_CODE)
                                .validityDateTo(LocalDate.now().minusDays(1))
                                .build()
                ))
                .pageInfo(buildPageInfo())
                .build();
        CreditorInstitutionInfo ciInfo = buildCIInfo();

        when(legacyPspCodeUtilMock.retrievePspCode(PSP_TAX_CODE, true)).thenReturn(PSP_CODE);
        when(gecClient.getPublicBundleSubscriptionByPSP(PSP_CODE, ID_BUNDLE, null, LIMIT, PAGE))
                .thenReturn(codeList);
        when(apiConfigSelfcareIntegrationClient.getCreditorInstitutionInfo(Collections.singletonList(CI_TAX_CODE)))
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
        assertNotNull(result.getPageInfo());
        assertEquals(LIMIT, result.getPageInfo().getLimit());
        assertEquals(PAGE, result.getPageInfo().getPage());
        assertEquals(1, result.getPageInfo().getTotalPages());

        assertEquals(CI_TAX_CODE, result.getCiSubscriptionInfoList().get(0).getCiTaxCode());
        assertTrue(result.getCiSubscriptionInfoList().get(0).getOnRemoval());
        assertEquals(ciInfo.getBusinessName(), result.getCiSubscriptionInfoList().get(0).getBusinessName());
    }

    @Test
    void getPublicBundleCISubscriptionsWaiting() {
        PublicBundleRequests publicBundleRequests = PublicBundleRequests.builder()
                .requestsList(
                        Collections.singletonList(
                                PublicBundleRequest.builder()
                                        .ciFiscalCode(CI_TAX_CODE)
                                        .build()
                        )
                )
                .pageInfo(buildPageInfo())
                .build();
        CreditorInstitutionInfo ciInfo = buildCIInfo();

        when(legacyPspCodeUtilMock.retrievePspCode(PSP_TAX_CODE, true)).thenReturn(PSP_CODE);
        when(gecClient.getPublicBundleSubscriptionRequestByPSP(PSP_CODE, null, ID_BUNDLE, LIMIT, PAGE))
                .thenReturn(publicBundleRequests);
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
        assertNull(result.getCiSubscriptionInfoList().get(0).getOnRemoval());
        assertEquals(ciInfo.getBusinessName(), result.getCiSubscriptionInfoList().get(0).getBusinessName());
    }

    @Test
    void getPublicBundleCISubscriptionsDetailAccepted() {
        CiBundleDetails bundleDetails = CiBundleDetails.builder()
                .idCIBundle(CI_BUNDLE_ID)
                .attributes(Collections.singletonList(buildCIBundleAttribute()))
                .build();

        when(legacyPspCodeUtilMock.retrievePspCode(PSP_TAX_CODE, true)).thenReturn(PSP_CODE);
        when(gecClient.getPublicBundleSubscriptionDetailByPSP(PSP_CODE, CI_TAX_CODE, ID_BUNDLE))
                .thenReturn(bundleDetails);
        when(taxonomyService.getTaxonomiesByCodes(Collections.singletonList(TRANSFER_CATEGORY)))
                .thenReturn(buildTaxonomyList());

        PublicBundleCISubscriptionsDetail result = assertDoesNotThrow(() -> sut
                .getPublicBundleCISubscriptionsDetail(
                        ID_BUNDLE,
                        PSP_TAX_CODE,
                        CI_TAX_CODE,
                        PublicBundleSubscriptionStatus.ACCEPTED)
        );

        assertNotNull(result);
        assertEquals(1, result.getCiBundleFeeList().size());
        assertNull(result.getBundleRequestId());
        assertEquals(CI_BUNDLE_ID, result.getIdCIBundle());

        assertEquals(SERVICE_TYPE, result.getCiBundleFeeList().get(0).getServiceType());
        assertEquals(TRANSFER_CATEGORY, result.getCiBundleFeeList().get(0).getSpecificBuiltInData());
        assertEquals(100L, result.getCiBundleFeeList().get(0).getPaymentAmount());

    }

    @Test
    void getPublicBundleCISubscriptionsDetailWaiting() {
        PublicBundleRequests publicBundleRequests = buildPspRequests();
        Taxonomy taxonomy = Taxonomy.builder()
                .serviceType(SERVICE_TYPE)
                .specificBuiltInData(TRANSFER_CATEGORY)
                .build();

        when(legacyPspCodeUtilMock.retrievePspCode(PSP_TAX_CODE, true)).thenReturn(PSP_CODE);
        when(gecClient.getPublicBundleSubscriptionRequestByPSP(PSP_CODE, CI_TAX_CODE, ID_BUNDLE, 1, PAGE))
                .thenReturn(publicBundleRequests);
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
        assertNull(result.getIdCIBundle());

        assertEquals(SERVICE_TYPE, result.getCiBundleFeeList().get(0).getServiceType());
        assertEquals(TRANSFER_CATEGORY, result.getCiBundleFeeList().get(0).getSpecificBuiltInData());
        assertEquals(100L, result.getCiBundleFeeList().get(0).getPaymentAmount());

    }

    @Test
    void getPublicBundleCISubscriptionsDetailWaitingNoResult() {
        PublicBundleRequests publicBundleRequests = PublicBundleRequests.builder()
                .requestsList(Collections.emptyList())
                .build();

        when(legacyPspCodeUtilMock.retrievePspCode(PSP_TAX_CODE, true)).thenReturn(PSP_CODE);
        when(gecClient.getPublicBundleSubscriptionRequestByPSP(PSP_CODE, CI_TAX_CODE, ID_BUNDLE, 1, PAGE))
                .thenReturn(publicBundleRequests);

        PublicBundleCISubscriptionsDetail result = assertDoesNotThrow(() -> sut
                .getPublicBundleCISubscriptionsDetail(
                        ID_BUNDLE,
                        PSP_TAX_CODE,
                        CI_TAX_CODE,
                        PublicBundleSubscriptionStatus.WAITING)
        );

        assertNotNull(result);
        assertTrue(result.getCiBundleFeeList().isEmpty());
        assertNull(result.getIdCIBundle());
        assertNull(result.getBundleRequestId());

        verify(taxonomyService, never()).getTaxonomiesByCodes(anyList());
    }

    @Test
    void deleteCIBundleSubscriptionSuccess() {
        assertDoesNotThrow(() ->
                sut.deleteCIBundleSubscription(CI_TAX_CODE, ID_BUNDLE, BUNDLE_NAME));
    }

    @Test
    void deleteCIBundleRequestSuccess() {
        assertDoesNotThrow(() ->
                sut.deleteCIBundleRequest(ID_BUNDLE_REQUEST, ID_BUNDLE));
    }

    @Test
    void createCIBundleRequestSuccess(){
        PublicBundleRequest bundleRequest = new PublicBundleRequest();
        BundleRequestId bundleRequestId = new BundleRequestId();
        bundleRequestId.setIdBundleRequest(ID_BUNDLE_REQUEST);
        when(gecClient.createCIBundleRequest(CI_TAX_CODE, bundleRequest)).thenReturn(bundleRequestId);

        assertDoesNotThrow(() ->
                sut.createCIBundleRequest(CI_TAX_CODE, bundleRequest, BUNDLE_NAME));
    }

    private PublicBundleRequests buildPspRequests() {
        return PublicBundleRequests.builder()
                .requestsList(
                        Collections.singletonList(
                                PublicBundleRequest.builder()
                                        .ciBundleAttributes(Collections.singletonList(buildCIBundleAttribute()))
                                        .id(ID_BUNDLE_REQUEST)
                                        .build()
                        )
                )
                .pageInfo(buildPageInfo())
                .build();
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

    private Bundles buildBundles(List<String> transferCategoryList, BundleType bundleType) {
        return Bundles.builder()
                .bundleList(
                        Collections.singletonList(
                                Bundle.builder()
                                        .id(ID_BUNDLE)
                                        .name("ecName")
                                        .type(bundleType)
                                        .transferCategoryList(transferCategoryList)
                                        .build())
                )
                .pageInfo(PageInfo.builder().build())
                .build();
    }

    private List<Taxonomy> buildTaxonomyList() {
        return Collections.singletonList(Taxonomy.builder()
                .serviceType(SERVICE_TYPE)
                .specificBuiltInData(TRANSFER_CATEGORY)
                .build());
    }

    private CIBundleAttribute buildCIBundleAttribute() {
        return CIBundleAttribute.builder()
                .maxPaymentAmount(100L)
                .transferCategory(TRANSFER_CATEGORY)
                .build();
    }
}
