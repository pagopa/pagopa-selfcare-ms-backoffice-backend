package it.pagopa.selfcare.pagopa.backoffice.web.model.mapper;

import it.pagopa.selfcare.pagopa.backoffice.connector.model.creditorInstitution.CreditorInstitutionDetails;
import it.pagopa.selfcare.pagopa.backoffice.connector.model.station.CreditorInstitutionStationEdit;
import it.pagopa.selfcare.pagopa.backoffice.web.model.creditorInstituions.CreditorInstitutionDetailsResource;
import it.pagopa.selfcare.pagopa.backoffice.web.model.creditorInstituions.CreditorInstitutionDto;
import it.pagopa.selfcare.pagopa.backoffice.web.model.creditorInstituions.CreditorInstitutionStationDto;
import it.pagopa.selfcare.pagopa.backoffice.web.model.creditorInstituions.CreditorInstitutionStationEditResource;

//@Mapper
public interface CreditorInstitutionMapper {

    CreditorInstitutionStationEdit fromDto(CreditorInstitutionStationDto dto);
    CreditorInstitutionDetails fromDto(CreditorInstitutionDto dto);
    CreditorInstitutionDetailsResource toResource(CreditorInstitutionDetails model);
    CreditorInstitutionStationEditResource toResource(CreditorInstitutionStationEdit model);
}
