package it.pagopa.selfcare.pagopa.backoffice.connector.model;

import it.pagopa.selfcare.pagopa.backoffice.connector.model.channel.ChannelDetails;
import it.pagopa.selfcare.pagopa.backoffice.connector.model.station.StationDetails;
import it.pagopa.selfcare.pagopa.backoffice.connector.model.wrapper.WrapperEntitiesOperations;
import it.pagopa.selfcare.pagopa.backoffice.connector.model.wrapper.WrapperEntityOperations;
import it.pagopa.selfcare.pagopa.backoffice.connector.model.wrapper.WrapperStatus;
import it.pagopa.selfcare.pagopa.backoffice.connector.model.wrapper.WrapperType;
import lombok.Data;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Data
public class DummyWrapperEntities<T> implements WrapperEntitiesOperations<T> {

    private String id;
    private WrapperType type;
    private WrapperStatus status;
    private Instant modifiedAt;
    private String modifiedBy;
    private String modifiedByOpt;
    private Instant createdAt;
    private String createdBy;
    private List<DummyWrapperEntity<T>> entities;

    private String note;

    @Override
    public List<WrapperEntityOperations<T>> getWrapperEntityOperationsSortedList() {
        List<WrapperEntityOperations<T>> list = new ArrayList<>(entities);
        list.sort(Comparator.comparing(WrapperEntityOperations::getCreatedAt));
        return list;

    }

    @Override
    public void updateCurrentWrapperEntity(WrapperEntityOperations<T> wrapperEntity, String status, String note, String modifiedByOpt) {
        this.entities.sort(Comparator.comparing(WrapperEntityOperations::getCreatedAt));
        DummyWrapperEntity<T> wrapper = this.entities.get(0);
        this.status = WrapperStatus.valueOf(status);
        wrapper.setEntity(wrapperEntity.getEntity());
        this.setModifiedAt(Instant.now());
        this.setModifiedBy(modifiedByOpt);
        wrapper.setNote(note);

    }


    public DummyWrapperEntities(DummyWrapperEntity<T> wrapperEntity) {
        this.createdAt = Instant.now();
        Object obj = wrapperEntity.getEntity();
        if (obj instanceof ChannelDetails) {

            this.id = ((ChannelDetails) obj).getChannelCode();
            this.type = WrapperType.CHANNEL;
        } else if (obj instanceof StationDetails) {
            this.id = ((StationDetails) obj).getStationCode();
            this.type = WrapperType.STATION;
        }
        this.status = WrapperStatus.TO_CHECK;
        if (entities == null) {
            entities = new ArrayList<>();
            entities.add(wrapperEntity);
        }
    }
}
