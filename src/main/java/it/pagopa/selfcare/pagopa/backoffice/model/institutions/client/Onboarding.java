package it.pagopa.selfcare.pagopa.backoffice.model.institutions.client;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.LocalDateTime;

@Data
@Builder
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class Onboarding {

    private String productId;
    private String status;
    private LocalDateTime createdAt;
    private Billing billing;
}
