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
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

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

    public IbanDeletionRequest createIbanDeletionRequest(String ciCode, String ibanValue, String scheduledExecutionDate) {


        // scheduledExecutionDate like 2025-12-12
        LocalDate tomorrow = LocalDate.now().plusDays(1);
        LocalDate scheduledExecutionDateAsLocalDate;

        try {
             scheduledExecutionDateAsLocalDate = LocalDate.parse(scheduledExecutionDate);
        } catch (DateTimeParseException e) {
            log.error("Invalid date format provided for scheduledExecutionDate: {}", scheduledExecutionDate);
            throw new AppException(AppError.BAD_REQUEST);
        }

        if (scheduledExecutionDateAsLocalDate.isBefore(tomorrow)) {
            log.error("Scheduled execution date {} is not a valid future date (must be at least {}) for ciCode: {}",
                    scheduledExecutionDate, tomorrow, ciCode);
            throw new AppException(AppError.BAD_REQUEST);
        }

        String validateScheduledExecutionDate = scheduledExecutionDateAsLocalDate.atStartOfDay().toString();

        String maskedIban = StringUtils.obfuscateKeepingLast4(ibanValue);

        log.info("Creating IBAN deletion request for ciCode: {}, ibanValue: {}, scheduled date: {}", ciCode, maskedIban, validateScheduledExecutionDate);

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

        Instant now = Instant.now();

        return Optional.of(IbanDeletionRequestEntity.builder()
                        .id(UUID.randomUUID().toString())
                        .requestedAt(now.toString())
                        .updatedAt(now.toString())
                        .ibanValue(ibanValue)
                        .scheduledExecutionDate(validateScheduledExecutionDate)
                        .status(IbanDeletionRequestStatus.PENDING)
                        .build())
                .map(task -> {
                    log.debug("Saving IBAN deletion request with ID: {}", task.getId());
                    return ibanDeletionRequestsRepository.save(task);
                })
                .map(savedEntity -> {
                    log.info("IBAN deletion request created successfully with ID: {} for ciCode: {}, IBAN: {}",
                            savedEntity.getId(), ciCode, maskedIban);
                    return IbanDeletionRequest.builder()
                            .id(savedEntity.getId())
                            .ciCode(savedEntity.getCreditorInstitutionCode())
                            .ibanValue(savedEntity.getIbanValue())
                            .scheduledExecutionDate(savedEntity.getScheduledExecutionDate())
                            .status(savedEntity.getStatus().name())
                            .build();
                })
                .orElseThrow(() -> {
                    log.error("Failed to create IBAN deletion request for ciCode: {}, IBAN: {}", ciCode, maskedIban);
                    return new AppException(AppError.INTERNAL_SERVER_ERROR);
                });
    }

    public IbanDeletionRequests getIbanDeletionRequests(String ciCode, String ibanValue) {

        String maskedIban = Optional.ofNullable(ibanValue)
                .map(StringUtils::obfuscateKeepingLast4)
                .orElse("all");

        log.info("Retrieving IBAN deletion requests for ciCode: {}, ibanValue: {}", ciCode, maskedIban);

        return Optional.ofNullable(ibanValue)
                .filter(value -> !value.isBlank())
                .map(value -> ibanDeletionRequestsRepository.findByIbanValue(value)
                        .map(List::of)
                        .orElse(List.of()))
                .orElseGet(() -> ibanDeletionRequestsRepository.findByCreditorInstitutionCodeAndStatus(ciCode, IbanDeletionRequestStatus.PENDING))
                .stream()
                .map(entity -> IbanDeletionRequest.builder()
                        .id(entity.getId())
                        .ciCode(entity.getCreditorInstitutionCode())
                        .ibanValue(entity.getIbanValue())
                        .scheduledExecutionDate(entity.getScheduledExecutionDate())
                        .status(entity.getStatus().name())
                        .build())
                .collect(Collectors.collectingAndThen(
                        Collectors.toList(),
                        requests -> {
                            log.info("Found {} IBAN deletion requests for ciCode: {}", requests.size(), ciCode);
                            return IbanDeletionRequests.builder()
                                    .requests(requests)
                                    .build();
                        }
                ));
    }

    public void cancelIbanDeletionRequest(String ciCode, String id) {

        log.info("Canceling IBAN deletion request with ID: {} for ciCode: {}", id, ciCode);

        ibanDeletionRequestsRepository.findById(id)
                .filter(request -> request.getStatus() == IbanDeletionRequestStatus.PENDING)
                .map(request -> {
                    request.setStatus(IbanDeletionRequestStatus.CANCELED);
                    request.setUpdatedAt(Instant.now().toString());
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
}
