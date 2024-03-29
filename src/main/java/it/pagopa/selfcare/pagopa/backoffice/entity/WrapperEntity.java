package it.pagopa.selfcare.pagopa.backoffice.entity;

import it.pagopa.selfcare.pagopa.backoffice.model.connector.channel.ChannelDetails;
import it.pagopa.selfcare.pagopa.backoffice.model.connector.station.StationDetails;
import it.pagopa.selfcare.pagopa.backoffice.model.connector.wrapper.WrapperStatus;
import it.pagopa.selfcare.pagopa.backoffice.model.connector.wrapper.WrapperType;
import lombok.*;
import lombok.experimental.FieldNameConstants;

import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@FieldNameConstants(onlyExplicitlyIncluded = true)
public class WrapperEntity<T> implements WrapperEntityOperations<T> {

    private String id;
    private WrapperType type;
    private Instant createdAt;

    private Instant modifiedAt;
    private T entity;

    private String modifiedBy;

    private String modifiedByOpt;

    private String note;

    @FieldNameConstants.Include
    private WrapperStatus status;

    public WrapperEntity(T entity) {
        this.createdAt = Instant.now();
        if(entity instanceof ChannelDetails cd) {
            this.id = cd.getChannelCode();
            this.type = WrapperType.CHANNEL;
        } else if(entity instanceof StationDetails sd) {
            this.id = sd.getStationCode();
            this.type = WrapperType.STATION;
        }

        this.entity = entity;
    }

    @Override
    public void setEntity() {
        this.setEntity(this.entity);
    }

}
