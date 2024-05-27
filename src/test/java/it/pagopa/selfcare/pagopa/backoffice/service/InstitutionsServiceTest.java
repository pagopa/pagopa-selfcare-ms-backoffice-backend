package it.pagopa.selfcare.pagopa.backoffice.service;

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

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

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
        institutionsService = new InstitutionsService(institutionsClient);
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

}