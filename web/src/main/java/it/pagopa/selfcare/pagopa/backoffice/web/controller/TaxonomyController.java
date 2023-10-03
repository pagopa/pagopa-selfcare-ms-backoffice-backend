package it.pagopa.selfcare.pagopa.backoffice.web.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import it.pagopa.selfcare.pagopa.backoffice.connector.model.station.Stations;
import it.pagopa.selfcare.pagopa.backoffice.connector.model.taxonomy.Taxonomies;
import it.pagopa.selfcare.pagopa.backoffice.connector.model.taxonomy.Taxonomy;
import it.pagopa.selfcare.pagopa.backoffice.core.TaxonomyService;
import it.pagopa.selfcare.pagopa.backoffice.web.model.mapper.TaxonomyMapper;
import it.pagopa.selfcare.pagopa.backoffice.web.model.stations.StationsResource;
import it.pagopa.selfcare.pagopa.backoffice.web.model.taxonomies.TaxonomiesResource;
import it.pagopa.selfcare.pagopa.backoffice.web.model.taxonomies.TaxonomyResource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping(value = "/taxonomy", produces = MediaType.APPLICATION_JSON_VALUE)
@Api(tags = "taxonomy")
public class TaxonomyController {

    private final TaxonomyService taxonomyService;


    @Autowired
    public TaxonomyController(TaxonomyService taxonomyService) {
        this.taxonomyService = taxonomyService;

    }

    @GetMapping("")
    @ResponseStatus(HttpStatus.OK)
    @ApiOperation(value = "", notes = "${swagger.api.taxonomy.getTaxonomy}")
    public  List<TaxonomyResource> getTaxonomy() {
        List<Taxonomy> taxonomies = taxonomyService.getTaxonomies();
        List<TaxonomyResource> resource = TaxonomyMapper.toResource(taxonomies);
        return resource;
    }
}
