package it.pagopa.selfcare.pagopa.backoffice.repository;

import it.pagopa.selfcare.pagopa.backoffice.entity.WrapperEntities;
import it.pagopa.selfcare.pagopa.backoffice.model.connector.wrapper.WrapperStatus;
import it.pagopa.selfcare.pagopa.backoffice.model.connector.wrapper.WrapperType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface WrapperRepository extends MongoRepository<WrapperEntities, String> {


    List<WrapperEntities<?>> findByStatusNot(WrapperStatus status);

    List<WrapperEntities<?>> findByStatus(WrapperStatus status);

    Optional<WrapperEntities<?>> findByIdAndType(String id, WrapperType wrapperType);

    Page<WrapperEntities<?>> findByIdLikeAndType(String idLike, WrapperType wrapperType, Pageable pageable);

    Page<WrapperEntities<?>> findByIdAndTypeAndBrokerCode(String id, WrapperType wrapperType, String brokerCode, Pageable pageable);

    Page<WrapperEntities<?>> findByIdLikeAndTypeAndBrokerCode(String id, WrapperType wrapperType, String brokerCode, Pageable pageable);

    Page<WrapperEntities<?>> findByIdLikeAndTypeAndBrokerCodeAndStatusNot(String id, WrapperType wrapperType, String brokerCode, WrapperStatus status, Pageable pageable);

    List<WrapperEntities<?>> findByType(WrapperType wrapperType);

    Page<WrapperEntities<?>> findByStatusAndTypeAndBrokerCode(WrapperStatus status, WrapperType wrapperType, String brokerCode, Pageable pageable);

    Page<WrapperEntities<?>> findByStatusAndTypeAndBrokerCodeAndIdLike(WrapperStatus status, WrapperType wrapperType, String brokerCode, String idLike, Pageable pageable);

    Page<WrapperEntities<?>> findByStatusAndType(WrapperStatus status, WrapperType wrapperType, Pageable pageable);

    Page<WrapperEntities<?>> findByStatusAndTypeAndIdLike(WrapperStatus status, WrapperType wrapperType, String idLike, Pageable pageable);

    Page<WrapperEntities<?>> findByTypeAndBrokerCode(WrapperType wrapperType, String brokerCode, Pageable pageable);

    Page<WrapperEntities<?>> findByTypeAndBrokerCodeAndStatusNot(WrapperType wrapperType, String brokerCode, WrapperStatus status, Pageable pageable);

    Page<WrapperEntities<?>> findByTypeAndBrokerCodeAndIdLike(WrapperType wrapperType, String brokerCode, String idLike, Pageable pageable);

    Page<WrapperEntities<?>> findByType(WrapperType wrapperType, Pageable pageable);

    Page<WrapperEntities<?>> findByTypeAndIdLike(WrapperType wrapperType, String idLike, Pageable pageable);

    Page<WrapperEntities<?>> findByStatusNotAndTypeAndBrokerCode(WrapperStatus status, WrapperType wrapperType, String brokerCode, Pageable pageable);

    Page<WrapperEntities<?>> findByStatusNotAndTypeAndBrokerCodeAndIdLike(WrapperStatus status, WrapperType wrapperType, String brokerCode, String idLike, Pageable pageable);

    Page<WrapperEntities<?>> findByStatusNotAndType(WrapperStatus status, WrapperType wrapperType, Pageable pageable);

    Page<WrapperEntities<?>> findByStatusNotAndTypeAndIdLike(WrapperStatus status, WrapperType wrapperType, String idLike, Pageable pageable);

}
