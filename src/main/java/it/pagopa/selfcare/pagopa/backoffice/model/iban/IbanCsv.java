package it.pagopa.selfcare.pagopa.backoffice.model.iban;

import lombok.Builder;
import lombok.Data;
import lombok.ToString;

@Data
@ToString
@Builder
public class IbanCsv {

    private String denominazioneEnte;
    private String codiceFiscale;
    private String iban;
    private String stato;
    private String dataScadenza;
    private String dataAttivazioneIban;
    private String descrizione;
    private String etichetta;
}
