package it.pagopa.selfcare.pagopa.backoffice.connector.model;

import it.pagopa.selfcare.pagopa.backoffice.connector.model.tavoloop.TavoloOpOperations;
import lombok.Data;

import java.time.Instant;

@Data
public class DummyTavoloOpEntity implements TavoloOpOperations {

    public String id;
    public String taxCode;
    public Instant modifiedAt;
    public String modifiedBy;
    public Instant createdAt;
    public String createdBy;
    public String name;
    public String referent;
    public String email;
    public String telephone;

}
