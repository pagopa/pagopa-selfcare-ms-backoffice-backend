package it.pagopa.selfcare.pagopa.backoffice.service;

import feign.FeignException;
import it.pagopa.selfcare.pagopa.backoffice.client.InstitutionsClient;
import it.pagopa.selfcare.pagopa.backoffice.exception.AppError;
import it.pagopa.selfcare.pagopa.backoffice.exception.AppException;
import it.pagopa.selfcare.pagopa.backoffice.model.notices.InstitutionUploadData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class InstitutionsServiceTest {

    @Mock
    private InstitutionsClient institutionsClient;

    private InstitutionsService institutionsService;

    @BeforeEach
    public void init() {
        Mockito.reset(institutionsClient);
        institutionsService = new InstitutionsService(institutionsClient, any());
    }

    private void setWhitelistLogoUrls(String whitelist) throws Exception {
        Field field = InstitutionsService.class.getDeclaredField("whitelistLogoUrls");
        field.setAccessible(true);
        field.set(institutionsService, whitelist);

        Method initMethod = InstitutionsService.class.getDeclaredMethod("init");
        initMethod.setAccessible(true);
        initMethod.invoke(institutionsService);
    }

    @Test
    void uploadInstitutionsData_shouldValidateLogoUrlAgainstWhitelist() throws Exception {
        MultipartFile multipartFile = Mockito.mock(MultipartFile.class);

        // Set the whitelist with localhost host
        setWhitelistLogoUrls("localhost");

        // Valid case: logoUrl with host in the whitelist
        String jsonValid = "{\"logo\":\"https://localhost:8080/printit-blob/v1/logo.png\"}";
        institutionsService.uploadInstitutionsData(jsonValid, multipartFile);
        verify(institutionsClient, times(1)).updateInstitutions(any(), any());
        reset(institutionsClient);

        // Invalid case: host not present in the whitelist
        String jsonInvalid = "{\"logo\":\"https://malicious.com/logo.png\"}";
        AppException ex = assertThrows(AppException.class, () -> {
            institutionsService.uploadInstitutionsData(jsonInvalid, multipartFile);
        });
        assertEquals(AppError.INSTITUTION_DATA_UPLOAD_LOGO_NOT_ALLOWED_BAD_REQUEST.getTitle(), ex.getTitle());

        // Case with logoUrl without host (file path)
        String jsonNoHost = "{\"logo\":\"file:///etc/passwd\"}";
        ex = assertThrows(AppException.class, () -> {
            institutionsService.uploadInstitutionsData(jsonNoHost, multipartFile);
        });
        assertEquals(AppError.INSTITUTION_DATA_UPLOAD_LOGO_NOT_ALLOWED_BAD_REQUEST.getTitle(), ex.getTitle());

        // Malformed logoUrl case
        String jsonMalformed = "{\"logo\":\"ht!tp://bad-url\"}";
        ex = assertThrows(AppException.class, () -> {
            institutionsService.uploadInstitutionsData(jsonMalformed, multipartFile);
        });
        assertEquals(AppError.INSTITUTION_DATA_UPLOAD_LOGO_NOT_ALLOWED_BAD_REQUEST.getTitle(), ex.getTitle());

        // Case without logo (null or missing) - should pass without exceptions
        String jsonNoLogo = "{}";
        institutionsService.uploadInstitutionsData(jsonNoLogo, multipartFile);
        verify(institutionsClient, times(1)).updateInstitutions(any(), any());
    }


    @Test
    void shouldReturnMappedTemplatesDataOnSuccess() {
        MultipartFile multipartFile = Mockito.mock(MultipartFile.class);
        institutionsService.uploadInstitutionsData("",multipartFile);
        verify(institutionsClient).updateInstitutions(any(),any());
    }

    @Test
    void shouldReturnKOOnException() {
        doAnswer(item -> {
            throw new AppException(AppError.INSTITUTION_DATA_UPLOAD_ERROR);
        }).when(institutionsClient).updateInstitutions(any(),any());
        MultipartFile multipartFile = Mockito.mock(MultipartFile.class);
        AppException appException = assertThrows(AppException.class,
                () -> institutionsService.uploadInstitutionsData("", multipartFile));
        assertEquals(AppError.INSTITUTION_DATA_UPLOAD_ERROR.title, appException.getTitle());
        verify(institutionsClient).updateInstitutions(any(),any());
    }

    @Test
    void shouldReturnBadRequestOnDownstreamDependencyBadRequest() {
        FeignException.BadRequest badRequest = Mockito.mock(FeignException.BadRequest.class);
        doAnswer(item -> {
            throw badRequest;
        }).when(institutionsClient).updateInstitutions(any(),any());
        MultipartFile multipartFile = Mockito.mock(MultipartFile.class);
        AppException appException = assertThrows(AppException.class,
                () -> institutionsService.uploadInstitutionsData("", multipartFile));
        assertEquals(AppError.INSTITUTION_DATA_UPLOAD_BAD_REQUEST.title, appException.getTitle());
        assertEquals(AppError.INSTITUTION_DATA_UPLOAD_BAD_REQUEST.httpStatus, appException.getHttpStatus());
        assertEquals(AppError.INSTITUTION_DATA_UPLOAD_BAD_REQUEST.details, appException.getMessage());
        verify(institutionsClient).updateInstitutions(any(),any());
    }

    @Test
    void shouldReturnTableClientClientOnException() {
        doAnswer(item -> {
            throw new RuntimeException("test");
        }).when(institutionsClient).updateInstitutions(any(),any());
        MultipartFile multipartFile = Mockito.mock(MultipartFile.class);
        AppException appException = assertThrows(AppException.class,
                () -> institutionsService.uploadInstitutionsData("", multipartFile));
        assertEquals(AppError.INSTITUTION_DATA_UPLOAD_ERROR.title, appException.getTitle());
        verify(institutionsClient).updateInstitutions(any(),any());
    }

    @Test
    void shouldReturnCreditorInstitutionDataOnValidRequest() {
        when(institutionsClient.getInstitutionData(any())).thenReturn(
                InstitutionUploadData.builder().build());
        InstitutionUploadData uploadData = assertDoesNotThrow(
                () -> institutionsService.getInstitutionData("test"));
        assertNotNull(uploadData);
        verify(institutionsClient).getInstitutionData(any());
    }

    @Test
    void shouldThrowExceptionOnInstitutionDataRecoveryKO() {
        when(institutionsClient.getInstitutionData(any())).thenAnswer(item -> {
            throw new AppException(AppError.INSTITUTION_NOT_FOUND);
        });
        AppException appException = assertThrows(AppException.class,
                () -> institutionsService.getInstitutionData("test"));
        assertNotNull(appException);
        assertEquals(AppError.INSTITUTION_NOT_FOUND.title, appException.getTitle());
    }

    @Test
    void shouldThrowExceptionOnInstitutionDataRecoveryKOUnexpected() {
        when(institutionsClient.getInstitutionData(any())).thenAnswer(item -> {
            throw new RuntimeException("error");
        });
        AppException appException = assertThrows(AppException.class,
                () -> institutionsService.getInstitutionData("test"));
        assertNotNull(appException);
        assertEquals(AppError.INSTITUTION_RETRIEVE_ERROR.title, appException.getTitle());
    }

}