package it.pagopa.selfcare.pagopa.backoffice.repository;

import it.pagopa.selfcare.pagopa.backoffice.entity.BrokerIbansEntity;
import it.pagopa.selfcare.pagopa.backoffice.entity.ProjectCreatedAt;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BrokerIbansRepository extends MongoRepository<BrokerIbansEntity, String> {

    Optional<BrokerIbansEntity> findByBrokerCode(String brokerCode);

    Optional<ProjectCreatedAt> findProjectedByBrokerCode(String brokerCode);
}
