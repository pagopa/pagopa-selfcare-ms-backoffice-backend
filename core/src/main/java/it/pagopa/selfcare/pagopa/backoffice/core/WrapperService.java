package it.pagopa.selfcare.pagopa.backoffice.core;

import it.pagopa.selfcare.pagopa.backoffice.connector.model.channel.ChannelDetails;
import it.pagopa.selfcare.pagopa.backoffice.connector.model.channel.WrapperEntitiesList;
import it.pagopa.selfcare.pagopa.backoffice.connector.model.station.StationDetails;
import it.pagopa.selfcare.pagopa.backoffice.connector.model.wrapper.WrapperEntitiesOperations;
import it.pagopa.selfcare.pagopa.backoffice.connector.model.wrapper.WrapperStatus;
import it.pagopa.selfcare.pagopa.backoffice.connector.model.wrapper.WrapperType;

public interface WrapperService {

    WrapperEntitiesOperations<ChannelDetails> createWrapperChannelDetails(ChannelDetails channelDetails,String note,String status);

    WrapperEntitiesOperations<StationDetails> createWrapperStationDetails(StationDetails channelDetails,String note,String status);

    WrapperEntitiesOperations findById(String id);
    WrapperEntitiesOperations<ChannelDetails> updateWrapperChannelDetails(ChannelDetails channelDetails,String note,String status, String createdBy);

    WrapperEntitiesOperations<ChannelDetails> updateWrapperChannelDetailsByOpt(ChannelDetails channelDetails,String note,String status);

    WrapperEntitiesOperations<StationDetails> updateWrapperStationDetailsByOpt(StationDetails stationDetails,String note,String status);
    WrapperEntitiesOperations<StationDetails> updateWrapperStationDetails(StationDetails channelDetails,String note,String status, String createdBy);

    WrapperEntitiesList findByStatusAndTypeAndBrokerCodeAndIdLike( WrapperStatus status ,WrapperType wrapperType,String brokerCode, String idLike, Integer page, Integer limit, String sorting);

    WrapperEntitiesList findByIdLikeOrTypeOrBrokerCode(String id, WrapperType wrapperType,String brokerCode, Integer page, Integer size);

}
