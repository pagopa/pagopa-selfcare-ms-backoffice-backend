package it.pagopa.selfcare.pagopa.backoffice.connector.rest.client;

import it.pagopa.selfcare.pagopa.backoffice.connector.api.GecConnector;
import it.pagopa.selfcare.pagopa.backoffice.connector.model.gec.Bundles;
import it.pagopa.selfcare.pagopa.backoffice.connector.model.gec.Touchpoints;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@FeignClient(name = "${rest-client.gec.serviceCode}", url = "${rest-client.gec.base-url}")
public interface GecRestClient extends GecConnector {

    @GetMapping(value = "${rest-client.gec.getBundlesByCI.path}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    Bundles getBundlesByCI(@RequestParam(required = true) String cifiscalcode,
                           @RequestParam(required = false) Integer limit,
                           @RequestParam(required = false) Integer page,
                           @RequestHeader(name = "X-Request-Id", required = false) String xRequestId);

    @GetMapping(value = "${rest-client.gec.getTouchpoints.path}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    Touchpoints getTouchpoints(@RequestParam(required = false) Integer limit,
                               @RequestParam(required = false) Integer page,
                               @RequestHeader(name = "X-Request-Id", required = false) String xRequestId);

    @GetMapping(value = "${rest-client.gec.getBundlesByPSP.path}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    Bundles getBundlesByPSP(@RequestParam(required = true) String pspcode,
                            @RequestParam(required = false) Integer limit,
                            @RequestParam(required = false) Integer page,
                            @RequestHeader(name = "X-Request-Id", required = false) String xRequestId);
}
