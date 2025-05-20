
package it.pagopa.selfcare.pagopa.backoffice.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import feign.FeignException;
import it.pagopa.selfcare.pagopa.backoffice.client.InstitutionsClient;
import it.pagopa.selfcare.pagopa.backoffice.exception.AppError;
import it.pagopa.selfcare.pagopa.backoffice.exception.AppException;
import it.pagopa.selfcare.pagopa.backoffice.model.notices.InstitutionUploadData;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.Arrays;

@Service
@Slf4j
public class InstitutionsService {

    @Value("${rest-client.external-api.printit-blob-urls}")
    private String printitBlobUrlsRaw;

    private final InstitutionsClient institutionClient;

    public InstitutionsService(InstitutionsClient institutionClient) {
        this.institutionClient = institutionClient;
    }

    public void uploadInstitutionsData(String institutionsData, MultipartFile logo) {
        try {

            String[] printitBlobUrls = Arrays.stream(printitBlobUrlsRaw.split(","))
                    .map(String::trim)
                    .filter(s -> !s.isEmpty())
                    .toArray(String[]::new);


            String logoUrl = new ObjectMapper()
                    .readTree(institutionsData)
                    .path("logo")
                    .asText(null);

            if (logo != null) {
                boolean isValid = Arrays.stream(printitBlobUrls)
                        .filter(url -> url != null && !url.isBlank())
                        .map(url -> url.endsWith("/") ? url.substring(0, url.length() -1) : url)
                        .anyMatch(url -> url.startsWith(logoUrl));
                if (!isValid) {
                    log.warn("Invalid logo URL: {}. Allowed base URLs: {}", logoUrl, Arrays.toString(printitBlobUrls));
                    throw new AppException(
                            AppError.INSTITUTION_DATA_UPLOAD_BAD_REQUEST,
                            "The logo URL must begin with one of the allowed base URLs"
                    );
                }
            }

            institutionClient.updateInstitutions(institutionsData, logo);
        } catch (AppException e) {
            throw e;
        } catch (FeignException.BadRequest e) {
            throw new AppException(AppError.INSTITUTION_DATA_UPLOAD_BAD_REQUEST, e);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new AppException(AppError.INSTITUTION_DATA_UPLOAD_ERROR, e);
        }
    }


    public InstitutionUploadData getInstitutionData(String institutionsData) {
        try {
            return institutionClient.getInstitutionData(institutionsData);
        } catch (AppException e) {
            throw e;
        } catch (FeignException e) {
            if (e.status() == 404) {
                throw new AppException(AppError.INSTITUTION_NOT_FOUND, e);
            }
            throw new AppException(AppError.INTERNAL_SERVER_ERROR, e);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new AppException(AppError.INSTITUTION_RETRIEVE_ERROR, e);
        }
    }
}
