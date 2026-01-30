package it.pagopa.selfcare.pagopa.backoffice.service;

import it.pagopa.selfcare.pagopa.backoffice.client.ApiConfigSelfcareIntegrationClient;
import it.pagopa.selfcare.pagopa.backoffice.entity.IbanDeletionRequestEntity;
import it.pagopa.selfcare.pagopa.backoffice.exception.AppError;
import it.pagopa.selfcare.pagopa.backoffice.exception.AppException;
import it.pagopa.selfcare.pagopa.backoffice.model.ibanrequests.IbanDeletionRequest;
import it.pagopa.selfcare.pagopa.backoffice.model.ibanrequests.IbanDeletionRequests;
import it.pagopa.selfcare.pagopa.backoffice.repository.IbanDeletionRequestsRepository;
import it.pagopa.selfcare.pagopa.backoffice.util.IbanDeletionRequestStatus;
import it.pagopa.selfcare.pagopa.backoffice.util.StringUtils;
import it.pagopa.selfcare.pagopa.backoffice.util.Utility;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Stream;

@Slf4j
@Service
public class IbanDeletionRequestsService {

    private final IbanDeletionRequestsRepository ibanDeletionRequestsRepository;
    private final ApiConfigSelfcareIntegrationClient apiConfigSelfcareIntegrationClient;
    private final AsyncNotificationService asyncNotificationService;

    @Autowired
    public IbanDeletionRequestsService(IbanDeletionRequestsRepository ibanDeletionRequestsRepository, ApiConfigSelfcareIntegrationClient apiConfigSelfcareIntegrationClient, AsyncNotificationService asyncNotificationService) {
        this.ibanDeletionRequestsRepository = ibanDeletionRequestsRepository;
        this.apiConfigSelfcareIntegrationClient = apiConfigSelfcareIntegrationClient;
        this.asyncNotificationService = asyncNotificationService;
    }

    public IbanDeletionRequest createIbanDeletionRequest(String ciCode, String ibanValue, String scheduledExecutionDate) {

        final String maskedIbanForLogs = Utility.sanitizeLogParam(StringUtils.obfuscateKeepingLast4(ibanValue));
        final String sanitizedCiCodeForLogs = Utility.sanitizeLogParam(ciCode);
        final String sanitizedScheduledExecutionDateForLogs = Utility.sanitizeLogParam(scheduledExecutionDate);

        final Instant now = Instant.now();
        final LocalDate tomorrow = LocalDate.now().plusDays(1);

        return Optional.of(scheduledExecutionDate)
                .map(LocalDate::parse)
                .filter(date -> !date.isBefore(tomorrow))
                .or(() -> {
                    log.error("Scheduled execution date {} is not a valid future date (must be at least {}) for ciCode: {}",
                            sanitizedScheduledExecutionDateForLogs, tomorrow, sanitizedCiCodeForLogs);
                    throw new AppException(AppError.BAD_REQUEST, "Invalid scheduledExecutionDate");
                })
                .map(validScheduledExecutionDate -> {
                    ibanDeletionRequestsRepository.findByCreditorInstitutionCodeAndStatusAndIbanValue(
                            ciCode,
                            IbanDeletionRequestStatus.PENDING.toString(),
                            ibanValue
                    ).ifPresent(
                            request -> {
                                log.error("Pending deletion request already exists for ciCode: {} and IBAN: {}",
                                        sanitizedCiCodeForLogs, maskedIbanForLogs);
                                throw new AppException(AppError.CONFLICT, "Pending deletion request already exists for this IBAN");
                            }
                    );
                    return validScheduledExecutionDate;
                })
                .map(validScheduledExecutionDate -> {
                    log.info("Creating IBAN deletion request for ciCode: {}, ibanValue: {}, scheduled date: {}",
                            sanitizedCiCodeForLogs, maskedIbanForLogs, sanitizedScheduledExecutionDateForLogs);
                    apiConfigSelfcareIntegrationClient.getCreditorInstitutionIbans(ciCode, null)
                            .getIbanList()
                            .stream()
                            .filter(iban -> iban.getIban().equals(ibanValue))
                            .findFirst()
                            .orElseThrow(() -> {
                                log.error("IBAN {} not found for ciCode: {}", maskedIbanForLogs, sanitizedCiCodeForLogs);
                                return new AppException(AppError.BAD_GATEWAY, "Invalid Iban");
                            });

                    log.debug("IBAN {} validated successfully for ciCode: {}", maskedIbanForLogs, sanitizedCiCodeForLogs);
                    return validScheduledExecutionDate;
                })
                .map(validScheduledExecutionDate -> IbanDeletionRequestEntity.builder()
                        .id(UUID.randomUUID().toString())
                        .creditorInstitutionCode(ciCode)
                        .requestedAt(now.toString())
                        .updatedAt(now.toString())
                        .ibanValue(ibanValue)
                        .scheduledExecutionDate(validScheduledExecutionDate.atStartOfDay(ZoneOffset.UTC).toInstant().toString())
                        .status(IbanDeletionRequestStatus.PENDING)
                        .build())
                .map(entity -> {
                    log.debug("Saving IBAN deletion request with ID: {}", entity.getId());
                    return ibanDeletionRequestsRepository.save(entity);
                })
                .map(savedEntity -> {
                    log.info("IBAN deletion request created successfully with ID: {} for ciCode: {}, IBAN: {}",
                            savedEntity.getId(), sanitizedCiCodeForLogs, maskedIbanForLogs);
                    return IbanDeletionRequest.builder()
                            .id(savedEntity.getId())
                            .ciCode(savedEntity.getCreditorInstitutionCode())
                            .ibanValue(savedEntity.getIbanValue())
                            .scheduledExecutionDate(savedEntity.getScheduledExecutionDate())
                            .status(savedEntity.getStatus().name())
                            .build();
                })
                .map(ibanDeletionRequest -> {
                    log.info("Sending IBAN deletion request notification email for request with ID: {} for ciCode: {}, IBAN: {}",
                            ibanDeletionRequest.getId(), sanitizedCiCodeForLogs, maskedIbanForLogs);
                    try {
                        asyncNotificationService.notifyIbanDeletion(ibanDeletionRequest.getCiCode(),
                                ibanDeletionRequest.getIbanValue(), ibanDeletionRequest.getScheduledExecutionDate());
                    } catch (Exception e) {
                        log.error("Could not send IBAN deletion request notification email for request with ID: {} for ciCode: {}, IBAN: {}",
                                ibanDeletionRequest.getId(), sanitizedCiCodeForLogs, maskedIbanForLogs, e);
                    }
                    return ibanDeletionRequest;
                })
                .orElseThrow(() -> {
                    log.error("Failed to create IBAN deletion request for ciCode: {}, IBAN: {}",
                            sanitizedCiCodeForLogs, maskedIbanForLogs);
                    return new AppException(AppError.INTERNAL_SERVER_ERROR, "Generic Error");
                });
    }

    public IbanDeletionRequests getIbanDeletionRequests(String ciCode, String ibanValue, String status) {

        String maskedIbanForLogs = Optional.ofNullable(ibanValue)
            .map(v -> Utility.sanitizeLogParam(StringUtils.obfuscateKeepingLast4(v)))
            .orElse("null");
        String sanitizedCiCodeForLogs = Utility.sanitizeLogParam(ciCode);
        String sanitizedStatusForLogs = Utility.sanitizeLogParam(status);

        log.info("Retrieving IBAN deletion requests for ciCode: {}, ibanValue: {}, status: {}",
            sanitizedCiCodeForLogs, maskedIbanForLogs, sanitizedStatusForLogs);

        boolean allParametersPresent = Stream.of(
                Optional.of(ciCode).filter(c -> !c.isBlank()),
                Optional.ofNullable(ibanValue).filter(v -> !v.isBlank()),
                Optional.of(status).filter(s -> !s.isBlank())
            ).allMatch(Optional::isPresent);

        return allParametersPresent
            ? ibanDeletionRequestsRepository.findByCreditorInstitutionCodeAndStatusAndIbanValue(
                ciCode,
                IbanDeletionRequestStatus.valueOf(status).toString(),
                ibanValue)
            .map(entity -> IbanDeletionRequest.builder()
                .id(entity.getId())
                .ciCode(entity.getCreditorInstitutionCode())
                .ibanValue(entity.getIbanValue())
                .scheduledExecutionDate(entity.getScheduledExecutionDate())
                .status(entity.getStatus().name())
                .build())
            .map(List::of)
            .map(list -> {
                log.info("Found {} IBAN deletion request(s) for ciCode: {}, iban: {}, status: {}",
                    list.size(), sanitizedCiCodeForLogs, maskedIbanForLogs, sanitizedStatusForLogs);
                return IbanDeletionRequests.builder().requests(list).build();
            })
            .orElseGet(() -> {
                log.info("No IBAN deletion request found for ciCode: {}, iban: {}, status: {}",
                    sanitizedCiCodeForLogs, maskedIbanForLogs, sanitizedStatusForLogs);
                return IbanDeletionRequests.builder().requests(List.of()).build();
            })
            : IbanDeletionRequests.builder().requests(List.of()).build();
    }

    public void cancelIbanDeletionRequest(String ciCode, String id) {

        final String sanitizedCiCodeForLogs = Utility.sanitizeLogParam(ciCode);
        final String sanitizedIdForLogs = Utility.sanitizeLogParam(id);

        log.info("Canceling IBAN deletion request with ID: {} for ciCode: {}", sanitizedIdForLogs,  sanitizedCiCodeForLogs);

        ibanDeletionRequestsRepository.findById(id)
                .filter(request -> request.getStatus() == IbanDeletionRequestStatus.PENDING)
                .map(request -> {
                    request.setStatus(IbanDeletionRequestStatus.CANCELED);
                    request.setUpdatedAt(Instant.now().toString());
                    return request;
                })
                .map(ibanDeletionRequestsRepository::save)
                .map(request -> {
                    final String maskedIbanForLogs = Utility.sanitizeLogParam(request.getIbanValue());
                    log.info("Sending IBAN restore request notification email for request with ID: {} for ciCode: {}, IBAN: {}",
                            request.getId(), sanitizedCiCodeForLogs, maskedIbanForLogs);
                    try {
                        asyncNotificationService.notifyIbanRestore(ciCode, request.getIbanValue());
                    } catch (Exception e) {
                        log.error("Could not send IBAN restore request notification email for request with ID: {} for ciCode: {}, IBAN: {}",
                                request.getId(), sanitizedCiCodeForLogs, maskedIbanForLogs, e);
                    }
                    return request;
                })
                .ifPresentOrElse(
                        savedRequest -> log.info("IBAN deletion request with ID: {} successfully canceled for ciCode: {}", sanitizedIdForLogs, sanitizedCiCodeForLogs),
                        () -> {
                            log.error("IBAN deletion request with ID: {} not found or not cancellable for ciCode: {}", sanitizedIdForLogs, sanitizedCiCodeForLogs);
                            throw new AppException(AppError.INTERNAL_SERVER_ERROR);
                        }
                );
    }
}
