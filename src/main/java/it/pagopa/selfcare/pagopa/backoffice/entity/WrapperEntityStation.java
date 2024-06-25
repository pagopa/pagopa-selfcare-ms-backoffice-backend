package it.pagopa.selfcare.pagopa.backoffice.entity;

import it.pagopa.selfcare.pagopa.backoffice.model.connector.station.StationDetails;
import it.pagopa.selfcare.pagopa.backoffice.model.connector.wrapper.WrapperStatus;
import it.pagopa.selfcare.pagopa.backoffice.model.connector.wrapper.WrapperType;
import lombok.*;

import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class WrapperEntityStation {

    private String id;
    private WrapperType type;
    private WrapperStatus status;
    private StationDetails entity;

    private Instant createdAt;
    private Instant modifiedAt;
    private String modifiedBy;
    private String modifiedByOpt;

    private String note;


    public WrapperEntityStation(StationDetails entity) {
        this.createdAt = Instant.now();
        this.id = entity.getStationCode();
        this.type = WrapperType.STATION;
        this.entity = entity;
    }


}
