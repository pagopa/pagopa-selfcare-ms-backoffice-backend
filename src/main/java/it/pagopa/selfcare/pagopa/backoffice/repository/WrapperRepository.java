package it.pagopa.selfcare.pagopa.backoffice.repository;

import it.pagopa.selfcare.pagopa.backoffice.entity.WrapperEntities;
import it.pagopa.selfcare.pagopa.backoffice.entity.WrapperEntitiesOperations;
import it.pagopa.selfcare.pagopa.backoffice.model.connector.wrapper.WrapperStatus;
import it.pagopa.selfcare.pagopa.backoffice.model.connector.wrapper.WrapperType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface WrapperRepository extends MongoRepository<WrapperEntities, String> {


    List<WrapperEntities<?>> findByStatusNot(WrapperStatus status);

    List<WrapperEntities<?>> findByStatus(WrapperStatus status);

    Page<WrapperEntitiesOperations<?>> findByIdAndType(String id, WrapperType wrapperType, Pageable pageable);

    Page<WrapperEntitiesOperations<?>> findByIdLikeAndType(String idLike, WrapperType wrapperType, Pageable pageable);

    Page<WrapperEntitiesOperations<?>> findByIdAndTypeAndBrokerCode(String id, WrapperType wrapperType,String brokerCode, Pageable pageable);

    Page<WrapperEntitiesOperations<?>> findByIdLikeAndTypeAndBrokerCode(String id, WrapperType wrapperType,String brokerCode, Pageable pageable);

    List<WrapperEntitiesOperations<?>> findByType(WrapperType wrapperType);

    Page<WrapperEntitiesOperations<?>> findByStatusAndTypeAndBrokerCode(WrapperStatus status, WrapperType wrapperType,String brokerCode, Pageable pageable);

    Page<WrapperEntitiesOperations<?>> findByStatusAndTypeAndBrokerCodeAndIdLike(WrapperStatus status, WrapperType wrapperType,String brokerCode,String idLike, Pageable pageable);

    Page<WrapperEntitiesOperations<?>> findByStatusAndType(WrapperStatus status, WrapperType wrapperType, Pageable pageable);

    Page<WrapperEntitiesOperations<?>> findByStatusAndTypeAndIdLike(WrapperStatus status, WrapperType wrapperType,String idLike, Pageable pageable);

    Page<WrapperEntitiesOperations<?>> findByTypeAndBrokerCode(WrapperType wrapperType,String brokerCode, Pageable pageable);

    Page<WrapperEntitiesOperations<?>> findByTypeAndBrokerCodeAndIdLike(WrapperType wrapperType,String brokerCode,String idLike, Pageable pageable);

    Page<WrapperEntitiesOperations<?>> findByType(WrapperType wrapperType, Pageable pageable);

    Page<WrapperEntitiesOperations<?>> findByTypeAndIdLike( WrapperType wrapperType,String idLike, Pageable pageable);

    Page<WrapperEntitiesOperations<?>> findByStatusNotAndTypeAndBrokerCode(WrapperStatus status, WrapperType wrapperType,String brokerCode, Pageable pageable);

    Page<WrapperEntitiesOperations<?>> findByStatusNotAndTypeAndBrokerCodeAndIdLike(WrapperStatus status, WrapperType wrapperType,String brokerCode,String idLike, Pageable pageable);

    Page<WrapperEntitiesOperations<?>> findByStatusNotAndType(WrapperStatus status, WrapperType wrapperType, Pageable pageable);

    Page<WrapperEntitiesOperations<?>> findByStatusNotAndTypeAndIdLike(WrapperStatus status, WrapperType wrapperType,String idLike, Pageable pageable);

}