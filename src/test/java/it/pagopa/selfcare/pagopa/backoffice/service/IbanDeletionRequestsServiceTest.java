package it.pagopa.selfcare.pagopa.backoffice.service;

import it.pagopa.selfcare.pagopa.backoffice.client.ApiConfigSelfcareIntegrationClient;
import it.pagopa.selfcare.pagopa.backoffice.entity.IbanDeletionRequestEntity;
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

    private final String CICODE = "77777777777";
    private final String IBANVALUE = "IT0000000000001000000123456";

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
                .iban(IBANVALUE)
                .active(true)
                .build();

        Ibans ibans = Ibans.builder()
                .ibanList(List.of(iban))
                .build();

        when(apiConfigSelfcareIntegrationClient.getCreditorInstitutionIbans(CICODE, null))
                .thenReturn(ibans);

        IbanDeletionRequestEntity savedEntity = IbanDeletionRequestEntity.builder()
                .id("task-123")
                .creditorInstitutionCode(CICODE)
                .ibanValue(IBANVALUE)
                .scheduledExecutionDate(scheduledDate)
                .status(IbanDeletionRequestStatus.PENDING)
                .build();

        when(ibanDeletionRequestsRepository.save(any(IbanDeletionRequestEntity.class)))
                .thenReturn(savedEntity);

        IbanDeletionRequest response = service.createIbanDeletionRequest(
                CICODE, IBANVALUE, scheduledDate
        );

        assertNotNull(response);
        assertEquals(CICODE, response.getCiCode());
        assertEquals(IBANVALUE, response.getIbanValue());
        assertEquals(scheduledDate, response.getScheduledExecutionDate());
        assertEquals("PENDING", response.getStatus());

        verify(apiConfigSelfcareIntegrationClient).getCreditorInstitutionIbans(CICODE, null);

        ArgumentCaptor<IbanDeletionRequestEntity> entityCaptor = ArgumentCaptor.forClass(IbanDeletionRequestEntity.class);
        verify(ibanDeletionRequestsRepository).save(entityCaptor.capture());

        IbanDeletionRequestEntity capturedEntity = entityCaptor.getValue();
        assertNotNull(capturedEntity.getId());
        assertEquals(IBANVALUE, capturedEntity.getIbanValue());
        assertEquals(expectedScheduledDate, capturedEntity.getScheduledExecutionDate());
        assertEquals(IbanDeletionRequestStatus.PENDING, capturedEntity.getStatus());
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

        when(apiConfigSelfcareIntegrationClient.getCreditorInstitutionIbans(CICODE, null))
                .thenReturn(ibans);

        AppException exception = assertThrows(AppException.class, () ->
                service.createIbanDeletionRequest(CICODE, IBANVALUE, scheduledDate)
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

        when(apiConfigSelfcareIntegrationClient.getCreditorInstitutionIbans(CICODE, null))
                .thenReturn(ibans);

        AppException exception = assertThrows(AppException.class, () ->
                service.createIbanDeletionRequest(CICODE, IBANVALUE, scheduledDate)
        );

        assertEquals(AppException.class, exception.getClass());
        verify(ibanDeletionRequestsRepository, never()).save(any());
    }

    @Test
    void createIbanDeletionRequest_shouldSetMidnightUTC() {

        String scheduledDate = "2030-12-12";
        String expectedScheduledDate = LocalDate.parse(scheduledDate).atStartOfDay(ZoneOffset.UTC).toInstant().toString();

        Iban iban = Iban.builder()
                .iban(IBANVALUE)
                .active(true)
                .build();

        Ibans ibans = Ibans.builder()
                .ibanList(List.of(iban))
                .build();

        when(apiConfigSelfcareIntegrationClient.getCreditorInstitutionIbans(CICODE, null))
                .thenReturn(ibans);

        IbanDeletionRequestEntity savedEntity = IbanDeletionRequestEntity.builder()
                .id("task-123")
                .ibanValue(IBANVALUE)
                .scheduledExecutionDate(scheduledDate)
                .status(IbanDeletionRequestStatus.PENDING)
                .build();

        when(ibanDeletionRequestsRepository.save(any(IbanDeletionRequestEntity.class)))
                .thenReturn(savedEntity);

        service.createIbanDeletionRequest(CICODE, IBANVALUE, scheduledDate);

        ArgumentCaptor<IbanDeletionRequestEntity> entityCaptor = ArgumentCaptor.forClass(IbanDeletionRequestEntity.class);
        verify(ibanDeletionRequestsRepository).save(entityCaptor.capture());

        IbanDeletionRequestEntity capturedEntity = entityCaptor.getValue();

        assertEquals(expectedScheduledDate, capturedEntity.getScheduledExecutionDate());
    }

    @Test
    void getIbanDeletionRequest_shouldReturnRequestSuccessfully() {

        LocalDate today = LocalDate.now();
        String scheduledDate = today.plusDays(7).atStartOfDay(ZoneOffset.UTC).toString();

        IbanDeletionRequestEntity entity = IbanDeletionRequestEntity.builder()
                .id("req-001")
                .creditorInstitutionCode(CICODE)
                .ibanValue(IBANVALUE)
                .scheduledExecutionDate(scheduledDate)
                .status(IbanDeletionRequestStatus.PENDING)
                .build();

        when(ibanDeletionRequestsRepository.findByIbanValue(IBANVALUE))
                .thenReturn(Optional.of(entity));

        IbanDeletionRequests response = service.getIbanDeletionRequests(CICODE, IBANVALUE);

        assertNotNull(response);
        assertNotNull(response.getRequests());
        assertEquals(1, response.getRequests().size());

        IbanDeletionRequest ibanDeletionRequest = response.getRequests().get(0);
        assertEquals("req-001", ibanDeletionRequest.getId());
        assertEquals(CICODE, ibanDeletionRequest.getCiCode());
        assertEquals(IBANVALUE, ibanDeletionRequest.getIbanValue());
        assertEquals(scheduledDate,
                ibanDeletionRequest.getScheduledExecutionDate());
        assertEquals("PENDING", ibanDeletionRequest.getStatus());

        verify(ibanDeletionRequestsRepository).findByIbanValue(IBANVALUE);
    }

    @Test
    void getIbanDeletionRequests_shouldReturnMultipleRequests_whenIbanValueIsNull() {

        Instant scheduledInstant1 = LocalDate.now().plusDays(5)
                .atStartOfDay(ZoneOffset.UTC).toInstant();
        Instant scheduledInstant2 = LocalDate.now().plusDays(10)
                .atStartOfDay(ZoneOffset.UTC).toInstant();

        IbanDeletionRequestEntity entity1 = IbanDeletionRequestEntity.builder()
                .id("req-001")
                .creditorInstitutionCode(CICODE)
                .ibanValue(IBANVALUE)
                .scheduledExecutionDate(scheduledInstant1.toString())
                .status(IbanDeletionRequestStatus.PENDING)
                .build();

        IbanDeletionRequestEntity entity2 = IbanDeletionRequestEntity.builder()
                .id("req-002")
                .creditorInstitutionCode(CICODE)
                .ibanValue("IT99X9999999999999999999999")
                .scheduledExecutionDate(scheduledInstant2.toString())
                .status(IbanDeletionRequestStatus.PENDING)
                .build();

        when(ibanDeletionRequestsRepository.findByCreditorInstitutionCodeAndStatus(CICODE, IbanDeletionRequestStatus.PENDING))
                .thenReturn(List.of(entity1, entity2));

        IbanDeletionRequests response = service.getIbanDeletionRequests(CICODE, null);

        assertNotNull(response);
        assertNotNull(response.getRequests());
        assertEquals(2, response.getRequests().size());

        IbanDeletionRequest request1 = response.getRequests().get(0);
        assertEquals("req-001", request1.getId());
        assertEquals(IBANVALUE, request1.getIbanValue());

        IbanDeletionRequest request2 = response.getRequests().get(1);
        assertEquals("req-002", request2.getId());
        assertEquals("IT99X9999999999999999999999", request2.getIbanValue());

        verify(ibanDeletionRequestsRepository).findByCreditorInstitutionCodeAndStatus(CICODE, IbanDeletionRequestStatus.PENDING);
    }

    @Test
    void getIbanDeletionRequests_shouldReturnEmptyList_whenNoRequestsFound() {

        when(ibanDeletionRequestsRepository.findByCreditorInstitutionCodeAndStatus(CICODE, IbanDeletionRequestStatus.PENDING))
                .thenReturn(List.of());

        IbanDeletionRequests response = service.getIbanDeletionRequests(CICODE, null);

        assertNotNull(response);
        assertNotNull(response.getRequests());
        assertEquals(0, response.getRequests().size());

        verify(ibanDeletionRequestsRepository).findByCreditorInstitutionCodeAndStatus(CICODE, IbanDeletionRequestStatus.PENDING);
    }
    @Test
    void getIbanDeletionRequest_shouldReturnEmptyList_whenNotFound() {

        when(ibanDeletionRequestsRepository.findByIbanValue(IBANVALUE))
                .thenReturn(Optional.empty());

        IbanDeletionRequests result = service.getIbanDeletionRequests(CICODE, IBANVALUE);

        assertNotNull(result);
        assertTrue(result.getRequests().isEmpty());
        verify(ibanDeletionRequestsRepository).findByIbanValue(IBANVALUE);
        verify(ibanDeletionRequestsRepository, never()).findByCreditorInstitutionCodeAndStatus(any(), any());
    }

    @Test
    void cancelIbanDeletionRequest_shouldCancelSuccessfully() {

        IbanDeletionRequestEntity entity = IbanDeletionRequestEntity.builder()
                .id("req-002")
                .ibanValue(IBANVALUE)
                .scheduledExecutionDate(Instant.now().toString())
                .status(IbanDeletionRequestStatus.PENDING)
                .build();

        when(ibanDeletionRequestsRepository.findById("req-002"))
                .thenReturn(java.util.Optional.of(entity));

        when(ibanDeletionRequestsRepository.save(any(IbanDeletionRequestEntity.class)))
                .thenReturn(entity);

        service.cancelIbanDeletionRequest(CICODE, "req-002");

        assertEquals(IbanDeletionRequestStatus.CANCELED, entity.getStatus());
        verify(ibanDeletionRequestsRepository).save(entity);
    }

    @Test
    void cancelIbanDeletionRequest_shouldThrowException_whenNotPending() {

        IbanDeletionRequestEntity entity = IbanDeletionRequestEntity.builder()
                .id("req-003")
                .ibanValue(IBANVALUE)
                .scheduledExecutionDate(Instant.now().toString())
                .status(IbanDeletionRequestStatus.COMPLETED)
                .build();

        when(ibanDeletionRequestsRepository.findById("req-003"))
                .thenReturn(java.util.Optional.of(entity));

        assertThrows(AppException.class,
                () -> service.cancelIbanDeletionRequest(CICODE, "req-003"));

        verify(ibanDeletionRequestsRepository, never()).save(any());
    }
}