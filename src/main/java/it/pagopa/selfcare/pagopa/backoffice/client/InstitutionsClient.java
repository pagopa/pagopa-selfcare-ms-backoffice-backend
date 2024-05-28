package it.pagopa.selfcare.pagopa.backoffice.client;

import it.pagopa.selfcare.pagopa.backoffice.config.feign.InstitutionsFeignClientConfig;
import it.pagopa.selfcare.pagopa.backoffice.model.notices.InstitutionUploadData;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@FeignClient(name = "institutions", url = "${rest-client.institutions.base-url}",
        configuration = InstitutionsFeignClientConfig.class)
public interface InstitutionsClient {

    @PostMapping(value = "/institutions/data", consumes = {
            MediaType.MULTIPART_FORM_DATA_VALUE})
    void updateInstitutions(
            @RequestPart("institutions-data") String institutionsDataContent,
            @RequestPart(value = "file") MultipartFile logo);

    @GetMapping(value = "/data/{taxCode}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    InstitutionUploadData getInstitutionData(
            @PathVariable(name = "taxCode") String taxCode);

}
