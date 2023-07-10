package it.pagopa.selfcare.pagopa.backoffice.web.model.channels;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import it.pagopa.selfcare.pagopa.backoffice.connector.model.PageInfo;
import lombok.Data;

import java.util.List;

@Data
public class BrokersPspResource {

    @JsonProperty("brokers_psp")
    @ApiModelProperty(value = "${swagger.model.pspbrokers}")
    List<BrokerPspResource> brokerPspResources;

    @JsonProperty("page_info")
    @ApiModelProperty(value = "${swagger.model.pageinfo}")
    private PageInfo pageInfo;
}
