package it.pagopa.selfcare.pagopa.backoffice.entity;

import it.pagopa.selfcare.pagopa.backoffice.model.connector.channel.ChannelDetails;
import it.pagopa.selfcare.pagopa.backoffice.model.connector.wrapper.WrapperStatus;
import it.pagopa.selfcare.pagopa.backoffice.model.connector.wrapper.WrapperType;
import lombok.Setter;
import lombok.experimental.FieldNameConstants;
import org.springframework.data.annotation.*;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Setter
public class ChannelDetailsWrapperResource {

    private String id;
    private String brokerCode;
    private WrapperType type;
    private WrapperStatus status;
    private Instant modifiedAt;
    private String modifiedBy;
    private String modifiedByOpt;
    private Instant createdAt;
    private String createdBy;
    private String note;

    // Lista senza generics, referenzia la classe concreta qui sotto
    private List<WrapperEntityChannelResource> entities;

        public static ChannelDetailsWrapperResource toResource(WrapperEntities<ChannelDetails> wrapperEntities) {
            if (wrapperEntities == null) {
                return null;
            }

            ChannelDetailsWrapperResource resource = new ChannelDetailsWrapperResource();
            resource.setId(wrapperEntities.getId());
            resource.setBrokerCode(wrapperEntities.getBrokerCode());
            resource.setType(wrapperEntities.getType());
            resource.setStatus(wrapperEntities.getStatus());
            resource.setModifiedAt(wrapperEntities.getModifiedAt());
            resource.setModifiedBy(wrapperEntities.getModifiedBy());
            resource.setModifiedByOpt(wrapperEntities.getModifiedByOpt());
            resource.setCreatedAt(wrapperEntities.getCreatedAt());
            resource.setCreatedBy(wrapperEntities.getCreatedBy());
            resource.setNote(wrapperEntities.getNote());

            if (wrapperEntities.getEntities() != null) {
                resource.setEntities(wrapperEntities.getEntities().stream()
                        .map(ChannelDetailsWrapperResource::toEntityResource)
                        .collect(Collectors.toList()));
            } else {
                resource.setEntities(new ArrayList<>());
            }

            return resource;
        }

        private static WrapperEntityChannelResource toEntityResource(WrapperEntity<ChannelDetails> wrapperEntity) {
            if (wrapperEntity == null) {
                return null;
            }

            WrapperEntityChannelResource resource = new WrapperEntityChannelResource();
            // Adegua questi setter ai reali getter presenti nella tua classe WrapperEntity
            resource.setId(wrapperEntity.getId());
            resource.setModifiedBy(wrapperEntity.getModifiedBy());
            resource.setModifiedByOpt(wrapperEntity.getModifiedByOpt());
            resource.setModifiedAt(wrapperEntity.getModifiedAt());
            resource.setStatus(wrapperEntity.getStatus());
            resource.setNote(wrapperEntity.getNote());

            resource.setEntity(wrapperEntity.getEntity());

            return resource;
        }


}
