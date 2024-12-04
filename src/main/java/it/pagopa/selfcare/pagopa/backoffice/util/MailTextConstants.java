package it.pagopa.selfcare.pagopa.backoffice.util;

public class MailTextConstants {

    // PSP notification
    public static final String BUNDLE_ACCEPT_OFFER_SUBJECT = "Offerta di adesione confermata";
    public static final String BUNDLE_ACCEPT_OFFER_BODY = "Ciao %n%n%n la tua offerta di adesione al pacchetto %s è stata accettata.%n%n%n Puoi gestire i tuoi pacchetti qui ( https://selfcare%s.platform.pagopa.it/ui/comm-bundles ).%n%n%nA presto,%n%nPagamenti pagoPa";
    public static final String BUNDLE_REJECT_OFFER_SUBJECT = "Offerta di adesione rifiutata";
    public static final String BUNDLE_REJECT_OFFER_BODY = "Ciao %n%n%n la tua offerta di adesione al pacchetto %s è stata rifiutata.%n%n%n Se riscontri dei problemi, puoi richiedere maggiori dettagli utilizzando il canale di assistenza ( https://selfcare.pagopa.it/assistenza ).%n%n%nA presto,%n%nPagamenti pagoPa";
    public static final String BUNDLE_CREATE_REQUEST_SUBJECT = "Nuova richiesta di attivazione pacchetto commissionale";
    public static final String BUNDLE_CREATE_REQUEST_BODY = "Ciao, %n%n%n ci sono nuove richieste di attivazione per il pacchetto commissionale %s.%n%n%n Puoi gestire i tuoi pacchetti qui ( https://selfcare%s.platform.pagopa.it/ui/comm-bundles ).%n%n%nA presto,%n%nPagamenti pagoPa";


    // CI notification
    public static final String BUNDLE_DELETE_SUBJECT = "Eliminazione pacchetto commissionale";
    public static final String BUNDLE_DELETE_BODY = "Ciao, %n%n%n %s ha richiesto l'eliminazione del pacchetto %s che non sarà più visibile a partire dalla mezzanotte di domani. Se riscontri dei problemi, contatta l'ente.%n%n Puoi gestire i tuoi pacchetti qui ( https://selfcare%s.platform.pagopa.it/ui/comm-bundles ).%n%n%nA presto,%n%nPagamenti pagoPa";
    public static final String BUNDLE_CREATE_OFFER_SUBJECT = "Nuova offerta di attivazione pacchetto commissionale";
    public static final String BUNDLE_CREATE_OFFER_BODY = "Ciao, %n%n%n c'è una nuova offerta di attivazione per il pacchetto commissionale %s.%n%n%n Puoi gestire i tuoi pacchetti qui ( https://selfcare%s.platform.pagopa.it/ui/comm-bundles ).%n%n%nA presto,%n%nPagamenti pagoPa";
    public static final String BUNDLE_ACCEPT_REQUEST_SUBJECT = "Richiesta di adesione confermata";
    public static final String BUNDLE_ACCEPT_REQUEST_BODY = "Ciao %n%n%n la tua richiesta di adesione al pacchetto %s è stata accettata.%n%n%n Puoi vedere e gestire il pacchetto da qui ( https://selfcare%s.platform.pagopa.it/ui/comm-bundles ).%n%n%nA presto,%n%nPagamenti pagoPa";
    public static final String BUNDLE_REJECT_REQUEST_SUBJECT = "Richiesta di adesione rifiutata";
    public static final String BUNDLE_REJECT_REQUEST_BODY = "Ciao %n%n%n la tua richiesta di adesione al pacchetto %s è stata rifiutata.%n%n%n Se riscontri dei problemi, puoi richiedere maggiori dettagli utilizzando il canale di assistenza ( https://selfcare.pagopa.it/assistenza ).%n%n%nA presto,%n%nPagamenti pagoPa";
    public static final String BUNDLE_DELETE_SUBSCRIPTION_SUBJECT = "Conferma rimozione da pacchetto";
    public static final String BUNDLE_DELETE_SUBSCRIPTION_BODY = "Ciao, %n%n%n sei stato rimosso dal pacchetto %s.%n%n%n Se riscontri dei problemi, puoi richiedere maggiori dettagli utilizzando il canale di assistenza ( https://selfcare.pagopa.it/assistenza ).%n%n%nA presto,%n%nPagamenti pagoPa";
    public static final String BUNDLE_DELETE_OFFER_SUBJECT = "Offerta di adesione eliminata";
    public static final String BUNDLE_DELETE_OFFER_BODY = "Ciao %n%n%n l'offerta di adesione al pacchetto %s è stata eliminata.%n%n%n Se riscontri dei problemi, puoi richiedere maggiori dettagli utilizzando il canale di assistenza ( https://selfcare.pagopa.it/assistenza ).%n%n%nA presto,%n%nPagamenti pagoPa";

    // cron-job notification
    public static final String BUNDLE_EXPIRE_SUBJECT = "Pacchetto commissionale in scadenza";
    public static final String BUNDLE_EXPIRE_BODY = "Ciao, %n%n%n il pacchetto %s del PSP %s (codice fiscale: %s) scadrà in data %s.%n%n%n Puoi vedere i tuoi pacchetti qui ( https://selfcare%s.platform.pagopa.it/ui/comm-bundles ).%n%n%nA presto,%n%nPagamenti pagoPa";


    private MailTextConstants() {}
}
