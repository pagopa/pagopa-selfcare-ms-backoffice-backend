
package it.pagopa.selfcare.pagopa.backoffice.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import feign.FeignException;
import it.pagopa.selfcare.pagopa.backoffice.client.InstitutionsClient;
import it.pagopa.selfcare.pagopa.backoffice.config.WhitelistConfig;
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

    private final WhitelistConfig whitelistConfig;
    private final InstitutionsClient institutionClient;

    public InstitutionsService(InstitutionsClient institutionClient, WhitelistConfig whitelistConfig) {
        this.institutionClient = institutionClient;
        this.whitelistConfig = whitelistConfig;
    }

    public void uploadInstitutionsData(String institutionsData, MultipartFile logo) {
        try {
            JsonNode logoNode = new ObjectMapper().readTree(institutionsData).path("logo");
            if (!logoNode.isMissingNode() && !logoNode.isNull()) {
                String logoUrl = logoNode.asText();
                if (!whitelistConfig.isAllowed(logoUrl)) {
                    throw new AppException(AppError.INSTITUTION_DATA_UPLOAD_BAD_REQUEST, "error logo url");
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
