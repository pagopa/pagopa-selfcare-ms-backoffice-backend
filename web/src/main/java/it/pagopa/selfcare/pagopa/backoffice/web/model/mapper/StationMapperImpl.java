package it.pagopa.selfcare.pagopa.backoffice.web.model.mapper;

import it.pagopa.selfcare.pagopa.backoffice.connector.model.station.Station;
import it.pagopa.selfcare.pagopa.backoffice.connector.model.station.StationDetail;
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
    public StationDetailResource toResource(StationDetail model) {
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
    public StationDetail fromDto(StationDetailsDto model) {
        if (model == null) {
            return null;
        }

        StationDetail stationDetail = new StationDetail();

        stationDetail.setStationCode(model.getStationCode());
        stationDetail.setEnabled(model.getEnabled());
        stationDetail.setBrokerDescription(model.getBrokerDescription());
        stationDetail.setIp(model.getIp());
        stationDetail.setNewPassword(model.getNewPassword());
        stationDetail.setPassword(model.getPassword());
        stationDetail.setPort(model.getPort());
        stationDetail.setProtocol(model.getProtocol());
        stationDetail.setRedirectIp(model.getRedirectIp());
        stationDetail.setRedirectPath(model.getRedirectPath());
        stationDetail.setRedirectPort(model.getRedirectPort());
        stationDetail.setRedirectQueryString(model.getRedirectQueryString());
        stationDetail.setRedirectProtocol(model.getRedirectProtocol());
        stationDetail.setService(model.getService());
        stationDetail.setPofService(model.getPofService());
        stationDetail.setBrokerCode(model.getBrokerCode());
        stationDetail.setProtocol4Mod(model.getProtocol4Mod());
        stationDetail.setIp4Mod(model.getIp4Mod());
        stationDetail.setPort4Mod(model.getPort4Mod());
        stationDetail.setService4Mod(model.getService4Mod());
        stationDetail.setProxyEnabled(model.getProxyEnabled());
        stationDetail.setProxyHost(model.getProxyHost());
        stationDetail.setProxyPort(model.getProxyPort());
        stationDetail.setProxyUsername(model.getProxyUsername());
        stationDetail.setProxyPassword(model.getProxyPassword());
        stationDetail.setThreadNumber(model.getThreadNumber());
        stationDetail.setTimeoutA(model.getTimeoutA());
        stationDetail.setTimeoutB(model.getTimeoutB());
        stationDetail.setTimeoutC(model.getTimeoutC());
        stationDetail.setFlagOnline(model.getFlagOnline());
        stationDetail.setBrokerObjId(model.getBrokerObjId());
        stationDetail.setRtInstantaneousDispatch(model.getRtInstantaneousDispatch());
        stationDetail.setTargetHost(model.getTargetHost());
        stationDetail.setTargetPort(model.getTargetPort());
        stationDetail.setTargetPath(model.getTargetPath());
        stationDetail.setPrimitiveVersion(model.getPrimitiveVersion());

        return stationDetail;
    }

}
