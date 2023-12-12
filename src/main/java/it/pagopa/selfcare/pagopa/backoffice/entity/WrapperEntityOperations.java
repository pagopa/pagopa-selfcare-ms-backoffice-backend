package it.pagopa.selfcare.pagopa.backoffice.entity;

import it.pagopa.selfcare.pagopa.backoffice.model.connector.wrapper.WrapperStatus;
import it.pagopa.selfcare.pagopa.backoffice.model.connector.wrapper.WrapperType;

import java.time.Instant;

public interface WrapperEntityOperations<T> {
//
//    WrapperEntityOperations<T> createWrapperEntity(T entity);

    void setEntity();

    WrapperStatus getStatus();

    void setStatus(WrapperStatus status);

    String getNote();

    void setNote(String note);

    String getId();

    void setId(String id);

    WrapperType getType();

    void setType(WrapperType type);

    Instant getCreatedAt();

    void setCreatedAt(Instant createdAt);

    T getEntity();

    String getModifiedBy();

    void setModifiedBy(String modifiedBy);

    Instant getModifiedAt();

    void setModifiedAt(Instant createdAt);

    String getModifiedByOpt();

    void setModifiedByOpt(String modifiedBy);
}
