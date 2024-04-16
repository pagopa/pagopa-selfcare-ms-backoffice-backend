package it.pagopa.selfcare.pagopa.backoffice.client;

import feign.Headers;
import feign.Param;
import it.pagopa.selfcare.pagopa.backoffice.config.feign.ForwarderFeignConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.ws.rs.HeaderParam;

@FeignClient(name = "forwarder-test", url = "${rest-client.forwarder.base-url}", configuration = ForwarderFeignConfig.class)
public interface ForwarderTestClient {

    @PostMapping(value = "/forward", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_XML_VALUE)
    String testForwardConnection(
            @RequestBody String data,
            @RequestHeader("X-Host-Url") String host,
            @RequestHeader("X-Host-Port") Integer port,
            @RequestHeader("X-Host-Path") String path
    );

}
