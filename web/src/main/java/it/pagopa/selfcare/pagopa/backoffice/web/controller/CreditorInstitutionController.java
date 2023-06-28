package it.pagopa.selfcare.pagopa.backoffice.web.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import it.pagopa.selfcare.pagopa.backoffice.connector.model.broker.BrokerDetails;
import it.pagopa.selfcare.pagopa.backoffice.connector.model.creditorInstitution.CreditorInstitutionDetails;
import it.pagopa.selfcare.pagopa.backoffice.core.ApiConfigService;
import it.pagopa.selfcare.pagopa.backoffice.web.model.creditorInstituions.CreditorInstitutionAndBrokerDto;
import it.pagopa.selfcare.pagopa.backoffice.web.model.creditorInstituions.CreditorInstitutionDetailsResource;
import it.pagopa.selfcare.pagopa.backoffice.web.model.creditorInstituions.CreditorInstitutionDto;
import it.pagopa.selfcare.pagopa.backoffice.web.model.creditorInstituions.UpdateCreditorInstitutionDto;
import it.pagopa.selfcare.pagopa.backoffice.web.model.mapper.BrokerMapper;
import it.pagopa.selfcare.pagopa.backoffice.web.model.mapper.CreditorInstitutionMapper;
import it.pagopa.selfcare.pagopa.backoffice.web.model.stations.BrokerDto;
import lombok.extern.slf4j.Slf4j;
import org.mapstruct.factory.Mappers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping(value = "/creditor-institutions", produces = MediaType.APPLICATION_JSON_VALUE)
@Api(tags = "creditor-institutions")
public class CreditorInstitutionController {

    CreditorInstitutionMapper mapper = Mappers.getMapper(CreditorInstitutionMapper.class);
    private final ApiConfigService apiConfigService;

    @Autowired
    public CreditorInstitutionController(ApiConfigService apiConfigService) {
        this.apiConfigService = apiConfigService;
    }

    @PostMapping(value = "", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    @ApiOperation(value = "", notes = "${swagger.api.creditor-institutions.createCreditorInstitution}")
    public CreditorInstitutionDetailsResource createCreditorInstitution(@RequestBody @NotNull CreditorInstitutionDto dto){
        log.trace("createCreditorInstitution start");
        String xRequestId = UUID.randomUUID().toString();
        log.debug("createCreditorInstitution dto = {}, xRequestId = {}", dto, xRequestId);
        CreditorInstitutionDetails creditorInstitution = mapper.fromDto(dto);
        CreditorInstitutionDetails created = apiConfigService.createCreditorInstitution(creditorInstitution, xRequestId);
        CreditorInstitutionDetailsResource result = mapper.toResource(created);
        log.debug("createCreditorInstitution result = {}", result);
        log.trace("createCreditorInstitution end");
        return result;
    }

    @PostMapping(value = "creditor-institution-and-broker", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    @ApiOperation(value = "", notes = "${swagger.api.creditor-institutions.createCreditorInstitutionAndBroker}")
    public CreditorInstitutionDetailsResource createCreditorInstitutionAndBroker(@RequestBody @NotNull CreditorInstitutionAndBrokerDto dto){
        log.trace("createCreditorInstitutionAndBroker start");
        String xRequestId = UUID.randomUUID().toString();
        log.debug("createCreditorInstitutionAndBroker dto = {}, xRequestId = {}", dto, xRequestId);
        CreditorInstitutionDto creditorInstitutionDto = dto.getCreditorInstitutionDto();
        BrokerDto brokerDto = dto.getBrokerDto();
        CreditorInstitutionDetails creditorInstitution = mapper.fromDto(creditorInstitutionDto);
        CreditorInstitutionDetails created = apiConfigService.createCreditorInstitution(creditorInstitution, xRequestId);
        apiConfigService.createBroker(BrokerMapper.fromDto(brokerDto), xRequestId);
        CreditorInstitutionDetailsResource result = mapper.toResource(created);
        log.debug("createCreditorInstitution result = {}", result);
        log.trace("createCreditorInstitution end");
        return result;
    }

    @GetMapping(value = "/{ecCode}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    @ApiOperation(value = "", notes = "${swagger.api.creditor-institutions.getCreditorInstitutionDetails}")
    public CreditorInstitutionDetailsResource getCreditorInstitutionDetails(@ApiParam("${swagger.request.ecCode}")
                                                                                @PathVariable("ecCode")String ecCode){
        log.trace("getCreditorInstitutionDetails start");
        String xRequestId = UUID.randomUUID().toString();
        log.debug("getCreditorInstitutionDetails ecCode = {}, xRequestId = {}", ecCode, xRequestId);
        CreditorInstitutionDetails creditorInstitutionDetails = apiConfigService.getCreditorInstitutionDetails(ecCode, xRequestId);
        CreditorInstitutionDetailsResource result = mapper.toResource(creditorInstitutionDetails);
        log.debug("getCreditorInstitutionDetails result = {}", result);
        log.trace("getCreditorInstitutionDetails end");
        return result;
    }

    @PutMapping(value = "/{ecCode}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    @ApiOperation(value = "", notes = "${swagger.api.creditor-institution.updateCreditorInstitutionDetails}")
    public CreditorInstitutionDetailsResource updateCreditorInstitutionDetails(@ApiParam("${swagger.request.ecCode}")
                                                     @PathVariable("ecCode") String ecCode,
                                                 @RequestBody @Valid UpdateCreditorInstitutionDto dto
                                                 ){
        log.trace("updateCreditorInstitutionDetails start");
        String xRequestId = UUID.randomUUID().toString();
        log.debug("updateCreditorInstitutionDetails dto = {}, xRequestId = {}", dto, xRequestId);
        CreditorInstitutionDetails creditorInstitution = mapper.fromDto(dto);
        CreditorInstitutionDetails created = apiConfigService.updateCreditorInstitutionDetails(ecCode, creditorInstitution, xRequestId);
        CreditorInstitutionDetailsResource result = mapper.toResource(created);
        log.debug("updateCreditorInstitutionDetails result = {}", result);
        log.trace("updateCreditorInstitutionDetails end");
        return result;
    }



}
