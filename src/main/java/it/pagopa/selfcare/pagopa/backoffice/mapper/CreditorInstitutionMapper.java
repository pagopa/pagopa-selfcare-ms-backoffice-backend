package it.pagopa.selfcare.pagopa.backoffice.mapper;

import it.pagopa.selfcare.pagopa.backoffice.model.connector.broker.BrokerDetails;
import it.pagopa.selfcare.pagopa.backoffice.model.connector.creditorinstitution.CreditorInstitution;
import it.pagopa.selfcare.pagopa.backoffice.model.connector.creditorinstitution.CreditorInstitutionAddress;
import it.pagopa.selfcare.pagopa.backoffice.model.connector.creditorinstitution.CreditorInstitutionDetails;
import it.pagopa.selfcare.pagopa.backoffice.model.connector.creditorinstitution.CreditorInstitutions;
import it.pagopa.selfcare.pagopa.backoffice.model.connector.station.CreditorInstitutionStationEdit;
import it.pagopa.selfcare.pagopa.backoffice.model.creditorinstituions.*;
import it.pagopa.selfcare.pagopa.backoffice.model.stations.BrokerDetailsResource;

//@Mapper
public interface CreditorInstitutionMapper {

    BrokerDetails fromDto(BrokerEcDto dto);

    CreditorInstitutionStationEdit fromDto(CreditorInstitutionStationDto dto);

    CreditorInstitutionDetails fromDto(CreditorInstitutionDto dto);

    BrokerDetailsResource toResource(BrokerDetails model);

    CreditorInstitutionDetailsResource toResource(CreditorInstitutionDetails model);

    CreditorInstitutionStationEditResource toResource(CreditorInstitutionStationEdit model);

    CreditorInstitutionAddressResource toResource(CreditorInstitutionAddress creditorInstitutionAddress);

    CreditorInstitutionAddress fromDto(CreditorInstitutionAddressDto creditorInstitutionAddressDto);

    CreditorInstitutionDetails fromDto(UpdateCreditorInstitutionDto dto);

    CreditorInstitutionResource toResorce(CreditorInstitution model);

    CreditorInstitutionsResource toResource(CreditorInstitutions model);

}
