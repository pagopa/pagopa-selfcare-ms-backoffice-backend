package it.pagopa.selfcare.pagopa.backoffice.repository;

import it.pagopa.selfcare.pagopa.backoffice.entity.TaxonomyEntity;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.time.Instant;
import java.util.List;

public interface TaxonomyRepository extends MongoRepository<TaxonomyEntity, String> {

    @Query("{ $and : [" +
            " { $or : [ { $expr: { $eq: ['?0', 'null'] } } , { ecTypeCode : ?0 } ] }, " +
            " { $or : [ { $expr: { $eq: ['?1', 'null'] } } , { macroAreaEcProgressive : ?1 } ] }, " +
            " { $or : [ { $expr: { $eq: ['?2', ''] } } , { 'specificBuiltInData': {$regex : ?2, $options: 'i'} } ] }, " +
            " { $or : [ { $expr: { $eq: ['?3', 'false'] } } , { $and: [ { 'endDate': { $gte : ?4 } }, { 'startDate': { $lte : ?4 } } ] } ] }, " +
            " ]}")
    List<TaxonomyEntity> searchTaxonomies(String ec, String macroArea, String code, Boolean valid, Instant now);

}
