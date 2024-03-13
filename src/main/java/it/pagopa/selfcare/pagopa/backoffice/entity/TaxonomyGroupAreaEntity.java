package it.pagopa.selfcare.pagopa.backoffice.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TaxonomyGroupAreaEntity {

    private String macroAreaEcProgressive;

    private String macroAreaName;

    private String macroAreaDescription;

}
