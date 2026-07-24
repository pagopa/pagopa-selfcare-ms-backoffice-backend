package it.pagopa.selfcare.pagopa.backoffice.repository;

import it.pagopa.selfcare.pagopa.backoffice.entity.CreditorInstitutionIbansEntity;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;

@Repository
public interface CreditorInstitutionsIbansRepository extends MongoRepository<CreditorInstitutionIbansEntity, String> {
    @Query("{ $or: [ { 'createdAt': { $lt: ?0 } }, { 'createdAt': null } ] }")
    List<CreditorInstitutionIbansEntity> findByCreatedAtBeforeOrNull(Instant dateBefore, Pageable pageable);

    void deleteAllByIdIn(List<String> ids);
}
