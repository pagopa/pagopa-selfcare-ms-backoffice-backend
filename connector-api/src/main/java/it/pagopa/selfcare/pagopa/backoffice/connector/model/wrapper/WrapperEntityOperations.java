package it.pagopa.selfcare.pagopa.backoffice.connector.model.wrapper;

import java.time.Instant;

public interface WrapperEntityOperations<T> {
//
//    WrapperEntityOperations<T> createWrapperEntity(T entity);

     void setId(String id);
     void setType(WrapperType type);
     void setCreatedAt(Instant createdAt);

     void setModifiedAt(Instant createdAt);
     void setNote(String note);
     void setEntity();

     void setModifiedBy(String modifiedBy);
     void setModifiedByOpt(String modifiedBy);
     void setStatus(WrapperStatus status);

     WrapperStatus getStatus();
     String getNote();
     String getId();
     WrapperType getType();
     Instant getCreatedAt();
     T getEntity();

     String getModifiedBy();
     Instant getModifiedAt();
     String getModifiedByOpt();
}
