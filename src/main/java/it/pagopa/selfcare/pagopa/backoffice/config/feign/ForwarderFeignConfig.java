package it.pagopa.selfcare.pagopa.backoffice.config.feign;

import feign.RequestInterceptor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;

import static it.pagopa.selfcare.pagopa.backoffice.util.Constants.APIM_SUBSCRIPTION_KEY;

public class ForwarderFeignConfig extends BaseFeignConfig {
    @Value("${authorization.forwarder.subscriptionKey}")
    private String forwarderSubscriptionKey;

    private String TEST_DATA="<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\" ?>\n" +
            "<soapenv:Envelope xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\n    " +
            "xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\"\n    " +
            "xmlns:tns=\"http://pagopa-api.pagopa.gov.it/paForNode\"\n    " +
            "xmlns:common=\"http://pagopa-api.pagopa.gov.it/xsd/common-types/v1.0.0/\"\n    " +
            "xmlns:pafn=\"http://pagopa-api.pagopa.gov.it/pa/paForNode.xsd\">\n    " +
            "<soapenv:Body>\n        <pafn:paVerifyPaymentNoticeReq>\n            " +
            "<idPA>77777777777</idPA>\n            <idBrokerPA>77777777777</idBrokerPA>\n            " +
            "<idStation>77777777777_01</idStation>\n            <qrCode>\n                " +
            "<fiscalCode>77777777777</fiscalCode>\n                " +
            "<noticeNumber>311111111112222222</noticeNumber>\n            </qrCode>\n        " +
            "</pafn:paVerifyPaymentNoticeReq>\n    </soapenv:Body>\n</soapenv:Envelope>";

    @Bean
    public RequestInterceptor subscriptionKey() {
        return requestTemplate -> requestTemplate.header(APIM_SUBSCRIPTION_KEY, forwarderSubscriptionKey)
                .header("soapaction","paVerifyPaymentNotice")
                .body(TEST_DATA);
    }
}
