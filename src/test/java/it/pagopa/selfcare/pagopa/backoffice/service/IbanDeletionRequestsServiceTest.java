package it.pagopa.selfcare.pagopa.backoffice.service;

import it.pagopa.selfcare.pagopa.backoffice.client.ApiConfigSelfcareIntegrationClient;
import it.pagopa.selfcare.pagopa.backoffice.entity.IbanDeletionRequestEntity;
import it.pagopa.selfcare.pagopa.backoffice.exception.AppError;
import it.pagopa.selfcare.pagopa.backoffice.exception.AppException;
import it.pagopa.selfcare.pagopa.backoffice.model.iban.Iban;
import it.pagopa.selfcare.pagopa.backoffice.model.ibanrequests.IbanDeletionRequest;
import it.pagopa.selfcare.pagopa.backoffice.model.ibanrequests.IbanDeletionRequests;
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
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest(classes = {IbanDeletionRequestsService.class})
class IbanDeletionRequestsServiceTest {

    private final String ciCode = "77777777777";
    private final String ibanValue = "IT0000000000001000000123456";

    @MockBean
    private IbanDeletionRequestsRepository ibanDeletionRequestsRepository;

    @MockBean
    private ApiConfigSelfcareIntegrationClient apiConfigSelfcareIntegrationClient;

    @Autowired
    private IbanDeletionRequestsService service;

    @Test
    void createIbanDeletionRequest_shouldCreateSuccessfully() {
        String scheduledDate = "2030-12-12";
        String expectedScheduledDate = LocalDate.parse(scheduledDate).atStartOfDay(ZoneOffset.UTC).toInstant().toString();
        Iban iban = Iban.builder()
                .iban(ibanValue)
                .active(true)
                .build();

        Ibans ibans = Ibans.builder()
                .ibanList(List.of(iban))
                .build();

        when(apiConfigSelfcareIntegrationClient.getCreditorInstitutionIbans(ciCode, null))
                .thenReturn(ibans);

        IbanDeletionRequestEntity savedEntity = IbanDeletionRequestEntity.builder()
                .id("task-123")
                .creditorInstitutionCode(ciCode)
                .ibanValue(ibanValue)
                .scheduledExecutionDate(scheduledDate)
                .status(IbanDeletionRequestStatus.PENDING)
                .build();

        when(ibanDeletionRequestsRepository.findByCreditorInstitutionCodeAndStatusAndIbanValue(ciCode, IbanDeletionRequestStatus.PENDING.toString(),ibanValue))
                .thenReturn(Optional.empty());

        when(ibanDeletionRequestsRepository.save(any(IbanDeletionRequestEntity.class)))
                .thenReturn(savedEntity);

        IbanDeletionRequest response = service.createIbanDeletionRequest(
                ciCode, ibanValue, scheduledDate
        );

        assertNotNull(response);
        assertEquals(ciCode, response.getCiCode());
        assertEquals(ibanValue, response.getIbanValue());
        assertEquals(scheduledDate, response.getScheduledExecutionDate());
        assertEquals("PENDING", response.getStatus());

        verify(apiConfigSelfcareIntegrationClient).getCreditorInstitutionIbans(ciCode, null);

        ArgumentCaptor<IbanDeletionRequestEntity> entityCaptor = ArgumentCaptor.forClass(IbanDeletionRequestEntity.class);
        verify(ibanDeletionRequestsRepository).save(entityCaptor.capture());

        IbanDeletionRequestEntity capturedEntity = entityCaptor.getValue();
        assertNotNull(capturedEntity.getId());
        assertEquals(ibanValue, capturedEntity.getIbanValue());
        assertEquals(expectedScheduledDate, capturedEntity.getScheduledExecutionDate());
        assertEquals(IbanDeletionRequestStatus.PENDING, capturedEntity.getStatus());
    }

    @Test
    void createIbanDeletionRequest_shouldThrowException_whenFoundPendingRequest() {
        String scheduledDate = "2030-12-12";
        String expectedScheduledDate = LocalDate.parse(scheduledDate).atStartOfDay(ZoneOffset.UTC).toInstant().toString();
        Iban iban = Iban.builder()
                .iban(ibanValue)
                .active(true)
                .build();

        Ibans ibans = Ibans.builder()
                .ibanList(List.of(iban))
                .build();

        when(apiConfigSelfcareIntegrationClient.getCreditorInstitutionIbans(ciCode, null))
                .thenReturn(ibans);

        IbanDeletionRequestEntity savedEntity = IbanDeletionRequestEntity.builder()
                .id("task-123")
                .creditorInstitutionCode(ciCode)
                .ibanValue(ibanValue)
                .scheduledExecutionDate(scheduledDate)
                .status(IbanDeletionRequestStatus.PENDING)
                .build();

        when(ibanDeletionRequestsRepository.findByCreditorInstitutionCodeAndStatusAndIbanValue(ciCode, IbanDeletionRequestStatus.PENDING.toString(),ibanValue))
                .thenReturn(Optional.ofNullable(savedEntity));


        AppException exception = assertThrows(AppException.class, () ->
                service.createIbanDeletionRequest(ciCode, ibanValue, scheduledDate)
        );

        assertEquals(AppException.class, exception.getClass());
        verify(ibanDeletionRequestsRepository, never()).save(any());
    }

    @Test
    void createIbanDeletionRequest_shouldThrowException_whenIbanNotFound() {

        String scheduledDate = "2030-12-12";

        Iban differentIban = Iban.builder()
                .iban("IT99X9999999999999999999999")
                .active(true)
                .build();

        Ibans ibans = Ibans.builder()
                .ibanList(List.of(differentIban))
                .build();

        when(apiConfigSelfcareIntegrationClient.getCreditorInstitutionIbans(ciCode, null))
                .thenReturn(ibans);

        AppException exception = assertThrows(AppException.class, () ->
                service.createIbanDeletionRequest(ciCode, ibanValue, scheduledDate)
        );

        assertEquals(AppException.class, exception.getClass());
        verify(ibanDeletionRequestsRepository, never()).save(any());
    }

    @Test
    void createIbanDeletionRequest_shouldThrowException_whenIbanListIsEmpty() {

        String scheduledDate = "2030-12-12";

        Ibans ibans = Ibans.builder()
                .ibanList(List.of())
                .build();

        when(apiConfigSelfcareIntegrationClient.getCreditorInstitutionIbans(ciCode, null))
                .thenReturn(ibans);

        AppException exception = assertThrows(AppException.class, () ->
                service.createIbanDeletionRequest(ciCode, ibanValue, scheduledDate)
        );

        assertEquals(AppException.class, exception.getClass());
        verify(ibanDeletionRequestsRepository, never()).save(any());
    }

    @Test
    void createIbanDeletionRequest_shouldSetMidnightUTC() {

        String scheduledDate = "2030-12-12";
        String expectedScheduledDate = LocalDate.parse(scheduledDate).atStartOfDay(ZoneOffset.UTC).toInstant().toString();

        Iban iban = Iban.builder()
                .iban(ibanValue)
                .active(true)
                .build();

        Ibans ibans = Ibans.builder()
                .ibanList(List.of(iban))
                .build();

        when(apiConfigSelfcareIntegrationClient.getCreditorInstitutionIbans(ciCode, null))
                .thenReturn(ibans);

        IbanDeletionRequestEntity savedEntity = IbanDeletionRequestEntity.builder()
                .id("task-123")
                .ibanValue(ibanValue)
                .scheduledExecutionDate(scheduledDate)
                .status(IbanDeletionRequestStatus.PENDING)
                .build();

        when(ibanDeletionRequestsRepository.save(any(IbanDeletionRequestEntity.class)))
                .thenReturn(savedEntity);

        service.createIbanDeletionRequest(ciCode, ibanValue, scheduledDate);

        ArgumentCaptor<IbanDeletionRequestEntity> entityCaptor = ArgumentCaptor.forClass(IbanDeletionRequestEntity.class);
        verify(ibanDeletionRequestsRepository).save(entityCaptor.capture());

        IbanDeletionRequestEntity capturedEntity = entityCaptor.getValue();

        assertEquals(expectedScheduledDate, capturedEntity.getScheduledExecutionDate());
    }

    @Test
    void createIbanDeletionRequest_shouldThrowException_whenDateIsInPast() {

        String pastDate = LocalDate.now().minusDays(1).toString();

        AppException exception = assertThrows(AppException.class, () ->
                service.createIbanDeletionRequest(ciCode, ibanValue, pastDate)
        );

        assertEquals(AppError.BAD_REQUEST.getTitle(), exception.getTitle());
        assertEquals("Invalid scheduledExecutionDate", exception.getMessage());
        verify(ibanDeletionRequestsRepository, never()).save(any());
        verify(apiConfigSelfcareIntegrationClient, never()).getCreditorInstitutionIbans(any(), any());
    }

    @Test
    void createIbanDeletionRequest_shouldThrowException_whenDateIsToday() {

        String today = LocalDate.now().toString();

        AppException exception = assertThrows(AppException.class, () ->
                service.createIbanDeletionRequest(ciCode, ibanValue, today)
        );

        assertEquals(AppError.BAD_REQUEST.getTitle(), exception.getTitle() );
        assertEquals("Invalid scheduledExecutionDate", exception.getMessage());
        verify(ibanDeletionRequestsRepository, never()).save(any());
        verify(apiConfigSelfcareIntegrationClient, never()).getCreditorInstitutionIbans(any(), any());
    }

    @Test
    void createIbanDeletionRequest_shouldThrowException_whenDateFormatIsInvalid() {

        String invalidDate = "invalid-date-format";

        assertThrows(Exception.class, () ->
                service.createIbanDeletionRequest(ciCode, ibanValue, invalidDate)
        );

        verify(ibanDeletionRequestsRepository, never()).save(any());
        verify(apiConfigSelfcareIntegrationClient, never()).getCreditorInstitutionIbans(any(), any());
    }

    @Test
    void createIbanDeletionRequest_shouldThrowException_whenRepositorySaveFails() {

        String scheduledDate = "2030-12-12";

        Iban iban = Iban.builder()
                .iban(ibanValue)
                .active(true)
                .build();

        Ibans ibans = Ibans.builder()
                .ibanList(List.of(iban))
                .build();

        when(apiConfigSelfcareIntegrationClient.getCreditorInstitutionIbans(ciCode, null))
                .thenReturn(ibans);

        when(ibanDeletionRequestsRepository.save(any(IbanDeletionRequestEntity.class)))
                .thenThrow(new RuntimeException("Database error"));

        assertThrows(RuntimeException.class, () ->
                service.createIbanDeletionRequest(ciCode, ibanValue, scheduledDate)
        );

        verify(apiConfigSelfcareIntegrationClient).getCreditorInstitutionIbans(ciCode, null);
        verify(ibanDeletionRequestsRepository).save(any(IbanDeletionRequestEntity.class));
    }

    @Test
    void getIbanDeletionRequest_shouldReturnRequestSuccessfully() {

        LocalDate today = LocalDate.now();
        String scheduledDate = today.plusDays(7).atStartOfDay(ZoneOffset.UTC).toString();

        IbanDeletionRequestEntity entity = IbanDeletionRequestEntity.builder()
                .id("req-001")
                .creditorInstitutionCode(ciCode)
                .ibanValue(ibanValue)
                .scheduledExecutionDate(scheduledDate)
                .status(IbanDeletionRequestStatus.PENDING)
                .build();

        when(ibanDeletionRequestsRepository.findByCreditorInstitutionCodeAndIbanValue(ciCode, ibanValue))
                .thenReturn(Optional.of(entity));

        IbanDeletionRequests response = service.getIbanDeletionRequests(ciCode, ibanValue);

        assertNotNull(response);
        assertNotNull(response.getRequests());
        assertEquals(1, response.getRequests().size());

        IbanDeletionRequest ibanDeletionRequest = response.getRequests().get(0);
        assertEquals("req-001", ibanDeletionRequest.getId());
        assertEquals(ciCode, ibanDeletionRequest.getCiCode());
        assertEquals(ibanValue, ibanDeletionRequest.getIbanValue());
        assertEquals(scheduledDate,
                ibanDeletionRequest.getScheduledExecutionDate());
        assertEquals("PENDING", ibanDeletionRequest.getStatus());

        verify(ibanDeletionRequestsRepository).findByCreditorInstitutionCodeAndIbanValue(ciCode, ibanValue);
    }

    @Test
    void getIbanDeletionRequests_shouldReturnMultipleRequests_whenIbanValueIsNull() {

        Instant scheduledInstant1 = LocalDate.now().plusDays(5)
                .atStartOfDay(ZoneOffset.UTC).toInstant();
        Instant scheduledInstant2 = LocalDate.now().plusDays(10)
                .atStartOfDay(ZoneOffset.UTC).toInstant();

        IbanDeletionRequestEntity entity1 = IbanDeletionRequestEntity.builder()
                .id("req-001")
                .creditorInstitutionCode(ciCode)
                .ibanValue(ibanValue)
                .scheduledExecutionDate(scheduledInstant1.toString())
                .status(IbanDeletionRequestStatus.PENDING)
                .build();

        IbanDeletionRequestEntity entity2 = IbanDeletionRequestEntity.builder()
                .id("req-002")
                .creditorInstitutionCode(ciCode)
                .ibanValue("IT99X9999999999999999999999")
                .scheduledExecutionDate(scheduledInstant2.toString())
                .status(IbanDeletionRequestStatus.PENDING)
                .build();

        when(ibanDeletionRequestsRepository.findByCreditorInstitutionCodeAndStatus(ciCode, IbanDeletionRequestStatus.PENDING))
                .thenReturn(List.of(entity1, entity2));

        IbanDeletionRequests response = service.getIbanDeletionRequests(ciCode, null);

        assertNotNull(response);
        assertNotNull(response.getRequests());
        assertEquals(2, response.getRequests().size());

        IbanDeletionRequest request1 = response.getRequests().get(0);
        assertEquals("req-001", request1.getId());
        assertEquals(ibanValue, request1.getIbanValue());

        IbanDeletionRequest request2 = response.getRequests().get(1);
        assertEquals("req-002", request2.getId());
        assertEquals("IT99X9999999999999999999999", request2.getIbanValue());

        verify(ibanDeletionRequestsRepository).findByCreditorInstitutionCodeAndStatus(ciCode, IbanDeletionRequestStatus.PENDING);
    }

    @Test
    void getIbanDeletionRequests_shouldReturnMultipleRequests_whenIbanValueIsBlank() {

        Instant scheduledInstant1 = LocalDate.now().plusDays(5)
                .atStartOfDay(ZoneOffset.UTC).toInstant();
        Instant scheduledInstant2 = LocalDate.now().plusDays(10)
                .atStartOfDay(ZoneOffset.UTC).toInstant();

        IbanDeletionRequestEntity entity1 = IbanDeletionRequestEntity.builder()
                .id("req-001")
                .creditorInstitutionCode(ciCode)
                .ibanValue(ibanValue)
                .scheduledExecutionDate(scheduledInstant1.toString())
                .status(IbanDeletionRequestStatus.PENDING)
                .build();

        IbanDeletionRequestEntity entity2 = IbanDeletionRequestEntity.builder()
                .id("req-002")
                .creditorInstitutionCode(ciCode)
                .ibanValue("IT99X9999999999999999999999")
                .scheduledExecutionDate(scheduledInstant2.toString())
                .status(IbanDeletionRequestStatus.PENDING)
                .build();

        when(ibanDeletionRequestsRepository.findByCreditorInstitutionCodeAndStatus(ciCode, IbanDeletionRequestStatus.PENDING))
                .thenReturn(List.of(entity1, entity2));

        IbanDeletionRequests response = service.getIbanDeletionRequests(ciCode, "");

        assertNotNull(response);
        assertNotNull(response.getRequests());
        assertEquals(2, response.getRequests().size());

        IbanDeletionRequest request1 = response.getRequests().get(0);
        assertEquals("req-001", request1.getId());
        assertEquals(ibanValue, request1.getIbanValue());

        IbanDeletionRequest request2 = response.getRequests().get(1);
        assertEquals("req-002", request2.getId());
        assertEquals("IT99X9999999999999999999999", request2.getIbanValue());

        verify(ibanDeletionRequestsRepository).findByCreditorInstitutionCodeAndStatus(ciCode, IbanDeletionRequestStatus.PENDING);
    }

    @Test
    void getIbanDeletionRequests_shouldReturnEmptyList_whenNoRequestsFound() {

        when(ibanDeletionRequestsRepository.findByCreditorInstitutionCodeAndStatus(ciCode, IbanDeletionRequestStatus.PENDING))
                .thenReturn(List.of());

        IbanDeletionRequests response = service.getIbanDeletionRequests(ciCode, null);

        assertNotNull(response);
        assertNotNull(response.getRequests());
        assertEquals(0, response.getRequests().size());

        verify(ibanDeletionRequestsRepository).findByCreditorInstitutionCodeAndStatus(ciCode, IbanDeletionRequestStatus.PENDING);
    }
    @Test
    void getIbanDeletionRequest_shouldReturnEmptyList_whenNotFound() {

        when(ibanDeletionRequestsRepository.findByCreditorInstitutionCodeAndIbanValue(ciCode,ibanValue))
                .thenReturn(Optional.empty());

        IbanDeletionRequests result = service.getIbanDeletionRequests(ciCode, ibanValue);

        assertNotNull(result);
        assertTrue(result.getRequests().isEmpty());
        verify(ibanDeletionRequestsRepository).findByCreditorInstitutionCodeAndIbanValue(ciCode, ibanValue);
        verify(ibanDeletionRequestsRepository, never()).findByCreditorInstitutionCodeAndStatus(any(), any());
    }

    @Test
    void cancelIbanDeletionRequest_shouldCancelSuccessfully() {

        IbanDeletionRequestEntity entity = IbanDeletionRequestEntity.builder()
                .id("req-002")
                .ibanValue(ibanValue)
                .scheduledExecutionDate(Instant.now().toString())
                .status(IbanDeletionRequestStatus.PENDING)
                .build();

        when(ibanDeletionRequestsRepository.findById("req-002"))
                .thenReturn(java.util.Optional.of(entity));

        when(ibanDeletionRequestsRepository.save(any(IbanDeletionRequestEntity.class)))
                .thenReturn(entity);

        service.cancelIbanDeletionRequest(ciCode, "req-002");

        assertEquals(IbanDeletionRequestStatus.CANCELED, entity.getStatus());
        verify(ibanDeletionRequestsRepository).save(entity);
    }

    @Test
    void cancelIbanDeletionRequest_shouldThrowException_whenNotPending() {

        IbanDeletionRequestEntity entity = IbanDeletionRequestEntity.builder()
                .id("req-003")
                .ibanValue(ibanValue)
                .scheduledExecutionDate(Instant.now().toString())
                .status(IbanDeletionRequestStatus.COMPLETED)
                .build();

        when(ibanDeletionRequestsRepository.findById("req-003"))
                .thenReturn(java.util.Optional.of(entity));

        assertThrows(AppException.class,
                () -> service.cancelIbanDeletionRequest(ciCode, "req-003"));

        verify(ibanDeletionRequestsRepository, never()).save(any());
    }
}