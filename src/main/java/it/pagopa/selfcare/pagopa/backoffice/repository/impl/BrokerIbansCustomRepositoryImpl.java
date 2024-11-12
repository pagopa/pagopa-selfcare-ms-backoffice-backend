package it.pagopa.selfcare.pagopa.backoffice.repository.impl;

import it.pagopa.selfcare.pagopa.backoffice.entity.BrokerIbansEntity;
import it.pagopa.selfcare.pagopa.backoffice.repository.BrokerIbansCustomRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Update;

import java.util.List;

import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;

public class BrokerIbansCustomRepositoryImpl implements BrokerIbansCustomRepository {

    private final MongoTemplate mongoTemplate;

    @Autowired
    public BrokerIbansCustomRepositoryImpl(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    /**
     * Updates the BrokerIbansEntity with the specified broker code by adding the provided list of iban to the
     * iban list.
     *
     * @param brokerCode      the broker tax code
     * @param ibansEntityList the list iban
     */
    @Override
    public void updateBrokerIbanList(String brokerCode, List<BrokerIbansEntity> ibansEntityList) {
        this.mongoTemplate.updateFirst(
                query(where("brokerCode").is(brokerCode)),
                new Update().push("ibans").each(ibansEntityList),
                BrokerIbansEntity.class
        );
    }
}