package it.pagopa.selfcare.pagopa.backoffice.model.institutions;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Email;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AssistanceContact {

    @Schema(description = "Institution's support email contact")
    @JsonProperty(value = "support_email")
    @Email
    private String supportEmail;

    @Schema(description = "Institution's support phone contact")
    @JsonProperty(value = "support_phone")
    private String supportPhone;
}
