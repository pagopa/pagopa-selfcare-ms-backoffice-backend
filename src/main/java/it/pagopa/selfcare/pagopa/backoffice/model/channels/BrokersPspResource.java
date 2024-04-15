package it.pagopa.selfcare.pagopa.backoffice.model.channels;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import it.pagopa.selfcare.pagopa.backoffice.model.connector.PageInfo;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BrokersPspResource {

    @JsonProperty("brokers_psp")
    @ApiModelProperty(value = " Psp's broker")
    List<BrokerPspResource> brokerPspList;

    @JsonProperty("page_info")
    @ApiModelProperty(value = "info pageable")
    private PageInfo pageInfo;
}
