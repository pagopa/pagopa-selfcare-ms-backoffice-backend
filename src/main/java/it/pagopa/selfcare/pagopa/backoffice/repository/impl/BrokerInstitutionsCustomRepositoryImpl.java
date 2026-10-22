package it.pagopa.selfcare.pagopa.backoffice.repository.impl;

import it.pagopa.selfcare.pagopa.backoffice.entity.BrokerInstitutionEntity;
import it.pagopa.selfcare.pagopa.backoffice.entity.BrokerInstitutionsEntity;
import it.pagopa.selfcare.pagopa.backoffice.repository.BrokerInstitutionsCustomRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Update;

import java.time.Instant;
import java.util.List;

import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;

public class BrokerInstitutionsCustomRepositoryImpl implements BrokerInstitutionsCustomRepository {

    private final MongoTemplate mongoTemplate;

    @Autowired
    public BrokerInstitutionsCustomRepositoryImpl(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    /**
     * Atomically replaces the institutions list of the BrokerInstitutionsEntity with the specified broker code and refreshes
     * its creation date. If no document exists for the broker, a new one is created.
     *
     * @param brokerCode   the broker tax code
     * @param institutions the full list of creditor institutions associated to the broker
     */
    @Override
    public void replaceBrokerInstitutionsList(String brokerCode, List<BrokerInstitutionEntity> institutions) {
        this.mongoTemplate.upsert(
                query(where("brokerCode").is(brokerCode)),
                new Update()
                        .set("institutions", institutions)
                        .set("createdAt", Instant.now()),
                BrokerInstitutionsEntity.class
        );
    }
}