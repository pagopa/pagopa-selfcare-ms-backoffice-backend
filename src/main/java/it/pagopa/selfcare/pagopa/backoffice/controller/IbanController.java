package it.pagopa.selfcare.pagopa.backoffice.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import it.pagopa.selfcare.pagopa.backoffice.model.ProblemJson;
import it.pagopa.selfcare.pagopa.backoffice.model.creditorinstituions.CreditorInstitutionDetailsResource;
import it.pagopa.selfcare.pagopa.backoffice.model.iban.Iban;
import it.pagopa.selfcare.pagopa.backoffice.model.iban.IbanCreate;
import it.pagopa.selfcare.pagopa.backoffice.model.iban.Ibans;
import it.pagopa.selfcare.pagopa.backoffice.service.IbanService;
import it.pagopa.selfcare.pagopa.backoffice.util.OpenApiTableMetadata;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotNull;

@Slf4j
@RestController
@RequestMapping(value = "/creditor-institutions/{ci-code}/ibans", produces = MediaType.APPLICATION_JSON_VALUE)
@Tag(name = "Ibans")
public class IbanController {

    @Autowired
    private IbanService ibanService;

    @GetMapping(value = "", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Get all IBANs related to creditor institution, filtering by specific label", security = {@SecurityRequirement(name = "JWT")})
    @OpenApiTableMetadata
    public Ibans getCreditorInstitutionIbans(@Parameter(description = "Creditor institution code") @PathVariable("ci-code") String ciCode,
                                             @Parameter(description = "Label to be used as search filter for associated IBANs") @RequestParam(required = false) String labelName) {

        return ibanService.getIban(ciCode, labelName);
    }

    @PostMapping(value = "", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Created", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = Iban.class))),
            @ApiResponse(responseCode = "409", description = "Conflict", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ProblemJson.class)))
    })
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create an IBAN owned by creditor institution", security = {@SecurityRequirement(name = "JWT")})
    @OpenApiTableMetadata
    public Iban createCreditorInstitutionIbans(@Parameter(description = "Creditor institution code") @PathVariable("ci-code") String ciCode,
                                               @RequestBody @NotNull IbanCreate requestDto) {

        return ibanService.createIban(ciCode, requestDto);
    }

    @PutMapping(value = "/{iban-value}", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Update a specific IBAN owned by creditor institution", security = {@SecurityRequirement(name = "JWT")})
    @OpenApiTableMetadata
    public Iban updateCreditorInstitutionIbans(@Parameter(description = "Creditor institution code") @PathVariable("ci-code") String ciCode,
                                               @Parameter(description = "IBAN identification value") @PathVariable("iban-value") String ibanValue,
                                               @RequestBody @NotNull IbanCreate requestDto) {

        return ibanService.updateIban(ciCode, ibanValue, requestDto);
    }

    @DeleteMapping(value = "/{iban-value}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Delete a specific IBAN owned by creditor institution", security = {@SecurityRequirement(name = "JWT")})
    @OpenApiTableMetadata
    public void deleteCreditorInstitutionIbans(@Parameter(description = "Creditor institution code") @PathVariable("ci-code") String ciCode,
                                               @Parameter(description = "IBAN identification value") @PathVariable("iban-value") String ibanValue) {

        ibanService.deleteIban(ciCode, ibanValue);
    }

}
