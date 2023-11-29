package it.pagopa.selfcare.pagopa.backoffice.mapper;

import it.pagopa.selfcare.pagopa.backoffice.model.connector.broker.Broker;
import it.pagopa.selfcare.pagopa.backoffice.model.connector.broker.BrokerDetails;
import it.pagopa.selfcare.pagopa.backoffice.model.connector.broker.Brokers;
import it.pagopa.selfcare.pagopa.backoffice.model.stations.BrokerDto;
import it.pagopa.selfcare.pagopa.backoffice.model.stations.BrokerResource;
import it.pagopa.selfcare.pagopa.backoffice.model.stations.BrokersResource;

import java.util.stream.Collectors;

public class BrokerMapper {

    public static BrokerDetails fromDto(BrokerDto brokerDto){
        if(brokerDto == null){
            return null;
        }
        BrokerDetails response = new BrokerDetails();

        response.setBrokerCode(brokerDto.getBrokerCode());
        response.setDescription(brokerDto.getDescription());
        response.setEnabled(true);
        response.setExtendedFaultBean(true);

        return response;
    }

    public static BrokerResource toResource(BrokerDetails broker){
        if(broker == null){
            return null;
        }
        BrokerResource response = new BrokerResource();

        response.setBrokerCode(broker.getBrokerCode());
        response.setDescription(broker.getDescription());
        response.setEnabled(broker.getEnabled());
        response.setExtendedFaultBean(broker.getExtendedFaultBean());

        return response;
    }

    public static BrokerResource toResource(Broker broker){
        if(broker == null){
            return null;
        }
        BrokerResource response = new BrokerResource();

        response.setBrokerCode(broker.getBrokerCode());
        response.setDescription(broker.getDescription());
        response.setEnabled(broker.getEnabled());

        return response;
    }

    public static BrokersResource toResource(Brokers brokers){
        if(brokers == null){
            return null;
        }

        BrokersResource brokersResource = new BrokersResource();

        brokersResource.setBrokerList(brokers.getBrokerList().stream()
                .map(BrokerMapper::toResource)
                .collect(Collectors.toList()));


        return brokersResource;
    }

}
