package it.pagopa.selfcare.pagopa.backoffice.connector.model.gec;

import lombok.Data;

import java.time.OffsetDateTime;

@Data
public class Touchpoint {

    private String id;

    private String name;

    private OffsetDateTime createdDate;
}
