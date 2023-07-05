package it.pagopa.selfcare.pagopa.backoffice.web.model.mapper;

import it.pagopa.selfcare.pagopa.backoffice.connector.model.creditorInstitution.*;
import it.pagopa.selfcare.pagopa.backoffice.connector.model.creditorInstitution.IbanLabel;
import it.pagopa.selfcare.pagopa.backoffice.connector.model.station.CreditorInstitutionStationEdit;
import it.pagopa.selfcare.pagopa.backoffice.web.model.creditorInstituions.*;

//@Mapper
public interface CreditorInstitutionMapper {

    CreditorInstitutionStationEdit fromDto(CreditorInstitutionStationDto dto);
    CreditorInstitutionDetails fromDto(CreditorInstitutionDto dto);
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

    IbanLabel fromDto(it.pagopa.selfcare.pagopa.backoffice.web.model.creditorInstituions.IbanLabel dto);
    it.pagopa.selfcare.pagopa.backoffice.web.model.creditorInstituions.IbanLabel toResource(IbanLabel model);
}
