package it.pagopa.selfcare.pagopa.backoffice.connector.model;

import it.pagopa.selfcare.pagopa.backoffice.connector.model.channel.ChannelDetails;
import it.pagopa.selfcare.pagopa.backoffice.connector.model.station.StationDetails;
import it.pagopa.selfcare.pagopa.backoffice.connector.model.wrapper.WrapperEntityOperations;
import it.pagopa.selfcare.pagopa.backoffice.connector.model.wrapper.WrapperStatus;
import it.pagopa.selfcare.pagopa.backoffice.connector.model.wrapper.WrapperType;
import lombok.Data;

import java.time.Instant;

@Data
public class DummyWrapperEntity<T> implements WrapperEntityOperations<T> {

    private String id;
    private WrapperType type;
    private Instant createdAt;

    private Instant ModifiedAt;
    private T entity;

    private  String note;

    private WrapperStatus status;

    private String setModifiedBy;

    private String setModifiedByOpt;

    private String modifiedBy;

    private String modifiedByOpt;

    @Override
    public void setEntity() {
        this.setEntity(this.entity);
    }

    public DummyWrapperEntity(T entity){
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

}
