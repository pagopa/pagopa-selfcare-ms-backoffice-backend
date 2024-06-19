package it.pagopa.selfcare.pagopa.backoffice.scheduler;

import feign.FeignException;
import it.pagopa.selfcare.pagopa.backoffice.client.ApiConfigClient;
import it.pagopa.selfcare.pagopa.backoffice.client.AwsSesClient;
import it.pagopa.selfcare.pagopa.backoffice.model.commissionbundle.client.Bundle;
import it.pagopa.selfcare.pagopa.backoffice.model.commissionbundle.client.BundleType;
import it.pagopa.selfcare.pagopa.backoffice.model.connector.channel.PaymentServiceProviderDetails;
import it.pagopa.selfcare.pagopa.backoffice.scheduler.function.BundleAllPages;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest(classes = CommissionBundleMailNotificationScheduler.class)
class CommissionBundleMailNotificationSchedulerTest {

    private static final String ID_BUNDLE = "idBundle";
    private static final String ID_BUNDLE_2 = "idBundle2";
    private static final String ID_BUNDLE_3 = "idBundle3";
    private static final String PSP_CODE = "pspCode";
    private static final String PSP_TAX_CODE = "pspTaxCode";
    public static final String BUNDLE_NAME = "bundleName";
    private static final String CI_TAX_CODE = "ciTaxCode";
    private static final String CI_TAX_CODE_2 = "ciTaxCode2";
    private static final String CI_TAX_CODE_3 = "ciTaxCode3";
    ;

    @MockBean
    private BundleAllPages bundleAllPages;

    @MockBean
    private ApiConfigClient apiConfigClient;

    @MockBean
    private AwsSesClient awsSesClient;

    @Autowired
    private CommissionBundleMailNotificationScheduler scheduler;

    @Test
    void mailNotificationSuccess() {
        HashSet<Bundle> bundles = new HashSet<>();
        bundles.add(buildBundle(BundleType.GLOBAL, ID_BUNDLE));
        bundles.add(buildBundle(BundleType.PUBLIC, ID_BUNDLE_2));
        bundles.add(buildBundle(BundleType.PRIVATE, ID_BUNDLE_3));

        when(bundleAllPages.getAllBundlesWithExpireDate(anyString()))
                .thenReturn(bundles)
                .thenReturn(bundles);
        when(bundleAllPages.getAllCITaxCodesAssociatedToABundle(ID_BUNDLE, BundleType.GLOBAL, PSP_CODE))
                .thenReturn(Set.of(CI_TAX_CODE))
                .thenReturn(Set.of(CI_TAX_CODE));
        when(bundleAllPages.getAllCITaxCodesAssociatedToABundle(ID_BUNDLE_2, BundleType.PUBLIC, PSP_CODE))
                .thenReturn(Set.of(CI_TAX_CODE_2))
                .thenReturn(Set.of(CI_TAX_CODE_2));
        when(bundleAllPages.getAllCITaxCodesAssociatedToABundle(ID_BUNDLE_3, BundleType.PRIVATE, PSP_CODE))
                .thenReturn(Set.of(CI_TAX_CODE_3))
                .thenReturn(Set.of(CI_TAX_CODE_3));
        when(apiConfigClient.getPSPDetails(PSP_CODE))
                .thenReturn(PaymentServiceProviderDetails.builder().taxCode(PSP_TAX_CODE).build())
                .thenReturn(PaymentServiceProviderDetails.builder().taxCode(PSP_TAX_CODE).build())
                .thenReturn(PaymentServiceProviderDetails.builder().taxCode(PSP_TAX_CODE).build())
                .thenReturn(PaymentServiceProviderDetails.builder().taxCode(PSP_TAX_CODE).build())
                .thenReturn(PaymentServiceProviderDetails.builder().taxCode(PSP_TAX_CODE).build())
                .thenReturn(PaymentServiceProviderDetails.builder().taxCode(PSP_TAX_CODE).build());

        assertDoesNotThrow(() -> scheduler.mailNotification());

        verify(awsSesClient, times(12)).sendEmail(any());
    }

    @Test
    void mailNotificationSuccessWithErrorRetrievingOnePSPTaxCode() {
        HashSet<Bundle> bundles = new HashSet<>();
        bundles.add(buildBundle(BundleType.GLOBAL, ID_BUNDLE));
        bundles.add(buildBundle(BundleType.PUBLIC, ID_BUNDLE_2));
        bundles.add(buildBundle(BundleType.PRIVATE, ID_BUNDLE_3));

        when(bundleAllPages.getAllBundlesWithExpireDate(anyString()))
                .thenReturn(bundles)
                .thenReturn(bundles);
        when(bundleAllPages.getAllCITaxCodesAssociatedToABundle(ID_BUNDLE, BundleType.GLOBAL, PSP_CODE))
                .thenReturn(Set.of(CI_TAX_CODE))
                .thenReturn(Set.of(CI_TAX_CODE));
        when(bundleAllPages.getAllCITaxCodesAssociatedToABundle(ID_BUNDLE_2, BundleType.PUBLIC, PSP_CODE))
                .thenReturn(Set.of(CI_TAX_CODE_2))
                .thenReturn(Set.of(CI_TAX_CODE_2));
        when(bundleAllPages.getAllCITaxCodesAssociatedToABundle(ID_BUNDLE_3, BundleType.PRIVATE, PSP_CODE))
                .thenReturn(Set.of(CI_TAX_CODE_3))
                .thenReturn(Set.of(CI_TAX_CODE_3));
        when(apiConfigClient.getPSPDetails(PSP_CODE))
                .thenReturn(PaymentServiceProviderDetails.builder().taxCode(PSP_TAX_CODE).build())
                .thenThrow(FeignException.class)
                .thenReturn(PaymentServiceProviderDetails.builder().taxCode(PSP_TAX_CODE).build())
                .thenReturn(PaymentServiceProviderDetails.builder().taxCode(PSP_TAX_CODE).build())
                .thenReturn(PaymentServiceProviderDetails.builder().taxCode(PSP_TAX_CODE).build())
                .thenReturn(PaymentServiceProviderDetails.builder().taxCode(PSP_TAX_CODE).build());

        assertDoesNotThrow(() -> scheduler.mailNotification());

        verify(awsSesClient, times(11)).sendEmail(any());
    }

    @Test
    void mailNotificationFail() {
        when(bundleAllPages.getAllBundlesWithExpireDate(anyString()))
                .thenThrow(FeignException.class);

        FeignException e = assertThrows(FeignException.class, () -> scheduler.mailNotification());

        assertNotNull(e);

        verify(awsSesClient, never()).sendEmail(any());
        verify(bundleAllPages, never()).getBundleSubscriptionByPSP(anyString(), anyString());
        verify(bundleAllPages, never()).getPublicBundleSubscriptionRequestByPSP(anyString(), anyString());
        verify(bundleAllPages, never()).getPrivateBundleOffersByPSP(anyString(), anyString());
    }

    private Bundle buildBundle(BundleType bundleType, String idBundle) {
        return Bundle.builder()
                .id(idBundle)
                .name(BUNDLE_NAME)
                .type(bundleType)
                .idPsp(PSP_CODE)
                .build();
    }
}