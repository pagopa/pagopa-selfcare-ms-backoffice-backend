package it.pagopa.selfcare.pagopa.backoffice.connector.dao;

import it.pagopa.selfcare.pagopa.backoffice.connector.dao.model.TavoloOpEntity;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface TavoloOpRepository extends MongoRepository<TavoloOpEntity,String> {


}
