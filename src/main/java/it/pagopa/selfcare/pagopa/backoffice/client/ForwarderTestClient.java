package it.pagopa.selfcare.pagopa.backoffice.client;

import feign.Headers;
import it.pagopa.selfcare.pagopa.backoffice.config.feign.ForwarderFeignConfig;
import it.pagopa.selfcare.pagopa.backoffice.config.feign.GpdFeignConfig;
import it.pagopa.selfcare.pagopa.backoffice.model.paymentsreceipts.ReceiptsInfo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.ws.rs.HeaderParam;

@Headers({"X-Host-Url: {host}", "X-Host-Port: {port}", "X-Host-Path: {path}"})
@FeignClient(name = "gpd", url = "${rest-client.forwarder.base-url}", configuration = ForwarderFeignConfig.class)
public interface ForwarderTestClient {

    @GetMapping(value = "/forward", produces = MediaType.APPLICATION_XML_VALUE)
    @ResponseBody
    String testForwardConnection(String host, Integer port, String path);

}
