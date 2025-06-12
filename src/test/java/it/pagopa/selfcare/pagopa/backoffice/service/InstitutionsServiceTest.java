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
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class InstitutionsServiceTest {

    @Mock
    private InstitutionsClient institutionsClient;

    private InstitutionsService institutionsService;

    @BeforeEach
    void init() {
        Mockito.reset(institutionsClient);
        Set<String> whitelist = Set.of("https://localhost:8080/printit-blob/v1/");
        institutionsService = new InstitutionsService(institutionsClient, whitelist);
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
    void uploadInstitutionsData_shouldValidateLogoUrlAgainstWhitelist() {
        MultipartFile multipartFile = Mockito.mock(MultipartFile.class);


        // ✅ Valid prefix - should pass
        String validJson = "{\"logo\":\"https://localhost:8080/printit-blob/v1/logo.png\"}";
        institutionsService.uploadInstitutionsData(validJson, multipartFile);
        verify(institutionsClient).updateInstitutions(any(), any());
        reset(institutionsClient);

        // ❌ Invalid prefix - should fail
        String invalidJson = "{\"logo\":\"https://malicious.com/logo.png\"}";
        AppException ex = assertThrows(AppException.class,
                () -> institutionsService.uploadInstitutionsData(invalidJson, multipartFile));
        assertEquals(AppError.INSTITUTION_DATA_UPLOAD_LOGO_NOT_ALLOWED_BAD_REQUEST.getTitle(), ex.getTitle());

        // ❌ Malformed URL - should fail
        String malformedJson = "{\"logo\":\"ht!tp://bad-url\"}";
        ex = assertThrows(AppException.class,
                () -> institutionsService.uploadInstitutionsData(malformedJson, multipartFile));
        assertEquals(AppError.INSTITUTION_DATA_UPLOAD_LOGO_NOT_ALLOWED_BAD_REQUEST.getTitle(), ex.getTitle());

        // ✅ No logo field - should pass
        String noLogoJson = "{}";
        institutionsService.uploadInstitutionsData(noLogoJson, multipartFile);
        verify(institutionsClient).updateInstitutions(any(), any());

        // ✅ Logo is null - should pass
        String nullLogoJson = "{\"logo\":null}";
        institutionsService.uploadInstitutionsData(nullLogoJson, multipartFile);
        verify(institutionsClient, times(2)).updateInstitutions(any(), any());  // chiamata 2 volte in totale
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