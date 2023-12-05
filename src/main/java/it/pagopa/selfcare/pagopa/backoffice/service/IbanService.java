package it.pagopa.selfcare.pagopa.backoffice.service;

import it.pagopa.selfcare.pagopa.backoffice.client.ApiConfigClient;
import it.pagopa.selfcare.pagopa.backoffice.model.iban.IbanCreate;
import it.pagopa.selfcare.pagopa.backoffice.model.iban.Iban;
import it.pagopa.selfcare.pagopa.backoffice.model.iban.IbanLabel;
import it.pagopa.selfcare.pagopa.backoffice.model.iban.Ibans;
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

    @Autowired
    private ApiConfigClient apiConfigClient;

    @Autowired
    private ModelMapper modelMapper;

    public Ibans getIban(String ciCode, String labelName) {
        return apiConfigClient.getCreditorInstitutionIbans(ciCode, labelName);
    }

    public Iban createIban(String ciCode, IbanCreate requestDto) {
        IbanCreate dto = apiConfigClient.createCreditorInstitutionIbans(ciCode, requestDto);
        return modelMapper.map(dto, Iban.class);
    }

    public Iban updateIban(String ciCode, String ibanValue, IbanCreate dto) {
        // updating labels, owned by other CI, related to the passed IBAN
        if (!isEmpty(dto.getLabels())) {
            Ibans ibansEnhanced = apiConfigClient.getCreditorInstitutionIbans(ciCode, dto.getLabels().get(0).getName());
            if (ibansEnhanced != null && !ObjectUtils.isEmpty(ibansEnhanced.getIbanList())) {
                ibansEnhanced.getIbanList().forEach(iban -> {
                    IbanCreate ibanCreate = modelMapper.map(iban, IbanCreate.class);
                    List<IbanLabel> ibanLabelList = ibanCreate.getLabels().stream()
                            .filter(f -> !(f.getName().equals(dto.getLabels().get(0).getName())))
                            .collect(Collectors.toList());
                    ibanCreate.setLabels(ibanLabelList);
                    apiConfigClient.updateCreditorInstitutionIbans(ciCode, iban.getIban(), ibanCreate);
                });
            }
        }
        // update IBAN values
        IbanCreate updatedDto = apiConfigClient.updateCreditorInstitutionIbans(ciCode, ibanValue, dto);
        return modelMapper.map(updatedDto, Iban.class);
    }

    public void deleteIban(String ciCode, String ibanValue) {
        apiConfigClient.deleteCreditorInstitutionIbans(ciCode, ibanValue);
    }
}
