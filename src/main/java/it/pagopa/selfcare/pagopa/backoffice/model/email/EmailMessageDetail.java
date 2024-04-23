package it.pagopa.selfcare.pagopa.backoffice.model.email;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.thymeleaf.context.Context;

/**
 * Model that contains the info to send an email
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class EmailMessageDetail {

    // tax code of the institution that will receive the email
    private String institutionTaxCode;
    private String subject;
    private String textBody;
    private String htmlBodyFileName;
    private Context htmlBodyContext;
}
