package it.pagopa.selfcare.pagopa.backoffice.client;

import it.pagopa.selfcare.pagopa.backoffice.model.stations.TestStationTypeEnum;
import kong.unirest.HttpResponse;
import kong.unirest.Unirest;
import kong.unirest.UnirestInstance;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import static it.pagopa.selfcare.pagopa.backoffice.util.Constants.APIM_SUBSCRIPTION_KEY;
import static it.pagopa.selfcare.pagopa.backoffice.util.Constants.HEADER_REQUEST_ID;

@Service
public class ForwarderClient {

    public static final String FAKE_PA_VERIFY = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\" ?>\n<soapenv:Envelope xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\n    xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\"\n    xmlns:tns=\"http://pagopa-api.pagopa.gov.it/paForNode\"\n    xmlns:common=\"http://pagopa-api.pagopa.gov.it/xsd/common-types/v1.0.0/\"\n    xmlns:pafn=\"http://pagopa-api.pagopa.gov.it/pa/paForNode.xsd\">\n    <soapenv:Body>\n        <pafn:paVerifyPaymentNoticeReq>\n            <idPA>77777777777</idPA>\n            <idBrokerPA>77777777777</idBrokerPA>\n            <idStation>77777777777_01</idStation>\n            <qrCode>\n                <fiscalCode>77777777777</fiscalCode>\n                <noticeNumber>311111111112222222</noticeNumber>\n            </qrCode>\n        </pafn:paVerifyPaymentNoticeReq>\n    </soapenv:Body>\n</soapenv:Envelope>";
    public static final String FAKE_PA_INVIART = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\" ?>\n<soapenv:Envelope xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:ppt=\"http://ws.pagamenti.telematici.gov/\" xmlns:tns=\"http://NodoPagamentiSPC.spcoop.gov.it/servizi/PagamentiTelematiciRT\" xmlns:ppthead=\"http://ws.pagamenti.telematici.gov/ppthead\">\n    <soapenv:Header>\n        <ppthead:intestazionePPT>\n            <identificativoIntermediarioPA>97532760581</identificativoIntermediarioPA>\n            <identificativoStazioneIntermediarioPA>97532760581_02</identificativoStazioneIntermediarioPA>\n            <identificativoDominio>97532760581</identificativoDominio>\n            <identificativoUnivocoVersamento>03704883822897258</identificativoUnivocoVersamento>\n            <codiceContestoPagamento>4ee8fc1b05874385afff5d0cc26bfff8</codiceContestoPagamento>\n        </ppthead:intestazionePPT>\n    </soapenv:Header>\n    <soapenv:Body>\n        <ppt:paaInviaRT>\n            <tipoFirma/>\n            <rt>aaa</rt>\n        </ppt:paaInviaRT>\n    </soapenv:Body>\n</soapenv:Envelope>";

    @Value("${rest-client.forwarder.base-url}")
    public String url;

    @Value("${authorization.forwarder.subscriptionKey}")
    public String forwarderSubscriptionKey;

    private UnirestInstance unirest;

    public ForwarderClient() {
        this.unirest = Unirest.primaryInstance();
    }

    public HttpResponse<String> testForwardConnection(
            String protocol, String host, Integer port, String path, TestStationTypeEnum testStationTypeEnum) {
        return TestStationTypeEnum.PA_REDIRECT.equals(testStationTypeEnum) ?
                unirest.get(protocol+"://"+host+":"+port+path)
                        .asString() :
                unirest.post(url + "/forward")
                .header("X-Host-Url", host)
                .header("X-Host-Port", String.valueOf(port))
                .header("X-Host-Path", path)
                .header(APIM_SUBSCRIPTION_KEY, forwarderSubscriptionKey)
                .header(HEADER_REQUEST_ID, MDC.get("requestId"))
                .header("Content-Type", "text/xml")
                .header("soapaction", testStationTypeEnum.equals(TestStationTypeEnum.PA_VERIFY) ?
                        "paVerifyPaymentNotice" : "paaInviaRT")
                .body(testStationTypeEnum.equals(TestStationTypeEnum.PA_VERIFY) ? FAKE_PA_VERIFY : FAKE_PA_INVIART)
                .asString();
    }

    public void setUnirest(UnirestInstance unirest) {
        this.unirest = unirest;
    }
}
