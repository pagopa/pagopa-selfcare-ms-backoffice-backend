package it.pagopa.selfcare.pagopa.backoffice.connector.model.station;

import lombok.Data;

@Data
public class Station {
    private String stationCode;
    private Boolean enabled;
    private String brokerDescription;
    private Long version;
    private StationStatus stationStatus;
}
