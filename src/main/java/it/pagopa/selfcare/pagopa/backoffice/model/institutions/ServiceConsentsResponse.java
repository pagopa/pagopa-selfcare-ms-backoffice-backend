package it.pagopa.selfcare.pagopa.backoffice.model.institutions;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ServiceConsentsResponse {

    @Schema(description = "List of services with expressed consent")
    @JsonProperty("services")
    @NotNull
    @Size(min = 1)
    private List<ServiceConsentInfo> services;
}
