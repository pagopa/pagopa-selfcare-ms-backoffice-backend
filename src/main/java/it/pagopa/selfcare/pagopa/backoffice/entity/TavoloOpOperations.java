package it.pagopa.selfcare.pagopa.backoffice.entity;

import java.time.Instant;

public interface TavoloOpOperations {

    String getId();

    void setId(String id);

    Instant getModifiedAt();

    void setModifiedAt(Instant modifiedAt);

    String getModifiedBy();

    void setModifiedBy(String modifiedBy);

    Instant getCreatedAt();

    void setCreatedAt(Instant createdAt);

    String getCreatedBy();

    void setCreatedBy(String createdBy);

    String getName();

    void setName(String name);

    String getReferent();

    void setReferent(String referent);

    String getEmail();

    void setEmail(String email);

    String getTelephone();

    void setTelephone(String telephone);

    String getTaxCode();

    void setTaxCode(String taxCode);

}
