package it.pagopa.selfcare.pagopa.backoffice.service;

import it.pagopa.selfcare.pagopa.backoffice.client.ApiConfigClient;
import it.pagopa.selfcare.pagopa.backoffice.client.ApiConfigSelfcareIntegrationClient;
import it.pagopa.selfcare.pagopa.backoffice.client.ExternalApiClient;
import it.pagopa.selfcare.pagopa.backoffice.model.iban.*;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.util.List;
import java.util.stream.Collectors;

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
        return apiConfigClient.getCreditorInstitutionIbans(ciCode, labelName);
    }

    public Iban createIban(String ciCode, IbanCreate requestDto) {
        IbanCreateApiconfig body = modelMapper.map(requestDto, IbanCreateApiconfig.class);
        IbanCreateApiconfig dto = apiConfigClient.createCreditorInstitutionIbans(ciCode, body);
        return modelMapper.map(dto, Iban.class);
    }

    public Iban updateIban(String ciCode, String ibanValue, IbanCreate dto) {
        // updating labels, owned by other CI, related to the passed IBAN
        if (!isEmpty(dto.getLabels())) {
            Ibans ibansEnhanced = apiConfigClient.getCreditorInstitutionIbans(ciCode, dto.getLabels().get(0).getName());
            if (ibansEnhanced != null && !ObjectUtils.isEmpty(ibansEnhanced.getIbanList())) {
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

}
