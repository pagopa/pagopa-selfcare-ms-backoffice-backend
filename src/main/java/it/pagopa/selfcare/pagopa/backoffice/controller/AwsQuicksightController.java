package it.pagopa.selfcare.pagopa.backoffice.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import it.pagopa.selfcare.pagopa.backoffice.model.quicksightdashboard.QuicksightEmbedUrlResponse;
import it.pagopa.selfcare.pagopa.backoffice.service.AwsQuicksightService;
import it.pagopa.selfcare.pagopa.backoffice.util.OpenApiTableMetadata;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping(value = "/quicksight", produces = MediaType.APPLICATION_JSON_VALUE)
@Tag(name = "Aws Quicksight")
public class AwsQuicksightController {

    private final AwsQuicksightService awsQuicksightService;

    @Autowired
    public AwsQuicksightController(AwsQuicksightService awsQuicksightService) {
        this.awsQuicksightService = awsQuicksightService;
    }

    @GetMapping(value = "/dashboard/{institution-id}", produces = {MediaType.APPLICATION_JSON_VALUE})
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Get aws quicksight dashboard's embed url", security = {@SecurityRequirement(name = "JWT")})
    @OpenApiTableMetadata(readWriteIntense = OpenApiTableMetadata.ReadWrite.READ)
    public QuicksightEmbedUrlResponse getEmbedUrlForAnonymousUser(
            @Parameter(description = "Institution's identifier") @PathVariable("institution-id") String institutionId) {
        return this.awsQuicksightService.generateEmbedUrlForAnonymousUser(institutionId);
    }
}
