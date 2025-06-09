
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
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Slf4j
public class InstitutionsService {

    private Set<String> allowedLogoHosts;

    private final InstitutionsClient institutionClient;

    private final ObjectMapper objectMapper = new ObjectMapper();

    public InstitutionsService(
        InstitutionsClient institutionClient,
        @Value("#{'${rest-client.institutions.whitelist.logo-urls}'.split(',')}")
        Set<String> allowedLogoHosts
    ) {
        this.institutionClient = institutionClient;
        this.allowedLogoHosts = allowedLogoHosts.stream()
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .collect(Collectors.toSet());
        log.info("Whitelist hosts initialized: {}", this.allowedLogoHosts);
    }

    public void uploadInstitutionsData(String institutionsData, MultipartFile logo) {
        try {
            JsonNode logoNode = objectMapper.readTree(institutionsData).path("logo");
            if (!logoNode.isMissingNode() && !logoNode.isNull()) {
                String logoUrl = logoNode.asText();
                if (!isAllowed(logoUrl)) {
                    throw new AppException(AppError.INSTITUTION_DATA_UPLOAD_LOGO_NOT_ALLOWED_BAD_REQUEST);
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

    private boolean isAllowed(String logoUrl) {
        if (logoUrl == null || logoUrl.isEmpty()) {
            return true;
        }

        for (String allowedPrefix : allowedLogoHosts) {
            if (logoUrl.startsWith(allowedPrefix)) {
                return true;
            }
        }

        log.warn("Logo URL not allowed: {}", logoUrl);
        return false;
    }

}
