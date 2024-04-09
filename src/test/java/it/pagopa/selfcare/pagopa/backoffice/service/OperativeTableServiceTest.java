package it.pagopa.selfcare.pagopa.backoffice.service;

import it.pagopa.selfcare.pagopa.backoffice.config.MappingsConfiguration;
import it.pagopa.selfcare.pagopa.backoffice.entity.TavoloOpEntity;
import it.pagopa.selfcare.pagopa.backoffice.exception.AppError;
import it.pagopa.selfcare.pagopa.backoffice.exception.AppException;
import it.pagopa.selfcare.pagopa.backoffice.model.tavoloop.TavoloOpDto;
import it.pagopa.selfcare.pagopa.backoffice.model.tavoloop.TavoloOpResource;
import it.pagopa.selfcare.pagopa.backoffice.model.tavoloop.TavoloOpResourceList;
import it.pagopa.selfcare.pagopa.backoffice.repository.TavoloOpRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.AuditorAware;

import java.time.Instant;
import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@SpringBootTest(classes = {OperativeTableService.class, MappingsConfiguration.class})
class OperativeTableServiceTest {

    @MockBean
    private AuditorAware<String> auditorAware;

    @MockBean
    private TavoloOpRepository tavoloOpRepository;

    @Autowired
    private OperativeTableService sut;

    @Test
    void getOperativeTableListSuccess() {
        TavoloOpEntity entity = buildTavoloOpEntity();
        when(tavoloOpRepository.findAll()).thenReturn(Collections.singletonList(entity));

        TavoloOpResourceList result = assertDoesNotThrow(() -> sut.getOperativeTables());

        assertNotNull(result);
        assertEquals(1, result.getTavoloOpResourceList().size());

        TavoloOpResource resource = result.getTavoloOpResourceList().get(0);
        assertEquals(entity.getName(), resource.getName());
        assertEquals(entity.getEmail(), resource.getEmail());
        assertEquals(entity.getCreatedAt(), resource.getCreatedAt());
        assertEquals(entity.getReferent(), resource.getReferent());
        assertEquals(entity.getModifiedBy(), resource.getModifiedBy());
        assertEquals(entity.getModifiedAt(), resource.getModifiedAt());
        assertEquals(entity.getTelephone(), resource.getTelephone());
        assertEquals(entity.getTaxCode(), resource.getTaxCode());
    }

    @Test
    void getOperativeTableSuccess() {
        TavoloOpEntity entity = buildTavoloOpEntity();
        when(tavoloOpRepository.findByTaxCode(anyString())).thenReturn(Optional.of(entity));

        TavoloOpResource result = assertDoesNotThrow(() -> sut.getOperativeTable(anyString()));

        assertNotNull(result);
        assertEquals(entity.getName(), result.getName());
        assertEquals(entity.getEmail(), result.getEmail());
        assertEquals(entity.getCreatedAt(), result.getCreatedAt());
        assertEquals(entity.getReferent(), result.getReferent());
        assertEquals(entity.getModifiedBy(), result.getModifiedBy());
        assertEquals(entity.getModifiedAt(), result.getModifiedAt());
        assertEquals(entity.getTelephone(), result.getTelephone());
        assertEquals(entity.getTaxCode(), result.getTaxCode());
    }

    @Test
    void getOperativeTableNotFound() {
        when(tavoloOpRepository.findByTaxCode(anyString())).thenReturn(Optional.empty());

        AppException e = assertThrows(AppException.class, () -> sut.getOperativeTable(anyString()));

        assertNotNull(e);
        assertEquals(AppError.OPERATIVE_TABLE_NOT_FOUND.getHttpStatus(), e.getHttpStatus());
        assertEquals(AppError.OPERATIVE_TABLE_NOT_FOUND.getTitle(), e.getTitle());
    }

    @Test
    void insertOperativeTable() {
        TavoloOpDto dto = buildTavoloOpDto();

        assertDoesNotThrow(() -> sut.insertOperativeTable(dto));
    }

    @Test
    void updateOperativeTable() {
        TavoloOpDto dto = buildTavoloOpDto();

        assertDoesNotThrow(() -> sut.updateOperativeTable(dto));
    }

    private TavoloOpEntity buildTavoloOpEntity() {
        TavoloOpEntity entity = new TavoloOpEntity();
        entity.setName("Name");
        entity.setEmail("email");
        entity.setCreatedAt(Instant.now());
        entity.setReferent("referent");
        entity.setModifiedAt(Instant.now());
        entity.setModifiedBy("modifier");
        entity.setTelephone("12234545");
        entity.setTaxCode("adf78f89s9890as");
        return entity;
    }

    private TavoloOpDto buildTavoloOpDto() {
        TavoloOpDto dto = new TavoloOpDto();
        dto.setName("Name");
        dto.setEmail("email");
        dto.setReferent("referent");
        dto.setTelephone("12234545");
        dto.setTaxCode("adf78f89s9890as");
        return dto;
    }
}