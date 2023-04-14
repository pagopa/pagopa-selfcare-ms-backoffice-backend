package it.pagopa.selfcare.pagopa.backoffice.connector.api;

import it.pagopa.selfcare.pagopa.backoffice.connector.model.channel.ChannelDetails;
import it.pagopa.selfcare.pagopa.backoffice.connector.model.station.StationDetails;
import it.pagopa.selfcare.pagopa.backoffice.connector.model.wrapper.WrapperEntitiesOperations;

import java.util.Optional;

public interface WrapperConnector {

    WrapperEntitiesOperations<ChannelDetails> insert(ChannelDetails channelDetails,String note,String status);

    WrapperEntitiesOperations<StationDetails> insert(StationDetails stationDetails,String note,String status);

    WrapperEntitiesOperations<ChannelDetails> update(ChannelDetails channelDetails,String note,String status);

    WrapperEntitiesOperations<ChannelDetails> updateByOpt(ChannelDetails channelDetails,String note,String status);

    WrapperEntitiesOperations<StationDetails> updateByOpt(StationDetails stationDetails,String note,String status);
    WrapperEntitiesOperations<StationDetails> update(StationDetails channelDetails,String note,String status);


//    WrapperEntitiesOperations save(WrapperEntitiesOperations wrapperEntitiesOperations);
//
//    List<WrapperEntitiesOperations> findByStatusNot(WrapperStatus status);
//
//    List<WrapperEntitiesOperations> findByStatus(WrapperStatus status);
//
//    List<WrapperEntitiesOperations> findAll();
//
//    boolean existsById(String id);
//
    Optional<WrapperEntitiesOperations> findById(String id);
//
//    void updateWrapperEntitiesStatus(String id, WrapperStatus status);
}
