package it.pagopa.selfcare.pagopa.backoffice.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import it.pagopa.selfcare.pagopa.backoffice.model.maintenance.MaintenanceMessage;
import it.pagopa.selfcare.pagopa.backoffice.service.MaintenanceService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@Slf4j
@RestController
@RequestMapping(value = "/maintenance", produces = MediaType.APPLICATION_JSON_VALUE)
public class MaintenanceController {

    private final MaintenanceService maintenanceService;

    @Autowired
    public MaintenanceController(MaintenanceService maintenanceService) {
        this.maintenanceService = maintenanceService;
    }

    /**
     * Retrieve the maintenance message
     *
     * @return the maintenance message
     */
    @Operation(
            summary = "Return the maintenance message",
            description = "Return the maintenance message for the specific environment",
            security = {@SecurityRequirement(name = "JWT")},
            tags = {"Home"}
    )
    @GetMapping(value = "/messages")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<@Valid MaintenanceMessage> getMaintenanceMessage() {
        return ResponseEntity.ok(this.maintenanceService.getMaintenanceMessages());
    }
}
