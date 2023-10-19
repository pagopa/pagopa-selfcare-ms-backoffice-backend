package it.pagopa.selfcare.pagopa.backoffice.web.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import it.pagopa.selfcare.pagopa.backoffice.connector.model.creditorInstitution.CreditorInstitutionDetails;
import it.pagopa.selfcare.pagopa.backoffice.connector.model.tavoloOp.TavoloOpOperations;
import it.pagopa.selfcare.pagopa.backoffice.core.TavoloOpService;
import it.pagopa.selfcare.pagopa.backoffice.core.TaxonomyService;
import it.pagopa.selfcare.pagopa.backoffice.web.model.creditorinstituions.CreditorInstitutionDetailsResource;
import it.pagopa.selfcare.pagopa.backoffice.web.model.mapper.CreditorInstitutionMapper;
import it.pagopa.selfcare.pagopa.backoffice.web.model.mapper.TavoloOpMapper;
import it.pagopa.selfcare.pagopa.backoffice.web.model.mapper.TaxonomyMapper;
import it.pagopa.selfcare.pagopa.backoffice.web.model.tavoloop.TavoloOpResource;
import it.pagopa.selfcare.pagopa.backoffice.web.model.taxonomies.TaxonomyResource;
import lombok.extern.slf4j.Slf4j;
import org.mapstruct.factory.Mappers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping(value = "/tavoloop", produces = MediaType.APPLICATION_JSON_VALUE)
@Api(tags = "tavoloop")
public class TavoloOpController {

    private final TavoloOpService tavoloOpService;


    @Autowired
    public TavoloOpController(TavoloOpService tavoloOpService) {
        this.tavoloOpService = tavoloOpService;

    }

    @GetMapping(value = "/{ecCode}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    @ApiOperation(value = "", notes = "${swagger.api.tavoloOp.getTavoloOpDetails}")
    public TavoloOpResource getTavoloOpDetails(@ApiParam("${swagger.request.ecCode}")
                                                                 @PathVariable("ecCode") String ecCode) {

        TavoloOpOperations tavoloOpOperations = tavoloOpService.findByTaxCode(ecCode);
        return TavoloOpMapper.toResource(tavoloOpOperations);
    }



}
