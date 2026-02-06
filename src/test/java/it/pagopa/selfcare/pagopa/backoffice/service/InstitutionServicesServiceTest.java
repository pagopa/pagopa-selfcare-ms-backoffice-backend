package it.pagopa.selfcare.pagopa.backoffice.service;

import it.pagopa.selfcare.pagopa.backoffice.client.ExternalApiClient;
import it.pagopa.selfcare.pagopa.backoffice.config.InstitutionServicesConfig;
import it.pagopa.selfcare.pagopa.backoffice.entity.InstitutionRTPServiceEntity;
import it.pagopa.selfcare.pagopa.backoffice.exception.AppError;
import it.pagopa.selfcare.pagopa.backoffice.exception.AppException;
import it.pagopa.selfcare.pagopa.backoffice.model.institutions.*;
import it.pagopa.selfcare.pagopa.backoffice.model.institutions.client.Institution;
import it.pagopa.selfcare.pagopa.backoffice.repository.InstitutionRTPServiceRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.time.ZoneId;
import java.time.OffsetDateTime;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class InstitutionServicesServiceTest {

    @Mock
    private ExternalApiClient externalApiClient;

    @Mock
    private InstitutionRTPServiceRepository rtpServiceRepository;

    @Mock
    private InstitutionServicesConfig defaultServicesConfig;

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
        ServiceId invalid = ServiceId.fromString("INVALID");

        ServiceConsentRequest request = new ServiceConsentRequest();

        // 2. Act & Assert
        AppException exception = assertThrows(AppException.class, () ->
                sut.saveServiceConsent(request, invalid, INSTITUTION_ID)
        );

        assertEquals(AppError.SERVICE_NOT_FOUND.httpStatus, exception.getHttpStatus());
        verifyNoInteractions(rtpServiceRepository);
    }

    @Test
    void getServiceConsents_RTPServiceFound_Success() {
        // 1. Arrange
        Institution institution = new Institution();
        institution.setId(INSTITUTION_ID);

        Instant time = Instant.now();

        InstitutionRTPServiceEntity rtpService = InstitutionRTPServiceEntity.builder()
                .id(INSTITUTION_ID)
                .consent("OPT_OUT")
                .consentDate(time)
                .institutionTaxCode(TAX_CODE)
                .name(DESCRIPTION)
                .build();

        when(externalApiClient.getInstitution(INSTITUTION_ID)).thenReturn(institution);
        when(rtpServiceRepository.findById(INSTITUTION_ID)).thenReturn(Optional.of(rtpService));
        when(defaultServicesConfig.getDefaultConsents()).thenReturn(Map.of(ServiceId.RTP, ServiceConsent.OPT_IN));

        // 2. Act
        ServiceConsentsResponse response = sut.getServiceConsents(INSTITUTION_ID);

        // 3. Assert

        // Verify repository interaction
        verify(rtpServiceRepository, times(1)).findById(INSTITUTION_ID);

        // Verify response services list size
        assertEquals(1, response.getServices().size());
        ServiceConsentInfo responseConsent = response.getServices().get(0);

        // Verify RTP service info
        assertEquals(ServiceId.RTP, responseConsent.getServiceId());
        assertEquals(ServiceConsent.OPT_OUT, responseConsent.getServiceConsent());

        // We verify that the response time is exactly the Entity time converted to Rome
        OffsetDateTime expectedRomeTime = time
                .atZone(ZoneId.of("Europe/Rome"))
                .toOffsetDateTime();

        assertEquals(expectedRomeTime, responseConsent.getConsentDate(),
                "The response date must be the entity date converted to Rome timezone");
    }

    @Test
    void getServiceConsents_RTPServiceNotFound_Success() {
        // 1. Arrange
        Institution institution = new Institution();
        institution.setId(INSTITUTION_ID);

        when(externalApiClient.getInstitution(INSTITUTION_ID)).thenReturn(institution);
        when(rtpServiceRepository.findById(INSTITUTION_ID)).thenReturn(Optional.empty());
        when(defaultServicesConfig.getDefaultConsents()).thenReturn(Map.of(ServiceId.RTP, ServiceConsent.OPT_IN));

        // 2. Act
        ServiceConsentsResponse response = sut.getServiceConsents(INSTITUTION_ID);

        // 3. Assert

        // Verify repository interaction
        verify(rtpServiceRepository, times(1)).findById(INSTITUTION_ID);

        // Verify response services list size
        assertEquals(1, response.getServices().size());
        ServiceConsentInfo responseConsent = response.getServices().get(0);

        // Verify RTP service info
        assertEquals(ServiceId.RTP, responseConsent.getServiceId());
        assertEquals(ServiceConsent.OPT_IN, responseConsent.getServiceConsent());

        // We verify that the response time is exactly the Unix epoch time converted to Rome
        OffsetDateTime expectedRomeTime = Instant.EPOCH
                .atZone(ZoneId.of("Europe/Rome"))
                .toOffsetDateTime();

        assertEquals(expectedRomeTime, responseConsent.getConsentDate(),
                "The response date must be the Unix epoch converted to Rome timezone");
    }

    @Test
    void getServiceConsents__InstitutionNotFound_ThrowsException() {
        // 1. Arrange
        when(externalApiClient.getInstitution(any())).thenReturn(null);

        // 2. Act & Assert
        AppException exception = assertThrows(AppException.class, () ->
                sut.getServiceConsents(INSTITUTION_ID)
        );

        assertEquals(AppError.INSTITUTION_NOT_FOUND.httpStatus, exception.getHttpStatus());

        // Ensure we didn't search the institution's saved consents
        verifyNoInteractions(rtpServiceRepository);
    }
}