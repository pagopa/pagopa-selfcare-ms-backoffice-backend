package it.pagopa.selfcare.pagopa.backoffice.service;

import it.pagopa.selfcare.pagopa.backoffice.client.ExternalApiClient;
import it.pagopa.selfcare.pagopa.backoffice.entity.InstitutionRTPServiceEntity;
import it.pagopa.selfcare.pagopa.backoffice.exception.AppError;
import it.pagopa.selfcare.pagopa.backoffice.exception.AppException;
import it.pagopa.selfcare.pagopa.backoffice.model.institutions.ServiceConsent;
import it.pagopa.selfcare.pagopa.backoffice.model.institutions.ServiceConsentRequest;
import it.pagopa.selfcare.pagopa.backoffice.model.institutions.ServiceConsentResponse;
import it.pagopa.selfcare.pagopa.backoffice.model.institutions.ServiceId;
import it.pagopa.selfcare.pagopa.backoffice.model.institutions.client.Institution;
import it.pagopa.selfcare.pagopa.backoffice.repository.InstitutionRTPServiceRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.ZoneId;
import java.time.OffsetDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class InstitutionServicesServiceTest {

    @Mock
    private ExternalApiClient externalApiClient;

    @Mock
    private InstitutionRTPServiceRepository rtpServiceRepository;

    @InjectMocks
    private InstitutionServicesService sut;

    @Captor
    private ArgumentCaptor<InstitutionRTPServiceEntity> entityCaptor;

    private static final String INSTITUTION_ID = "inst-123";
    private static final String TAX_CODE = "TAX_CODE_XYZ";
    private static final String DESCRIPTION = "Test Bank";

    @Test
    void saveServiceConsent_RTP_Success() {
        // 1. Arrange
        Institution institution = new Institution();
        institution.setId(INSTITUTION_ID);
        institution.setTaxCode(TAX_CODE);
        institution.setDescription(DESCRIPTION);

        ServiceConsentRequest request = new ServiceConsentRequest(ServiceConsent.OPT_IN);

        when(externalApiClient.getInstitution(INSTITUTION_ID)).thenReturn(institution);

        // 2. Act
        ServiceConsentResponse response = sut.saveServiceConsent(request, ServiceId.RTP, INSTITUTION_ID);

        // 3. Assert

        // Verify repository interaction and capture the entity
        verify(rtpServiceRepository, times(1)).save(entityCaptor.capture());
        InstitutionRTPServiceEntity savedEntity = entityCaptor.getValue();

        // Check fields mapped in the entity
        assertEquals(INSTITUTION_ID, savedEntity.getId());
        assertEquals(TAX_CODE, savedEntity.getInstitutionTaxCode());
        assertEquals(DESCRIPTION, savedEntity.getName());
        assertEquals(request.getConsent().toString(), savedEntity.getConsent());
        assertNotNull(savedEntity.getConsentDate());

        // Check the Response object
        assertEquals(request.getConsent(), response.getConsent());

        // We verify that the response time is exactly the Entity time converted to Rome
        OffsetDateTime expectedRomeTime = savedEntity.getConsentDate()
                .atZone(ZoneId.of("Europe/Rome"))
                .toOffsetDateTime();

        assertEquals(expectedRomeTime, response.getDate(),
                "The response date must be the entity date converted to Rome timezone");
    }

    @Test
    void saveServiceConsent_InstitutionNotFound_ThrowsException() {
        // 1. Arrange
        when(externalApiClient.getInstitution(any())).thenReturn(null);

        ServiceConsentRequest request = new ServiceConsentRequest();

        // 2. Act & Assert
        AppException exception = assertThrows(AppException.class, () ->
                sut.saveServiceConsent(request, ServiceId.RTP, INSTITUTION_ID)
        );

        assertEquals(AppError.INSTITUTION_NOT_FOUND.httpStatus, exception.getHttpStatus());

        // Ensure we didn't try to save anything
        verifyNoInteractions(rtpServiceRepository);
    }

    @Test
    void saveServiceConsent_ServiceNotSupported_ThrowsException() {
        // 1. Arrange
        Institution institution = new Institution();
        institution.setId(INSTITUTION_ID);

        when(externalApiClient.getInstitution(INSTITUTION_ID)).thenReturn(institution);

        ServiceConsentRequest request = new ServiceConsentRequest();

        // 2. Act & Assert
        AppException exception = assertThrows(AppException.class, () ->
                sut.saveServiceConsent(request, ServiceId.fromString("INVALID"), INSTITUTION_ID)
        );

        assertEquals(AppError.SERVICE_NOT_FOUND.httpStatus, exception.getHttpStatus());
        verifyNoInteractions(rtpServiceRepository);
    }
}