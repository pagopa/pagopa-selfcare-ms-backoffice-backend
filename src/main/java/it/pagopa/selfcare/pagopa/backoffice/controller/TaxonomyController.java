package it.pagopa.selfcare.pagopa.backoffice.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import it.pagopa.selfcare.pagopa.backoffice.model.taxonomies.Taxonomies;
import it.pagopa.selfcare.pagopa.backoffice.service.TaxonomyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/taxonomies")
@Tag(name = "Taxonomy")
public class TaxonomyController {

    @Autowired
    private TaxonomyService taxonomyService;

    @GetMapping("")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Get a list of taxonomies", security = {@SecurityRequirement(name = "JWT")})
    @Cacheable(value = "taxonomy")
    public Taxonomies getTaxonomies() {
        return taxonomyService.getTaxonomies();
    }

}