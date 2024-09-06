package it.pagopa.selfcare.pagopa.backoffice.repository;

import it.pagopa.selfcare.pagopa.backoffice.entity.WrapperEntityStations;
import it.pagopa.selfcare.pagopa.backoffice.model.connector.wrapper.WrapperStatus;
import it.pagopa.selfcare.pagopa.backoffice.model.connector.wrapper.WrapperType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.time.Instant;
import java.util.Optional;

public interface WrapperStationsRepository extends MongoRepository<WrapperEntityStations, String> {

    Page<WrapperEntityStations> findByIdLikeAndTypeAndBrokerCodeAndStatusNot(String id, WrapperType wrapperType, String brokerCode, WrapperStatus status, Pageable pageable);

    Page<WrapperEntityStations> findByTypeAndBrokerCodeAndStatusNot(WrapperType wrapperType, String brokerCode, WrapperStatus status, Pageable pageable);

    Page<WrapperEntityStations> findByType(WrapperType wrapperType, Pageable pageable);

    Page<WrapperEntityStations> findByIdLikeAndType(String idLike, WrapperType wrapperType, Pageable pageable);

    Page<WrapperEntityStations> findByTypeAndBrokerCode(WrapperType wrapperType, String brokerCode, Pageable pageable);

    Page<WrapperEntityStations> findByIdLikeAndTypeAndBrokerCode(String id, WrapperType wrapperType, String brokerCode, Pageable pageable);

    Optional<WrapperEntityStations> findByIdAndType(String id, WrapperType wrapperType);

    Page<WrapperEntityStations> findByTypeAndBrokerCodeAndStatusNotAndCreatedAtBetween(WrapperType station, String brokerCode, WrapperStatus approved, Instant createAtAfter, Instant createAtBefore, Pageable paging);

    Page<WrapperEntityStations> findByIdLikeAndTypeAndBrokerCodeAndStatusNotAndCreatedAtBetween(String stationCode, WrapperType station, String brokerCode, WrapperStatus approved, Instant createAtAfter, Instant createAtBefore, Pageable paging);

    @Query(value = "{ 'type' : ?0, 'brokerCode' : ?1, 'status' : ?2 , " +
            "'entities.createdAt' : {$gte: ?3, $lt: ?4} ," +
            " 'entities.entity.service' : $not: {{ $regex: 'gps' , $options:'i' }} }")
    Page<WrapperEntityStations> findByTypeAndBrokerCodeAndStatusNotAndCreatedAtBetweenAndConnectionSync(
            WrapperType station, String brokerCode, WrapperStatus approved,
            Instant createAtAfter, Instant createAtBefore, Pageable paging);


    @Query(value = "{ 'type' : ?0, 'brokerCode' : ?1, 'status' : ?2 , " +
            "'entities.createdAt' : {$gte: ?3, $lt: ?4} ," +
            " 'entities.entity.service' : { $regex: 'gps' , $options:'i' } }")
    Page<WrapperEntityStations> findByTypeAndBrokerCodeAndStatusNotAndCreatedAtBetweenAndConnectionAsync(
            WrapperType station, String brokerCode, WrapperStatus approved,
            Instant createAtAfter, Instant createAtBefore, Pageable paging);

    @Query(value = "{ 'type' : ?0, 'brokerCode' : ?1, 'status' : { $not: ?2 } , " +
            "'entities.createdAt' : {$gte: ?3, $lt: ?4} ," +
            " 'entities.entity.service' : $not: {{ $regex: 'gps' , $options:'i' }} }")
    Page<WrapperEntityStations> findByIdLikeAndTypeAndBrokerCodeAndStatusNotAndCreatedAtBetweenAndConnectionSync(
            String stationCode, WrapperType station, String brokerCode, WrapperStatus approved,
            Instant toInstant, Instant toInstant1, Pageable paging);

    @Query(value = "{ 'type' : ?0, 'brokerCode' : ?1, 'status' : { $not: ?2 } , " +
            "'entities.createdAt' : {$gte: ?3, $lt: ?4} ," +
            " 'entities.entity.service' : $not: {{ $regex: 'gps' , $options:'i' }} }")
    Page<WrapperEntityStations> findByIdLikeAndTypeAndBrokerCodeAndStatusNotAndCreatedAtBetweenAndConnectionAsync(
            String stationCode, WrapperType station, String brokerCode, WrapperStatus approved,
            Instant toInstant, Instant toInstant1, Pageable paging);
}
