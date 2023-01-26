package it.pagopa.selfcare.pagopa.backoffice.core;

import it.pagopa.selfcare.pagopa.backoffice.connector.api.ApiConfigConnector;
import it.pagopa.selfcare.pagopa.backoffice.connector.model.channel.Channels;
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
@ContextConfiguration(classes = ApiConfigServiceImpl.class)
class ApiConfigServiceImplTest {

    @Autowired
    private ApiConfigServiceImpl apiConfigService;

    @MockBean
    private ApiConfigConnector apiConfigConnectorMock;

    @Test
    void getChannels_nullPage() {
        //given
        final Integer limit = 1;
        final Integer page = null;
        final String code = "code";
        final String sort = "sort";
        final String xRequestId = "xRequestId";
        //when
        apiConfigConnectorMock.getChannels(limit, page, code, sort, xRequestId);
        //then
        verify(apiConfigConnectorMock, times(1))
                .getChannels(limit, page, code, sort, xRequestId);
        verifyNoMoreInteractions(apiConfigConnectorMock);
    }

    @Test
    void getChannels() {
        //given
        final Integer limit = 1;
        final Integer page = 1;
        final String code = "code";
        final String sort = "sort";
        final String xRequestId = "xRequestId";
        Channels channelsMock = mock(Channels.class);
        when(apiConfigConnectorMock.getChannels(any(), any(), any(), any(), any()))
                .thenReturn(channelsMock);
        //when
        Channels channels = apiConfigService.getChannels(limit, page, code, sort, xRequestId);
        //then
        assertNotNull(channels);
        assertEquals(channelsMock, channels);
        reflectionEqualsByName(channelsMock, channels);
        verify(apiConfigConnectorMock, times(1))
                .getChannels(limit, page, code, sort, xRequestId);
        verifyNoMoreInteractions(apiConfigConnectorMock);
    }


}
