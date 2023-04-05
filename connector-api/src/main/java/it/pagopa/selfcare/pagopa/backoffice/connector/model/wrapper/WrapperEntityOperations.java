package it.pagopa.selfcare.pagopa.backoffice.connector.model.wrapper;

import java.time.LocalDateTime;

public interface WrapperEntityOperations<T> {
//
//    WrapperEntityOperations<T> createWrapperEntity(T entity);

     void setId(String id);
     void setType(WrapperType type);
     void setCreatedAt(LocalDateTime createdAt);

     void setModifiedAt(LocalDateTime createdAt);
     void setNote(String note);
     void setEntity();

     void setModifiedBy(String modifiedBy);
     void setModifiedByOpt(String modifiedBy);
     void setStatus(WrapperStatus status);

     WrapperStatus getStatus();
     String getNote();
     String getId();
     WrapperType getType();
     LocalDateTime getCreatedAt();
     T getEntity();

     String getModifiedBy();
     LocalDateTime getModifiedAt();
     String getModifiedByOpt();
}
