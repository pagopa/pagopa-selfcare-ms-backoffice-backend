package it.pagopa.selfcare.pagopa.backoffice.repository;

import it.pagopa.selfcare.pagopa.backoffice.entity.TaxonomyGroupEntity;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface TaxonomyGroupRepository extends MongoRepository<TaxonomyGroupEntity, String> {

}
