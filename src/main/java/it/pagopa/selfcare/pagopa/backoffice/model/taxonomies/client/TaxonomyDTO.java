package it.pagopa.selfcare.pagopa.backoffice.model.taxonomies.client;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class TaxonomyDTO {


    @JsonProperty("CODICE TIPO ENTE CREDITORE")
    private String ecTypeCode;


    @JsonProperty("TIPO ENTE CREDITORE")
    private String ecType;


    @JsonProperty("PROGRESSIVO MACRO AREA PER ENTE CREDITORE")
    private String macroAreaEcProgressive;


    @JsonProperty("NOME MACRO AREA")
    private String macroAreaName;

    @JsonProperty("DESCRIZIONE MACRO AREA")
    private String macroAreaDescription;


    @JsonProperty("CODICE TIPOLOGIA SERVIZIO")
    private String serviceTypeCode;


    @JsonProperty("TIPO SERVIZIO")
    private String serviceType;

    @JsonProperty("MOTIVO GIURIDICO DELLA RISCOSSIONE")
    private String legalReasonCollection;

    @JsonProperty("DESCRIZIONE TIPO SERVIZIO")
    private String serviceTypeDescription;

    @JsonProperty("VERSIONE TASSONOMIA")
    private String taxonomyVersion;

    @JsonProperty("DATI SPECIFICI INCASSO")
    private String specificBuiltInData;

    @JsonProperty("DATA INIZIO VALIDITA")
    private String startDate;

    @JsonProperty("DATA FINE VALIDITA")
    private String endDate;

}
