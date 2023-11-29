package it.pagopa.selfcare.pagopa.backoffice.service;

import it.pagopa.selfcare.pagopa.backoffice.client.ApiConfigClient;
import it.pagopa.selfcare.pagopa.backoffice.client.ApiConfigSelfcareIntegrationClient;
import it.pagopa.selfcare.pagopa.backoffice.mapper.BrokerMapper;
import it.pagopa.selfcare.pagopa.backoffice.mapper.CreditorInstitutionMapper;
import it.pagopa.selfcare.pagopa.backoffice.model.connector.broker.BrokerDetails;
import it.pagopa.selfcare.pagopa.backoffice.model.connector.broker.Brokers;
import it.pagopa.selfcare.pagopa.backoffice.model.connector.station.StationDetailsList;
import it.pagopa.selfcare.pagopa.backoffice.model.creditorinstituions.BrokerEcDto;
import it.pagopa.selfcare.pagopa.backoffice.model.stations.*;
import lombok.extern.slf4j.Slf4j;
import org.mapstruct.factory.Mappers;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class BrokerService {

    private final CreditorInstitutionMapper mapper = Mappers.getMapper(CreditorInstitutionMapper.class);

    @Autowired
    private ApiConfigClient apiConfigClient;

    @Autowired
    private ApiConfigSelfcareIntegrationClient apiConfigSelfcareIntegrationClient;

    @Autowired
    private ModelMapper modelMapper;


    public BrokerResource createBroker(BrokerDto brokerDto) {
        BrokerDetails broker = apiConfigClient.createBroker(BrokerMapper.fromDto(brokerDto));
        return BrokerMapper.toResource(broker);
    }


    public BrokerDetailsResource updateBrokerForCI(BrokerEcDto brokerEcDto, String brokerCode) {
        BrokerDetails dto = mapper.fromDto(brokerEcDto);
        dto = apiConfigClient.updateBrokerEc(dto, brokerCode);
        return mapper.toResource(dto);
    }

    public StationDetailsResourceList getStationsDetailsListByBroker(String brokerId, String stationId, Integer limit, Integer page) {
        StationDetailsList response = apiConfigSelfcareIntegrationClient.getStationsDetailsListByBroker(brokerId, stationId, limit, page);
        return modelMapper.map(response, StationDetailsResourceList.class);
    }


    public BrokersResource getBrokersEC(Integer limit, Integer page, String code, String name, String orderby, String ordering) {
        Brokers response = apiConfigClient.getBrokersEC(limit, page, code, name, orderby, ordering);
        return modelMapper.map(response, BrokersResource.class);
    }
}
