package it.pagopa.selfcare.pagopa.backoffice.web.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import it.pagopa.selfcare.pagopa.backoffice.connector.model.broker.BrokerDetails;
import it.pagopa.selfcare.pagopa.backoffice.connector.model.creditorInstitution.CreditorInstitutionAssociatedCodeList;
import it.pagopa.selfcare.pagopa.backoffice.connector.model.creditorInstitution.CreditorInstitutionDetails;
import it.pagopa.selfcare.pagopa.backoffice.connector.model.creditorInstitution.CreditorInstitutions;
import it.pagopa.selfcare.pagopa.backoffice.core.ApiConfigSelfcareIntegrationService;
import it.pagopa.selfcare.pagopa.backoffice.core.ApiConfigService;
import it.pagopa.selfcare.pagopa.backoffice.web.model.creditorinstituions.*;
import it.pagopa.selfcare.pagopa.backoffice.web.model.mapper.BrokerMapper;
import it.pagopa.selfcare.pagopa.backoffice.web.model.mapper.CreditorInstitutionMapper;
import it.pagopa.selfcare.pagopa.backoffice.web.model.stations.BrokerDetailsResource;
import it.pagopa.selfcare.pagopa.backoffice.web.model.stations.BrokerDto;
import lombok.extern.slf4j.Slf4j;
import org.mapstruct.factory.Mappers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

@Slf4j
@RestController
@RequestMapping(value = "/creditor-institutions", produces = MediaType.APPLICATION_JSON_VALUE)
@Api(tags = "creditor-institutions")
public class CreditorInstitutionController {

    CreditorInstitutionMapper mapper = Mappers.getMapper(CreditorInstitutionMapper.class);
    private final ApiConfigService apiConfigService;

    private final ApiConfigSelfcareIntegrationService apiConfigSelfcareIntegrationService;

    @Autowired
    public CreditorInstitutionController(ApiConfigService apiConfigService, ApiConfigSelfcareIntegrationService apiConfigSelfcareIntegrationService) {
        this.apiConfigService = apiConfigService;
        this.apiConfigSelfcareIntegrationService = apiConfigSelfcareIntegrationService;
    }


    @PostMapping(value = "", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    @ApiOperation(value = "", notes = "${swagger.api.creditor-institutions.createCreditorInstitution}")
    public CreditorInstitutionDetailsResource createCreditorInstitution(@RequestBody @NotNull CreditorInstitutionDto dto) {


        CreditorInstitutionDetails creditorInstitution = mapper.fromDto(dto);
        CreditorInstitutionDetails created = apiConfigService.createCreditorInstitution(creditorInstitution);

        return mapper.toResource(created);
    }

    @PostMapping(value = "creditor-institution-and-broker", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    @ApiOperation(value = "", notes = "${swagger.api.creditor-institutions.createCreditorInstitutionAndBroker}")
    public CreditorInstitutionDetailsResource createCreditorInstitutionAndBroker(@RequestBody @NotNull CreditorInstitutionAndBrokerDto dto) {


        CreditorInstitutionDto creditorInstitutionDto = dto.getCreditorInstitutionDto();
        BrokerDto brokerDto = dto.getBrokerDto();
        CreditorInstitutionDetails creditorInstitution = mapper.fromDto(creditorInstitutionDto);
        CreditorInstitutionDetails created = apiConfigService.createCreditorInstitution(creditorInstitution);
        apiConfigService.createBroker(BrokerMapper.fromDto(brokerDto));

        return mapper.toResource(created);
    }

    @GetMapping(value = "/{ecCode}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    @ApiOperation(value = "", notes = "${swagger.api.creditor-institutions.getCreditorInstitutionDetails}")
    public CreditorInstitutionDetailsResource getCreditorInstitutionDetails(@ApiParam("${swagger.request.ecCode}")
                                                                            @PathVariable("ecCode") String ecCode) {


        CreditorInstitutionDetails creditorInstitutionDetails = apiConfigService.getCreditorInstitutionDetails(ecCode);


        return mapper.toResource(creditorInstitutionDetails);
    }

    @GetMapping(value = "/get-creditor-institutions", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    @ApiOperation(value = "", notes = "${swagger.api.creditor-institutions.getCreditorInstitutions}")
    public CreditorInstitutionsResource getCreditorInstitutions(@ApiParam("${swagger.request.limit}")
                                                                @RequestParam(required = false, defaultValue = "50") Integer limit,
                                                                @ApiParam("${swagger.request.page}")
                                                                @RequestParam Integer page,
                                                                @ApiParam("${swagger.request.ecCode}")
                                                                @RequestParam(required = false, value = "ecCode") String ecCode,
                                                                @ApiParam("${swagger.request.name}")
                                                                @RequestParam(required = false, value = "name") String name,
                                                                @ApiParam("${swagger.request.sorting}")
                                                                @RequestParam(required = false, value = "sorting") String sorting) {


        CreditorInstitutions creditorInstitutions = apiConfigService.getCreditorInstitutions(limit, page, ecCode, name, sorting);

        return mapper.toResource(creditorInstitutions);
    }

    @PutMapping(value = "/{ecCode}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    @ApiOperation(value = "", notes = "${swagger.api.creditor-institution.updateCreditorInstitutionDetails}")
    public CreditorInstitutionDetailsResource updateCreditorInstitutionDetails(@ApiParam("${swagger.request.ecCode}")
                                                                               @PathVariable("ecCode") String ecCode,
                                                                               @RequestBody @Valid UpdateCreditorInstitutionDto dto
    ) {


        CreditorInstitutionDetails creditorInstitution = mapper.fromDto(dto);
        CreditorInstitutionDetails created = apiConfigService.updateCreditorInstitutionDetails(ecCode, creditorInstitution);


        return mapper.toResource(created);
    }

    @GetMapping(value = "/{ecCode}/segregationcodes", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    @ApiOperation(value = "", notes = "${swagger.api.creditor-institutions.getCreditorInstitutionSegregationcodes}")
    public CreditorInstitutionAssociatedCodeList getCreditorInstitutionSegregationcodes(@ApiParam("${swagger.request.ecCode}")
                                                                                        @PathVariable("ecCode") String ecCode) {

            return apiConfigSelfcareIntegrationService.getCreditorInstitutionSegregationcodes(ecCode);
        }


    @PutMapping(value = "/ec-broker/{code}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    @ApiOperation(value = "", notes = "${swagger.api.creditor-institution.updateBrokerEc}")
    public BrokerDetailsResource updateBrokerEc(@RequestBody @Valid BrokerEcDto dto,
                                                @ApiParam("${swagger.request.brokerCode}")
                                                @PathVariable("code") String brokerCode) {

        BrokerDetails brokerDetails = mapper.fromDto(dto);
        BrokerDetails created = apiConfigService.updateBrokerEc(brokerCode, brokerDetails);
        return mapper.toResource(created);
    }
}
