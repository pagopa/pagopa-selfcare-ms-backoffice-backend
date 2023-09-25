package it.pagopa.selfcare.pagopa.backoffice.web.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import it.pagopa.selfcare.pagopa.backoffice.connector.model.creditorInstitution.IbanCreate;
import it.pagopa.selfcare.pagopa.backoffice.connector.model.creditorInstitution.IbanLabel;
import it.pagopa.selfcare.pagopa.backoffice.connector.model.creditorInstitution.IbansEnhanced;
import it.pagopa.selfcare.pagopa.backoffice.core.ApiConfigService;
import it.pagopa.selfcare.pagopa.backoffice.web.model.creditorInstituions.IbanCreateRequestDto;
import it.pagopa.selfcare.pagopa.backoffice.web.model.creditorInstituions.IbanResource;
import it.pagopa.selfcare.pagopa.backoffice.web.model.creditorInstituions.IbansResource;
import it.pagopa.selfcare.pagopa.backoffice.web.model.mapper.CreditorInstitutionMapper;
import lombok.extern.slf4j.Slf4j;
import org.mapstruct.factory.Mappers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import static org.springframework.util.ObjectUtils.isEmpty;

@Slf4j
@RestController
@RequestMapping(value = "/creditorinstitutions/ibans", produces = MediaType.APPLICATION_JSON_VALUE)
@Api(tags = "Ibans")
public class IbanController {

    CreditorInstitutionMapper mapper = Mappers.getMapper(CreditorInstitutionMapper.class);
    private final ApiConfigService apiConfigService;

    @Autowired
    public IbanController(ApiConfigService apiConfigService) {
        this.apiConfigService = apiConfigService;
    }

    @GetMapping(value = "/{creditorinstitutioncode}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    @ApiOperation(value = "", notes = "${swagger.api.creditor-institutions.ibans}")
    public IbansResource getCreditorInstitutionIbans(@ApiParam("${swagger.request.ecCode}")
                                                     @PathVariable("creditorinstitutioncode") String creditorinstitutioncode,
                                                     @ApiParam("${swagger.api.creditor-institutions.ibans.labels.name}")
                                                     @RequestParam(required = false) String labelName
                                                     ){
        
        

        IbansEnhanced ibans = apiConfigService.getCreditorInstitutionIbans(creditorinstitutioncode,labelName);

        IbansResource resource = mapper.toResource(ibans);

        
        
        return resource;
    }

    @PostMapping(value = "/create", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    @ApiOperation(value = "", notes = "${swagger.api.creditor-institutions.ibans.create}")
    public IbanResource createCreditorInstitutionIbans(@RequestBody @NotNull IbanCreateRequestDto requestDto){
        
        

        IbanCreate ibanCreate = mapper.fromDto(requestDto);
        IbanCreate ibans = apiConfigService.createCreditorInstitutionIbans(requestDto.getCreditorInstitutionCode(), ibanCreate);

        IbanResource resource = mapper.toResource(ibans);

        
        
        return resource;
    }

    @DeleteMapping(value = "/{creditorinstitutioncode}/delete/{ibanValue}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    @ApiOperation(value = "", notes = "${swagger.api.creditor-institutions.ibans.delete}")
    public void deleteCreditorInstitutionIbans(@ApiParam("${swagger.request.ecCode}")
                                               @PathVariable("creditorinstitutioncode") String creditorinstitutioncode,
                                               @ApiParam("${swagger.request.pspCode}")
                                               @PathVariable("ibanValue") String ibanValue){
        
        
        apiConfigService.deleteCreditorInstitutionIbans(creditorinstitutioncode, ibanValue);
        
    }

    @PutMapping(value = "/update", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    @ApiOperation(value = "", notes = "${swagger.api.creditor-institutions.ibans.put}")
    public IbanResource updateCreditorInstitutionIbans(@RequestBody @NotNull IbanCreateRequestDto requestDto){
        
        

        if (!isEmpty(requestDto.getLabels())) {
            IbansEnhanced ibansEnhanced = apiConfigService.getCreditorInstitutionIbans(requestDto.getCreditorInstitutionCode(), requestDto.getLabels().get(0).getName());
            if (ibansEnhanced != null && !isEmpty(ibansEnhanced.getIbanList())) {
                ibansEnhanced.getIbanList().forEach(iban -> {
                    IbanCreate ibanCreate = mapper.toIbanCreate(iban);
                    List<IbanLabel> ibanLabelList = ibanCreate.getLabels().stream().filter(f -> !(f.getName().equals(requestDto.getLabels().get(0).getName()))).collect(Collectors.toList());
                    ibanCreate.setLabels(ibanLabelList);
                    apiConfigService.updateCreditorInstitutionIbans(requestDto.getCreditorInstitutionCode(), ibanCreate);
                });
            }
        }

        IbanCreate ibanCreate = mapper.fromDto(requestDto);
        IbanCreate ibans = apiConfigService.updateCreditorInstitutionIbans(requestDto.getCreditorInstitutionCode(), ibanCreate);

        IbanResource resource = mapper.toResource(ibans);

        
        
        return resource;
    }

}
