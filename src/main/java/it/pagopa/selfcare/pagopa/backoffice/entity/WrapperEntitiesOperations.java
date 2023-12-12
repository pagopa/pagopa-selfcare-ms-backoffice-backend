package it.pagopa.selfcare.pagopa.backoffice.entity;

import it.pagopa.selfcare.pagopa.backoffice.model.connector.wrapper.WrapperStatus;
import it.pagopa.selfcare.pagopa.backoffice.model.connector.wrapper.WrapperType;

import java.time.Instant;
import java.util.List;

public interface WrapperEntitiesOperations<T> {

    String getId();

    void setId(String id);

    String getBrokerCode();

    void setBrokerCode(String brokerCode);

    WrapperType getType();

    void setType(WrapperType wrapperType);

    WrapperStatus getStatus();

    void setStatus(WrapperStatus status);

    Instant getModifiedAt();

    void setModifiedAt(Instant modifiedAt);

    String getModifiedBy();

    void setModifiedBy(String modifiedBy);

    String getModifiedByOpt();

    void setModifiedByOpt(String modifiedByOpt);

    Instant getCreatedAt();

    void setCreatedAt(Instant createdAt);

    String getCreatedBy();

    void setCreatedBy(String createdBy);

    String getNote();

    void setNote(String note);

    List<WrapperEntityOperations<T>> getWrapperEntityOperationsSortedList();

    void sortEntitesByCreatedAt();

    void updateCurrentWrapperEntity(WrapperEntityOperations<T> wrapperEntity, String status, String note, String modifiedByOpt);
}
