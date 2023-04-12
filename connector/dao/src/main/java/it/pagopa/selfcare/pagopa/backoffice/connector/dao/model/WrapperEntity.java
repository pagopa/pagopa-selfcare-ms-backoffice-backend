package it.pagopa.selfcare.pagopa.backoffice.connector.dao.model;

import it.pagopa.selfcare.pagopa.backoffice.connector.model.channel.ChannelDetails;
import it.pagopa.selfcare.pagopa.backoffice.connector.model.station.StationDetails;
import it.pagopa.selfcare.pagopa.backoffice.connector.model.wrapper.WrapperEntityOperations;
import it.pagopa.selfcare.pagopa.backoffice.connector.model.wrapper.WrapperStatus;
import it.pagopa.selfcare.pagopa.backoffice.connector.model.wrapper.WrapperType;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldNameConstants;

import java.time.Instant;
import java.time.LocalDateTime;
@Data
@NoArgsConstructor
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

    public WrapperEntity(T entity){
        this.createdAt = Instant.now();
        if (entity instanceof ChannelDetails) {
            this.id = ((ChannelDetails) entity).getChannelCode();
            this.type = WrapperType.CHANNEL;
        } else if (entity instanceof StationDetails) {
            this.id = ((StationDetails) entity).getStationCode();
            this.type = WrapperType.STATION;
        }

        this.entity= entity;
    }

    @Override
    public void setEntity() {
        this.setEntity(this.entity);
    }


//    @Override
//    public WrapperEntity<T> createWrapperEntity(T entity) {
//        return new WrapperEntity<T>(entity);
//    }

}
