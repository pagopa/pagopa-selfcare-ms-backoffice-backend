package it.pagopa.selfcare.pagopa.backoffice.connector.dao;


import it.pagopa.selfcare.pagopa.backoffice.connector.dao.model.TavoloOpEntity;
import it.pagopa.selfcare.pagopa.backoffice.connector.model.tavoloop.TavoloOpOperations;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface TavoloOpRepository extends MongoRepository<TavoloOpEntity,String> {

    TavoloOpOperations findByTaxCode(String id);


}
