package it.pagopa.selfcare.pagopa.backoffice.service;

import it.pagopa.selfcare.pagopa.backoffice.client.ApiConfigClient;
import it.pagopa.selfcare.pagopa.backoffice.client.ApiConfigSelfcareIntegrationClient;
import it.pagopa.selfcare.pagopa.backoffice.mapper.ChannelMapper;
import it.pagopa.selfcare.pagopa.backoffice.model.channels.*;
import it.pagopa.selfcare.pagopa.backoffice.model.connector.channel.BrokerPspDetails;
import it.pagopa.selfcare.pagopa.backoffice.model.connector.channel.BrokersPsp;
import it.pagopa.selfcare.pagopa.backoffice.model.connector.channel.ChannelDetailsList;
import it.pagopa.selfcare.pagopa.backoffice.model.connector.channel.PaymentServiceProviders;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.validation.constraints.NotNull;

@Service
public class PspBrokerService {

    private final ApiConfigClient apiConfigClient;

    private final ApiConfigSelfcareIntegrationClient apiConfigSelfcareIntegrationClient;

    private final ModelMapper modelMapper;

    @Autowired
    public PspBrokerService(ApiConfigClient apiConfigClient, ApiConfigSelfcareIntegrationClient apiConfigSelfcareIntegrationClient, ModelMapper modelMapper) {
        this.apiConfigClient = apiConfigClient;
        this.apiConfigSelfcareIntegrationClient = apiConfigSelfcareIntegrationClient;
        this.modelMapper = modelMapper;
    }


    public BrokersPspResource getBrokersForPSP(Integer limit, Integer page, String filterByCode, String filterByName, String orderBy, String sorting) {
        BrokersPsp dto = this.apiConfigClient.getBrokersPsp(limit, page, filterByCode, filterByName, orderBy, sorting);
        return this.modelMapper.map(dto, BrokersPspResource.class);
    }

    public BrokerPspDetailsResource getBrokerForPsp(String brokerPspCode) {
        BrokerPspDetails dto = this.apiConfigClient.getBrokerPsp(brokerPspCode);
        return this.modelMapper.map(dto, BrokerPspDetailsResource.class);
    }

    public BrokerPspDetailsResource createBrokerForPSP(BrokerPspDetailsDto brokerPspDetailsDto) {
        BrokerPspDetails dto = this.modelMapper.map(brokerPspDetailsDto, BrokerPspDetails.class);
        dto = this.apiConfigClient.createBrokerPsp(dto);
        return this.modelMapper.map(dto, BrokerPspDetailsResource.class);
    }

    public BrokerPspDetailsResource updateBrokerPSP(String brokerCode, @NotNull BrokerPspDetailsDto brokerPspDetailsDto) {
        BrokerPspDetails brokerPspDetails = this.modelMapper.map(brokerPspDetailsDto, BrokerPspDetails.class);
        BrokerPspDetails response = this.apiConfigClient.updateBrokerPSP(brokerCode, brokerPspDetails);
        return this.modelMapper.map(response, BrokerPspDetailsResource.class);
    }

    public ChannelDetailsResourceList getChannelByBroker(String brokerId, String channelId, Integer limit, Integer page) {
        ChannelDetailsList dto = apiConfigSelfcareIntegrationClient.getChannelDetailsListByBroker(brokerId, channelId, limit, page);
        return ChannelMapper.fromChannelDetailsList(dto);
    }

    public PaymentServiceProvidersResource getPSPAssociatedToBroker(String brokerPspCode, Integer limit, Integer page) {
        PaymentServiceProviders dto = this.apiConfigClient.getPspBrokerPsp(limit, page, brokerPspCode);
        return ChannelMapper.toResource(dto);
    }

    /**
     * Deletes the Payment Service Provider's broker
     *
     * @param brokerTaxCode Tax code of the broker to delete
     */
    public void deletePspBroker(String brokerTaxCode) {
        this.apiConfigClient.deleteBrokerPsp(brokerTaxCode);
    }
}
