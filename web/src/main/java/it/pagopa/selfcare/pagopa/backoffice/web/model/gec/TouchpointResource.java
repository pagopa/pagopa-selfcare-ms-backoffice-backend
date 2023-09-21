package it.pagopa.selfcare.pagopa.backoffice.web.model.gec;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.time.OffsetDateTime;
@Data
public class TouchpointResource {
    @JsonProperty("id")
    private String id;

    @JsonProperty("name")
    private String name;

    @JsonProperty("createdDate")
    private OffsetDateTime created_date;
}
