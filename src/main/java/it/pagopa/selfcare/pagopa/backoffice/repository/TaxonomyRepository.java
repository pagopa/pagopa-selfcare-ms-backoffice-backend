package it.pagopa.selfcare.pagopa.backoffice.repository;

import it.pagopa.selfcare.pagopa.backoffice.entity.TaxonomyEntity;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface TaxonomyRepository extends MongoRepository<TaxonomyEntity, String> {

}
