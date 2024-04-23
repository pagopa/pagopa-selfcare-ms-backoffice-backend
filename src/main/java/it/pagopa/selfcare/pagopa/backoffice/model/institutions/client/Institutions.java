package it.pagopa.selfcare.pagopa.backoffice.model.institutions.client;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.List;

@Data
@Builder
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class Institutions {
    private List<Institution> institutions;
}



