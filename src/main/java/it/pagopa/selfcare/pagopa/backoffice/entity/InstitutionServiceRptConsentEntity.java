package it.pagopa.selfcare.pagopa.backoffice.entity;

import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.util.List;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
@Document("institutions-services-rtp-consent")
@ToString
public class InstitutionServiceRptConsentEntity {

    public enum Consent{
        OPT_IN,
        OPT_OUT
    }
    @Id
    private String id;

    private String institutionTaxCode;

    private OffsetDateTime consentDate;

    private Consent consent;

    private String institutionName;
}
