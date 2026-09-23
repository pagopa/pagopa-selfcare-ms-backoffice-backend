package it.pagopa.selfcare.pagopa.backoffice.entity;

import it.pagopa.selfcare.pagopa.backoffice.model.connector.channel.ChannelDetails;
import it.pagopa.selfcare.pagopa.backoffice.model.connector.wrapper.WrapperStatus;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@NoArgsConstructor
public class WrapperEntityChannelResource {
    private String id;
    private String modifiedBy;
    private String modifiedByOpt;
    private Instant modifiedAt;
    private WrapperStatus status;
    private String note;

    // Tipo concreto anziché 'T'
    private ChannelDetails entity;
}
