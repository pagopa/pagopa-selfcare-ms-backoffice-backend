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

    @GetMapping(value = "/dashboard", produces = {MediaType.APPLICATION_JSON_VALUE})
    @ResponseStatus(HttpStatus.OK)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = QuicksightEmbedUrlResponse.class))),
            @ApiResponse(responseCode = "403", description = "Forbidden", content = @Content(schema = @Schema(implementation = ProblemJson.class))),
            @ApiResponse(responseCode = "429", description = "Too many requests", content = @Content(schema = @Schema())),
            @ApiResponse(responseCode = "500", description = "Service unavailable", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ProblemJson.class)))
    })
    @Operation(summary = "Get aws quicksight dashboard's embed url", security = {@SecurityRequirement(name = "JWT")})
    @OpenApiTableMetadata(readWriteIntense = OpenApiTableMetadata.ReadWrite.READ)
    public QuicksightEmbedUrlResponse getEmbedUrlForAnonymousUser(
            @Parameter(description = "Institution's id for dashboard retrieval(Only for operator PagoPA)") @RequestParam(required = false) String institutionId
    ) {
        return this.awsQuicksightService.generateEmbedUrlForAnonymousUser(institutionId);
    }
}
