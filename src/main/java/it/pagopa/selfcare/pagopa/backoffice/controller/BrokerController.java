package it.pagopa.selfcare.pagopa.backoffice.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import it.pagopa.selfcare.pagopa.backoffice.model.creditorinstituions.BrokerEcDto;
import it.pagopa.selfcare.pagopa.backoffice.model.stations.*;
import it.pagopa.selfcare.pagopa.backoffice.service.BrokerService;
import it.pagopa.selfcare.pagopa.backoffice.service.IbanService;
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

    private final IbanService ibanService;

    @Autowired
    public BrokerController(BrokerService brokerService, IbanService ibanService) {
        this.brokerService = brokerService;
        this.ibanService = ibanService;
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

    @GetMapping(value = "/{broker-id}/ibans/export", produces = "text/csv")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Export all IBANs of all creditor institutions handled by a broker EC to CSV", security = {@SecurityRequirement(name = "JWT")},
            description = "The CSV file contains the following columns: `denominazioneEnte, codiceFiscale, iban, stato, dataAttivazioneIban, descrizione, etichetta`")
    @OpenApiTableMetadata(readWriteIntense = OpenApiTableMetadata.ReadWrite.READ, cacheable = true)
    @Cacheable(value = "exportIbansToCsv")
    public ResponseEntity<Resource> exportIbansToCsv(@Parameter(description = "SelfCare Broker Id. it's an UUID") @PathVariable("broker-id") String brokerId) {

        byte[] file = ibanService.exportIbansToCsv(brokerId);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=iban-export.csv")
                .contentType(MediaType.parseMediaType("text/csv"))
                .body(new ByteArrayResource(file));
    }
}
