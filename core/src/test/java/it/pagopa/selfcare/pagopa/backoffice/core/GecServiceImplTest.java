package it.pagopa.selfcare.pagopa.backoffice.core;

import it.pagopa.selfcare.pagopa.backoffice.connector.api.GecConnector;
import it.pagopa.selfcare.pagopa.backoffice.connector.model.gec.BundleType;
import it.pagopa.selfcare.pagopa.backoffice.connector.model.gec.Bundles;
import it.pagopa.selfcare.pagopa.backoffice.connector.model.gec.Touchpoints;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.ArrayList;

import static it.pagopa.selfcare.pagopa.TestUtils.reflectionEqualsByName;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = GecServiceImpl.class)
class GecServiceImplTest {

    @Autowired
    private GecServiceImpl gecService;

    @MockBean
    private GecConnector gecConnectorMock;


    @Test
    void getBundlesByCI() {
        //given
        final Integer limit = 1;
        final Integer page = 1;
        final String cifiscalcode = "cifiscalcode";

        Bundles bundlesMock = mock(Bundles.class);
        when(gecConnectorMock.getBundlesByCI(any(), any(), any()))
                .thenReturn(bundlesMock);
        //when
        Bundles bundles = gecService.getBundlesByCI(cifiscalcode, limit, page);
        //then
        assertNotNull(bundles);
        assertEquals(bundlesMock, bundles);
        reflectionEqualsByName(bundlesMock, bundles);
        verify(gecConnectorMock, times(1))
                .getBundlesByCI(cifiscalcode, limit, page);
        verifyNoMoreInteractions(gecConnectorMock);
    }

    @Test
    void getTouchpoints() {
        //given
        final Integer limit = 1;
        final Integer page = 1;

        Touchpoints touchpointsMock = mock(Touchpoints.class);
        when(gecConnectorMock.getTouchpoints( any(), any()))
                .thenReturn(touchpointsMock);
        //when
        Touchpoints touchpoints = gecService.getTouchpoints(limit, page);
        //then
        assertNotNull(touchpoints);
        assertEquals(touchpointsMock, touchpoints);
        reflectionEqualsByName(touchpointsMock, touchpoints);
        verify(gecConnectorMock, times(1))
                .getTouchpoints(limit, page);
        verifyNoMoreInteractions(gecConnectorMock);
    }

    @Test
    void getBundlesByPSP() {
        //given
        final Integer limit = 1;
        final Integer page = 1;
        final String pspcode = "pspcode";
        final ArrayList<BundleType> boundleType = new ArrayList<>();
        final String name = "name";


        Bundles bundlesMock = mock(Bundles.class);
        when(gecConnectorMock.getBundlesByPSP(any(), any(), any(), any(), any()))
                .thenReturn(bundlesMock);
        //when
        Bundles bundles = gecService.getBundlesByPSP(pspcode, boundleType, name, limit, page);
        //then
        assertNotNull(bundles);
        assertEquals(bundlesMock, bundles);
        reflectionEqualsByName(bundlesMock, bundles);
        verify(gecConnectorMock, times(1))
                .getBundlesByPSP(pspcode, boundleType, name, limit, page);
        verifyNoMoreInteractions(gecConnectorMock);
    }
}
