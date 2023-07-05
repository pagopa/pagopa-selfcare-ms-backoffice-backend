package it.pagopa.selfcare.pagopa.backoffice.connector.dao.model;


import it.pagopa.selfcare.pagopa.backoffice.connector.model.channel.ChannelDetails;
import it.pagopa.selfcare.pagopa.backoffice.connector.model.station.StationDetails;
import it.pagopa.selfcare.pagopa.backoffice.connector.model.wrapper.WrapperEntitiesOperations;
import it.pagopa.selfcare.pagopa.backoffice.connector.model.wrapper.WrapperEntityOperations;
import it.pagopa.selfcare.pagopa.backoffice.connector.model.wrapper.WrapperStatus;
import it.pagopa.selfcare.pagopa.backoffice.connector.model.wrapper.WrapperType;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldNameConstants;
import org.springframework.data.annotation.*;
import org.springframework.data.domain.Persistable;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Data
@NoArgsConstructor
@EqualsAndHashCode(of = "id")
@Document("wrappers")
@FieldNameConstants(onlyExplicitlyIncluded = true)
public class WrapperEntities<T> implements WrapperEntitiesOperations<T>, Persistable<String> {

    @Id
    private String id;

    private String brokerCode;
    @FieldNameConstants.Include
    private WrapperType type;

    @FieldNameConstants.Include
    private WrapperStatus status;

    @LastModifiedDate
    @FieldNameConstants.Include
    private Instant modifiedAt;
    @LastModifiedBy
    @FieldNameConstants.Include
    private String modifiedBy;

    @FieldNameConstants.Include
    private String modifiedByOpt;
    @CreatedDate
    private Instant createdAt;
    @CreatedBy
    private String createdBy;

    private String note;

    private List<WrapperEntity<T>> entities;


    public WrapperEntities(WrapperEntity<T> wrapperEntity) {
        this();
        this.createdAt = Instant.now();
        Object obj = wrapperEntity.getEntity();
        if (obj instanceof ChannelDetails) {

            this.id = ((ChannelDetails) obj).getChannelCode();
            this.type = WrapperType.CHANNEL;
            this.brokerCode = ((ChannelDetails) obj).getBrokerPspCode();
        } else if (obj instanceof StationDetails) {
            this.id = ((StationDetails) obj).getStationCode();
            this.type = WrapperType.STATION;
            this.brokerCode = ((StationDetails) obj).getBrokerCode();
        }
        this.status = wrapperEntity.getStatus();
        if (entities == null) {
            entities = new ArrayList<>();
        }
        entities.add(wrapperEntity);
    }

    @Override
    public boolean isNew() {
        return false;
    }


    public void sortEntitesByCreatedAt(){
        this.entities.sort(Comparator.comparing(WrapperEntityOperations::getCreatedAt,Comparator.reverseOrder()));
    }

    @Override
    public List<WrapperEntityOperations<T>> getWrapperEntityOperationsSortedList() {
        List<WrapperEntityOperations<T>> list = new ArrayList<>(this.entities);
        list.sort(Comparator.comparing(WrapperEntityOperations::getCreatedAt,Comparator.reverseOrder()));
        return list;

    }


    @Override
    public void updateCurrentWrapperEntity(WrapperEntityOperations<T> wrapperEntity, String status, String note, String modifiedByOpt) {
        this.entities.sort(Comparator.comparing(WrapperEntityOperations::getCreatedAt,Comparator.reverseOrder()));
        WrapperEntity<T> wrapper = this.entities.get(0);
        this.status = WrapperStatus.valueOf(status);
        wrapper.setEntity(wrapperEntity.getEntity());
        this.setModifiedAt(Instant.now());
        this.setModifiedByOpt(modifiedByOpt);
        wrapper.setNote(note);
        wrapper.setModifiedAt(Instant.now());
        wrapper.setModifiedByOpt(modifiedByOpt);
        wrapper.setStatus(WrapperStatus.valueOf(status));
    }



    public static class Fields {
        public static String id = org.springframework.data.mongodb.core.aggregation.Fields.UNDERSCORE_ID;
    }

}
