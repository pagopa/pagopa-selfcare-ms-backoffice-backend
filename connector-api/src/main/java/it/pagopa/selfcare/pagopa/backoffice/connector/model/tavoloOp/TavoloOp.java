package it.pagopa.selfcare.pagopa.backoffice.connector.model.tavoloOp;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.time.Instant;

@Data
public class TavoloOp {

    private String id;

    private String taxCode;

    private String name;

    private String referent;

    private String email;

    private String telephone;


    private Instant modifiedAt;


    private String modifiedBy;


    private Instant createdAt;


    private String createdBy;

}
