package it.pagopa.selfcare.pagopa.backoffice.web.model.mapper;

import it.pagopa.selfcare.pagopa.backoffice.connector.model.broker.BrokerDetails;
import it.pagopa.selfcare.pagopa.backoffice.connector.model.channel.WrapperEntitiesList;
import it.pagopa.selfcare.pagopa.backoffice.connector.model.station.Station;
import it.pagopa.selfcare.pagopa.backoffice.connector.model.station.StationDetails;
import it.pagopa.selfcare.pagopa.backoffice.connector.model.station.StationDetailsList;
import it.pagopa.selfcare.pagopa.backoffice.connector.model.station.Stations;
import it.pagopa.selfcare.pagopa.backoffice.connector.model.wrapper.WrapperEntityOperations;
import it.pagopa.selfcare.pagopa.backoffice.connector.model.wrapper.WrapperStation;
import it.pagopa.selfcare.pagopa.backoffice.connector.model.wrapper.WrapperStations;
import it.pagopa.selfcare.pagopa.backoffice.connector.model.wrapper.WrapperStatus;
import it.pagopa.selfcare.pagopa.backoffice.web.model.stations.*;

import javax.annotation.processing.Generated;
import java.time.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Generated(
        value = "org.mapstruct.ap.MappingProcessor",
        date = "2023-03-07T19:24:17+0100",
        comments = "version: 1.5.3.Final, compiler: javac, environment: Java 11.0.17 (OpenLogic)"
)
public class StationMapperImpl implements StationMapper {

    @Override
    public StationDetailResource toResource(StationDetails model) {
        if (model == null) {
            return null;
        }

        StationDetailResource stationDetailResource = new StationDetailResource();

        stationDetailResource.setStationCode(model.getStationCode());
        stationDetailResource.setEnabled(model.getEnabled());
        stationDetailResource.setBrokerDescription(model.getBrokerDescription());
        stationDetailResource.setVersion(model.getVersion());
        stationDetailResource.setStationStatus(model.getStationStatus());
        stationDetailResource.setActivationDate(model.getActivationDate());
        stationDetailResource.setCreatedAt(model.getCreatedAt());
        stationDetailResource.setModifiedAt(model.getModifiedAt());
        stationDetailResource.setAssociatedCreditorInstitutions(model.getAssociatedCreditorInstitutions());
        stationDetailResource.setIp(model.getIp());
        stationDetailResource.setNewPassword(model.getNewPassword());
        stationDetailResource.setPassword(model.getPassword());
        stationDetailResource.setPort(model.getPort());
        stationDetailResource.setProtocol(model.getProtocol());
        stationDetailResource.setRedirectIp(model.getRedirectIp());
        stationDetailResource.setRedirectPath(model.getRedirectPath());
        stationDetailResource.setRedirectPort(model.getRedirectPort());
        stationDetailResource.setRedirectQueryString(model.getRedirectQueryString());
        stationDetailResource.setRedirectProtocol(model.getRedirectProtocol());
        stationDetailResource.setService(model.getService());
        stationDetailResource.setPofService(model.getPofService());
        stationDetailResource.setBrokerCode(model.getBrokerCode());
        stationDetailResource.setProtocol4Mod(model.getProtocol4Mod());
        stationDetailResource.setIp4Mod(model.getIp4Mod());
        stationDetailResource.setPort4Mod(model.getPort4Mod());
        stationDetailResource.setService4Mod(model.getService4Mod());
        stationDetailResource.setProxyEnabled(model.getProxyEnabled());
        stationDetailResource.setProxyHost(model.getProxyHost());
        stationDetailResource.setProxyPort(model.getProxyPort());
        stationDetailResource.setProxyUsername(model.getProxyUsername());
        stationDetailResource.setProxyPassword(model.getProxyPassword());
        stationDetailResource.setThreadNumber(model.getThreadNumber());
        stationDetailResource.setTimeoutA(model.getTimeoutA());
        stationDetailResource.setTimeoutB(model.getTimeoutB());
        stationDetailResource.setTimeoutC(model.getTimeoutC());
        stationDetailResource.setFlagOnline(model.getFlagOnline());
        stationDetailResource.setBrokerObjId(model.getBrokerObjId());
        stationDetailResource.setRtInstantaneousDispatch(model.getRtInstantaneousDispatch());
        stationDetailResource.setTargetHost(model.getTargetHost());
        stationDetailResource.setTargetPort(model.getTargetPort());
        stationDetailResource.setTargetPath(model.getTargetPath());
        stationDetailResource.setTargetHostPof(model.getTargetHostPof());
        stationDetailResource.setTargetPortPof(model.getTargetPortPof());
        stationDetailResource.setTargetPathPof(model.getTargetPathPof());
        stationDetailResource.setPrimitiveVersion(model.getPrimitiveVersion());

        BrokerDetailsResource brokerDetailsResource = new BrokerDetailsResource();
        BrokerDetails brokerDetails = model.getIntermediarioPa();
        if(brokerDetails!=null){
            brokerDetailsResource.setBrokerDetails(brokerDetails.getBrokerDetails());
            brokerDetailsResource.setExtendedFaultBean(brokerDetails.getExtendedFaultBean());
            brokerDetailsResource.setEnabled(brokerDetails.getEnabled());
            brokerDetailsResource.setBrokerCode(brokerDetails.getBrokerCode());
            stationDetailResource.setBrokerDetails(brokerDetailsResource);
        }

        return stationDetailResource;
    }

    @Override
    public StationsResource toResource(Stations model) {
        if (model == null) {
            return null;
        }

        StationsResource stationsResource = new StationsResource();

        stationsResource.setStationsList(model.getStationsList().stream()
                .map(station -> toResource(station))
                .collect(Collectors.toList()));
        stationsResource.setPageInfo(model.getPageInfo());

        return stationsResource;
    }

    @Override
    public StationResource toStationsResource(StationDetails model) {
        if (model == null) {
            return null;
        }

        StationResource stationResource = new StationResource();

        stationResource.setStationCode(model.getStationCode());
        stationResource.setEnabled(model.getEnabled());
        stationResource.setBrokerDescription(model.getBrokerDescription());
        stationResource.setVersion(model.getVersion());
        stationResource.setStationStatus(model.getStationStatus());
        stationResource.setActivationDate(model.getActivationDate());
        stationResource.setCreatedAt(model.getCreatedAt());
        stationResource.setModifiedAt(model.getModifiedAt());
        stationResource.setAssociatedCreditorInstitutions(model.getAssociatedCreditorInstitutions());

        return stationResource;
    }

    @Override
    public Station fromStationDetails(StationDetails stationDetails) {
        if (stationDetails == null) {
            return null;
        }

        Station station = new Station();
        station.setStationCode(stationDetails.getStationCode());
        station.setStationStatus(stationDetails.getStationStatus());
        station.setEnabled(stationDetails.getEnabled());
        station.setVersion(stationDetails.getVersion());
        station.setActivationDate(stationDetails.getActivationDate());
        station.setBrokerDescription(stationDetails.getBrokerDescription());
        station.setCreatedAt(stationDetails.getCreatedAt());
        station.setModifiedAt(stationDetails.getModifiedAt());
        return station;
    }

    @Override
    public Stations fromWrapperEntitiesList(WrapperEntitiesList wrapperEntitiesList) {

        if (wrapperEntitiesList == null || wrapperEntitiesList.getWrapperEntities() == null) {
            return null;
        }

        Stations stations = new Stations();
        List<Station> stationList = new ArrayList<>();
        wrapperEntitiesList.getWrapperEntities().forEach(
                ent -> stationList.add(fromStationDetails(
                        (StationDetails) ent.getWrapperEntityOperationsSortedList().get(0).getEntity())));

        stations.setStationsList(stationList);
        stations.setPageInfo(wrapperEntitiesList.getPageInfo());
        return stations;
    }

    @Override
    public StationDetailsResourceList fromStationDetailsList(StationDetailsList model) {
        if (model == null) {
            return null;
        }
        StationDetailsResourceList resource = new StationDetailsResourceList();
        resource.setStationsDetailsList(model.getStationsDetailsList().stream().map(i ->
                toResource(i)).collect(Collectors.toList()));

        resource.setPageInfo(model.getPageInfo());
        return resource;
    }

    public WrapperStation toWrapperStation(Station model) {
        if (model == null) {
            return null;
        }

        WrapperStation wrapperStation = new WrapperStation();

        wrapperStation.setStationCode(model.getStationCode());
        wrapperStation.setEnabled(model.getEnabled());
        wrapperStation.setBrokerDescription(model.getBrokerDescription());
        wrapperStation.setVersion(model.getVersion());
        wrapperStation.setStationStatus(model.getStationStatus());
        wrapperStation.setActivationDate(model.getActivationDate());
        wrapperStation.setAssociatedCreditorInstitutions(model.getAssociatedCreditorInstitutions());
        //default per gli ogetti di apiconfig poiche non hanno questi campi
        wrapperStation.setWrapperStatus(WrapperStatus.APPROVED);
        LocalDate dateTime = LocalDate.of(2000, 1, 1);
        Instant instant = dateTime.atStartOfDay(ZoneOffset.UTC).toInstant();
        wrapperStation.setCreatedAt(instant);
        wrapperStation.setModifiedAt(instant);

        return wrapperStation;
    }

    @Override
    public WrapperStations toWrapperStations(Stations model) {
        if (model == null) {
            return null;
        }

        WrapperStations wrapperStations = new WrapperStations();

        wrapperStations.setStationsList(model.getStationsList().stream()
                .map(station -> toWrapperStation(station))
                .collect(Collectors.toList()));
        wrapperStations.setPageInfo(model.getPageInfo());

        return wrapperStations;
    }

    @Override
    public WrapperStations toWrapperStations(WrapperEntitiesList wrapperEntitiesList) {
        if (wrapperEntitiesList == null) {
            return null;
        }

        WrapperStations wrapperStations = new WrapperStations();
        List<WrapperStation> stationList = new ArrayList<>();

        wrapperEntitiesList.getWrapperEntities().forEach(
                ent -> stationList.add(toWrapperStation(
                        (WrapperEntityOperations<StationDetails>) ent.getWrapperEntityOperationsSortedList().get(0))));

        wrapperStations.setStationsList(stationList);
        wrapperStations.setPageInfo(wrapperEntitiesList.getPageInfo());

        return wrapperStations;
    }

    @Override
    public WrapperStation toWrapperStation(WrapperEntityOperations<StationDetails> wrapperEntityOperations) {
        if (wrapperEntityOperations == null) {
            return null;
        }

        WrapperStation wrapperStation = new WrapperStation();

        wrapperStation.setStationCode(wrapperEntityOperations.getEntity().getStationCode());
        wrapperStation.setEnabled(wrapperEntityOperations.getEntity().getEnabled());
        wrapperStation.setBrokerDescription(wrapperEntityOperations.getEntity().getBrokerDescription());
        wrapperStation.setVersion(wrapperEntityOperations.getEntity().getVersion());
        wrapperStation.setStationStatus(wrapperEntityOperations.getEntity().getStationStatus());
        wrapperStation.setActivationDate(wrapperEntityOperations.getEntity().getActivationDate());
        wrapperStation.setAssociatedCreditorInstitutions(wrapperEntityOperations.getEntity().getAssociatedCreditorInstitutions());

        wrapperStation.setWrapperStatus(wrapperEntityOperations.getStatus());
        wrapperStation.setCreatedAt(wrapperEntityOperations.getCreatedAt());
        wrapperStation.setModifiedAt(wrapperEntityOperations.getModifiedAt());

        return wrapperStation;

    }

    @Override
    public WrapperStationsResource toWrapperStationsResource(WrapperStations wrapperStations) {
        if (wrapperStations == null) {
            return null;
        }

        WrapperStationsResource wrapperStationsResource = new WrapperStationsResource();

        wrapperStationsResource.setStationsList(wrapperStations.getStationsList().stream()
                .map(station -> toWrapperStationResource(station))
                .collect(Collectors.toList()));
        wrapperStationsResource.setPageInfo(wrapperStations.getPageInfo());

        return wrapperStationsResource;
    }

    @Override
    public WrapperStationResource toWrapperStationResource(WrapperStation wrapperStation) {
        if (wrapperStation == null) {
            return null;
        }

        WrapperStationResource wrapperStationResource = new WrapperStationResource();

        wrapperStationResource.setStationCode(wrapperStation.getStationCode());
        wrapperStationResource.setEnabled(wrapperStation.getEnabled());
        wrapperStationResource.setBrokerDescription(wrapperStation.getBrokerDescription());
        wrapperStationResource.setVersion(wrapperStation.getVersion());
        wrapperStationResource.setStationStatus(wrapperStation.getStationStatus());
        wrapperStationResource.setActivationDate(wrapperStation.getActivationDate());
        wrapperStationResource.setAssociatedCreditorInstitutions(wrapperStation.getAssociatedCreditorInstitutions());
        wrapperStationResource.setWrapperStatus(wrapperStation.getWrapperStatus());
        wrapperStationResource.setCreatedAt(wrapperStation.getCreatedAt());
        wrapperStationResource.setModifiedAt(wrapperStation.getModifiedAt());

        return wrapperStationResource;
    }

    @Override
    public List<StationResource> toResourceList(WrapperEntitiesList wrapperEntitiesList) {

        if (wrapperEntitiesList == null || wrapperEntitiesList.getWrapperEntities() == null) {
            return null;
        }

        List<StationResource> stationResourceList = new ArrayList<>();

        wrapperEntitiesList.getWrapperEntities()
                .forEach(ent -> stationResourceList
                        .add(toStationsResource(
                                (StationDetails) ent.getWrapperEntityOperationsSortedList().get(0).getEntity())));

        return stationResourceList;

    }

    @Override
    public List<StationResource> toResourceList(Stations stations) {

        if (stations == null || stations.getStationsList() == null) {
            return null;
        }

        List<StationResource> stationResourceList = new ArrayList<>();

        stations.getStationsList().forEach(sta -> stationResourceList.add(toResource(sta)));
        return stationResourceList;
    }

    @Override
    public StationResource toResource(Station model) {
        if (model == null) {
            return null;
        }

        StationResource stationResource = new StationResource();

        stationResource.setStationCode(model.getStationCode());
        stationResource.setEnabled(model.getEnabled());
        stationResource.setBrokerDescription(model.getBrokerDescription());
        stationResource.setVersion(model.getVersion());
        stationResource.setStationStatus(model.getStationStatus());
        stationResource.setActivationDate(model.getActivationDate());
        stationResource.setCreatedAt(model.getCreatedAt());
        stationResource.setModifiedAt(model.getModifiedAt());
        stationResource.setAssociatedCreditorInstitutions(model.getAssociatedCreditorInstitutions());

        return stationResource;
    }

    @Override
    public StationDetails fromDto(StationDetailsDto model) {
        if (model == null) {
            return null;
        }

        StationDetails stationDetails = new StationDetails();

        stationDetails.setStationCode(model.getStationCode());
        stationDetails.setEnabled(model.getEnabled());
        stationDetails.setBrokerDescription(model.getBrokerDescription());
        stationDetails.setIp(model.getIp());
//        stationDetails.setVersion();
        stationDetails.setNewPassword(model.getNewPassword());
        stationDetails.setPassword(model.getPassword());
        stationDetails.setPort(model.getPort());
        stationDetails.setProtocol(model.getProtocol());
        stationDetails.setRedirectIp(model.getRedirectIp());
        stationDetails.setRedirectPath(model.getRedirectPath());
        stationDetails.setRedirectPort(model.getRedirectPort());
        stationDetails.setRedirectQueryString(model.getRedirectQueryString());
        stationDetails.setRedirectProtocol(model.getRedirectProtocol());
        stationDetails.setService(model.getService());
        stationDetails.setPofService(model.getPofService());
        stationDetails.setBrokerCode(model.getBrokerCode());
        stationDetails.setProtocol4Mod(model.getProtocol4Mod());
        stationDetails.setIp4Mod(model.getIp4Mod());
        stationDetails.setPort4Mod(model.getPort4Mod());
        stationDetails.setService4Mod(model.getService4Mod());
        stationDetails.setProxyEnabled(model.getProxyEnabled());
        stationDetails.setProxyHost(model.getProxyHost());
        stationDetails.setProxyPort(model.getProxyPort());
        stationDetails.setProxyUsername(model.getProxyUsername());
        stationDetails.setProxyPassword(model.getProxyPassword());
        stationDetails.setThreadNumber(model.getThreadNumber());
        stationDetails.setTimeoutA(model.getTimeoutA());
        stationDetails.setTimeoutB(model.getTimeoutB());
        stationDetails.setTimeoutC(model.getTimeoutC());
        stationDetails.setFlagOnline(model.getFlagOnline());
        stationDetails.setBrokerObjId(model.getBrokerObjId());
        stationDetails.setRtInstantaneousDispatch(model.getRtInstantaneousDispatch());
        stationDetails.setTargetHost(model.getTargetHost());
        stationDetails.setTargetPort(model.getTargetPort());
        stationDetails.setTargetPath(model.getTargetPath());
        stationDetails.setPrimitiveVersion(model.getPrimitiveVersion());

        return stationDetails;
    }

    @Override
    public StationDetails fromWrapperStationDetailsDto(WrapperStationDetailsDto model) {
        if (model == null) {
            return null;
        }

        StationDetails stationDetails = new StationDetails();
        stationDetails.setStationCode(model.getStationCode());
        stationDetails.setRedirectIp(model.getRedirectIp());
        stationDetails.setRedirectPath(model.getRedirectPath());
        stationDetails.setRedirectPort(model.getRedirectPort());
        stationDetails.setRedirectQueryString(model.getRedirectQueryString());
        stationDetails.setRedirectProtocol(model.getRedirectProtocol());
        stationDetails.setBrokerCode(model.getBrokerCode());
        stationDetails.setTargetHost(model.getTargetHost());
        stationDetails.setTargetPort(model.getTargetPort());
        stationDetails.setTargetPath(model.getTargetPath());
        stationDetails.setPrimitiveVersion(model.getPrimitiveVersion());
        stationDetails.setStationCode(model.getStationCode());
        stationDetails.setRedirectIp(model.getRedirectIp());
        stationDetails.setRedirectPath(model.getRedirectPath());
        stationDetails.setRedirectPort(model.getRedirectPort());
        stationDetails.setRedirectQueryString(model.getRedirectQueryString());
        stationDetails.setRedirectProtocol(model.getRedirectProtocol());
        stationDetails.setBrokerCode(model.getBrokerCode());
        stationDetails.setTargetHost(model.getTargetHost());
        stationDetails.setTargetPort(model.getTargetPort());
        stationDetails.setTargetPath(model.getTargetPath());
        stationDetails.setPrimitiveVersion(model.getPrimitiveVersion());
        //default
        stationDetails.setTimeoutA(15L);
        stationDetails.setTimeoutB(30L);
        stationDetails.setTimeoutC(120L);
        stationDetails.setThreadNumber(1L);
        return stationDetails;
    }

}
