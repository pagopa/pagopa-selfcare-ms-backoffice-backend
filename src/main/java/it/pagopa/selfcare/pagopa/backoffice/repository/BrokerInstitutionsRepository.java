package it.pagopa.selfcare.pagopa.backoffice.repository;

import it.pagopa.selfcare.pagopa.backoffice.entity.BrokerInstitutionsEntity;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BrokerInstitutionsRepository extends MongoRepository<BrokerInstitutionsEntity, String> {

    Optional<BrokerInstitutionsEntity> findByBrokerCode(String brokerCode);
}
