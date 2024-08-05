package it.pagopa.selfcare.pagopa.backoffice.client;

import it.pagopa.selfcare.pagopa.backoffice.model.email.EmailMessageDetail;
import it.pagopa.selfcare.pagopa.backoffice.model.institutions.SelfcareProductUser;
import it.pagopa.selfcare.pagopa.backoffice.model.institutions.client.Institution;
import it.pagopa.selfcare.pagopa.backoffice.model.institutions.client.InstitutionProductUsers;
import it.pagopa.selfcare.pagopa.backoffice.model.institutions.client.Institutions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.util.ReflectionTestUtils;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring5.SpringTemplateEngine;
import software.amazon.awssdk.services.ses.SesClient;
import software.amazon.awssdk.services.ses.model.MessageRejectedException;
import software.amazon.awssdk.services.ses.model.SendEmailRequest;
import software.amazon.awssdk.services.ses.model.SendEmailResponse;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest(classes = AwsSesClient.class)
class AwsSesClientTest {

    private static final String INSTITUTION_TAX_CODE = "institutionTaxCode";
    private static final String HTML_TEMPLATE_FILE_NAME = "htmlTemplateFile.html";
    private static final String INSTITUTION_ID = "institutionId";

    @MockBean
    private SesClient sesClient;

    @MockBean
    private SpringTemplateEngine templateEngine;

    @MockBean
    private ExternalApiClient externalApiClient;

    @Autowired
    private AwsSesClient sut;

    @Test
    void sendEmailPRODSuccess() {
        ReflectionTestUtils.setField(sut, "environment", "prod");

        Institutions institutions = buildInstitutions();
        List<InstitutionProductUsers> institutionProductUsers = buildInstitutionProductUsers();

        when(externalApiClient.getInstitutionsFiltered(INSTITUTION_TAX_CODE)).thenReturn(institutions);
        when(externalApiClient.getInstitutionProductUsers(
                INSTITUTION_ID,
                null,
                null,
                Collections.singletonList(SelfcareProductUser.ADMIN.getProductUser()))
        ).thenReturn(institutionProductUsers);
        when(templateEngine.process(anyString(), any())).thenReturn("html template");
        when(sesClient.sendEmail(any(SendEmailRequest.class))).thenReturn(SendEmailResponse.builder().build());

        assertDoesNotThrow(() -> sut.sendEmail(buildEmailMessageDetail(INSTITUTION_TAX_CODE)));
    }

    @Test
    void sendEmailNotPRODSuccess() {
        ReflectionTestUtils.setField(sut, "environment", "dev");

        when(templateEngine.process(anyString(), any())).thenReturn("html template");
        when(sesClient.sendEmail(any(SendEmailRequest.class))).thenReturn(SendEmailResponse.builder().build());

        assertDoesNotThrow(() -> sut.sendEmail(buildEmailMessageDetail(INSTITUTION_TAX_CODE)));

        verify(externalApiClient, never()).getInstitutionsFiltered(INSTITUTION_TAX_CODE);
        verify(externalApiClient, never()).getInstitutionProductUsers(
                INSTITUTION_ID,
                null,
                null,
                Collections.singletonList(SelfcareProductUser.ADMIN.getProductUser())
        );
    }

    @Test
    void sendEmailPRODFail() {
        ReflectionTestUtils.setField(sut, "environment", "prod");

        Institutions institutions = buildInstitutions();
        List<InstitutionProductUsers> institutionProductUsers = buildInstitutionProductUsers();

        when(externalApiClient.getInstitutionsFiltered(INSTITUTION_TAX_CODE)).thenReturn(institutions);
        when(externalApiClient.getInstitutionProductUsers(
                INSTITUTION_ID,
                null,
                null,
                Collections.singletonList(SelfcareProductUser.ADMIN.getProductUser()))
        ).thenReturn(institutionProductUsers);
        when(templateEngine.process(anyString(), any())).thenReturn("html template");
        when(sesClient.sendEmail(any(SendEmailRequest.class))).thenThrow(MessageRejectedException.class);

        assertDoesNotThrow(() -> sut.sendEmail(buildEmailMessageDetail(INSTITUTION_TAX_CODE)));
    }

    @Test
    void sendEmailPRODNoInstitutionTaxCodeSkipped() {
        ReflectionTestUtils.setField(sut, "environment", "prod");
        ReflectionTestUtils.setField(sut, "testEmailAddress", "test@mail.it");

        assertDoesNotThrow(() -> sut.sendEmail(buildEmailMessageDetail(null)));

        verify(externalApiClient, never()).getInstitutionsFiltered(INSTITUTION_TAX_CODE);
        verify(externalApiClient, never()).getInstitutionProductUsers(
                INSTITUTION_ID,
                null,
                null,
                Collections.singletonList(SelfcareProductUser.ADMIN.getProductUser())
        );
        verify(templateEngine, never()).process(anyString(), any());
        verify(sesClient, never()).sendEmail(any(SendEmailRequest.class));
    }

    @Test
    void sendEmailNoInstitutionFoundSkipped() {
        ReflectionTestUtils.setField(sut, "environment", "prod");

        when(externalApiClient.getInstitutionsFiltered(INSTITUTION_TAX_CODE))
                .thenReturn(Institutions.builder().institutions(Collections.emptyList()).build());

        assertDoesNotThrow(() -> sut.sendEmail(buildEmailMessageDetail(INSTITUTION_TAX_CODE)));

        verify(externalApiClient, never()).getInstitutionProductUsers(
                INSTITUTION_ID,
                null,
                null,
                Collections.singletonList(SelfcareProductUser.ADMIN.getProductUser())
        );
        verify(templateEngine, never()).process(anyString(), any());
        verify(sesClient, never()).sendEmail(any(SendEmailRequest.class));
    }

    @Test
    void sendEmailNoDestinationSkipped() {
        ReflectionTestUtils.setField(sut, "environment", "prod");

        Institutions institutions = buildInstitutions();

        when(externalApiClient.getInstitutionsFiltered(INSTITUTION_TAX_CODE)).thenReturn(institutions);
        when(externalApiClient.getInstitutionProductUsers(
                INSTITUTION_ID,
                null,
                null,
                Collections.singletonList(SelfcareProductUser.ADMIN.getProductUser()))
        ).thenReturn(Collections.emptyList());

        assertDoesNotThrow(() -> sut.sendEmail(buildEmailMessageDetail(INSTITUTION_TAX_CODE)));

        verify(templateEngine, never()).process(anyString(), any());
        verify(sesClient, never()).sendEmail(any(SendEmailRequest.class));
    }

    @Test
    void sendEmailNotPRODAndNoTestEmailSkipped() {
        ReflectionTestUtils.setField(sut, "environment", "dev");
        ReflectionTestUtils.setField(sut, "testEmailAddress", null);

        assertDoesNotThrow(() -> sut.sendEmail(buildEmailMessageDetail(INSTITUTION_TAX_CODE)));

        verify(externalApiClient, never()).getInstitutionsFiltered(INSTITUTION_TAX_CODE);
        verify(externalApiClient, never()).getInstitutionProductUsers(
                INSTITUTION_ID,
                null,
                null,
                Collections.singletonList(SelfcareProductUser.ADMIN.getProductUser())
        );
        verify(templateEngine, never()).process(anyString(), any());
        verify(sesClient, never()).sendEmail(any(SendEmailRequest.class));
    }

    @Test
    void sendEmailPRODSWithPagopaOperatorSuccess() {
        ReflectionTestUtils.setField(sut, "environment", "prod");

        Institutions institutions = buildInstitutions();
        List<InstitutionProductUsers> institutionProductUsers = buildInstitutionProductUsers();

        when(externalApiClient.getInstitutionsFiltered(INSTITUTION_TAX_CODE)).thenReturn(institutions);
        when(externalApiClient.getInstitutionProductUsers(
                INSTITUTION_ID,
                null,
                null,
                Collections.singletonList(SelfcareProductUser.ADMIN.getProductUser()))
        ).thenReturn(institutionProductUsers);
        when(templateEngine.process(anyString(), any())).thenReturn("html template");
        when(sesClient.sendEmail(any(SendEmailRequest.class))).thenReturn(SendEmailResponse.builder().build());

        assertDoesNotThrow(() -> sut.sendEmail(buildEmailMessageDetail(INSTITUTION_TAX_CODE), true));
    }

    private EmailMessageDetail buildEmailMessageDetail(String institutionTaxCode) {
        return EmailMessageDetail.builder()
                .institutionTaxCode(institutionTaxCode)
                .htmlBodyFileName(HTML_TEMPLATE_FILE_NAME)
                .htmlBodyContext(new Context())
                .textBody("textBody")
                .subject("subject")
                .destinationUserType(SelfcareProductUser.ADMIN)
                .build();
    }

    private List<InstitutionProductUsers> buildInstitutionProductUsers() {
        return Collections.singletonList(
                InstitutionProductUsers.builder()
                        .email("email")
                        .build()
        );
    }

    private Institutions buildInstitutions() {
        return Institutions.builder()
                .institutions(Collections.singletonList(
                        Institution.builder()
                                .id(INSTITUTION_ID)
                                .build()
                ))
                .build();
    }
}