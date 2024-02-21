package it.pagopa.selfcare.pagopa.backoffice.service;

import it.pagopa.selfcare.pagopa.backoffice.client.GecClient;
import it.pagopa.selfcare.pagopa.backoffice.model.commissionbundle.client.BundlePaymentTypesDTO;
import it.pagopa.selfcare.pagopa.backoffice.model.commissionbundle.client.BundleRequest;
import it.pagopa.selfcare.pagopa.backoffice.model.commissionbundle.client.BundleType;
import it.pagopa.selfcare.pagopa.backoffice.model.commissionbundle.client.TouchpointsDTO;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.Collections;
import java.util.List;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest
class CommissionBundleServiceTest {

    public static final String PSP_CODE = "pspCode";
    public static final String PSP_NAME = "pspName";
    public static final int LIMIT = 50;
    public static final int PAGE = 0;
    public static final String ID_BUNDLE = "idBundle";
    @MockBean
    private GecClient client;
    @Autowired
    private CommissionBundleService service;

    @Test
    void getBundlesPaymentTypes() {
        when(client.getPaymenttypes(LIMIT, PAGE)).thenReturn(
                new BundlePaymentTypesDTO()
        );
        Assertions.assertDoesNotThrow(
                () -> service.getBundlesPaymentTypes(LIMIT, PAGE)
        );
    }

    @Test
    void getTouchpoints() {
        when(client.getTouchpoints(LIMIT, PAGE)).thenReturn(
                new TouchpointsDTO()
        );
        Assertions.assertDoesNotThrow(
                () -> service.getTouchpoints(LIMIT, PAGE)
        );
    }

    @Test
    void getBundlesByPSP() {
        List<BundleType> bundleTypeList = Collections.singletonList(BundleType.GLOBAL);
        Assertions.assertDoesNotThrow(
                () -> service.getBundlesByPSP(PSP_CODE, bundleTypeList, PSP_NAME, LIMIT, PAGE)
        );
        verify(client).getBundlesByPSP(PSP_CODE, bundleTypeList, PSP_NAME, LIMIT, PAGE);
    }

    @Test
    void createPSPBundle() {
        BundleRequest bundleRequest = new BundleRequest();
        Assertions.assertDoesNotThrow(
                () -> service.createPSPBundle(PSP_CODE, bundleRequest)
        );
        verify(client).createPSPBundle(PSP_CODE, bundleRequest);
    }

    @Test
    void getBundleDetailByPSP() {
        Assertions.assertDoesNotThrow(
                () -> service.getBundleDetailByPSP(PSP_CODE, ID_BUNDLE)
        );
        verify(client).getBundleDetailByPSP(PSP_CODE, ID_BUNDLE);
    }

    @Test
    void updatePSPBundle() {
        BundleRequest bundleRequest = new BundleRequest();
        Assertions.assertDoesNotThrow(
                () -> service.updatePSPBundle(PSP_CODE, ID_BUNDLE, bundleRequest)
        );
        verify(client).updatePSPBundle(PSP_CODE, ID_BUNDLE, bundleRequest);
    }

    @Test
    void deletePSPBundle() {
        Assertions.assertDoesNotThrow(
                () -> service.deletePSPBundle(PSP_CODE, ID_BUNDLE)
        );
        verify(client).deletePSPBundle(PSP_CODE, ID_BUNDLE);
    }
}