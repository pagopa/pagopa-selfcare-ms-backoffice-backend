package it.pagopa.selfcare.pagopa.backoffice.service;

import it.pagopa.selfcare.pagopa.backoffice.client.ApiConfigClient;
import it.pagopa.selfcare.pagopa.backoffice.client.ApiConfigSelfcareIntegrationClient;
import it.pagopa.selfcare.pagopa.backoffice.client.ExternalApiClient;
import it.pagopa.selfcare.pagopa.backoffice.entity.BrokerIbanEntity;
import it.pagopa.selfcare.pagopa.backoffice.entity.BrokerIbansEntity;
import it.pagopa.selfcare.pagopa.backoffice.exception.AppError;
import it.pagopa.selfcare.pagopa.backoffice.exception.AppException;
import it.pagopa.selfcare.pagopa.backoffice.model.iban.*;
import it.pagopa.selfcare.pagopa.backoffice.repository.BrokerIbansRepository;
import it.pagopa.selfcare.pagopa.backoffice.util.Utility;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static it.pagopa.selfcare.pagopa.backoffice.util.Utility.deNull;
import static org.springframework.util.ObjectUtils.isEmpty;

@Slf4j
@Service
public class IbanService {

    private final ApiConfigClient apiConfigClient;

    private final ApiConfigSelfcareIntegrationClient apiConfigSelfcareIntegrationClient;

    private final ExternalApiClient externalApiClient;

    private final ModelMapper modelMapper;

    @Value("${ibans.export-csv.preview_size}")
    private Integer ibanExportCSVPreviewSize;

    @Autowired
    private BrokerIbansRepository brokerIbansRepository;

    @Autowired
    public IbanService(ApiConfigClient apiConfigClient, ApiConfigSelfcareIntegrationClient apiConfigSelfcareIntegrationClient, ExternalApiClient externalApiClient, ModelMapper modelMapper) {
        this.apiConfigClient = apiConfigClient;
        this.apiConfigSelfcareIntegrationClient = apiConfigSelfcareIntegrationClient;
        this.externalApiClient = externalApiClient;
        this.modelMapper = modelMapper;
    }


    public Ibans getIban(String ciCode, String labelName) {
        return apiConfigClient.getCreditorInstitutionIbans(ciCode, labelName);
    }

    public Iban createIban(String ciCode, IbanCreate requestDto) {
        IbanCreateApiconfig body = modelMapper.map(requestDto, IbanCreateApiconfig.class);
        IbanCreateApiconfig dto = apiConfigClient.createCreditorInstitutionIbans(ciCode, body);
        return modelMapper.map(dto, Iban.class);
    }

    public Iban updateIban(String ciCode, String ibanValue, IbanCreate dto) {
        // updating labels, owned by other CI, related to the passed IBAN
        if(!isEmpty(dto.getLabels())) {
            Ibans ibansEnhanced = apiConfigClient.getCreditorInstitutionIbans(ciCode, dto.getLabels().get(0).getName());
            if(ibansEnhanced != null && !ObjectUtils.isEmpty(ibansEnhanced.getIbanList())) {
                ibansEnhanced.getIbanList().forEach(iban -> {
                    IbanCreateApiconfig ibanCreate = modelMapper.map(iban, IbanCreateApiconfig.class);
                    List<IbanLabel> ibanLabelList = ibanCreate.getLabels()
                            .stream()
                            .filter(f -> !(f.getName().equals(dto.getLabels().get(0).getName())))
                            .collect(Collectors.toList());
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

    /**
     * This method is used for exporting a set of IBANs to a CSV format.
     * First, the system gets all the delegations for the input brokerCode.
     * IBAN details are formatted into a CSV row structure.
     *
     * @param brokerCode The broker code used to retrieve delegations and hence the IBANs.
     * @return The byte array representation of the generated CSV file.
     */
    public byte[] exportIbansToCsv(String brokerCode) {
        BrokerIbansEntity ibans = brokerIbansRepository.findByBrokerCode(brokerCode)
                .orElseThrow(() -> new AppException(AppError.BROKER_NOT_FOUND, brokerCode));

        List<String> headers = Arrays.asList("denominazioneEnte", "codiceFiscale", "iban", "stato", "dataAttivazioneIban", "descrizione", "etichetta");
        return Utility.createCsv(headers, mapToCsv(ibans.getIbans()));
    }


    /**
     * This method processes a list of IbanCsv objects, mapping them to a List of lists of String type.
     *
     * @param ibans The list of IbanCsv objects to be processed.
     * @return The list of lists after mapping and ensuring no null values.
     */
    private List<List<String>> mapToCsv(List<BrokerIbanEntity> ibans) {
        return ibans.stream()
                .map(elem -> Arrays.asList(deNull(elem.getCiName()),
                        deNull(elem.getCiFiscalCode()),
                        deNull(elem.getIban()),
                        deNull(elem.getStatus()),
                        deNull(elem.getValidityDate()),
                        deNull(elem.getDescription()),
                        deNull(elem.getLabel())
                ))
                .collect(Collectors.toList());
    }
}
