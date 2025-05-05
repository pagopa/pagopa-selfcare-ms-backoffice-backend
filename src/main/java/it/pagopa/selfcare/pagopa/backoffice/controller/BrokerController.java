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
import it.pagopa.selfcare.pagopa.backoffice.model.export.BrokerECExportStatus;
import it.pagopa.selfcare.pagopa.backoffice.model.institutions.CIBrokerDelegationPage;
import it.pagopa.selfcare.pagopa.backoffice.model.institutions.CIBrokerStationPage;
import it.pagopa.selfcare.pagopa.backoffice.model.stations.BrokerDto;
import it.pagopa.selfcare.pagopa.backoffice.model.stations.BrokerResource;
import it.pagopa.selfcare.pagopa.backoffice.service.BrokerService;
import it.pagopa.selfcare.pagopa.backoffice.service.ExportService;
import it.pagopa.selfcare.pagopa.backoffice.util.OpenApiTableMetadata;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping(value = "/brokers", produces = MediaType.APPLICATION_JSON_VALUE)
@Tag(name = "Creditor institution's Brokers")
public class BrokerController {

    private final BrokerService brokerService;

    private final ExportService exportService;

    @Autowired
    public BrokerController(BrokerService brokerService, ExportService exportService) {
        this.brokerService = brokerService;
        this.exportService = exportService;
    }


    @PostMapping(value = "")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create a Broker", security = {@SecurityRequirement(name = "JWT")})
    @OpenApiTableMetadata(readWriteIntense = OpenApiTableMetadata.ReadWrite.WRITE)
    public BrokerResource createBroker(@RequestBody BrokerDto brokerDto) {
        return brokerService.createBroker(brokerDto);
    }

    @GetMapping(value = "/{broker-tax-code}/ibans/export",
            produces = {"text/csv", MediaType.APPLICATION_JSON_VALUE})
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Export all IBANs of all creditor institutions handled by a broker EC to CSV",
            security = {@SecurityRequirement(name = "JWT")},
            description = "The CSV file contains the following columns: `denominazioneEnte, codiceFiscale, iban, " +
                    "stato, dataAttivazioneIban, descrizione, etichetta`")
    @OpenApiTableMetadata(readWriteIntense = OpenApiTableMetadata.ReadWrite.READ, cacheable = true)
    @Cacheable(value = "exportIbansToCsv")
    public ResponseEntity<Resource> exportIbansToCsv(
            @Parameter(description = "SelfCare Broker Code. it's a tax code")
            @PathVariable("broker-tax-code") String brokerCode
    ) {
        byte[] file = exportService.exportIbansToCsv(brokerCode);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=iban-export.csv")
                .contentType(MediaType.parseMediaType("text/csv"))
                .body(new ByteArrayResource(file));
    }

    @GetMapping(value = "/{broker-tax-code}/creditor-institutions/export",
            produces = {"text/csv", MediaType.APPLICATION_JSON_VALUE})
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Export all creditor institutions handled by a broker EC to CSV",
            security = {@SecurityRequirement(name = "JWT")},
            description = "The CSV file contains the following columns: `companyName, taxCode, intermediated, brokerCompanyName," +
                    " brokerTaxCode, model, auxDigit, segregationCode, applicationCode, cbillCode, stationId, stationState, " +
                    "activationDate, version, broadcast`")
    @OpenApiTableMetadata(readWriteIntense = OpenApiTableMetadata.ReadWrite.READ, cacheable = true)
    @Cacheable(value = "exportCreditorInstitutionToCsv")
    public ResponseEntity<Resource> exportCreditorInstitutionToCsv(
            @Parameter(description = "SelfCare Broker Code. it's a tax code")
            @PathVariable("broker-tax-code") String brokerCode
    ) {
        byte[] file = exportService.exportCreditorInstitutionToCsv(brokerCode);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=ci-export.csv")
                .contentType(MediaType.parseMediaType("text/csv"))
                .body(new ByteArrayResource(file));
    }

    @GetMapping(value = "/{broker-tax-code}/export-status", produces = {MediaType.APPLICATION_JSON_VALUE})
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Get all info about data exports for the broker EC",
            security = {@SecurityRequirement(name = "JWT")})
    @OpenApiTableMetadata(readWriteIntense = OpenApiTableMetadata.ReadWrite.READ)
    public BrokerECExportStatus getBrokerExportStatus(
            @Parameter(description = "SelfCare Broker Code. it's a tax code")
            @PathVariable("broker-tax-code") String brokerCode
    ) {
        return exportService.getBrokerExportStatus(brokerCode);
    }

    /**
     * Retrieves the list of creditor institution's delegation for the specified broker
     *
     * @param brokerCode the broker tax code
     * @param brokerId   the broker identifier
     * @param ciName     creditor institution's name, used for filtering result
     * @param page       page number
     * @param limit      number of element in the page
     * @return the requested page of broker's delegations
     */
    @GetMapping("/{broker-tax-code}/delegations")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Retrieve all delegations for given ci broker",
            security = {@SecurityRequirement(name = "JWT")})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = CIBrokerDelegationPage.class))),
            @ApiResponse(responseCode = "400", description = "Bad Request", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ProblemJson.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content(schema = @Schema())),
            @ApiResponse(responseCode = "404", description = "Not found", content = @Content(schema = @Schema(implementation = ProblemJson.class))),
            @ApiResponse(responseCode = "429", description = "Too many requests", content = @Content(schema = @Schema())),
            @ApiResponse(responseCode = "500", description = "Service unavailable", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ProblemJson.class)))
    })
    @OpenApiTableMetadata(readWriteIntense = OpenApiTableMetadata.ReadWrite.READ, cacheable = false)
    public CIBrokerDelegationPage getCIBrokerDelegation(
            @Parameter(description = "Broker's tax code") @PathVariable("broker-tax-code") String brokerCode,
            @Parameter(description = "Broker's unique id") @RequestParam String brokerId,
            @Parameter(description = "Creditor institution's name, used for filtering results")
            @RequestParam(required = false) String ciName,
            @RequestParam(required = false, defaultValue = "10") Integer limit,
            @RequestParam(required = false, defaultValue = "0") Integer page
    ) {
        return this.brokerService.getCIBrokerDelegation(brokerCode, brokerId, ciName, page, limit);
    }

    /**
     * Retrieve the paginated association info between broker's stations and creditor institutions for
     * the specified broker's tax code and creditor institution's tax code.
     *
     * @param brokerTaxCode broker's tax code
     * @param ciTaxCode     creditor institution's tax code
     * @param stationCode   station identifier
     * @param page          page number
     * @param limit         page size
     * @return the association info
     */
    @GetMapping("/{broker-tax-code}/creditor-institutions/{ci-tax-code}/stations")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Retrieve all broker's station associated with the given creditor institution",
            security = {@SecurityRequirement(name = "JWT")})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = CIBrokerStationPage.class))),
            @ApiResponse(responseCode = "400", description = "Bad Request", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ProblemJson.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content(schema = @Schema())),
            @ApiResponse(responseCode = "404", description = "Not found", content = @Content(schema = @Schema(implementation = ProblemJson.class))),
            @ApiResponse(responseCode = "429", description = "Too many requests", content = @Content(schema = @Schema())),
            @ApiResponse(responseCode = "500", description = "Service unavailable", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ProblemJson.class)))
    })
    @OpenApiTableMetadata(readWriteIntense = OpenApiTableMetadata.ReadWrite.READ, cacheable = false)
    public CIBrokerStationPage getCIBrokerStations(
            @Parameter(description = "Broker's tax code") @PathVariable("broker-tax-code") String brokerTaxCode,
            @Parameter(description = "Creditor institution's tax code") @PathVariable("ci-tax-code") String ciTaxCode,
            @Parameter(description = "Station identifier, used for filtering results") @RequestParam(required = false) String stationCode,
            @RequestParam(required = false, defaultValue = "10") Integer limit,
            @RequestParam(required = false, defaultValue = "0") Integer page
    ) {
        return this.brokerService.getCIBrokerStations(brokerTaxCode, ciTaxCode, stationCode, page, limit);
    }

    /**
     * Deletes the Creditor Institution's broker
     *
     * @param brokerTaxCode Tax code of the broker to delete
     */
    @DeleteMapping(value = "/{broker-tax-code}", produces = {MediaType.APPLICATION_JSON_VALUE})
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Deletes the Creditor Institution's broker", security = {@SecurityRequirement(name = "JWT")})
    @OpenApiTableMetadata
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK", content = @Content(schema = @Schema())),
            @ApiResponse(responseCode = "400", description = "Bad Request", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ProblemJson.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content(schema = @Schema())),
            @ApiResponse(responseCode = "403", description = "Forbidden", content = @Content(schema = @Schema())),
            @ApiResponse(responseCode = "404", description = "Not Found", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ProblemJson.class))),
            @ApiResponse(responseCode = "500", description = "Service unavailable", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ProblemJson.class)))
    })
    public void deleteCIBroker(
            @Parameter(description = "Broker tax code") @PathVariable("broker-tax-code") String brokerTaxCode
    ) {
        this.brokerService.deleteCIBroker(brokerTaxCode);
    }
}
