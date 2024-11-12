package it.pagopa.selfcare.pagopa.backoffice.repository.impl;

import it.pagopa.selfcare.pagopa.backoffice.entity.BrokerInstitutionEntity;
import it.pagopa.selfcare.pagopa.backoffice.entity.BrokerInstitutionsEntity;
import it.pagopa.selfcare.pagopa.backoffice.repository.BrokerInstitutionsCustomRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Update;

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
     * Updates the BrokerInstitutionsEntity with the specified broker code by adding the provided list of institutions to the
     * institutions list.
     *
     * @param brokerCode  the broker tax code
     * @param institutions the list creditor institutions
     */
    @Override
    public void updateBrokerInstitutionsList(String brokerCode, List<BrokerInstitutionEntity> institutions) {
        this.mongoTemplate.updateFirst(
                query(where("brokerCode").is(brokerCode)),
                new Update().push("institutions").each(institutions),
                BrokerInstitutionsEntity.class
        );
    }
}