package it.pagopa.selfcare.pagopa.backoffice.scheduler.function;

import it.pagopa.selfcare.pagopa.backoffice.client.GecClient;
import it.pagopa.selfcare.pagopa.backoffice.model.commissionbundle.client.Bundle;
import it.pagopa.selfcare.pagopa.backoffice.model.commissionbundle.client.BundleCreditorInstitutionResource;
import it.pagopa.selfcare.pagopa.backoffice.model.commissionbundle.client.BundleOffers;
import it.pagopa.selfcare.pagopa.backoffice.model.commissionbundle.client.BundleType;
import it.pagopa.selfcare.pagopa.backoffice.model.commissionbundle.client.Bundles;
import it.pagopa.selfcare.pagopa.backoffice.model.commissionbundle.client.CiBundleDetails;
import it.pagopa.selfcare.pagopa.backoffice.model.commissionbundle.client.PspBundleOffer;
import it.pagopa.selfcare.pagopa.backoffice.model.commissionbundle.client.PublicBundleRequest;
import it.pagopa.selfcare.pagopa.backoffice.model.commissionbundle.client.PublicBundleRequests;
import it.pagopa.selfcare.pagopa.backoffice.model.connector.PageInfo;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@SpringBootTest(classes = BundleAllPages.class)
class BundleAllPagesTest {

    private static final String ID_BUNDLE = "idBundle";
    private static final String PSP_CODE = "pspCode";
    private static final String CI_TAX_CODE = "ciTaxCode";

    @MockBean
    private GecClient gecClient;

    @Autowired
    private BundleAllPages sut;

    @Test
    void getAllBundlesWithExpireDateSuccess() {
        int limit = 2;
        ReflectionTestUtils.setField(sut, "getAllBundlesPageLimit", limit);

        String expireAt = LocalDate.now().toString();
        long numOfBundles = 1L;
        Bundles bundles = Bundles.builder()
                .bundleList(Collections.singletonList(new Bundle()))
                .pageInfo(PageInfo.builder().totalItems(numOfBundles).build())
                .build();

        when(gecClient.getBundles(
                List.of(BundleType.GLOBAL, BundleType.PUBLIC, BundleType.PRIVATE),
                null,
                null,
                expireAt,
                1,
                0
        )).thenReturn(bundles);
        when(gecClient.getBundles(
                eq(List.of(BundleType.GLOBAL, BundleType.PUBLIC, BundleType.PRIVATE)),
                eq(null),
                eq(null),
                eq(expireAt),
                eq(limit),
                anyInt()
        )).thenReturn(bundles);

        Set<Bundle> result = assertDoesNotThrow(() -> sut.getAllBundlesWithExpireDate(expireAt));

        assertNotNull(result);
        assertEquals(numOfBundles, result.size());
    }

    @Test
    void getBundleSubscriptionByPSPSuccess() {
        int limit = 2;
        ReflectionTestUtils.setField(sut, "getAllBundlesPageLimit", limit);

        long numOfBundles = 1L;
        BundleCreditorInstitutionResource resource = BundleCreditorInstitutionResource.builder()
                .ciBundleDetails(Collections.singletonList(CiBundleDetails.builder().ciTaxCode(CI_TAX_CODE).build()))
                .pageInfo(PageInfo.builder().totalItems(numOfBundles).build())
                .build();

        when(gecClient.getBundleSubscriptionByPSP(PSP_CODE, ID_BUNDLE, null, 1, 0)).thenReturn(resource);
        when(gecClient.getBundleSubscriptionByPSP(eq(PSP_CODE), eq(ID_BUNDLE), eq(null), eq(limit), anyInt())).thenReturn(resource);

        Set<String> result = assertDoesNotThrow(() -> sut.getBundleSubscriptionByPSP(PSP_CODE, ID_BUNDLE));

        assertNotNull(result);
        assertEquals(numOfBundles, result.size());
    }

    @Test
    void getPublicBundleSubscriptionRequestByPSPSuccess() {
        int limit = 2;
        ReflectionTestUtils.setField(sut, "getAllBundlesPageLimit", limit);

        long numOfBundles = 1L;
        PublicBundleRequests resource = PublicBundleRequests.builder()
                .requestsList(Collections.singletonList(PublicBundleRequest.builder().ciFiscalCode(CI_TAX_CODE).build()))
                .pageInfo(PageInfo.builder().totalItems(numOfBundles).build())
                .build();

        when(gecClient.getPublicBundleSubscriptionRequestByPSP(PSP_CODE, ID_BUNDLE, null, 1, 0))
                .thenReturn(resource);
        when(gecClient.getPublicBundleSubscriptionRequestByPSP(eq(PSP_CODE), eq(ID_BUNDLE), eq(null), eq(limit), anyInt()))
                .thenReturn(resource);

        Set<String> result = assertDoesNotThrow(() -> sut.getPublicBundleSubscriptionRequestByPSP(PSP_CODE, ID_BUNDLE));

        assertNotNull(result);
        assertEquals(numOfBundles, result.size());
    }

    @Test
    void getPrivateBundleOffersByPSPSuccess() {
        int limit = 2;
        ReflectionTestUtils.setField(sut, "getAllBundlesPageLimit", limit);

        long numOfBundles = 1L;
        BundleOffers resource = BundleOffers.builder()
                .offers(Collections.singletonList(PspBundleOffer.builder().ciFiscalCode(CI_TAX_CODE).build()))
                .pageInfo(PageInfo.builder().totalItems(numOfBundles).build())
                .build();

        when(gecClient.getPrivateBundleOffersByPSP(PSP_CODE, ID_BUNDLE, null, 1, 0)).thenReturn(resource);
        when(gecClient.getPrivateBundleOffersByPSP(eq(PSP_CODE), eq(ID_BUNDLE), eq(null), eq(limit), anyInt())).thenReturn(resource);

        Set<String> result = assertDoesNotThrow(() -> sut.getPrivateBundleOffersByPSP(PSP_CODE, ID_BUNDLE));

        assertNotNull(result);
        assertEquals(numOfBundles, result.size());
    }
}