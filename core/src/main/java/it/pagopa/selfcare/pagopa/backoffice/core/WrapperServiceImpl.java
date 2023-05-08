package it.pagopa.selfcare.pagopa.backoffice.core;

import it.pagopa.selfcare.pagopa.backoffice.connector.api.WrapperConnector;
import it.pagopa.selfcare.pagopa.backoffice.connector.exception.ResourceNotFoundException;
import it.pagopa.selfcare.pagopa.backoffice.connector.model.channel.ChannelDetails;
import it.pagopa.selfcare.pagopa.backoffice.connector.model.channel.WrapperEntitiesList;
import it.pagopa.selfcare.pagopa.backoffice.connector.model.station.StationDetails;
import it.pagopa.selfcare.pagopa.backoffice.connector.model.wrapper.WrapperEntitiesOperations;
import it.pagopa.selfcare.pagopa.backoffice.connector.model.wrapper.WrapperStatus;
import it.pagopa.selfcare.pagopa.backoffice.connector.model.wrapper.WrapperType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class WrapperServiceImpl implements WrapperService {

    private final WrapperConnector wrapperConnector;

    @Autowired
    public WrapperServiceImpl(WrapperConnector wrapperConnector) {
        this.wrapperConnector = wrapperConnector;
    }


    @Override
    public WrapperEntitiesOperations<ChannelDetails> createWrapperChannelDetails(ChannelDetails channelDetails, String note, String status) {
        log.trace("createWrapperChannelDetails start");
        log.debug("createWrapperChannelDetails channelDetails = {}", channelDetails);
        WrapperEntitiesOperations<ChannelDetails> response = wrapperConnector.insert(channelDetails, note, status);
        log.debug("createWrapperChannelDetails result = {}", response);
        log.trace("createWrapperChannelDetails end");
        return response;
    }

    @Override
    public WrapperEntitiesOperations<StationDetails> createWrapperStationDetails(StationDetails stationDetails, String note, String status) {
        log.trace("createWrapperStationDetails start");
        log.debug("createWrapperStationDetails stationDetails = {}", stationDetails);
        WrapperEntitiesOperations<StationDetails> response = wrapperConnector.insert(stationDetails, note, status);
        log.debug("createWrapperStationDetails result = {}", response);
        log.trace("createWrapperStationDetails end");
        return response;
    }

    @Override
    public WrapperEntitiesOperations findById(String id) {
        log.trace("findById start");
        log.debug("findById id = {}", id);
        WrapperEntitiesOperations response = wrapperConnector.findById(id).orElseThrow(ResourceNotFoundException::new);
        response.sortEntitesByCreatedAt();
        log.debug("findById result = {}", response);
        log.trace("findById end");
        return response;
    }

    @Override
    public WrapperEntitiesOperations<ChannelDetails> updateWrapperChannelDetails(ChannelDetails channelDetails, String note, String status) {
        log.trace("updateWrapperChannelDetails start");
        log.debug("updateWrapperChannelDetails channelDetails = {}, channelCode = {}", channelDetails, channelDetails.getChannelCode());
        WrapperEntitiesOperations<ChannelDetails> response = wrapperConnector.update(channelDetails, note, status);
        log.debug("updateWrapperChannelDetails result = {}", response);
        log.trace("updateWrapperChannelDetails end");
        return response;
    }

    @Override
    public WrapperEntitiesOperations<ChannelDetails> updateWrapperChannelDetailsByOpt(ChannelDetails channelDetails, String note, String status) {
        log.trace("updateWrapperChannelDetailsByOpt start");
        log.debug("updateWrapperChannelDetailsByOpt channelDetails = {}, channelCode = {}", channelDetails, channelDetails.getChannelCode());
        WrapperEntitiesOperations<ChannelDetails> response = wrapperConnector.updateByOpt(channelDetails, note, status);
        log.debug("updateWrapperChannelDetailsByOpt result = {}", response);
        log.trace("updateWrapperChannelDetailsByOpt end");
        return response;
    }


    @Override
    public WrapperEntitiesOperations<StationDetails> updateWrapperStationDetailsByOpt(StationDetails stationDetails, String note, String status) {
        log.trace("updateWrapperStationDetailsByOpt start");
        log.debug("updateWrapperStationDetailsByOpt stationDetails = {}, stationCode = {}", stationDetails, stationDetails.getStationCode());
        WrapperEntitiesOperations<StationDetails> response = wrapperConnector.updateByOpt(stationDetails, note, status);
        log.debug("updateWrapperStationDetailsByOpt result = {}", response);
        log.trace("updateWrapperStationDetailsByOpt end");
        return response;
    }

    @Override
    public WrapperEntitiesOperations<StationDetails> updateWrapperStationDetails(StationDetails stationDetails, String note, String status) {
        log.trace("updateWrapperChannelDetails start");
        log.debug("updateWrapperChannelDetails stationDetails = {}, channelCode = {}", stationDetails, stationDetails.getStationCode());
        WrapperEntitiesOperations<StationDetails> response = wrapperConnector.update(stationDetails, note, status);
        log.debug("updateWrapperChannelDetails result = {}", response);
        log.trace("updateWrapperChannelDetails end");
        return response;
    }

    @Override
    public WrapperEntitiesList findByStatusAndTypeAndBrokerCodeAndIdLike(WrapperStatus status, WrapperType wrapperType, String brokerCode, String idLike, Integer page, Integer limit, String sorting) {
        log.trace("findByStatusAndType start");
        log.debug("findByStatusAndType status = {}, type = ", status, wrapperType);
        WrapperEntitiesList response;
        response = wrapperConnector.findByStatusAndTypeAndBrokerCodeAndIdLike(status, wrapperType, brokerCode, idLike, page, limit, sorting);
        log.debug("findByStatusAndType result = {}", response);
        log.trace("findByStatusAndType end");
        return response;
    }
}
