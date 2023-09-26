package it.pagopa.selfcare.pagopa.backoffice.web.model.stations;


import it.pagopa.selfcare.pagopa.backoffice.web.model.channels.PaymentServiceProviderDetailsResource;
import it.pagopa.selfcare.pagopa.backoffice.web.model.creditorInstituions.CreditorInstitutionDetailsResource;
import lombok.Data;

@Data
public class BrokerOrEcDetailsResource {

    private BrokerDetailsResource brokerDetailsResource;

    private CreditorInstitutionDetailsResource creditorInstitutionDetailsResource;
}
