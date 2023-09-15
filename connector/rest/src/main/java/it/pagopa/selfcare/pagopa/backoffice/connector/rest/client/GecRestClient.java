package it.pagopa.selfcare.pagopa.backoffice.connector.rest.client;

import feign.RequestLine;
import it.pagopa.selfcare.pagopa.backoffice.connector.api.ApiConfigConnector;
import it.pagopa.selfcare.pagopa.backoffice.connector.model.broker.BrokerDetails;
import it.pagopa.selfcare.pagopa.backoffice.connector.model.broker.Brokers;
import it.pagopa.selfcare.pagopa.backoffice.connector.model.channel.*;
import it.pagopa.selfcare.pagopa.backoffice.connector.model.creditorInstitution.CreditorInstitutionDetails;
import it.pagopa.selfcare.pagopa.backoffice.connector.model.creditorInstitution.CreditorInstitutions;
import it.pagopa.selfcare.pagopa.backoffice.connector.model.creditorInstitution.IbanCreate;
import it.pagopa.selfcare.pagopa.backoffice.connector.model.creditorInstitution.IbansEnhanced;
import it.pagopa.selfcare.pagopa.backoffice.connector.model.station.CreditorInstitutionStationEdit;
import it.pagopa.selfcare.pagopa.backoffice.connector.model.station.CreditorInstitutionStations;
import it.pagopa.selfcare.pagopa.backoffice.connector.model.station.StationDetails;
import it.pagopa.selfcare.pagopa.backoffice.connector.model.station.Stations;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotNull;

@FeignClient(name = "${rest-client.gec.serviceCode}", url = "${rest-client.gec.base-url}")
public interface GecRestClient extends ApiConfigConnector {

//    @GetMapping(value = "${rest-client.gec.getBrokersPsp.path}", produces = MediaType.APPLICATION_JSON_VALUE)
//    @ResponseBody
//    @RequestLine("getBrokersPsp")
//    BrokersPsp getBrokersPsp(@RequestParam(required = false, defaultValue = "50") Integer limit,
//                             @RequestParam(required = true) Integer page,
//                             @RequestParam(required = false, name = "code") String filterByCode,
//                             @RequestParam(required = false, name = "name") String filterByName,
//                             @RequestParam(required = false, name = "orderby", defaultValue = "CODE") String orderBy,
//                             @RequestParam(required = false, name = "ordering", defaultValue = "DESC") String sort,
//                             @RequestHeader(name = "X-Request-Id", required = false) String xRequestId);


}
