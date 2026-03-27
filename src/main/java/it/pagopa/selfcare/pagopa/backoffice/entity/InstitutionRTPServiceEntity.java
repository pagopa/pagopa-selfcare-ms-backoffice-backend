package it.pagopa.selfcare.pagopa.backoffice.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document("institutions-services-rtp-consent")
public class InstitutionRTPServiceEntity {
    @Id
    private String id;
    private String institutionTaxCode;
    private String consent;
    private Instant consentDate;
    private String name;
}
