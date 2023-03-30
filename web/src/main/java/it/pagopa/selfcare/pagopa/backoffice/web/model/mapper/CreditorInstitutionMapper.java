package it.pagopa.selfcare.pagopa.backoffice.web.model.mapper;

import it.pagopa.selfcare.pagopa.backoffice.connector.model.station.CreditorInstitutionStationEdit;
import it.pagopa.selfcare.pagopa.backoffice.web.model.creditorInstituions.CreditorInstitutionStationDto;
import it.pagopa.selfcare.pagopa.backoffice.web.model.creditorInstituions.CreditorInstitutionStationEditResource;

public class CreditorInstitutionMapper {

    public static CreditorInstitutionStationEdit fromDto(CreditorInstitutionStationDto model){
        CreditorInstitutionStationEdit station = null;
        if (model!= null){
            station = new CreditorInstitutionStationEdit();
            station.setStationCode(model.getStationCode());
            station.setBroadcast(true);
            station.setMod4(true);
            station.setSegregationCode(1l);
            station.setAuxDigit(1l);
            station.setApplicationCode(1l);
        }
        return station;
    }

    public static CreditorInstitutionStationEditResource toResource(CreditorInstitutionStationEdit model){
        CreditorInstitutionStationEditResource resource = null;
        if(model != null){
            resource = new CreditorInstitutionStationEditResource();
            resource.setStationCode(model.getStationCode());
            resource.setSegregationCode(model.getSegregationCode());
            resource.setMod4(model.getMod4());
            resource.setApplicationCode(model.getApplicationCode());
            resource.setAuxDigit(model.getAuxDigit());
            resource.setBroadcast(model.getBroadcast());
        }
        return resource;
    }
}
