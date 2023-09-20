package it.pagopa.selfcare.pagopa.backoffice.web.model.gec;

import com.fasterxml.jackson.annotation.JsonProperty;
import it.pagopa.selfcare.pagopa.backoffice.connector.model.PageInfo;
import it.pagopa.selfcare.pagopa.backoffice.connector.model.gec.Bundle;
import lombok.Data;

import java.util.List;

@Data
public class BundlesResource {

    @JsonProperty("bundles")
    private List<BundleResource> bundles;

    @JsonProperty("pageInfo")
    private PageInfo pageInfo;
}
