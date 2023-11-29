package it.pagopa.selfcare.pagopa.backoffice.model.commissionbundle.client;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class BundlePaymentTypeDTO {

    private String id;
    private String name;
    private String description;
    private LocalDateTime createdDate;
}
