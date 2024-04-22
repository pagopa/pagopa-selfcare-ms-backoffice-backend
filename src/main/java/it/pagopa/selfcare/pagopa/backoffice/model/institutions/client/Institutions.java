package it.pagopa.selfcare.pagopa.backoffice.model.institutions.client;

import lombok.Builder;
import lombok.Data;
import lombok.ToString;

import java.util.List;

@Data
@Builder
@ToString
public class Institutions {
    private List<Institution> institutions;
}



