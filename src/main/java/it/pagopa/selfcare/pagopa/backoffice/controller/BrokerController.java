package it.pagopa.selfcare.pagopa.backoffice.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import it.pagopa.selfcare.pagopa.backoffice.model.creditorinstituions.BrokerEcDto;
import it.pagopa.selfcare.pagopa.backoffice.model.export.BrokerECExportStatus;
import it.pagopa.selfcare.pagopa.backoffice.model.stations.*;
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

import javax.validation.Valid;

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

    @GetMapping(value = "", produces = {MediaType.APPLICATION_JSON_VALUE})
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Get paginated list of creditor brokers", security = {@SecurityRequirement(name = "JWT")})
    @OpenApiTableMetadata(readWriteIntense = OpenApiTableMetadata.ReadWrite.READ)
    public BrokersResource getBrokersEC(@Parameter(description = "") @RequestParam(required = false, defaultValue = "50") Integer limit,
                                        @Parameter(description = "Page number. Page value starts from 0") @RequestParam Integer page,
                                        @RequestParam(required = false) String code,
                                        @RequestParam(required = false) String name,
                                        @Parameter(description = "order by name or code, default = CODE") @RequestParam(required = false, defaultValue = "CODE") String orderby,
                                        @Parameter() @RequestParam(required = false, defaultValue = "DESC") String ordering) {
        return brokerService.getBrokersEC(limit, page, code, name, orderby, ordering);
    }


    @PutMapping(value = "/{broker-code}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Update an existing EC broker", security = {@SecurityRequirement(name = "JWT")})
    @OpenApiTableMetadata(readWriteIntense = OpenApiTableMetadata.ReadWrite.WRITE)
    public BrokerDetailsResource updateBroker(@RequestBody @Valid BrokerEcDto dto,
                                              @Parameter(description = "Broker code") @PathVariable("broker-code") String brokerCode) {

        return brokerService.updateBrokerForCI(dto, brokerCode);
    }

    @GetMapping(value = "/{broker-code}/stations", produces = {MediaType.APPLICATION_JSON_VALUE})
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Get paginated list of stations given a broker code", security = {@SecurityRequirement(name = "JWT")})
    @OpenApiTableMetadata(readWriteIntense = OpenApiTableMetadata.ReadWrite.READ)
    public StationDetailsResourceList getStationsDetailsListByBroker(@PathVariable("broker-code") String brokerCode,
                                                                     @RequestParam(required = false) String stationId,
                                                                     @RequestParam(required = false, defaultValue = "10") Integer limit,
                                                                     @RequestParam(required = false, defaultValue = "0") Integer page) {
        return brokerService.getStationsDetailsListByBroker(brokerCode, stationId, limit, page);
    }

    @GetMapping(value = "/{broker-code}/ibans/export", produces = {"text/csv", MediaType.APPLICATION_JSON_VALUE})
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Export all IBANs of all creditor institutions handled by a broker EC to CSV", security = {@SecurityRequirement(name = "JWT")},
            description = "The CSV file contains the following columns: `denominazioneEnte, codiceFiscale, iban, stato, dataAttivazioneIban, descrizione, etichetta`")
    @OpenApiTableMetadata(readWriteIntense = OpenApiTableMetadata.ReadWrite.READ, cacheable = true)
    @Cacheable(value = "exportIbansToCsv")
    public ResponseEntity<Resource> exportIbansToCsv(@Parameter(description = "SelfCare Broker Code. it's a tax code") @PathVariable("broker-code") String brokerCode) {

        byte[] file = exportService.exportIbansToCsv(brokerCode);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=iban-export.csv")
                .contentType(MediaType.parseMediaType("text/csv"))
                .body(new ByteArrayResource(file));
    }

    @GetMapping(value = "/{broker-code}/creditor-institutions/export", produces = {"text/csv", MediaType.APPLICATION_JSON_VALUE})
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Export all creditor institutions handled by a broker EC to CSV", security = {@SecurityRequirement(name = "JWT")},
            description = "The CSV file contains the following columns: `companyName, amministrativeCode, taxCode, intermediated, brokerCompanyName, brokerTaxCode, model, auxDigit, segregationCode, applicationCode, cbillCode, stationId, stationState, activationDate, version, broadcast`")
    @OpenApiTableMetadata(readWriteIntense = OpenApiTableMetadata.ReadWrite.READ, cacheable = true)
    @Cacheable(value = "exportCreditorInstitutionToCsv")
    public ResponseEntity<Resource> exportCreditorInstitutionToCsv(@Parameter(description = "SelfCare Broker Code. it's a tax code") @PathVariable("broker-code") String brokerCode) {

        byte[] file = exportService.exportCreditorInstitutionToCsv(brokerCode);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=ci-export.csv")
                .contentType(MediaType.parseMediaType("text/csv"))
                .body(new ByteArrayResource(file));
    }

    @GetMapping(value = "/{broker-code}/export-status", produces = {MediaType.APPLICATION_JSON_VALUE})
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Get all info about data exports for the broker EC", security = {@SecurityRequirement(name = "JWT")})
    @OpenApiTableMetadata(readWriteIntense = OpenApiTableMetadata.ReadWrite.READ)
    public BrokerECExportStatus getBrokerExportStatus(@Parameter(description = "SelfCare Broker Code. it's a tax code") @PathVariable("broker-code") String brokerCode) {

        return exportService.getBrokerExportStatus(brokerCode);
    }
}
