package it.pagopa.selfcare.pagopa.backoffice.web.model.stations;


import it.pagopa.selfcare.pagopa.backoffice.web.model.creditorinstituions.CreditorInstitutionDetailsResource;
import lombok.Data;

@Data
public class BrokerAndEcDetailsResource {

    private BrokerResource brokerDetailsResource;

    private CreditorInstitutionDetailsResource creditorInstitutionDetailsResource;
}
