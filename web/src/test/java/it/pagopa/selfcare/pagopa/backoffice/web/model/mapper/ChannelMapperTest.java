package it.pagopa.selfcare.pagopa.backoffice.web.model.mapper;

import it.pagopa.selfcare.pagopa.backoffice.connector.model.channel.*;
import it.pagopa.selfcare.pagopa.backoffice.web.model.channels.*;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
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
    void toPaymentTypesResource() {
        //given
        PaymentTypes model = mockInstance(new PaymentTypes());
        //when
        PaymentTypesResource resource = ChannelMapper.toResource(model);
        //then
        assertNotNull(resource);
        reflectionEqualsByName(model, resource);
    }

    @Test
    void toPaymentTypesResource_null() {
        //given
        PaymentTypes model = null;
        //when
        PaymentTypesResource resource = ChannelMapper.toResource(model);
        //then
        assertNull(resource);
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
        PspChannelPaymentTypes model2 = mockInstance(new PspChannelPaymentTypes());
        //when
        ChannelDetailsResource resource = ChannelMapper.toResource(model, model2);
        //then
        assertNotNull(resource);
        reflectionEqualsByName(model, resource);
    }

    @Test
    void toChannelDetailsResource_null() {
        //given
        ChannelDetails models = null;
        PspChannelPaymentTypes model2 = null;
        //when
        ChannelDetailsResource resources = ChannelMapper.toResource(models, model2);
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
        ChannelDetailsDto dto = null;
        //when
        ChannelDetails model = ChannelMapper.fromChannelDetailsDto(dto);
        //then
        assertNull(model);
    }

    @Test
    void toPspChannelPaymentTypesResource() {
        //given
        PspChannelPaymentTypes model = mockInstance(new PspChannelPaymentTypes());
        //when
        PspChannelPaymentTypesResource resource = ChannelMapper.toResource(model);
        //then
        assertNotNull(resource);
        reflectionEqualsByName(model, resource);
    }

    @Test
    void toPspChannelPaymentTypesResource_null() {
        //given
        PspChannelPaymentTypes models = null;
        //when
        PspChannelPaymentTypesResource resources = ChannelMapper.toResource(models);
        //then
        assertNull(resources);
    }

    @Test
    void toPaymentServiceProviders() {
        //given
        PaymentServiceProviders model = mockInstance(new PaymentServiceProviders());
        PaymentServiceProvider paymentServiceProvider = mockInstance(new PaymentServiceProvider());
        model.setPaymentServiceProviderList(List.of(paymentServiceProvider));
        //when
        PaymentServiceProvidersResource resource = ChannelMapper.toResource(model);
        //then
        assertNotNull(resource);
        reflectionEqualsByName(model, resource);
    }

    @Test
    void toPaymentServiceProviders_null() {
        //given
        PaymentServiceProviders models = null;
        //when
        PaymentServiceProvidersResource resources = ChannelMapper.toResource(models);
        //then
        assertNull(resources);
    }

    @Test
    void toPaymentServiceProvider() {
        //given
        PaymentServiceProvider model = mockInstance(new PaymentServiceProvider());
        //when
        PaymentServiceProviderResource resource = ChannelMapper.toResource(model);
        //then
        assertNotNull(resource);
        reflectionEqualsByName(model, resource);
    }

    @Test
    void toPaymentServiceProvider_null() {
        //given
        PaymentServiceProvider model = null;
        //when
        PaymentServiceProviderResource resource = ChannelMapper.toResource(model);
        //then
        assertNull(resource);
    }

    @Test
    void toChannelPspList() {
        //given
        ChannelPspList model = mockInstance(new ChannelPspList());
        ChannelPsp model2 = mockInstance(new ChannelPsp());
        model.setPsp(List.of(model2));
        //when
        ChannelPspListResource resource = ChannelMapper.toResource(model);
        //then
        assertNotNull(resource);
        reflectionEqualsByName(model, resource);
    }

    @Test
    void toChannelPspList_null() {
        //given
        ChannelPspList model = null;
        //when
        ChannelPspListResource resource = ChannelMapper.toResource(model);
        //then
        assertNull(resource);
    }

    @Test
    void toChannelPsp() {
        //given
        ChannelPsp model = mockInstance(new ChannelPsp());
        model.setPspCode("pspcode");
        model.setPaymentTypeList(new ArrayList<>());
        //when
        ChannelPspResource resource = ChannelMapper.toResource(model);
        //then
        assertNotNull(resource);
        reflectionEqualsByName(model, resource);
    }

    @Test
    void toChannelPsp_null() {
        //given
        ChannelPsp model = null;
        //when
        ChannelPspResource resource = ChannelMapper.toResource(model);
        //then
        assertNull(resource);
    }
}
