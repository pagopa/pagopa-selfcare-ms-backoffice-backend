package it.pagopa.selfcare.pagopa.backoffice.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import it.pagopa.selfcare.pagopa.backoffice.model.taxonomies.Taxonomies;
import it.pagopa.selfcare.pagopa.backoffice.model.taxonomies.TaxonomyGroups;
import it.pagopa.selfcare.pagopa.backoffice.service.TaxonomyService;
import it.pagopa.selfcare.pagopa.backoffice.util.OpenApiTableMetadata;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/taxonomies")
@Tag(name = "Taxonomy")
public class TaxonomyController {

    @Autowired
    private TaxonomyService taxonomyService;

    @GetMapping("")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Get a list of taxonomies", security = {@SecurityRequirement(name = "JWT")})
    @OpenApiTableMetadata(cacheable = true)
    @Cacheable(value = "taxonomy")
    public Taxonomies getTaxonomies(
            @RequestParam(value = "code", required = false, defaultValue = "") String code,
            @RequestParam(value = "ec", required = false) String ec,
            @RequestParam(value = "macro_area", required = false) String macroArea,
            @RequestParam(value = "only_valid", defaultValue = "false") Boolean onlyValid) {
        return taxonomyService.getTaxonomies(code, ec, macroArea,onlyValid);
    }

    @GetMapping("/groups")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Get a list of taxonomies groups", security = {@SecurityRequirement(name = "JWT")})
    @OpenApiTableMetadata(cacheable = true)
    @Cacheable(value = "taxonomyGroups")
    public TaxonomyGroups getTaxonomyGroups() {
        return taxonomyService.getTaxonomyGroups();
    }

}
