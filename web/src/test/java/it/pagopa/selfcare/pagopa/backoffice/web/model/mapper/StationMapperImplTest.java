package it.pagopa.selfcare.pagopa.backoffice.web.model.mapper;

import it.pagopa.selfcare.pagopa.backoffice.connector.model.PageInfo;
import it.pagopa.selfcare.pagopa.backoffice.connector.model.channel.WrapperEntitiesList;
import it.pagopa.selfcare.pagopa.backoffice.connector.model.station.Station;
import it.pagopa.selfcare.pagopa.backoffice.connector.model.station.StationDetails;
import it.pagopa.selfcare.pagopa.backoffice.connector.model.station.Stations;
import it.pagopa.selfcare.pagopa.backoffice.connector.model.wrapper.WrapperEntitiesOperations;
import it.pagopa.selfcare.pagopa.backoffice.web.model.stations.StationDetailResource;
import it.pagopa.selfcare.pagopa.backoffice.web.model.stations.StationResource;
import it.pagopa.selfcare.pagopa.backoffice.web.model.stations.StationsResource;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.util.ArrayList;
import java.util.List;

import static it.pagopa.selfcare.pagopa.TestUtils.*;
import static org.junit.jupiter.api.Assertions.*;

class StationMapperImplTest {
    private StationMapper mapper = Mappers.getMapper(StationMapper.class);

    @Test
    void toStationsResource() {
        //given
        Stations stations = mockInstance(new Stations());
        Station station = mockInstance(new Station());
        PageInfo pageInfo = mockInstance(new PageInfo());
        stations.setStationsList(List.of(station));
        stations.setPageInfo(pageInfo);
        //when
        StationsResource resource = mapper.toResource(stations);
        //then
        assertNotNull(resource);
        assertFalse(resource.getStationsList().isEmpty());
        assertNotNull(resource.getStationsList().get(0));
        checkNotNullFields(resource.getStationsList().get(0), "wrapperStatus");
        reflectionEqualsByName(stations, resource);
    }

    @Test
    void toStationsResource_null() {
        //given
        Stations stations = null;
        //when
        StationsResource resource = mapper.toResource(stations);
        //then
        assertNull(resource);
    }

    @Test
    void toStationDetailResource() {
        //given
        StationDetails stationDetails = mockInstance(new StationDetails());
        //when
        StationDetailResource resource = mapper.toResource(stationDetails);
        //then
        assertNotNull(resource);
        checkNotNullFields(resource, "wrapperStatus", "createdBy", "modifiedBy");
    }

    @Test
    void toStationDetailResource_null() {
        //given
        StationDetails stationDetails = null;
        //when
        StationDetailResource resource = mapper.toResource(stationDetails);
        //then
        assertNull(resource);
    }

    @Test
    void toStationResource() {
        //given
        Station station = mockInstance(new Station());
        //when
        StationResource resource = mapper.toResource(station);
        //then
        assertNotNull(resource);
        checkNotNullFields(resource, "wrapperStatus");
        reflectionEqualsByName(station, resource);
    }

    @Test
    void toStationResource_null() {
        //given
        Station station = null;
        //when
        StationResource resource = mapper.toResource(station);
        //then
        assertNull(resource);
    }
    @Test
    void fromStationDetails() {
        //given
        StationDetails stationDetails = mockInstance(new StationDetails());
        //when
        Station resource = mapper.fromStationDetails(stationDetails);
        //then
        assertNotNull(resource);
        checkNotNullFields(resource);
    }

    @Test
    void fromWrapperEntitiesList() {
        //given
        WrapperEntitiesList wrapperEntitiesList = mockInstance(new WrapperEntitiesList());
        List<WrapperEntitiesOperations<?>> wrapperEntities = mockInstance(new ArrayList<>());
        wrapperEntitiesList.setWrapperEntities(wrapperEntities);
        //when
        Stations resource = mapper.fromWrapperEntitiesList(wrapperEntitiesList);
        //then
        assertNotNull(resource);
        checkNotNullFields(resource);
    }

    @Test
    void toResourceListFromWrapperEntities() {
        //given
        WrapperEntitiesList wrapperEntitiesList = mockInstance(new WrapperEntitiesList());
        List<WrapperEntitiesOperations<?>> wrapperEntities = mockInstance(new ArrayList<>());
        wrapperEntitiesList.setWrapperEntities(wrapperEntities);
        //when
        List<StationResource> resource = mapper.toResourceList(wrapperEntitiesList);
        //then
        assertNotNull(resource);
        checkNotNullFields(resource);
    }

    @Test
    void toResourceList() {
        //given
        Stations stations = mockInstance(new Stations());
        List<Station> stationList = new ArrayList<>();
        stations.setStationsList(stationList);
        //when
        List<StationResource> resource = mapper.toResourceList(stations);
        //then
        assertNotNull(resource);
        checkNotNullFields(resource);
    }
}
