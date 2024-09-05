package it.pagopa.selfcare.pagopa.backoffice.entity;


import it.pagopa.selfcare.pagopa.backoffice.model.connector.wrapper.WrapperStatus;
import it.pagopa.selfcare.pagopa.backoffice.model.connector.wrapper.WrapperType;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldNameConstants;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
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
public class WrapperEntityChannels implements Persistable<String> {

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

    private List<WrapperEntityChannel> entities;

    public WrapperEntityChannels(WrapperEntityChannel wrapperEntity) {
        this.createdAt = Instant.now();
        this.id = wrapperEntity.getEntity().getChannelCode();
        this.type = WrapperType.CHANNEL;
        this.brokerCode = wrapperEntity.getEntity().getBrokerPspCode();
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

    public void sortEntitiesById() {
        this.entities.sort(Comparator.comparing(WrapperEntityChannel::getId, Comparator.naturalOrder()));
    }

    public static class Fields {
        public static String id = org.springframework.data.mongodb.core.aggregation.Fields.UNDERSCORE_ID;
    }
}
