package it.pagopa.selfcare.pagopa.backoffice.web.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import it.pagopa.selfcare.pagopa.backoffice.core.ApiConfigService;
import it.pagopa.selfcare.pagopa.backoffice.web.model.creditorInstituions.CreditorInstitutionDetailsResource;
import it.pagopa.selfcare.pagopa.backoffice.web.model.creditorInstituions.CreditorInstitutionDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotNull;

@Slf4j
@RestController
@RequestMapping(value = "/creditor-institutions", produces = MediaType.APPLICATION_JSON_VALUE)
@Api(tags = "creditor-institutions")
public class CreditorInstitutionController {

    private final ApiConfigService apiConfigService;

    @Autowired
    public CreditorInstitutionController(ApiConfigService apiConfigService) {
        this.apiConfigService = apiConfigService;
    }

    @PostMapping(value = "", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    @ApiOperation(value = "", notes = "${swagger.api.creditor-institutions.createCreditorInstitution}")
    public CreditorInstitutionDetailsResource createCreditorInstitution(@RequestBody @NotNull CreditorInstitutionDto dto){

        return null;
    }

}
