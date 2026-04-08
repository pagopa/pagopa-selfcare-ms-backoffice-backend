package it.pagopa.selfcare.pagopa.backoffice.service;

import it.pagopa.selfcare.pagopa.backoffice.client.ApiConfigSelfcareIntegrationClient;
import it.pagopa.selfcare.pagopa.backoffice.client.AwsSesClient;
import it.pagopa.selfcare.pagopa.backoffice.model.creditorinstituions.client.CreditorInstitutionInfo;
import it.pagopa.selfcare.pagopa.backoffice.model.email.EmailMessageDetail;
import it.pagopa.selfcare.pagopa.backoffice.scheduler.function.BundleAllPages;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.List;
import java.util.Set;

import static it.pagopa.selfcare.pagopa.backoffice.util.MailTextConstants.*;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.*;

@SpringBootTest(classes = AsyncNotificationService.class)
class AsyncNotificationServiceTest {

    private static final String CI_TAX_CODE = "ciTaxCode";
    private static final String CI_TAX_CODE_2 = "ciTaxCode2";
    private static final String PSP_NAME = "pspName";
    private static final String IBAN = "iban";
    private static final String DELETE_DATE = "10-10-2025";
    private static final String BUNDLE_NAME = "bundleName";
    private static final String CI_BUSINESS_NAME = "Test Institution";

    private static final CreditorInstitutionInfo creditorInstitutionInfo = CreditorInstitutionInfo.builder()
            .ciTaxCode(CI_TAX_CODE)
            .businessName(CI_BUSINESS_NAME)
            .build();

    @MockBean
    private BundleAllPages bundleAllPages;

    @MockBean
    private AwsSesClient awsSesClient;

    @MockBean
    private ApiConfigSelfcareIntegrationClient apiConfigSelfcareIntegrationClient;

    @Autowired
    private AsyncNotificationService sut;

    @Test
    void notifyDeletePSPBundleAsyncSuccess() {
        assertDoesNotThrow(() ->
                sut.notifyDeletePSPBundleAsync(Set.of(CI_TAX_CODE, CI_TAX_CODE_2), BUNDLE_NAME, PSP_NAME));

        verify(awsSesClient, times(2)).sendEmail(any());
    }

    @Test
    void notifyIbanCreationTest() {
        when(apiConfigSelfcareIntegrationClient.getCreditorInstitutionInfo(anyList()))
                .thenReturn(List.of(creditorInstitutionInfo));
        ArgumentCaptor<EmailMessageDetail> emailCaptor = ArgumentCaptor.forClass(EmailMessageDetail.class);

        assertDoesNotThrow(() -> sut.notifyIbanCreation(CI_TAX_CODE));

        verify(awsSesClient, times(1)).sendEmail(emailCaptor.capture());
        EmailMessageDetail messageDetail = emailCaptor.getValue();

        assertEquals(CI_TAX_CODE, messageDetail.getInstitutionTaxCode());
        assertEquals(IBAN_CREATE_SUBJECT, messageDetail.getSubject());
    }

    @Test
    void notifyIbanUpdateTest() {
        when(apiConfigSelfcareIntegrationClient.getCreditorInstitutionInfo(anyList()))
                .thenReturn(List.of(creditorInstitutionInfo));
        ArgumentCaptor<EmailMessageDetail> emailCaptor = ArgumentCaptor.forClass(EmailMessageDetail.class);

        assertDoesNotThrow(() -> sut.notifyIbanUpdate(CI_TAX_CODE, IBAN));

        verify(awsSesClient, times(1)).sendEmail(emailCaptor.capture());
        EmailMessageDetail messageDetail = emailCaptor.getValue();

        assertEquals(CI_TAX_CODE, messageDetail.getInstitutionTaxCode());
        assertEquals(IBAN_UPDATE_SUBJECT, messageDetail.getSubject());
    }

    @Test
    void notifyIbanDeletionTest() {
        when(apiConfigSelfcareIntegrationClient.getCreditorInstitutionInfo(anyList()))
                .thenReturn(List.of(creditorInstitutionInfo));
        ArgumentCaptor<EmailMessageDetail> emailCaptor = ArgumentCaptor.forClass(EmailMessageDetail.class);

        assertDoesNotThrow(() -> sut.notifyIbanDeletion(CI_TAX_CODE, IBAN, DELETE_DATE));

        verify(awsSesClient, times(1)).sendEmail(emailCaptor.capture());
        EmailMessageDetail messageDetail = emailCaptor.getValue();

        assertEquals(CI_TAX_CODE, messageDetail.getInstitutionTaxCode());
        assertEquals(IBAN_DELETE_SUBJECT, messageDetail.getSubject());
    }

    @Test
    void notifyIbanRestoreTest() {
        when(apiConfigSelfcareIntegrationClient.getCreditorInstitutionInfo(anyList()))
                .thenReturn(List.of(creditorInstitutionInfo));
        ArgumentCaptor<EmailMessageDetail> emailCaptor = ArgumentCaptor.forClass(EmailMessageDetail.class);

        assertDoesNotThrow(() -> sut.notifyIbanRestore(CI_TAX_CODE, IBAN));

        verify(awsSesClient, times(1)).sendEmail(emailCaptor.capture());
        EmailMessageDetail messageDetail = emailCaptor.getValue();

        assertEquals(CI_TAX_CODE, messageDetail.getInstitutionTaxCode());
        assertEquals(IBAN_RESTORE_SUBJECT, messageDetail.getSubject());
    }
}