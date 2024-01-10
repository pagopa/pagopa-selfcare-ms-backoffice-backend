package it.pagopa.selfcare.pagopa.backoffice.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import it.pagopa.selfcare.pagopa.backoffice.entity.TavoloOpOperations;
import it.pagopa.selfcare.pagopa.backoffice.model.tavoloop.TavoloOpDto;
import it.pagopa.selfcare.pagopa.backoffice.model.tavoloop.TavoloOpResource;
import it.pagopa.selfcare.pagopa.backoffice.model.tavoloop.TavoloOpResourceList;
import it.pagopa.selfcare.pagopa.backoffice.service.OperativeTableService;
import it.pagopa.selfcare.pagopa.backoffice.util.OpenApiTableMetadata;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/operative-tables")
@Tag(name = "Operative Tables")
public class OperativeTableController {

    @Autowired
    private OperativeTableService operativeTableService;


    @GetMapping(value = "", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Get All operative tables", security = {@SecurityRequirement(name = "JWT")})
    @OpenApiTableMetadata
    public TavoloOpResourceList getOperativeTables() {

        return operativeTableService.getOperativeTables();
    }

    @GetMapping(value = "/{ci-code}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Get operative table details", security = {@SecurityRequirement(name = "JWT")})
    @OpenApiTableMetadata
    public TavoloOpResource getOperativeTable(@Parameter(description = "Creditor Institution code") @PathVariable("ci-code") String ciCode) {

        return operativeTableService.getOperativeTable(ciCode);
    }

    @PostMapping(value = "", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Insert a new operative table", security = {@SecurityRequirement(name = "JWT")})
    @OpenApiTableMetadata
    public TavoloOpOperations insertOperativeTable(@RequestBody TavoloOpDto dto) {

        return operativeTableService.insertOperativeTable(dto);
    }

    @PutMapping(value = "/{ci-code}", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Update an existent operative table", security = {@SecurityRequirement(name = "JWT")})
    @OpenApiTableMetadata
    public TavoloOpOperations updateOperativeTable(@PathVariable("ci-code") String ciCode,
                                                   @RequestBody TavoloOpDto dto) {

        // TODO: utilizzare ec-code
        return operativeTableService.updateOperativeTable(dto);
    }

}
