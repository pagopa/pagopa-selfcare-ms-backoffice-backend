package it.pagopa.selfcare.pagopa.backoffice.model.commissionbundle.client;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class TouchpointDTO {

    private String id;

    private String name;

    private LocalDateTime createdDate;
}
