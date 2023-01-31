package it.pagopa.selfcare.pagopa.backoffice.web.model.mapper;

import it.pagopa.selfcare.pagopa.backoffice.connector.model.channel.Channel;
import it.pagopa.selfcare.pagopa.backoffice.connector.model.channel.ChannelDetails;
import it.pagopa.selfcare.pagopa.backoffice.connector.model.channel.Channels;
import it.pagopa.selfcare.pagopa.backoffice.web.model.channels.ChannelDetailsDto;
import it.pagopa.selfcare.pagopa.backoffice.web.model.channels.ChannelDetailsResource;
import it.pagopa.selfcare.pagopa.backoffice.web.model.channels.ChannelResource;
import it.pagopa.selfcare.pagopa.backoffice.web.model.channels.ChannelsResource;
import org.junit.jupiter.api.Test;

import java.util.List;

import static it.pagopa.selfcare.pagopa.TestUtils.mockInstance;
import static it.pagopa.selfcare.pagopa.TestUtils.reflectionEqualsByName;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

class ChannelMapperTest {

    @Test
    void toChannelResource_null() {
        //given
        Channel model = null;
        //when
        ChannelResource resource = ChannelMapper.toResource(model);
        //then
        assertNull(resource);
    }

    @Test
    void toChannelResource() {
        //given
        Channel model = mockInstance(new Channel());

        //when
        ChannelResource resource = ChannelMapper.toResource(model);
        //then
        assertNotNull(resource);
        reflectionEqualsByName(model, resource);
    }

    @Test
    void toChannelsResource_null() {
        //given
        Channels models = null;
        //when
        ChannelsResource resources = ChannelMapper.toResource(models);
        //then
        assertNull(resources);
    }

    @Test
    void toChannelsResource() {
        //given
        Channels model = mockInstance(new Channels());
        model.setChannelList(List.of(new Channel()));
        //when
        ChannelsResource resource = ChannelMapper.toResource(model);
        //then
        assertNotNull(resource);
        reflectionEqualsByName(model, resource);
    }

    @Test
    void toChannelDetailsResource() {
        //given
        ChannelDetails model = mockInstance(new ChannelDetails());
        //when
        ChannelDetailsResource resource = ChannelMapper.toResource(model);
        //then
        assertNotNull(resource);
        reflectionEqualsByName(model, resource);
    }

    @Test
    void toChannelDetailsResource_null() {
        //given
        ChannelDetails models = null;
        //when
        ChannelDetailsResource resources = ChannelMapper.toResource(models);
        //then
        assertNull(resources);
    }

    @Test
    void fromChannelDetailsDto() {
        //given
        ChannelDetailsDto dto = mockInstance(new ChannelDetailsDto());
        //when
        ChannelDetails model = ChannelMapper.fromChannelDetailsDto(dto);
        //then
        assertNotNull(model);
        reflectionEqualsByName(dto, model);
    }

    @Test
    void fromChannelDetailsDto_null() {
        //given
        ChannelDetailsDto dto =  null;
        //when
        ChannelDetails model =  ChannelMapper.fromChannelDetailsDto(dto);
        //then
        assertNull(model);
    }
}
