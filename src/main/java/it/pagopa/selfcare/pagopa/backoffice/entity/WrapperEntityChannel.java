package it.pagopa.selfcare.pagopa.backoffice.entity;

import it.pagopa.selfcare.pagopa.backoffice.model.connector.channel.ChannelDetails;
import it.pagopa.selfcare.pagopa.backoffice.model.connector.wrapper.WrapperStatus;
import it.pagopa.selfcare.pagopa.backoffice.model.connector.wrapper.WrapperType;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class WrapperEntityChannel {

    private String id;
    private WrapperType type;
    private WrapperStatus status;
    private ChannelDetails entity;

    private Instant createdAt;
    private Instant modifiedAt;
    private String modifiedBy;
    private String modifiedByOpt;

    private String note;

    public WrapperEntityChannel(ChannelDetails entity) {
        this.createdAt = Instant.now();
        this.id = entity.getChannelCode();
        this.type = WrapperType.CHANNEL;
        this.entity = entity;
    }
}