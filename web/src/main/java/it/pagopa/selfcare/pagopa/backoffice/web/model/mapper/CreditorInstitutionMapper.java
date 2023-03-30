package it.pagopa.selfcare.pagopa.backoffice.web.model.mapper;

import it.pagopa.selfcare.pagopa.backoffice.connector.model.station.CreditorInstitutionStation;
import it.pagopa.selfcare.pagopa.backoffice.web.model.creditorInstituions.CreditorInstitutionStationDto;
import it.pagopa.selfcare.pagopa.backoffice.web.model.creditorInstituions.CreditorInstitutionStationResource;

public class CreditorInstitutionMapper {

    public static CreditorInstitutionStation fromDto(CreditorInstitutionStationDto model){
        CreditorInstitutionStation station = null;
        if (model!= null){
            station = new CreditorInstitutionStation();
            station.setStationCode(model.getStationCode());
            station.setBroadcast(true);
            station.setMod4(true);
//            station.setSegregationCode(0l);
            station.setAuxDigit(1l);
        }
        return station;
    }

    public static CreditorInstitutionStationResource toResource(CreditorInstitutionStation model){
        CreditorInstitutionStationResource resource = null;
        if(model != null){//FIXME add missing fields
            resource = new CreditorInstitutionStationResource();
            resource.setStationCode(model.getStationCode());
            resource.setSegregationCode(model.getSegregationCode());
            resource.setMod4(model.getMod4());
            resource.setApplicationCode(model.getApplicationCode());
            resource.setAuxDigit(model.getAuxDigit());
            resource.setAssociatedCreditorInstitutions(model.getAssociatedCreditorInstitutions());
            resource.setEnabled(model.getEnabled());
            resource.setBroadcast(model.getBroadcast());
            resource.setVersion(model.getVersion());
        }
        return resource;
    }
}
