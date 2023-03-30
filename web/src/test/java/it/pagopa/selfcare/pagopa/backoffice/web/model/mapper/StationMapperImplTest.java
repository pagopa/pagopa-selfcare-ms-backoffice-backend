package it.pagopa.selfcare.pagopa.backoffice.web.model.mapper;

import it.pagopa.selfcare.pagopa.backoffice.connector.model.PageInfo;
import it.pagopa.selfcare.pagopa.backoffice.connector.model.station.Station;
import it.pagopa.selfcare.pagopa.backoffice.connector.model.station.StationDetails;
import it.pagopa.selfcare.pagopa.backoffice.connector.model.station.Stations;
import it.pagopa.selfcare.pagopa.backoffice.web.model.stations.StationDetailResource;
import it.pagopa.selfcare.pagopa.backoffice.web.model.stations.StationResource;
import it.pagopa.selfcare.pagopa.backoffice.web.model.stations.StationsResource;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

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
        checkNotNullFields(resource.getStationsList().get(0));
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
        checkNotNullFields(resource);
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
        checkNotNullFields(resource);
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
}
