package it.pagopa.selfcare.pagopa.backoffice.model.connector.wrapper;

import com.fasterxml.jackson.annotation.JsonProperty;
import it.pagopa.selfcare.pagopa.backoffice.model.connector.PageInfo;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class WrapperChannels {
    @JsonProperty(required = true)
    private List<WrapperChannel> channelList;
    @JsonProperty(required = true)
    private PageInfo pageInfo;
}
