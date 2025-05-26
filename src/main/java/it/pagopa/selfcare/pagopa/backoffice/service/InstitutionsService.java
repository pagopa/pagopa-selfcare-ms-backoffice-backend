
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

import javax.annotation.PostConstruct;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

@Service
@Slf4j
public class InstitutionsService {

    @Value("${rest-client.institutions.whitelist.logo-urls}")
    private String whitelistLogoUrls;

    private Set<String> allowedLogoHosts;

    private final InstitutionsClient institutionClient;

    private final ObjectMapper objectMapper = new ObjectMapper();

    public InstitutionsService(InstitutionsClient institutionClient) {
        this.institutionClient = institutionClient;
    }

    @PostConstruct
    public void init() {
        if (whitelistLogoUrls != null && !whitelistLogoUrls.isEmpty()) {
            allowedLogoHosts = new HashSet<>();
            Arrays.stream(whitelistLogoUrls.split("\\s*,\\s*"))
                    .map(this::extractHost)
                    .filter(host -> host != null)
                    .forEach(allowedLogoHosts::add);
        } else {
            allowedLogoHosts = new HashSet<>();
        }
        log.info("Whitelist hosts initialized: {}", allowedLogoHosts);
    }

    private String extractHost(String url) {
        try {
            URI uri = new URI(url);
            return uri.getHost();
        } catch (URISyntaxException e) {
            log.warn("Invalid URL in whitelist: {}", url);
            return null;
        }
    }

    public boolean isAllowed(String logoUrl) {
        if (logoUrl == null || logoUrl.isEmpty()) {
            return true;
        }
        try {
            URI uri = new URI(logoUrl);
            String host = uri.getHost();
            if (host == null) {
                log.warn("Logo URL has no host part: {}", logoUrl);
                return false;
            }
            return allowedLogoHosts.contains(host);
        } catch (URISyntaxException e) {
            log.warn("Invalid logo URL syntax: {}", logoUrl);
            return false;
        }
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
}
