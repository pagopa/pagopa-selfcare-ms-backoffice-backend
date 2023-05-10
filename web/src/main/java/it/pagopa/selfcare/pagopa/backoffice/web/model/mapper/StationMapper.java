package it.pagopa.selfcare.pagopa.backoffice.web.model.mapper;

import it.pagopa.selfcare.pagopa.backoffice.connector.model.station.Station;
import it.pagopa.selfcare.pagopa.backoffice.connector.model.station.StationDetails;
import it.pagopa.selfcare.pagopa.backoffice.connector.model.station.Stations;
import it.pagopa.selfcare.pagopa.backoffice.connector.model.wrapper.WrapperEntitiesOperations;
import it.pagopa.selfcare.pagopa.backoffice.web.model.stations.StationDetailResource;
import it.pagopa.selfcare.pagopa.backoffice.web.model.stations.StationDetailsDto;
import it.pagopa.selfcare.pagopa.backoffice.web.model.stations.StationResource;
import it.pagopa.selfcare.pagopa.backoffice.web.model.stations.StationsResource;
import it.pagopa.selfcare.pagopa.backoffice.web.model.stations.*;
import org.mapstruct.Mapper;

//@Mapper
public interface StationMapper {

    StationDetailResource toResource(StationDetails model);

    StationsResource toResource(Stations model);

    StationResource toStationsResource(StationDetails model);

    StationResource toResource(Station model);

    StationDetails fromDto(StationDetailsDto model);

    StationDetails fromWrapperStationDetailsDto(WrapperStationDetailsDto model);

}
