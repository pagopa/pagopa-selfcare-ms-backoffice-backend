package it.pagopa.selfcare.pagopa.backoffice.service;

import it.pagopa.selfcare.pagopa.backoffice.client.AwsSesClient;
import it.pagopa.selfcare.pagopa.backoffice.scheduler.function.BundleAllPages;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@SpringBootTest(classes = AsyncNotificationService.class)
class AsyncNotificationServiceTest {

    private static final String PSP_CODE = "pspCode";
    private static final String CI_TAX_CODE = "ciTaxCode";
    private static final String CI_TAX_CODE_2 = "ciTaxCode2";
    private static final String PSP_NAME = "pspName";
    private static final String ID_BUNDLE = "idBundle";
    public static final String BUNDLE_NAME = "bundleName";

    @MockBean
    private BundleAllPages bundleAllPages;

    @MockBean
    private AwsSesClient awsSesClient;

    @Autowired
    private AsyncNotificationService sut;

    @Test
    void notifyDeletePSPBundleAsyncSuccess() {
        assertDoesNotThrow(() ->
                sut.notifyDeletePSPBundleAsync(Set.of(CI_TAX_CODE, CI_TAX_CODE_2), BUNDLE_NAME, PSP_NAME));

        verify(awsSesClient, times(2)).sendEmail(any());
    }
}