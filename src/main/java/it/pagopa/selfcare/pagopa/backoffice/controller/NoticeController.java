package it.pagopa.selfcare.pagopa.backoffice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import it.pagopa.selfcare.pagopa.backoffice.model.ProblemJson;
import it.pagopa.selfcare.pagopa.backoffice.model.notices.InstitutionUploadData;
import it.pagopa.selfcare.pagopa.backoffice.service.InstitutionsService;
import it.pagopa.selfcare.pagopa.backoffice.util.OpenApiTableMetadata;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import javax.validation.Validator;
import javax.validation.constraints.NotNull;

/**
 * Rest Controller containing APIs for institution data management
 */

@RestController
@RequestMapping(value = "/notice", produces = MediaType.APPLICATION_JSON_VALUE)
@Validated
@Tag(name = "Notice APIs")
public class NoticeController {

    private final InstitutionsService institutionsService;

    private final ObjectMapper objectMapper;

    private final Validator validator;

    public NoticeController(InstitutionsService institutionsService, ObjectMapper objectMapper, Validator validator) {
        this.institutionsService = institutionsService;
        this.objectMapper = objectMapper;
        this.validator = validator;
    }

    /**
     * Uploads institutions data to the related storage, using the taxCode provided within
     * the UploadData instance, if the institution is already on the storage, the content
     * will be updated. The institution json data will include the link on the uploaded logo
     * @param institutionsDataContent institution data to upload
     * @param logo institution logo to upload
     */
    @Operation(summary = "uploadInstitutionData",
            description = "Uploads or updates the provided institution data and logo on the related storage," +
                    " to be used in the payment notice generation process",
            security = {@SecurityRequirement(name = "ApiKey")})
    @OpenApiTableMetadata(readWriteIntense = OpenApiTableMetadata.ReadWrite.WRITE,
            external = true, internal = false)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "400", description = "Bad Request",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ProblemJson.class))),
            @ApiResponse(responseCode = "401",
                    description = "Unauthorized", content = @Content(schema = @Schema())),
            @ApiResponse(responseCode = "403",
                    description = "Forbidden", content = @Content(schema = @Schema())),
            @ApiResponse(responseCode = "429",
                    description = "Too many requests", content = @Content(schema = @Schema())),
            @ApiResponse(responseCode = "500",
                    description = "Service unavailable",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = ProblemJson.class)))
    })
    @PostMapping(value = "/institutions/data", consumes = {
            MediaType.MULTIPART_FORM_DATA_VALUE})
    public void updateInstitutions(
            @Parameter(description = "String containing the json data to upload ", required = true,
                    schema = @Schema(implementation = InstitutionUploadData.class))
            @Valid @NotNull @RequestPart("institutions-data") String institutionsDataContent,
            @Parameter(description = "logo file to upload", required = true)
            @Valid @NotNull @RequestParam(value = "file", required = false) MultipartFile logo
    ) {
        institutionsService.uploadInstitutionsData(institutionsDataContent, logo);
    }

    /**
     * Retrieving institution data, related to the provided taxCode
     * @param taxCode institution data to be used retrieval
     * @return institution data
     */
    @Operation(summary = "getInstitutionData",
            description = "Retrieves saved institution data and logo on the related storage," +
                    " to be used in the payment notice generation process",
            security = {@SecurityRequirement(name = "ApiKey")})
    @OpenApiTableMetadata(readWriteIntense = OpenApiTableMetadata.ReadWrite.WRITE,
            external = true, internal = false)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ProblemJson.class))),
            @ApiResponse(responseCode = "400", description = "Bad Request",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ProblemJson.class))),
            @ApiResponse(responseCode = "401",
                    description = "Unauthorized", content = @Content(schema = @Schema())),
            @ApiResponse(responseCode = "403",
                    description = "Forbidden", content = @Content(schema = @Schema())),
            @ApiResponse(responseCode = "404",
                    description = "Not Found", content = @Content(
                    schema = @Schema(implementation = ProblemJson.class))),
            @ApiResponse(responseCode = "429",
                    description = "Too many requests", content = @Content(schema = @Schema())),
            @ApiResponse(responseCode = "500",
                    description = "Service unavailable",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ProblemJson.class)))
    })
    @GetMapping(value = "/institutions/data/{taxCode}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public InstitutionUploadData getInstitutionData(
            @Parameter(description = "tax code of the CI to use for retrieval")
            @PathVariable(name = "taxCode") String taxCode) {
        return institutionsService.getInstitutionData(taxCode);
    }

}
