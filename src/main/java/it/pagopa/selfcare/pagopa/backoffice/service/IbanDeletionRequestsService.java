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

        final String maskedIbanForLogs = StringUtils.obfuscateKeepingLast4(ibanValue);
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
                .orElseThrow(() -> {
                    log.error("Failed to create IBAN deletion request for ciCode: {}, IBAN: {}",
                            sanitizedCiCodeForLogs, maskedIbanForLogs);
                    return new AppException(AppError.INTERNAL_SERVER_ERROR, "Generic Error");
                });
    }

    public IbanDeletionRequests getIbanDeletionRequests(String ciCode, String ibanValue) {

        String maskedIbanForLogs = StringUtils.obfuscateKeepingLast4(ibanValue);
        String sanitizedCiCodeForLogs = Utility.sanitizeLogParam(ciCode);

        log.info("Retrieving IBAN deletion requests for ciCode: {}, ibanValue: {}", sanitizedCiCodeForLogs, maskedIbanForLogs);

        return Optional.ofNullable(ibanValue)
                .filter(value -> !value.isBlank())
                .map(value -> ibanDeletionRequestsRepository.findByCreditorInstitutionCodeAndIbanValue(ciCode, value)
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
                            log.info("Found {} IBAN deletion requests for ciCode: {}, iban: {}", requests.size(), sanitizedCiCodeForLogs, maskedIbanForLogs);
                            return IbanDeletionRequests.builder()
                                    .requests(requests)
                                    .build();
                        }
                ));
    }

    public void cancelIbanDeletionRequest(String ciCode, String id) {

        String sanitizedCiCodeForLogs = Utility.sanitizeLogParam(ciCode);
        String sanitizedIdForLogs = Utility.sanitizeLogParam(id);

        log.info("Canceling IBAN deletion request with ID: {} for ciCode: {}", sanitizedIdForLogs,  sanitizedCiCodeForLogs);

        ibanDeletionRequestsRepository.findById(id)
                .filter(request -> request.getStatus() == IbanDeletionRequestStatus.PENDING)
                .map(request -> {
                    request.setStatus(IbanDeletionRequestStatus.CANCELED);
                    request.setUpdatedAt(Instant.now().toString());
                    return request;
                })
                .map(ibanDeletionRequestsRepository::save)
                .ifPresentOrElse(
                        savedRequest -> log.info("IBAN deletion request with ID: {} successfully canceled for ciCode: {}", sanitizedIdForLogs, sanitizedCiCodeForLogs),
                        () -> {
                            log.error("IBAN deletion request with ID: {} not found or not cancellable for ciCode: {}", sanitizedIdForLogs, sanitizedCiCodeForLogs);
                            throw new AppException(AppError.INTERNAL_SERVER_ERROR);
                        }
                );
    }
}
