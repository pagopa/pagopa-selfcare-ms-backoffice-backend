package it.pagopa.selfcare.pagopa.backoffice.web.model.mapper;

import it.pagopa.selfcare.pagopa.backoffice.connector.model.channel.WrapperEntitiesList;
import it.pagopa.selfcare.pagopa.backoffice.connector.model.station.Station;
import it.pagopa.selfcare.pagopa.backoffice.connector.model.station.StationDetails;
import it.pagopa.selfcare.pagopa.backoffice.connector.model.station.StationDetailsList;
import it.pagopa.selfcare.pagopa.backoffice.connector.model.station.Stations;
import it.pagopa.selfcare.pagopa.backoffice.connector.model.wrapper.WrapperEntityOperations;
import it.pagopa.selfcare.pagopa.backoffice.connector.model.wrapper.WrapperStation;
import it.pagopa.selfcare.pagopa.backoffice.connector.model.wrapper.WrapperStations;
import it.pagopa.selfcare.pagopa.backoffice.connector.model.wrapper.WrapperStatus;
import it.pagopa.selfcare.pagopa.backoffice.web.model.stations.StationDetailResource;
import it.pagopa.selfcare.pagopa.backoffice.web.model.stations.StationDetailsDto;
import it.pagopa.selfcare.pagopa.backoffice.web.model.stations.StationResource;
import it.pagopa.selfcare.pagopa.backoffice.web.model.stations.StationsResource;
import it.pagopa.selfcare.pagopa.backoffice.web.model.stations.*;

import java.util.List;

//@Mapper
public interface StationMapper {

    StationDetailResource toResource(StationDetails model);

    StationDetailResource toResource(StationDetails model, WrapperStatus status, String createdBy, String modifiedBy);

    StationsResource toResource(Stations model);

    StationResource toStationsResource(StationDetails model);

    Station fromStationDetails(StationDetails model);

    List<StationResource> toResourceList(WrapperEntitiesList wrapperEntitiesList);

    List<StationResource> toResourceList(Stations stations);

    StationResource toResource(Station model);

    StationDetails fromDto(StationDetailsDto model);

    StationDetails fromWrapperStationDetailsDto(WrapperStationDetailsDto model);

    Stations fromWrapperEntitiesList(WrapperEntitiesList wrapperEntitiesList);

    StationDetailsResourceList fromStationDetailsList(StationDetailsList model);
    WrapperStation toWrapperStation(Station station);

    WrapperStations toWrapperStations(Stations station);

    WrapperStations toWrapperStations(WrapperEntitiesList wrapperEntitiesList);

    WrapperStation toWrapperStation(WrapperEntityOperations<StationDetails> wrapperEntitiesList);

    WrapperStationsResource toWrapperStationsResource(WrapperStations wrapperStations);

    WrapperStationResource toWrapperStationResource(WrapperStation wrapperStation);

}
