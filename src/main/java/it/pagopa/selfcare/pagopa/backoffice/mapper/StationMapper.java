package it.pagopa.selfcare.pagopa.backoffice.mapper;

import it.pagopa.selfcare.pagopa.backoffice.entity.WrapperEntityOperations;
import it.pagopa.selfcare.pagopa.backoffice.model.connector.channel.WrapperEntitiesList;
import it.pagopa.selfcare.pagopa.backoffice.model.connector.channel.WrapperStationList;
import it.pagopa.selfcare.pagopa.backoffice.model.connector.station.Station;
import it.pagopa.selfcare.pagopa.backoffice.model.connector.station.StationDetails;
import it.pagopa.selfcare.pagopa.backoffice.model.connector.station.StationDetailsList;
import it.pagopa.selfcare.pagopa.backoffice.model.connector.station.Stations;
import it.pagopa.selfcare.pagopa.backoffice.model.connector.wrapper.WrapperStation;
import it.pagopa.selfcare.pagopa.backoffice.model.connector.wrapper.WrapperStations;
import it.pagopa.selfcare.pagopa.backoffice.model.connector.wrapper.WrapperStatus;
import it.pagopa.selfcare.pagopa.backoffice.model.stations.*;

import java.time.Instant;
import java.util.List;

//@Mapper
public interface StationMapper {

    StationDetailResource toResource(StationDetails model);

    StationDetailResource toResource(StationDetails model, WrapperStatus status, String createdBy, String modifiedBy, Instant createdAt, String note);

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

    WrapperStations toWrapperStations(WrapperStationList wrapperEntitiesList);

    WrapperStation toWrapperStation(WrapperEntityOperations<StationDetails> wrapperEntitiesList);

    WrapperStationsResource toWrapperStationsResource(WrapperStations wrapperStations);

    WrapperStationResource toWrapperStationResource(WrapperStation wrapperStation);

}
