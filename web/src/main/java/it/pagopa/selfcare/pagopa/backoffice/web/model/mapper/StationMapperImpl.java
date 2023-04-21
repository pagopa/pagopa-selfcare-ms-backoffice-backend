package it.pagopa.selfcare.pagopa.backoffice.web.model.mapper;

import it.pagopa.selfcare.pagopa.backoffice.connector.model.station.Station;
import it.pagopa.selfcare.pagopa.backoffice.connector.model.station.StationDetails;
import it.pagopa.selfcare.pagopa.backoffice.connector.model.station.Stations;
import it.pagopa.selfcare.pagopa.backoffice.web.model.stations.StationDetailResource;
import it.pagopa.selfcare.pagopa.backoffice.web.model.stations.StationDetailsDto;
import it.pagopa.selfcare.pagopa.backoffice.web.model.stations.StationResource;
import it.pagopa.selfcare.pagopa.backoffice.web.model.stations.StationsResource;

import javax.annotation.processing.Generated;
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
        stationDetailResource.setOperatedBy(model.getOperatedBy());
        stationDetailResource.setTargetHostPof( model.getTargetHostPof() );
        stationDetailResource.setTargetPortPof( model.getTargetPortPof() );
        stationDetailResource.setTargetPathPof( model.getTargetPathPof() );
        stationDetailResource.setPrimitiveVersion(model.getPrimitiveVersion());

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

}
