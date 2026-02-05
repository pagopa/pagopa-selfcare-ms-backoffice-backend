package it.pagopa.selfcare.pagopa.backoffice.service;

import it.pagopa.selfcare.pagopa.backoffice.audit.AuditScope;
import it.pagopa.selfcare.pagopa.backoffice.client.ApiConfigClient;
import it.pagopa.selfcare.pagopa.backoffice.client.ApiConfigSelfcareIntegrationClient;
import it.pagopa.selfcare.pagopa.backoffice.client.ExternalApiClient;
import it.pagopa.selfcare.pagopa.backoffice.model.connector.creditorinstitution.CreditorInstitutionDetails;
import it.pagopa.selfcare.pagopa.backoffice.model.iban.*;
import it.pagopa.selfcare.pagopa.backoffice.util.Utility;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.stream.Collectors;

import static it.pagopa.selfcare.pagopa.backoffice.util.IbanOperationsCsvUtil.convertOperationsToCsv;
import static org.springframework.util.ObjectUtils.isEmpty;

@Slf4j
@Service
public class IbanService {

    private final ApiConfigClient apiConfigClient;

    private final ApiConfigSelfcareIntegrationClient apiConfigSelfcareIntegrationClient;

    private final ExternalApiClient externalApiClient;

    private final ModelMapper modelMapper;

    @Autowired
    public IbanService(ApiConfigClient apiConfigClient, ApiConfigSelfcareIntegrationClient apiConfigSelfcareIntegrationClient, ExternalApiClient externalApiClient, ModelMapper modelMapper) {
        this.apiConfigClient = apiConfigClient;
        this.apiConfigSelfcareIntegrationClient = apiConfigSelfcareIntegrationClient;
        this.externalApiClient = externalApiClient;
        this.modelMapper = modelMapper;
    }


    public Ibans getIban(String ciCode, String labelName) {
        return apiConfigSelfcareIntegrationClient.getCreditorInstitutionIbans(ciCode, labelName);
    }

    public Iban createIban(String ciCode, IbanCreate requestDto) {
        IbanCreateApiconfig body = modelMapper.map(requestDto, IbanCreateApiconfig.class);
        IbanCreateApiconfig dto = apiConfigClient.createCreditorInstitutionIbans(ciCode, body);
        return modelMapper.map(dto, Iban.class);
    }

    public Iban updateIban(String ciCode, String ibanValue, IbanCreate dto) {
        // updating labels, owned by other CI, related to the passed IBAN
        if(!isEmpty(dto.getLabels())) {
            Ibans ibansEnhanced = apiConfigSelfcareIntegrationClient.getCreditorInstitutionIbans(ciCode, dto.getLabels().get(0).getName());
            if(ibansEnhanced != null && !ObjectUtils.isEmpty(ibansEnhanced.getIbanList())) {
                ibansEnhanced.getIbanList().forEach(iban -> {
                    IbanCreateApiconfig ibanCreate = modelMapper.map(iban, IbanCreateApiconfig.class);
                    List<IbanLabel> ibanLabelList = ibanCreate.getLabels()
                            .stream()
                            .filter(f -> !(f.getName().equals(dto.getLabels().get(0).getName())))
                            .toList();
                    ibanCreate.setLabels(ibanLabelList);
                    apiConfigClient.updateCreditorInstitutionIbans(ciCode, iban.getIban(), ibanCreate);
                });
            }
        }
        // update IBAN values
        IbanCreateApiconfig updatedDto = apiConfigClient.updateCreditorInstitutionIbans(ciCode, ibanValue, modelMapper.map(dto, IbanCreateApiconfig.class));
        return modelMapper.map(updatedDto, Iban.class);
    }

    public void deleteIban(String ciCode, String ibanValue) {
        apiConfigClient.deleteCreditorInstitutionIbans(ciCode, ibanValue);
    }

    public void processBulkIbanOperations(String ciCode, List<IbanOperation> operations) {

        final String sanitizedCiCodeForLogs = Utility.sanitizeLogParam(ciCode);

        log.info("Processing bulk IBAN operations for ciCode: {}, total operations: {}", sanitizedCiCodeForLogs, operations.size());

        log.debug("Retrieve CI business name for: {}", sanitizedCiCodeForLogs);
        CreditorInstitutionDetails creditorInstitutionDetails = apiConfigClient.getCreditorInstitutionDetails(ciCode);
        String ciName = creditorInstitutionDetails.getBusinessName();

        log.debug("Convert {} operations to CSV format", operations.size());
        MultipartFile csvData = convertOperationsToCsv(ciCode, ciName, operations);

        log.debug("Calling API to create bulk IBANs for CI: {} with CSV data of {} bytes",
                sanitizedCiCodeForLogs, csvData.getSize());
        apiConfigClient.createCreditorInstitutionIbansBulk(csvData);

        operations.stream()
                .collect(Collectors.groupingBy(
                        IbanOperation::getType,
                        Collectors.counting()))
                .forEach((operation, count) ->
                        log.info("Operation {}: {} IBANs", operation, count));

        try (var audit = AuditScope.enable()) {
            log.info("Bulk IBAN operations completed successfully for CI: {}, operations: {}",
                    sanitizedCiCodeForLogs, operations);
        }
    }
}
