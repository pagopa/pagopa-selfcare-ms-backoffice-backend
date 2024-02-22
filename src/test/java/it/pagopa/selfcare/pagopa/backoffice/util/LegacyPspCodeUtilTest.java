package it.pagopa.selfcare.pagopa.backoffice.util;

import it.pagopa.selfcare.pagopa.backoffice.entity.PspLegacyEntity;
import it.pagopa.selfcare.pagopa.backoffice.exception.AppException;
import it.pagopa.selfcare.pagopa.backoffice.repository.PspLegacyRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class LegacyPspCodeUtilTest {

    private LegacyPspCodeUtil pspCodeUtil;

    private final PspLegacyRepository pspLegacyRepository;

    private final String PSP_PREFIX = "PSP";

    private final String TEST_CF = "TESTCF";

    private final String TEST_CF_LONG = "TESTCF00000001";

    public LegacyPspCodeUtilTest() {
        pspLegacyRepository = Mockito.mock(PspLegacyRepository.class);
    }

    @BeforeEach
    public void initMock() {
        Mockito.reset(pspLegacyRepository);
        this.pspCodeUtil = new LegacyPspCodeUtil(pspLegacyRepository);
    }

    @Test
    void taxCodeNotInRepositoryShouldThrowExceptionWhenMissingMappingDirect() {
        when(pspLegacyRepository.findByCf(any())).thenReturn(Optional.empty());
        assertThrows(AppException.class, () -> pspCodeUtil.retrievePspCode(TEST_CF, false));
        verify(pspLegacyRepository).findByCf(any());
    }

    @Test
    void taxCodeNotInRepositoryShouldThrowExceptionWhenMissingMappingNotDirect() {
        when(pspLegacyRepository.findByCf(any())).thenReturn(Optional.empty());
        assertThrows(AppException.class, () -> pspCodeUtil.retrievePspCode(TEST_CF, false));
        verify(pspLegacyRepository).findByCf(any());
    }

    @Test
    void taxCodeInRepositoryShouldProducePspCodeFromFirstAbiWhenMappingNotDirect() {
        when(pspLegacyRepository.findByCf(any())).thenReturn(Optional.of(getResultEntity()));
        String codeResult = pspCodeUtil.retrievePspCode(TEST_CF, false);
        assertNotNull(codeResult);
        assertEquals("ABI1", codeResult);
        verify(pspLegacyRepository).findByCf(any());
    }

    @Test
    void taxCodeInRepositoryShouldProducePspCodeFromFirstBicWhenMappingDirect() {
        when(pspLegacyRepository.findByCf(any())).thenReturn(Optional.of(getResultEntity()));
        String codeResult = pspCodeUtil.retrievePspCode(TEST_CF, true);
        assertNotNull(codeResult);
        assertEquals("BIC1", codeResult);
        verify(pspLegacyRepository).findByCf(any());
    }


    @Test
    void taxCodeInRepositoryShouldProducePspCodeFromFirstAbiWhenMappingDirectAndNullBicList() {
        PspLegacyEntity pspLegacyEntityWithoutBic = getResultEntity();
        pspLegacyEntityWithoutBic.setBic(null);
        when(pspLegacyRepository.findByCf(any())).thenReturn(Optional.of(pspLegacyEntityWithoutBic));
        String codeResult = pspCodeUtil.retrievePspCode(TEST_CF, false);
        assertNotNull(codeResult);
        assertEquals("ABI1", codeResult);
        verify(pspLegacyRepository).findByCf(any());
    }

    @Test
    void taxCodeInRepositoryShouldProducePspCodeFromFirstAbiWhenMappingDirectAndEmptyBicList() {
        PspLegacyEntity pspLegacyEntityWithoutBic = getResultEntity();
        pspLegacyEntityWithoutBic.setBic(Collections.emptyList());
        when(pspLegacyRepository.findByCf(any())).thenReturn(Optional.of(pspLegacyEntityWithoutBic));
        String codeResult = pspCodeUtil.retrievePspCode(TEST_CF, false);
        assertNotNull(codeResult);
        assertEquals("ABI1", codeResult);
        verify(pspLegacyRepository).findByCf(any());
    }

    @Test
    void taxCodeInRepositoryShouldProducePspCodeFromFirstBicWhenMappingNotDirectAndNullAbiList() {
        PspLegacyEntity pspLegacyEntityWithoutAbi = getResultEntity();
        pspLegacyEntityWithoutAbi.setAbi(null);
        when(pspLegacyRepository.findByCf(any())).thenReturn(Optional.of(pspLegacyEntityWithoutAbi));
        String codeResult = pspCodeUtil.retrievePspCode(TEST_CF, true);
        assertNotNull(codeResult);
        assertEquals("BIC1", codeResult);
        verify(pspLegacyRepository).findByCf(any());
    }

    @Test
    void taxCodeInRepositoryShouldProducePspCodeFromFirstBicWhenMappingNotDirectAndEmptyAbiList() {
        PspLegacyEntity pspLegacyEntityWithoutAbi = getResultEntity();
        pspLegacyEntityWithoutAbi.setAbi(Collections.emptyList());
        when(pspLegacyRepository.findByCf(any())).thenReturn(Optional.of(pspLegacyEntityWithoutAbi));
        String codeResult = pspCodeUtil.retrievePspCode(TEST_CF, true);
        assertNotNull(codeResult);
        assertEquals("BIC1", codeResult);
        verify(pspLegacyRepository).findByCf(any());
    }

    public PspLegacyEntity getResultEntity() {
        return PspLegacyEntity
                .builder()
                .cf(TEST_CF)
                .abi(List.of(new String[]{"ABI1", "AB12"}))
                .bic(List.of(new String[]{"BIC1", "BIC2"}))
                .build();
    }

}
