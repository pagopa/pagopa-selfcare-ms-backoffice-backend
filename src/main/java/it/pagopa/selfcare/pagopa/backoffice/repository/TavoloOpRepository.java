package it.pagopa.selfcare.pagopa.backoffice.repository;


import it.pagopa.selfcare.pagopa.backoffice.entity.TavoloOpEntity;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TavoloOpRepository extends MongoRepository<TavoloOpEntity, String> {

    Optional<TavoloOpEntity> findByTaxCode(String id);
}
