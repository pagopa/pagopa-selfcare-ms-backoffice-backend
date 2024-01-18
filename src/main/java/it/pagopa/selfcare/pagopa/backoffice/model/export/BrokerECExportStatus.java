package it.pagopa.selfcare.pagopa.backoffice.model.export;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class BrokerECExportStatus {

    @JsonProperty("broker_ibans_last_update")
    private Instant brokerIbansLastUpdate;

    @JsonProperty("broker_institutions_last_update")
    private Instant brokerInstitutionsLastUpdate;
}
