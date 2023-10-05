package it.pagopa.selfcare.pagopa.backoffice.web.controller;

import feign.FeignException;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import it.pagopa.selfcare.pagopa.backoffice.connector.exception.ResourceNotFoundException;
import it.pagopa.selfcare.pagopa.backoffice.connector.logging.LogUtils;
import it.pagopa.selfcare.pagopa.backoffice.connector.model.broker.Brokers;
import it.pagopa.selfcare.pagopa.backoffice.connector.model.channel.BrokerPspDetails;
import it.pagopa.selfcare.pagopa.backoffice.connector.model.channel.PaymentServiceProviderDetails;
import it.pagopa.selfcare.pagopa.backoffice.connector.model.creditorInstitution.CreditorInstitutionDetails;
import it.pagopa.selfcare.pagopa.backoffice.core.ApiConfigService;
import it.pagopa.selfcare.pagopa.backoffice.web.model.channels.BrokerOrPspDetailsResource;
import it.pagopa.selfcare.pagopa.backoffice.web.model.channels.BrokerPspDetailsResource;
import it.pagopa.selfcare.pagopa.backoffice.web.model.channels.PaymentServiceProviderDetailsResource;
import it.pagopa.selfcare.pagopa.backoffice.web.model.creditorInstituions.CreditorInstitutionDetailsResource;
import it.pagopa.selfcare.pagopa.backoffice.web.model.mapper.BrokerMapper;
import it.pagopa.selfcare.pagopa.backoffice.web.model.mapper.ChannelMapper;
import it.pagopa.selfcare.pagopa.backoffice.web.model.mapper.CreditorInstitutionMapper;
import it.pagopa.selfcare.pagopa.backoffice.web.model.stations.BrokerAndEcDetailsResource;
import it.pagopa.selfcare.pagopa.backoffice.web.model.stations.BrokerResource;
import lombok.extern.slf4j.Slf4j;
import org.mapstruct.factory.Mappers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import static org.springframework.util.ObjectUtils.isEmpty;

@Slf4j
@RestController
@RequestMapping(value = "/utils", produces = MediaType.APPLICATION_JSON_VALUE)
@Api(tags = "Utils")
public class UtilsController {

    private final ApiConfigService apiConfigService;

    CreditorInstitutionMapper mapper = Mappers.getMapper(CreditorInstitutionMapper.class);


    @Autowired
    public UtilsController(ApiConfigService apiConfigService) {
        this.apiConfigService = apiConfigService;
    }

    @GetMapping(value = "/psp-brokers/{code}/details", produces = {MediaType.APPLICATION_JSON_VALUE})
    @ResponseStatus(HttpStatus.OK)
    @ApiOperation(value = "", notes = "${swagger.api.channels.getBrokerPsp}")
    public BrokerOrPspDetailsResource getBrokerAndPspDetails(@ApiParam("swagger.request.brokerpspcode")
                                                             @PathVariable(required = true, name = "code") String brokerPspCode) throws Exception {
        log.trace("getBrokerAndPspDetails start");

        BrokerPspDetailsResource brokerPspDetailsResource = null;
        PaymentServiceProviderDetailsResource paymentServiceProviderDetailsResource = null;
        BrokerPspDetails brokerPspDetails;
        PaymentServiceProviderDetails paymentServiceProviderDetails;
        try {
            brokerPspDetails = apiConfigService.getBrokerPsp(brokerPspCode);
            brokerPspDetailsResource = ChannelMapper.toResource(brokerPspDetails);
        } catch (FeignException.NotFound e) {
            log.trace("getBrokerAndPspDetails - Not BrokerPSP found");
        }

        try {
            paymentServiceProviderDetails = apiConfigService.getPSPDetails(brokerPspCode);
            paymentServiceProviderDetailsResource = ChannelMapper.toResource(paymentServiceProviderDetails);
        } catch (FeignException.NotFound e) {
            log.trace("getBrokerAndPspDetails - Not PaymentServiceProvider found");
        }

        if (brokerPspDetailsResource == null && paymentServiceProviderDetailsResource == null) {
            throw new ResourceNotFoundException("Nessun dato trovato per il broker o per il creditorInstitution");
        }

        BrokerOrPspDetailsResource resource = new BrokerOrPspDetailsResource();
        resource.setBrokerPspDetailsResource(brokerPspDetailsResource);
        resource.setPaymentServiceProviderDetailsResource(paymentServiceProviderDetailsResource);

        log.debug(LogUtils.CONFIDENTIAL_MARKER, "getBrokerAndPspDetails result = {}", resource);
        log.trace("getBrokerAndPspDetails end");

        return resource;
    }

    @GetMapping(value = "/ec-brokers/{code}/details", produces = {MediaType.APPLICATION_JSON_VALUE})
    @ResponseStatus(HttpStatus.OK)
    @ApiOperation(value = "", notes = "${swagger.api.channels.getBrokerPsp}")
    public BrokerAndEcDetailsResource getBrokerAndEcDetails(@ApiParam("swagger.request.brokerEcCode")
                                                            @PathVariable(required = true, name = "code") String brokerEcCode) throws Exception {
        log.trace("getBrokerOrEcDetails start");

        BrokerResource brokerResource = null;
        CreditorInstitutionDetailsResource creditorInstitutionDetailsResource = null;
        Brokers brokers;
        CreditorInstitutionDetails creditorInstitutionDetails;

        try {
            brokers = apiConfigService.getBrokersEC(1, 0, brokerEcCode, null, null, "ASC");
            if(brokers != null && !isEmpty(brokers.getBrokerList())) {
                brokerResource = BrokerMapper.toResource(brokers.getBrokerList().get(0));
            }
        } catch (FeignException.NotFound e) {
            log.trace("getBrokerOrEcDetails - Not BrokerEC found");
        }

        try {
            creditorInstitutionDetails = apiConfigService.getCreditorInstitutionDetails(brokerEcCode);
            creditorInstitutionDetailsResource = mapper.toResource(creditorInstitutionDetails);
        } catch (FeignException.NotFound e) {
            log.trace("getBrokerOrEcDetails - Not CreditorInstitution found");
        }

        if (brokerResource==null  && creditorInstitutionDetailsResource == null) {
            throw new ResourceNotFoundException("Nessun dato trovato per il broker o per il creditorInstitution");
        }
        BrokerAndEcDetailsResource resource = new BrokerAndEcDetailsResource();
        resource.setBrokerDetailsResource(brokerResource);
        resource.setCreditorInstitutionDetailsResource(creditorInstitutionDetailsResource);

        log.debug(LogUtils.CONFIDENTIAL_MARKER, "getBrokerOrEcDetails result = {}", resource);
        log.trace("getBrokerOrEcDetails end");

        return resource;
    }
}
