package it.pagopa.selfcare.pagopa.backoffice.client;

import it.pagopa.selfcare.pagopa.backoffice.config.feign.InstitutionsFeignClientConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

@FeignClient(name = "institutions", url = "${rest-client.institutions.base-url}",
        configuration = InstitutionsFeignClientConfig.class)
public interface InstitutionsClient {

    @PostMapping(value = "/data", consumes = {
            MediaType.MULTIPART_FORM_DATA_VALUE})
    public void updateInstitutions(
            @RequestPart("institutions-data") String institutionsDataContent,
            @RequestParam(value = "file", required = false) MultipartFile logo);

}
