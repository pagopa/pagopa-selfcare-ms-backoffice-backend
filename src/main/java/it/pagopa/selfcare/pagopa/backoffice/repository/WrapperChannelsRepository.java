package it.pagopa.selfcare.pagopa.backoffice.repository;

import it.pagopa.selfcare.pagopa.backoffice.entity.WrapperEntityChannels;
import it.pagopa.selfcare.pagopa.backoffice.model.connector.wrapper.WrapperStatus;
import it.pagopa.selfcare.pagopa.backoffice.model.connector.wrapper.WrapperType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface WrapperChannelsRepository extends MongoRepository<WrapperEntityChannels, String> {

    Page<WrapperEntityChannels> findByIdLikeAndTypeAndBrokerCodeAndStatusNot(String id, WrapperType wrapperType, String brokerCode, WrapperStatus status, Pageable pageable);

    Page<WrapperEntityChannels> findByTypeAndBrokerCodeAndStatusNot(WrapperType wrapperType, String brokerCode, WrapperStatus status, Pageable pageable);
}