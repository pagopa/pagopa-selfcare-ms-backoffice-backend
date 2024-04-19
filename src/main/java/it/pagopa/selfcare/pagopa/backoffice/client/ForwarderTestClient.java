package it.pagopa.selfcare.pagopa.backoffice.client;

import kong.unirest.HttpResponse;
import kong.unirest.Unirest;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import static it.pagopa.selfcare.pagopa.backoffice.util.Constants.APIM_SUBSCRIPTION_KEY;
import static it.pagopa.selfcare.pagopa.backoffice.util.Constants.HEADER_REQUEST_ID;

@Service
public class ForwarderTestClient {

    public static final String FAKE_PA_VERIFY = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\" ?>\n<soapenv:Envelope xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\n    xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\"\n    xmlns:tns=\"http://pagopa-api.pagopa.gov.it/paForNode\"\n    xmlns:common=\"http://pagopa-api.pagopa.gov.it/xsd/common-types/v1.0.0/\"\n    xmlns:pafn=\"http://pagopa-api.pagopa.gov.it/pa/paForNode.xsd\">\n    <soapenv:Body>\n        <pafn:paVerifyPaymentNoticeReq>\n            <idPA>77777777777</idPA>\n            <idBrokerPA>77777777777</idBrokerPA>\n            <idStation>77777777777_01</idStation>\n            <qrCode>\n                <fiscalCode>77777777777</fiscalCode>\n                <noticeNumber>311111111112222222</noticeNumber>\n            </qrCode>\n        </pafn:paVerifyPaymentNoticeReq>\n    </soapenv:Body>\n</soapenv:Envelope>";


    @Value("${rest-client.forwarder.base-url}")
    public String url;


    @Value("${authorization.forwarder.subscriptionKey}")
    public String forwarderSubscriptionKey;

    public HttpResponse<String> testForwardConnection(String host, Integer port, String path) {
        return Unirest.post(url + "/forward")
                .header("X-Host-Url", host)
                .header("X-Host-Port", String.valueOf(port))
                .header("X-Host-Path", path)
                .header(APIM_SUBSCRIPTION_KEY, forwarderSubscriptionKey)
                .header(HEADER_REQUEST_ID, MDC.get("requestId"))
                .header("Content-Type", "text/xml")
                .header("soapaction", "paVerifyPaymentNotice")
                .body(FAKE_PA_VERIFY)
                .asString();
    }

}
