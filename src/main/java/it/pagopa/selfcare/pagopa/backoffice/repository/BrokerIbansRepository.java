package it.pagopa.selfcare.pagopa.backoffice.repository;

import it.pagopa.selfcare.pagopa.backoffice.entity.BrokerIbansEntity;
import it.pagopa.selfcare.pagopa.backoffice.entity.ProjectCreatedAt;
import org.springframework.data.mongodb.repository.Aggregation;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.Optional;
import java.util.Set;

@Repository
public interface BrokerIbansRepository extends MongoRepository<BrokerIbansEntity, String>, BrokerIbansCustomRepository {

    Optional<BrokerIbansEntity> findByBrokerCode(String brokerCode);

    Optional<ProjectCreatedAt> findProjectedByBrokerCode(String brokerCode);

    @Aggregation(pipeline = {"{ $match : { createdAt : { $gte: ?0 } } }", "{ $project: { brokerCode: 1 } }"})
    Set<String> findProjectedByCreatedAtGreaterThen(Instant createdAt);

    void deleteAllByCreatedAtBefore(Instant createdAt);
}
