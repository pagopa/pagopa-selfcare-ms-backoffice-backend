package it.pagopa.selfcare.pagopa.backoffice.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import it.pagopa.selfcare.pagopa.backoffice.model.configuration.PaymentTypes;
import it.pagopa.selfcare.pagopa.backoffice.model.configuration.WfespPluginConfs;
import it.pagopa.selfcare.pagopa.backoffice.model.connector.channel.WrapperEntitiesList;
import it.pagopa.selfcare.pagopa.backoffice.model.connector.wrapper.WrapperStatus;
import it.pagopa.selfcare.pagopa.backoffice.model.connector.wrapper.WrapperType;
import it.pagopa.selfcare.pagopa.backoffice.service.ConfigurationService;
import it.pagopa.selfcare.pagopa.backoffice.service.WrapperService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping(value = "/configurations", produces = MediaType.APPLICATION_JSON_VALUE)
@Tag(name = "Configuration")
public class ConfigurationController {

    @Autowired
    private ConfigurationService configurationService;

    @Autowired
    private WrapperService wrapperService;

    @GetMapping(value = "/payment-types", produces = {MediaType.APPLICATION_JSON_VALUE})
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Get list of payment type", security = {@SecurityRequirement(name = "JWT")})
    public PaymentTypes getPaymentTypes() {

        return configurationService.getPaymentTypes();
    }

    @GetMapping(value = "/wfesp-plugins", produces = {MediaType.APPLICATION_JSON_VALUE})
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Update a WrapperChannel on Cosmodb", security = {@SecurityRequirement(name = "JWT")})
    public WfespPluginConfs getWfespPlugins() {

        return configurationService.getWfespPlugins();
    }

    @GetMapping(value = "/wrapper/{wrapper-type}/status/{wrapper-status}", produces = {MediaType.APPLICATION_JSON_VALUE})
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Get Wrapper Channel Details from cosmos db", security = {@SecurityRequirement(name = "JWT")})
    public WrapperEntitiesList getWrapperByTypeAndStatus(@Parameter(description = "Type of Wrapper like CHANNEL or STATION") @PathVariable("wrapper-type") WrapperType wrapperType,
                                                         @Parameter(description = "Validation Status of a CHANNEL or STATION") @PathVariable(required = false, value = "wrapper-status") WrapperStatus wrapperStatus,
                                                         @Parameter(description = "Number of elements on one page. Default = 50") @RequestParam(required = false, defaultValue = "50") Integer limit,
                                                         @Parameter(description = "Page number. Page value starts from 0") @RequestParam Integer page,
                                                         @Parameter(description = "Broker code filter for search") @RequestParam(required = false, value = "broker-code") String brokerCode,
                                                         @Parameter(description = "Query with sql like parameter for field id search") @RequestParam(required = false, value = "id-like") String idLike,
                                                         @Parameter(description = "Method of sorting") @RequestParam(required = false, value = "sorting") String sorting) {

        return wrapperService.findByStatusAndTypeAndBrokerCodeAndIdLike(wrapperStatus, wrapperType, brokerCode, idLike, page, limit, sorting);
    }
}
