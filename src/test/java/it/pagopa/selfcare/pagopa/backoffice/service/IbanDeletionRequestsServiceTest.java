package it.pagopa.selfcare.pagopa.backoffice.service;

import it.pagopa.selfcare.pagopa.backoffice.client.ApiConfigSelfcareIntegrationClient;
import it.pagopa.selfcare.pagopa.backoffice.entity.IbanDeletionRequestEntity;
import it.pagopa.selfcare.pagopa.backoffice.exception.AppException;
import it.pagopa.selfcare.pagopa.backoffice.model.iban.Iban;
import it.pagopa.selfcare.pagopa.backoffice.model.iban.IbanDeletionRequestResponse;
import it.pagopa.selfcare.pagopa.backoffice.model.iban.Ibans;
import it.pagopa.selfcare.pagopa.backoffice.repository.IbanDeletionRequestsRepository;
import it.pagopa.selfcare.pagopa.backoffice.util.IbanDeletionRequestStatus;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest(classes = {IbanDeletionRequestsService.class})
class IbanDeletionRequestsServiceTest {

    private final String CI_CODE = "77777777777";
    private final String IBAN_VALUE = "IT0000000000001000000123456";

    @MockBean
    private IbanDeletionRequestsRepository ibanDeletionRequestsRepository;

    @MockBean
    private ApiConfigSelfcareIntegrationClient apiConfigSelfcareIntegrationClient;

    @Autowired
    private IbanDeletionRequestsService service;

    @Test
    void createIbanDeletionRequest_shouldCreateSuccessfully() {

        LocalDate scheduledDate = LocalDate.now().plusDays(7);
        Instant expectedInstant = scheduledDate.atStartOfDay(ZoneOffset.UTC).toInstant();

        Iban iban = Iban.builder()
                .iban(IBAN_VALUE)
                .active(true)
                .build();

        Ibans ibans = Ibans.builder()
                .ibanList(List.of(iban))
                .build();

        when(apiConfigSelfcareIntegrationClient.getCreditorInstitutionIbans(CI_CODE, null))
                .thenReturn(ibans);

        IbanDeletionRequestEntity savedEntity = IbanDeletionRequestEntity.builder()
                .id("task-123")
                .ibanValue(IBAN_VALUE)
                .scheduledExecutionDate(expectedInstant)
                .status(IbanDeletionRequestStatus.PENDING)
                .build();

        when(ibanDeletionRequestsRepository.save(any(IbanDeletionRequestEntity.class)))
                .thenReturn(savedEntity);

        IbanDeletionRequestResponse response = service.createIbanDeletionRequest(
                CI_CODE, IBAN_VALUE, scheduledDate
        );

        assertNotNull(response);
        assertEquals(CI_CODE, response.getCiCode());
        assertEquals(IBAN_VALUE, response.getIbanValue());
        assertEquals(scheduledDate, response.getScheduledExecutionDate());
        assertEquals("PENDING", response.getStatus());

        verify(apiConfigSelfcareIntegrationClient).getCreditorInstitutionIbans(CI_CODE, null);

        ArgumentCaptor<IbanDeletionRequestEntity> entityCaptor = ArgumentCaptor.forClass(IbanDeletionRequestEntity.class);
        verify(ibanDeletionRequestsRepository).save(entityCaptor.capture());

        IbanDeletionRequestEntity capturedEntity = entityCaptor.getValue();
        assertNotNull(capturedEntity.getId());
        assertEquals(IBAN_VALUE, capturedEntity.getIbanValue());
        assertEquals(expectedInstant, capturedEntity.getScheduledExecutionDate());
        assertEquals(IbanDeletionRequestStatus.PENDING, capturedEntity.getStatus());
    }

    @Test
    void createIbanDeletionRequest_shouldThrowException_whenIbanNotFound() {

        LocalDate scheduledDate = LocalDate.now().plusDays(7);

        Iban differentIban = Iban.builder()
                .iban("IT99X9999999999999999999999")
                .active(true)
                .build();

        Ibans ibans = Ibans.builder()
                .ibanList(List.of(differentIban))
                .build();

        when(apiConfigSelfcareIntegrationClient.getCreditorInstitutionIbans(CI_CODE, null))
                .thenReturn(ibans);

        AppException exception = assertThrows(AppException.class, () ->
                service.createIbanDeletionRequest(CI_CODE, IBAN_VALUE, scheduledDate)
        );

        assertEquals(AppException.class, exception.getClass());
        verify(ibanDeletionRequestsRepository, never()).save(any());
    }

    @Test
    void createIbanDeletionRequest_shouldThrowException_whenIbanListIsEmpty() {

        LocalDate scheduledDate = LocalDate.now().plusDays(7);

        Ibans ibans = Ibans.builder()
                .ibanList(List.of())
                .build();

        when(apiConfigSelfcareIntegrationClient.getCreditorInstitutionIbans(CI_CODE, null))
                .thenReturn(ibans);

        AppException exception = assertThrows(AppException.class, () ->
                service.createIbanDeletionRequest(CI_CODE, IBAN_VALUE, scheduledDate)
        );

        assertEquals(AppException.class, exception.getClass());
        verify(ibanDeletionRequestsRepository, never()).save(any());
    }

    @Test
    void createIbanDeletionRequest_shouldSetMidnightUTC() {

        LocalDate scheduledDate = LocalDate.of(2025, 12, 15);
        Instant expectedInstant = scheduledDate.atStartOfDay(ZoneOffset.UTC).toInstant();

        Iban iban = Iban.builder()
                .iban(IBAN_VALUE)
                .active(true)
                .build();

        Ibans ibans = Ibans.builder()
                .ibanList(List.of(iban))
                .build();

        when(apiConfigSelfcareIntegrationClient.getCreditorInstitutionIbans(CI_CODE, null))
                .thenReturn(ibans);

        IbanDeletionRequestEntity savedEntity = IbanDeletionRequestEntity.builder()
                .id("task-123")
                .ibanValue(IBAN_VALUE)
                .scheduledExecutionDate(expectedInstant)
                .status(IbanDeletionRequestStatus.PENDING)
                .build();

        when(ibanDeletionRequestsRepository.save(any(IbanDeletionRequestEntity.class)))
                .thenReturn(savedEntity);

        service.createIbanDeletionRequest(CI_CODE, IBAN_VALUE, scheduledDate);

        ArgumentCaptor<IbanDeletionRequestEntity> entityCaptor = ArgumentCaptor.forClass(IbanDeletionRequestEntity.class);
        verify(ibanDeletionRequestsRepository).save(entityCaptor.capture());

        IbanDeletionRequestEntity capturedEntity = entityCaptor.getValue();

        assertEquals("2025-12-15T00:00:00Z", capturedEntity.getScheduledExecutionDate().toString());
    }

    @Test
    void getIbanDeletionRequest_shouldReturnRequestSuccessfully() {

        Instant scheduledInstant = LocalDate.now().plusDays(5)
                .atStartOfDay(ZoneOffset.UTC).toInstant();

        IbanDeletionRequestEntity entity = IbanDeletionRequestEntity.builder()
                .id("req-001")
                .ibanValue(IBAN_VALUE)
                .scheduledExecutionDate(scheduledInstant)
                .status(IbanDeletionRequestStatus.PENDING)
                .build();

        when(ibanDeletionRequestsRepository.findByIbanValue(IBAN_VALUE))
                .thenReturn(entity);

        IbanDeletionRequestResponse response = service.getIbanDeletionRequest(CI_CODE, IBAN_VALUE);

        assertNotNull(response);
        assertEquals("req-001", response.getId());
        assertEquals(CI_CODE, response.getCiCode());
        assertEquals(IBAN_VALUE, response.getIbanValue());
        assertEquals(LocalDate.ofInstant(scheduledInstant, ZoneOffset.UTC),
                response.getScheduledExecutionDate());
        assertEquals("PENDING", response.getStatus());

        verify(ibanDeletionRequestsRepository).findByIbanValue(IBAN_VALUE);
    }

    @Test
    void getIbanDeletionRequest_shouldThrowException_whenNotFound() {

        when(ibanDeletionRequestsRepository.findByIbanValue(IBAN_VALUE))
                .thenReturn(null);

        assertThrows(AppException.class,
                () -> service.getIbanDeletionRequest(CI_CODE, IBAN_VALUE));

        verify(ibanDeletionRequestsRepository).findByIbanValue(IBAN_VALUE);
    }

    @Test
    void cancelIbanDeletionRequest_shouldCancelSuccessfully() {

        IbanDeletionRequestEntity entity = IbanDeletionRequestEntity.builder()
                .id("req-002")
                .ibanValue(IBAN_VALUE)
                .scheduledExecutionDate(Instant.now())
                .status(IbanDeletionRequestStatus.PENDING)
                .build();

        when(ibanDeletionRequestsRepository.findById("req-002"))
                .thenReturn(java.util.Optional.of(entity));

        when(ibanDeletionRequestsRepository.save(any(IbanDeletionRequestEntity.class)))
                .thenReturn(entity);

        service.cancelIbanDeletionRequest(CI_CODE, "req-002");

        assertEquals(IbanDeletionRequestStatus.CANCELED, entity.getStatus());
        verify(ibanDeletionRequestsRepository).save(entity);
    }

    @Test
    void cancelIbanDeletionRequest_shouldThrowException_whenNotPending() {

        IbanDeletionRequestEntity entity = IbanDeletionRequestEntity.builder()
                .id("req-003")
                .ibanValue(IBAN_VALUE)
                .scheduledExecutionDate(Instant.now())
                .status(IbanDeletionRequestStatus.COMPLETED)
                .build();

        when(ibanDeletionRequestsRepository.findById("req-003"))
                .thenReturn(java.util.Optional.of(entity));

        assertThrows(AppException.class,
                () -> service.cancelIbanDeletionRequest(CI_CODE, "req-003"));

        verify(ibanDeletionRequestsRepository, never()).save(any());
    }

    @Test
    void updateIbanDeletionRequestSchedule_shouldUpdateSuccessfully() {

        LocalDate newDate = LocalDate.now().plusDays(10);
        Instant newInstant = newDate.atStartOfDay(ZoneOffset.UTC).toInstant();

        IbanDeletionRequestEntity entity = IbanDeletionRequestEntity.builder()
                .id("req-004")
                .ibanValue(IBAN_VALUE)
                .scheduledExecutionDate(Instant.now())
                .status(IbanDeletionRequestStatus.PENDING)
                .build();

        when(ibanDeletionRequestsRepository.findById("req-004"))
                .thenReturn(java.util.Optional.of(entity));

        when(ibanDeletionRequestsRepository.save(any(IbanDeletionRequestEntity.class)))
                .thenReturn(entity);

        IbanDeletionRequestResponse response =
                service.updateIbanDeletionRequestSchedule(CI_CODE, "req-004", newDate);

        assertNotNull(response);
        assertEquals(CI_CODE, response.getCiCode());
        assertEquals(IBAN_VALUE, response.getIbanValue());
        assertEquals(newDate, response.getScheduledExecutionDate());
        assertEquals("PENDING", response.getStatus());
        assertEquals(newInstant, entity.getScheduledExecutionDate());

        verify(ibanDeletionRequestsRepository).save(entity);
    }

    @Test
    void updateIbanDeletionRequestSchedule_shouldThrowException_whenNotPending() {

        IbanDeletionRequestEntity entity = IbanDeletionRequestEntity.builder()
                .id("req-005")
                .ibanValue(IBAN_VALUE)
                .scheduledExecutionDate(Instant.now())
                .status(IbanDeletionRequestStatus.CANCELED)
                .build();

        when(ibanDeletionRequestsRepository.findById("req-005"))
                .thenReturn(java.util.Optional.of(entity));

        assertThrows(AppException.class,
                () -> service.updateIbanDeletionRequestSchedule(CI_CODE, "req-005", LocalDate.now()));

        verify(ibanDeletionRequestsRepository, never()).save(any());
    }

}