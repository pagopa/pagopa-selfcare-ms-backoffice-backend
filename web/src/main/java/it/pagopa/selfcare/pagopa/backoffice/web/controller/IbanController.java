package it.pagopa.selfcare.pagopa.backoffice.web.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import it.pagopa.selfcare.pagopa.backoffice.connector.model.creditorInstitution.IbansDetails;
import it.pagopa.selfcare.pagopa.backoffice.core.ApiConfigService;
import it.pagopa.selfcare.pagopa.backoffice.web.model.creditorInstituions.IbanRequestDto;
import it.pagopa.selfcare.pagopa.backoffice.web.model.creditorInstituions.IbansResource;
import it.pagopa.selfcare.pagopa.backoffice.web.model.mapper.CreditorInstitutionMapper;
import lombok.extern.slf4j.Slf4j;
import org.mapstruct.factory.Mappers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotNull;
import java.util.UUID;

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

    @GetMapping(value = "", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    @ApiOperation(value = "", notes = "${swagger.api.creditor-institutions.ibans}")
    public IbansResource getCreditorInstitutionIbans(@RequestBody @NotNull IbanRequestDto requestDto){
        log.trace("getCreditorInstitutionsIbans start");
        String xRequestId = UUID.randomUUID().toString();
        log.debug("getCreditorInstitutionsIbans ecCode = {}, xRequestId = {}", requestDto.getCreditorInstitutionCode(), xRequestId);

        IbansDetails ibans = apiConfigService.getCreditorInstitutionIbans(requestDto.getCreditorInstitutionCode(), xRequestId);

        IbansResource resource = mapper.toResource(ibans);

        log.debug("getCreditorInstitutionsIbans result = {}", resource);
        log.trace("getCreditorInstitutionsIbans end");
        return resource;
    }

}
