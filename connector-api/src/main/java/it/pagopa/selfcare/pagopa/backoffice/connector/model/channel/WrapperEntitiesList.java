package it.pagopa.selfcare.pagopa.backoffice.connector.model.channel;

import com.fasterxml.jackson.annotation.JsonProperty;
import it.pagopa.selfcare.pagopa.backoffice.connector.model.PageInfo;
import it.pagopa.selfcare.pagopa.backoffice.connector.model.wrapper.WrapperEntitiesOperations;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.List;

@Data
public class WrapperEntitiesList
{
    @JsonProperty("wrapper_entities")
    @NotNull
    private List<WrapperEntitiesOperations> wrapperEntities;

    @JsonProperty("page_info")
    @NotNull
    private PageInfo pageInfo;
}





