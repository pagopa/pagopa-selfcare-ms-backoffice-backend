package it.pagopa.selfcare.pagopa.backoffice.repository;

import it.pagopa.selfcare.pagopa.backoffice.entity.IbanDeletionRequestEntity;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IbanDeletionRequestsRepository extends MongoRepository<IbanDeletionRequestEntity, String>  {

    IbanDeletionRequestEntity findByIbanValue(String ibanValue);
}
