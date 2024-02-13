package it.pagopa.selfcare.pagopa.backoffice.repository;

import it.pagopa.selfcare.pagopa.backoffice.entity.PspLegacyEntity;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PspLegacyRepository extends MongoRepository<PspLegacyEntity, String>  {
    Optional<PspLegacyEntity> findByCf(String cf);

}
