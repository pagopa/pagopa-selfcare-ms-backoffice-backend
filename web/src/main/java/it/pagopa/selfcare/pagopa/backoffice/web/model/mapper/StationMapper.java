package it.pagopa.selfcare.pagopa.backoffice.web.model.mapper;

import it.pagopa.selfcare.pagopa.backoffice.connector.model.channel.WrapperEntitiesList;
import it.pagopa.selfcare.pagopa.backoffice.connector.model.station.Station;
import it.pagopa.selfcare.pagopa.backoffice.connector.model.station.StationDetails;
import it.pagopa.selfcare.pagopa.backoffice.connector.model.station.Stations;
import it.pagopa.selfcare.pagopa.backoffice.connector.model.wrapper.WrapperEntitiesOperations;
import it.pagopa.selfcare.pagopa.backoffice.connector.model.wrapper.WrapperEntityOperations;
import it.pagopa.selfcare.pagopa.backoffice.web.model.stations.StationDetailResource;
import it.pagopa.selfcare.pagopa.backoffice.web.model.stations.StationDetailsDto;
import it.pagopa.selfcare.pagopa.backoffice.web.model.stations.StationResource;
import it.pagopa.selfcare.pagopa.backoffice.web.model.stations.StationsResource;
import it.pagopa.selfcare.pagopa.backoffice.web.model.stations.*;
import org.mapstruct.Mapper;

import java.util.List;

//@Mapper
public interface StationMapper {

    StationDetailResource toResource(StationDetails model);

    StationsResource toResource(Stations model);

    StationResource toStationsResource(StationDetails model);

    Station fromStationDetails(StationDetails model);

    List<StationResource> toResourceList(WrapperEntitiesList wrapperEntitiesList);

    List<StationResource> toResourceList(Stations stations);

    StationResource toResource(Station model);

    StationDetails fromDto(StationDetailsDto model);

    StationDetails fromWrapperStationDetailsDto(WrapperStationDetailsDto model);

    Stations fromWrapperEntitiesList(WrapperEntitiesList wrapperEntitiesList);

}
