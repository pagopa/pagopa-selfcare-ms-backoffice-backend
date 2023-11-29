package it.pagopa.selfcare.pagopa.backoffice.model.channels;

import lombok.Data;

@Data
public class BrokerOrPspDetailsResource {

    private BrokerPspDetailsResource brokerPspDetailsResource;

    private PaymentServiceProviderDetailsResource paymentServiceProviderDetailsResource;

}
