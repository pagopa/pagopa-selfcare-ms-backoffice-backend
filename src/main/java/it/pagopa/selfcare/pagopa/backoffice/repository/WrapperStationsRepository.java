package it.pagopa.selfcare.pagopa.backoffice.repository;

import it.pagopa.selfcare.pagopa.backoffice.entity.WrapperEntityStations;
import it.pagopa.selfcare.pagopa.backoffice.model.connector.wrapper.WrapperStatus;
import it.pagopa.selfcare.pagopa.backoffice.model.connector.wrapper.WrapperType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface WrapperStationsRepository extends MongoRepository<WrapperEntityStations, String> {

    Page<WrapperEntityStations> findByIdLikeAndTypeAndBrokerCodeAndStatusNot(String id, WrapperType wrapperType, String brokerCode, WrapperStatus status, Pageable pageable);

    Page<WrapperEntityStations> findByTypeAndBrokerCodeAndStatusNot(WrapperType wrapperType, String brokerCode, WrapperStatus status, Pageable pageable);

    Page<WrapperEntityStations> findByType(WrapperType wrapperType, Pageable pageable);

    Page<WrapperEntityStations> findByIdLikeAndType(String idLike, WrapperType wrapperType, Pageable pageable);

    Page<WrapperEntityStations> findByTypeAndBrokerCode(WrapperType wrapperType, String brokerCode, Pageable pageable);

    Page<WrapperEntityStations> findByIdLikeAndTypeAndBrokerCode(String id, WrapperType wrapperType, String brokerCode, Pageable pageable);

}
