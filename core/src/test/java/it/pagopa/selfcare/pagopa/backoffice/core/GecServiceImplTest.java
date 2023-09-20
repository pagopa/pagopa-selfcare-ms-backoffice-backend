package it.pagopa.selfcare.pagopa.backoffice.core;

import it.pagopa.selfcare.pagopa.backoffice.connector.api.GecConnector;
import it.pagopa.selfcare.pagopa.backoffice.connector.model.channel.Channels;
import it.pagopa.selfcare.pagopa.backoffice.connector.model.gec.Bundles;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

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

        final String xRequestId = "xRequestId";
        Bundles bundlesMock = mock(Bundles.class);
        when(gecConnectorMock.getBundlesByCI(any(), any(), any(), any()))
                .thenReturn(bundlesMock);
        //when
        Bundles bundles = gecService.getBundlesByCI(cifiscalcode, limit, page, xRequestId);
        //then
        assertNotNull(bundles);
        assertEquals(bundlesMock, bundles);
        reflectionEqualsByName(bundlesMock, bundles);
        verify(gecConnectorMock, times(1))
                .getBundlesByCI(cifiscalcode, limit, page, xRequestId);
        verifyNoMoreInteractions(gecConnectorMock);
    }
}
