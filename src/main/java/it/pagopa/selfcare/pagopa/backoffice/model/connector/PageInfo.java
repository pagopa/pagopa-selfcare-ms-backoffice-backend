package it.pagopa.selfcare.pagopa.backoffice.model.connector;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
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
    @Schema(description = "Page number")
    Integer page;

    @JsonProperty("limit")
    @Schema(description = "Required number of items per page")
    Integer limit;

    @JsonProperty("items_found")
    @JsonAlias("itemsFound")
    @Schema(description = "Number of items found. (The last page may have fewer elements than required)")
    Integer itemsFound;

    @JsonProperty("total_pages")
    @JsonAlias("totalPages")
    @Schema(description = "Total number of pages")
    Integer totalPages;

    @JsonProperty("total_items")
    @JsonAlias("totalItems")
    Long totalItems;
}
