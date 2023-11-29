package it.pagopa.selfcare.pagopa.backoffice.model.connector;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PageInfo {

    @JsonProperty("page")
    Integer page;

    @JsonProperty("limit")
    Integer limit;

    @JsonProperty("items_found")
    @JsonAlias("itemsFound")
    Integer itemsFound;

    @JsonProperty("total_pages")
    @JsonAlias("totalPages")
    Integer totalPages;

    @JsonProperty("total_items")
    @JsonAlias("totalItems")
    Long totalItems;
}
