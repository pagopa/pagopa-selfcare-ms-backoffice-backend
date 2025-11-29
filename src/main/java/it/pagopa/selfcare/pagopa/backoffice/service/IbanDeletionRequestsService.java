package it.pagopa.selfcare.pagopa.backoffice.service;

import it.pagopa.selfcare.pagopa.backoffice.client.ApiConfigSelfcareIntegrationClient;
import it.pagopa.selfcare.pagopa.backoffice.entity.IbanDeletionRequestEntity;
import it.pagopa.selfcare.pagopa.backoffice.exception.AppError;
import it.pagopa.selfcare.pagopa.backoffice.exception.AppException;
import it.pagopa.selfcare.pagopa.backoffice.model.iban.*;
import it.pagopa.selfcare.pagopa.backoffice.repository.IbanDeletionRequestsRepository;
import it.pagopa.selfcare.pagopa.backoffice.util.IbanDeletionRequestStatus;
import it.pagopa.selfcare.pagopa.backoffice.util.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
public class IbanDeletionRequestsService {

    private final IbanDeletionRequestsRepository ibanDeletionRequestsRepository;
    private final ApiConfigSelfcareIntegrationClient apiConfigSelfcareIntegrationClient;

    @Autowired
    public IbanDeletionRequestsService(IbanDeletionRequestsRepository ibanDeletionRequestsRepository, ApiConfigSelfcareIntegrationClient apiConfigSelfcareIntegrationClient) {
        this.ibanDeletionRequestsRepository = ibanDeletionRequestsRepository;
        this.apiConfigSelfcareIntegrationClient = apiConfigSelfcareIntegrationClient;
    }

    public IbanDeletionRequestResponse createIbanDeletionRequest(String ciCode, String ibanValue, LocalDate scheduledExecutionDate) {

        String maskedIban = StringUtils.obfuscateKeepingLast4(ibanValue);

        log.info("Creating IBAN deletion request for ciCode: {}, ibanValue: {}, scheduled date: {}", ciCode, maskedIban, scheduledExecutionDate);

        apiConfigSelfcareIntegrationClient.getCreditorInstitutionIbans(ciCode, null)
                .getIbanList()
                .stream()
                .filter(iban -> iban.getIban().equals(ibanValue))
                .findFirst()
                .orElseThrow(() -> {
                    log.error("IBAN {} not found for ciCode: {}", maskedIban, ciCode);
                    return new AppException(AppError.UNAUTHORIZED);
                });

        log.debug("IBAN {} validated successfully for ciCode: {}", maskedIban, ciCode);

        Instant scheduledExecutionInstant = scheduledExecutionDate.atStartOfDay(ZoneOffset.UTC).toInstant();

        return Optional.of(IbanDeletionRequestEntity.builder()
                        .id(UUID.randomUUID().toString())
                        .ibanValue(ibanValue)
                        .scheduledExecutionDate(scheduledExecutionInstant)
                        .status(IbanDeletionRequestStatus.PENDING)
                        .build())
                .map(task -> {
                    log.debug("Saving IBAN deletion request with ID: {}", task.getId());
                    return ibanDeletionRequestsRepository.save(task);
                })
                .map(savedTask -> {
                    log.info("IBAN deletion request created successfully with ID: {} for ciCode: {}, IBAN: {}",
                            savedTask.getId(), ciCode, maskedIban);
                    return IbanDeletionRequestResponse.builder()
                            .ciCode(ciCode)
                            .ibanValue(ibanValue)
                            .scheduledExecutionDate(scheduledExecutionDate)
                            .status(savedTask.getStatus().name())
                            .build();
                })
                .orElseThrow(() -> {
                    log.error("Failed to create IBAN deletion request for ciCode: {}, IBAN: {}", ciCode, maskedIban);
                    return new AppException(AppError.INTERNAL_SERVER_ERROR);
                });
    }

    public IbanDeletionRequestResponse getIbanDeletionRequest(String ciCode, String ibanValue) {

        String maskedIban = StringUtils.obfuscateKeepingLast4(ibanValue);

        log.info("Retrieving IBAN deletion request for ciCode: {}, ibanValue: {}", ciCode, maskedIban);

        return Optional.ofNullable(ibanValue)
                .filter(value -> !value.isBlank())
                .map(ibanDeletionRequestsRepository::findByIbanValue)
                .map(entity -> {
                    log.info("Found IBAN deletion request with ID: {} for ciCode: {}", entity.getId(), ciCode);
                    return IbanDeletionRequestResponse.builder()
                            .id(entity.getId())
                            .ciCode(ciCode)
                            .ibanValue(entity.getIbanValue())
                            .scheduledExecutionDate(LocalDate.ofInstant(entity.getScheduledExecutionDate(), ZoneOffset.UTC))
                            .status(entity.getStatus().name())
                            .build();
                })
                .orElseThrow(() -> {
                    log.error("Iban deletion request not found for ciCode: {}, IBAN: {}", ciCode, maskedIban);
                    return new AppException(AppError.INTERNAL_SERVER_ERROR);
                });
    }

    public void cancelIbanDeletionRequest(String ciCode, String id) {

        log.info("Canceling IBAN deletion request with ID: {} for ciCode: {}", id, ciCode);

        ibanDeletionRequestsRepository.findById(id)
                .filter(request -> request.getStatus() == IbanDeletionRequestStatus.PENDING)
                .map(request -> {
                    request.setStatus(IbanDeletionRequestStatus.CANCELED);
                    return request;
                })
                .map(ibanDeletionRequestsRepository::save)
                .ifPresentOrElse(
                        savedRequest -> log.info("IBAN deletion request with ID: {} successfully canceled for ciCode: {}", id, ciCode),
                        () -> {
                            log.error("IBAN deletion request with ID: {} not found or not cancellable for ciCode: {}", id, ciCode);
                            throw new AppException(AppError.INTERNAL_SERVER_ERROR);
                        }
                );
    }

    public IbanDeletionRequestResponse updateIbanDeletionRequestSchedule(String ciCode, String id, LocalDate newScheduledDate) {
        log.info("Updating IBAN deletion request schedule with ID: {} for ciCode: {}, new date: {}", id, ciCode, newScheduledDate);

        return ibanDeletionRequestsRepository.findById(id)
                .filter(request -> request.getStatus() == IbanDeletionRequestStatus.PENDING)
                .map(request -> {
                    Instant newScheduledInstant = newScheduledDate.atStartOfDay(ZoneOffset.UTC).toInstant();
                    request.setScheduledExecutionDate(newScheduledInstant);
                    return request;
                })
                .map(ibanDeletionRequestsRepository::save)
                .map(updatedRequest -> {
                    log.info("IBAN deletion request with ID: {} successfully updated for ciCode: {}", id, ciCode);
                    return IbanDeletionRequestResponse.builder()
                            .ciCode(ciCode)
                            .ibanValue(updatedRequest.getIbanValue())
                            .scheduledExecutionDate(newScheduledDate)
                            .status(updatedRequest.getStatus().name())
                            .build();
                })
                .orElseThrow(() -> {
                    log.error("IBAN deletion request with ID: {} not found or not updatable for ciCode: {}", id, ciCode);
                    return new AppException(AppError.INTERNAL_SERVER_ERROR);
                });
    }
}
