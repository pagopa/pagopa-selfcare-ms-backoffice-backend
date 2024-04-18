package it.pagopa.selfcare.pagopa.backoffice.client;

import it.pagopa.selfcare.pagopa.backoffice.config.feign.ForwarderFeignConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(name = "forwarder-test", url = "${rest-client.forwarder.base-url}", configuration = ForwarderFeignConfig.class)
public interface ForwarderTestClient {

    @PostMapping(value = "/forward", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.TEXT_XML_VALUE)
    String testForwardConnection(
            @RequestHeader("X-Host-Url") String host,
            @RequestHeader("X-Host-Port") Integer port,
            @RequestHeader("X-Host-Path") String path
    );

}
