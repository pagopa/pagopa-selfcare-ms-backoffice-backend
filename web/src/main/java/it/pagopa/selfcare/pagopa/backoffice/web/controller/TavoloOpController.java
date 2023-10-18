package it.pagopa.selfcare.pagopa.backoffice.web.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import it.pagopa.selfcare.pagopa.backoffice.core.TavoloOpService;
import it.pagopa.selfcare.pagopa.backoffice.core.TaxonomyService;
import it.pagopa.selfcare.pagopa.backoffice.web.model.mapper.TaxonomyMapper;
import it.pagopa.selfcare.pagopa.backoffice.web.model.taxonomies.TaxonomyResource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

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



}
