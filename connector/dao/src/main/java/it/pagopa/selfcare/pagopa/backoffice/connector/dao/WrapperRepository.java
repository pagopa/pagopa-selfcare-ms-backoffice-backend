package it.pagopa.selfcare.pagopa.backoffice.connector.dao;

import it.pagopa.selfcare.pagopa.backoffice.connector.dao.model.WrapperEntities;
import it.pagopa.selfcare.pagopa.backoffice.connector.model.wrapper.WrapperStatus;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface WrapperRepository extends MongoRepository<WrapperEntities, String> {


    List<WrapperEntities> findByStatusNot(WrapperStatus status);

    List<WrapperEntities> findByStatus(WrapperStatus status);

    List<WrapperEntities> findByStatusAndType(WrapperStatus status);

}
