package it.pagopa.selfcare.pagopa.backoffice.repository;


import it.pagopa.selfcare.pagopa.backoffice.entity.TavoloOpEntity;
import it.pagopa.selfcare.pagopa.backoffice.entity.TavoloOpOperations;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TavoloOpRepository extends MongoRepository<TavoloOpEntity, String> {

    TavoloOpOperations findByTaxCode(String id);
}
