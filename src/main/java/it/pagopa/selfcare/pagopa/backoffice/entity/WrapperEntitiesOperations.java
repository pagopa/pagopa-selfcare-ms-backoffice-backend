package it.pagopa.selfcare.pagopa.backoffice.entity;

import it.pagopa.selfcare.pagopa.backoffice.model.connector.wrapper.WrapperStatus;
import it.pagopa.selfcare.pagopa.backoffice.model.connector.wrapper.WrapperType;

import java.time.Instant;
import java.util.List;

public interface WrapperEntitiesOperations<T> {

    void setId(String id);

    void setType(WrapperType wrapperType);

    void setStatus(WrapperStatus status);

    void setModifiedAt(Instant modifiedAt);

    void setModifiedBy(String modifiedBy);

    void setModifiedByOpt(String modifiedByOpt);

    void setCreatedAt(Instant createdAt);

    void setCreatedBy(String createdBy);

    void setBrokerCode(String brokerCode);

    void setNote(String note);

    String getId();

    String getBrokerCode();

    WrapperType getType();

    WrapperStatus getStatus();

    Instant getModifiedAt();

    String getModifiedBy();

    String getModifiedByOpt();

    Instant getCreatedAt();

    String getCreatedBy();

    String getNote();

    List<WrapperEntityOperations<T>> getWrapperEntityOperationsSortedList();

    void sortEntitesByCreatedAt();

    void updateCurrentWrapperEntity(WrapperEntityOperations<T> wrapperEntity, String status, String note, String modifiedByOpt);
}