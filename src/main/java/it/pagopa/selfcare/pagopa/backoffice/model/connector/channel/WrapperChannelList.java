package it.pagopa.selfcare.pagopa.backoffice.model.connector.channel;

import com.fasterxml.jackson.annotation.JsonProperty;
import it.pagopa.selfcare.pagopa.backoffice.entity.WrapperEntityChannels;
import it.pagopa.selfcare.pagopa.backoffice.model.connector.PageInfo;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WrapperChannelList {

    @JsonProperty("wrapper_entities")
    @NotNull
    private List<WrapperEntityChannels> wrapperEntities;

    @JsonProperty("page_info")
    @NotNull
    private PageInfo pageInfo;
}