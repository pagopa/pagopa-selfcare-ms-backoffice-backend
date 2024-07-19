package it.pagopa.selfcare.pagopa.backoffice.repository;

import it.pagopa.selfcare.pagopa.backoffice.entity.CreditorInstitutionIbansEntity;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CreditorInstitutionsIbansRepository extends MongoRepository<CreditorInstitutionIbansEntity, String> {

}
