package it.pagopa.selfcare.pagopa.backoffice.connector.model;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import javax.validation.constraints.PositiveOrZero;

@Data
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
