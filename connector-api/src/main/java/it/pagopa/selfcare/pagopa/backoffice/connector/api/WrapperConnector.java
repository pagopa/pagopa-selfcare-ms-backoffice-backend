package it.pagopa.selfcare.pagopa.backoffice.connector.api;

import it.pagopa.selfcare.pagopa.backoffice.connector.model.channel.ChannelDetails;
import it.pagopa.selfcare.pagopa.backoffice.connector.model.channel.WrapperEntitiesList;
import it.pagopa.selfcare.pagopa.backoffice.connector.model.station.StationDetails;
import it.pagopa.selfcare.pagopa.backoffice.connector.model.wrapper.WrapperEntitiesOperations;
import it.pagopa.selfcare.pagopa.backoffice.connector.model.wrapper.WrapperStatus;
import it.pagopa.selfcare.pagopa.backoffice.connector.model.wrapper.WrapperType;

import java.util.Optional;

public interface WrapperConnector {

    WrapperEntitiesOperations<ChannelDetails> insert(ChannelDetails channelDetails, String note, String status);

    WrapperEntitiesOperations<StationDetails> insert(StationDetails stationDetails, String note, String status);

    WrapperEntitiesOperations<ChannelDetails> update(ChannelDetails channelDetails, String note, String status);

    WrapperEntitiesOperations<ChannelDetails> updateByOpt(ChannelDetails channelDetails, String note, String status);

    WrapperEntitiesOperations<StationDetails> updateByOpt(StationDetails stationDetails, String note, String status);

    WrapperEntitiesOperations<StationDetails> update(StationDetails channelDetails, String note, String status);

    WrapperEntitiesList findByStatusAndTypeAndBrokerCodeAndIdLike(WrapperStatus status,WrapperType wrapperType,String brokerCode, String idLike, Integer page, Integer size, String sorting);

    Optional<WrapperEntitiesOperations> findById(String id);

    WrapperEntitiesList findByIdAndType(String id, WrapperType wrapperType);
}
