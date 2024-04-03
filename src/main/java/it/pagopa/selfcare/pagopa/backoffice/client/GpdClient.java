package it.pagopa.selfcare.pagopa.backoffice.client;

import it.pagopa.selfcare.pagopa.backoffice.config.feign.GpdFeignConfig;
import it.pagopa.selfcare.pagopa.backoffice.model.paymentsreceipts.ReceiptsInfo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@FeignClient(name = "gpd", url = "${rest-client.gpd.base-url}", configuration = GpdFeignConfig.class)
public interface GpdClient {

    @GetMapping(value = "/payments/{organizationFiscalCode}/receipts", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    ReceiptsInfo getPaymentsReceipts(@PathVariable String organizationFiscalCode,
                                     @RequestParam Integer pageNum,
                                     @RequestParam(required = false) Integer pageSize,
                                     @RequestParam(required = false) String debtor,
                                     @RequestParam(required = false) String service,
                                     @RequestParam(required = false) String from,
                                     @RequestParam(required = false) String to);
}
