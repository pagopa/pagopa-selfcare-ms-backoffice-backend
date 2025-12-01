package it.pagopa.selfcare.pagopa.backoffice.repository;

import it.pagopa.selfcare.pagopa.backoffice.entity.IbanDeletionRequestEntity;
import it.pagopa.selfcare.pagopa.backoffice.util.IbanDeletionRequestStatus;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface IbanDeletionRequestsRepository extends MongoRepository<IbanDeletionRequestEntity, String>  {

    Optional<IbanDeletionRequestEntity> findByIbanValue(String ibanValue);
    List<IbanDeletionRequestEntity> findByCreditorInstitutionCodeAndStatus(String ciCode, IbanDeletionRequestStatus status);
}
