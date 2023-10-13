package it.pagopa.selfcare.pagopa.backoffice.web.model.mapper;

import it.pagopa.selfcare.pagopa.backoffice.connector.model.broker.BrokerDetails;
import it.pagopa.selfcare.pagopa.backoffice.connector.model.creditorInstitution.IbanLabel;
import it.pagopa.selfcare.pagopa.backoffice.connector.model.creditorInstitution.*;
import it.pagopa.selfcare.pagopa.backoffice.connector.model.station.CreditorInstitutionStationEdit;
import it.pagopa.selfcare.pagopa.backoffice.web.model.creditorInstituions.*;
import it.pagopa.selfcare.pagopa.backoffice.web.model.stations.BrokerDetailsResource;

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

    IbanResource toResource(IbanEnhanced model);
    IbansResource toResource(IbansEnhanced model);
    IbanCreate fromDto(IbanCreateRequestDto ibanCreateRequestDto);
    IbanResource toResource(IbanCreate model);
    IbanCreate toIbanCreate(IbanEnhanced ibanEnhanced);

    IbanLabel fromDto(it.pagopa.selfcare.pagopa.backoffice.web.model.creditorInstituions.IbanLabel dto);
    it.pagopa.selfcare.pagopa.backoffice.web.model.creditorInstituions.IbanLabel toResource(IbanLabel model);
}
