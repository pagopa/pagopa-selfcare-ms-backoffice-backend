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
        WrapperEntitiesOperations<ChannelDetails> response = wrapperConnector.insert(channelDetails, note, status);
        return response;
    }

    @Override
    public WrapperEntitiesOperations<StationDetails> createWrapperStationDetails(StationDetails stationDetails, String note, String status) {
        WrapperEntitiesOperations<StationDetails> response = wrapperConnector.insert(stationDetails, note, status);
        return response;
    }

    @Override
    public WrapperEntitiesOperations findById(String id) {
        WrapperEntitiesOperations response = wrapperConnector.findById(id).orElseThrow(ResourceNotFoundException::new);
        response.sortEntitesByCreatedAt();
        return response;
    }

    @Override
    public WrapperEntitiesOperations<ChannelDetails> updateWrapperChannelDetails(ChannelDetails channelDetails, String note, String status, String createdBy) {
        WrapperEntitiesOperations<ChannelDetails> response = wrapperConnector.update(channelDetails, note, status, createdBy);
        return response;
    }

    @Override
    public WrapperEntitiesOperations<ChannelDetails> updateWrapperChannelDetailsByOpt(ChannelDetails channelDetails, String note, String status) {
        WrapperEntitiesOperations<ChannelDetails> response = wrapperConnector.updateByOpt(channelDetails, note, status);
        return response;
    }


    @Override
    public WrapperEntitiesOperations<StationDetails> updateWrapperStationDetailsByOpt(StationDetails stationDetails, String note, String status) {
        WrapperEntitiesOperations<StationDetails> response = wrapperConnector.updateByOpt(stationDetails, note, status);
        return response;
    }

    @Override
    public WrapperEntitiesOperations<StationDetails> updateWrapperStationDetails(StationDetails stationDetails, String note, String status, String createdBy) {
        WrapperEntitiesOperations<StationDetails> response = wrapperConnector.update(stationDetails, note, status, createdBy);
        return response;
    }

    @Override
    public WrapperEntitiesList findByStatusAndTypeAndBrokerCodeAndIdLike(WrapperStatus status, WrapperType wrapperType, String brokerCode, String idLike, Integer page, Integer limit, String sorting) {
        WrapperEntitiesList response;
        response = wrapperConnector.findByStatusAndTypeAndBrokerCodeAndIdLike(status, wrapperType, brokerCode, idLike, page, limit, sorting);
        return response;
    }

    public WrapperEntitiesList findByIdLikeOrTypeOrBrokerCode(String id, WrapperType wrapperType,String brokerCode, Integer page, Integer size) {
        WrapperEntitiesList response;
        response = wrapperConnector.findByIdLikeOrTypeOrBrokerCode(id, wrapperType,brokerCode, page, size);
        return response;
    }


}
