package it.pagopa.selfcare.pagopa.backoffice.connector.model.creditorInstitution;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class IbanCreate {

    private String description;


    private LocalDateTime dueDate;


    private String iban;


    private boolean isActive;


    private List<IbanLabel> labels;

    private LocalDateTime validityDate;
}
