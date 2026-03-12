package it.pagopa.selfcare.pagopa.backoffice.service;

import it.pagopa.selfcare.pagopa.backoffice.client.ApiConfigClient;
import it.pagopa.selfcare.pagopa.backoffice.model.configuration.PaymentType;
import it.pagopa.selfcare.pagopa.backoffice.model.configuration.PaymentTypes;
import org.junit.jupiter.api.Test;
import org.mockito.BDDMockito;
import org.mockito.Mockito;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;


class ConfigurationServiceTest {

    private final ApiConfigClient apiConfigClient = Mockito.mock(ApiConfigClient.class);

    private final ConfigurationService configurationService = new ConfigurationService(apiConfigClient);

    @Test
    void shouldReturnPaymentTypesAddingANYPaymentCodeType(){
        //pre-condition
        PaymentTypes paymentTypes = new PaymentTypes(
                List.of(new PaymentType("description","CP"))
        );
        given(apiConfigClient.getPaymentTypes()).willReturn(paymentTypes);
        PaymentTypes expectedPaymentTypeResponse = new PaymentTypes(
                Stream.concat(paymentTypes.getPaymentTypeList().stream(),
                        Stream.of(new PaymentType("","ANY"))).toList()
        );
        //test
        PaymentTypes responsePaymentType = configurationService.getPaymentTypes();

        //verifications
        verify(apiConfigClient, times(1)).getPaymentTypes();
        assertEquals(expectedPaymentTypeResponse, responsePaymentType);
    }
}