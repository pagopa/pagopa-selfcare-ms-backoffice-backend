package it.pagopa.selfcare.pagopa.backoffice.model.stations;


import it.pagopa.selfcare.pagopa.backoffice.model.creditorinstituions.CreditorInstitutionDetailsResource;
import lombok.Data;

@Data
public class BrokerAndEcDetailsResource {

    private BrokerResource brokerDetailsResource;

    private CreditorInstitutionDetailsResource creditorInstitutionDetailsResource;
}
