package it.pagopa.selfcare.pagopa.backoffice.connector.model.tavoloOp;

import java.time.Instant;

public interface TavoloOpOperations {

    void setId(String id);

    /**
     * private String name;
     * <p>
     * private String referent;
     * <p>
     * private String email;
     * <p>
     * private String telephone;
     *
     * @param modifiedAt
     */

    void setModifiedAt(Instant modifiedAt);

    void setModifiedBy(String modifiedBy);

    void setCreatedAt(Instant createdAt);

    void setCreatedBy(String createdBy);

    void setName(String name);

    void setReferent(String referent);

    void setEmail(String email);

    void setTelephone(String telephone);


    String getId();


    Instant getModifiedAt();

    String getModifiedBy();


    Instant getCreatedAt();

    String getCreatedBy();

    String getName();
    String getReferent();

    String getEmail();

    String getTelephone();


}
