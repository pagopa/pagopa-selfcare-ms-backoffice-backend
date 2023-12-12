package it.pagopa.selfcare.pagopa.backoffice.model.institutions;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DelegationExternal {

    private String id;

    private String brokerId;

    private String brokerName;

    private String institutionId;

    private String institutionRootName;

    private String institutionName;

    private String productId;

    private String type;

    private String brokerTaxCode;

    private String brokerType;

    private String institutionType;

    private String taxCode;
}
