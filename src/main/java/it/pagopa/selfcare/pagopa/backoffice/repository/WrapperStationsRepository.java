package it.pagopa.selfcare.pagopa.backoffice.repository;

import it.pagopa.selfcare.pagopa.backoffice.entity.WrapperEntities;
import it.pagopa.selfcare.pagopa.backoffice.model.connector.station.StationDetails;
import it.pagopa.selfcare.pagopa.backoffice.model.connector.wrapper.WrapperStatus;
import it.pagopa.selfcare.pagopa.backoffice.model.connector.wrapper.WrapperType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface WrapperStationsRepository extends MongoRepository<WrapperEntities<StationDetails>, String> {

    Page<WrapperEntities<StationDetails>> findStationByIdLikeAndTypeAndBrokerCodeAndStatusNot(String id, WrapperType wrapperType, String brokerCode, WrapperStatus status, Pageable pageable);

    Page<WrapperEntities<StationDetails>> findStationByTypeAndBrokerCodeAndStatusNot(WrapperType wrapperType, String brokerCode, WrapperStatus status, Pageable pageable);

    Page<WrapperEntities<StationDetails>> findByType(WrapperType wrapperType, Pageable pageable);

    Page<WrapperEntities<StationDetails>> findByIdLikeAndType(String idLike, WrapperType wrapperType, Pageable pageable);

    Page<WrapperEntities<StationDetails>> findByTypeAndBrokerCode(WrapperType wrapperType, String brokerCode, Pageable pageable);

    Page<WrapperEntities<StationDetails>> findByIdLikeAndTypeAndBrokerCode(String id, WrapperType wrapperType, String brokerCode, Pageable pageable);

}
