package it.pagopa.selfcare.pagopa.backoffice.web.model.mapper;

import it.pagopa.selfcare.pagopa.backoffice.connector.model.broker.BrokerDetails;
import it.pagopa.selfcare.pagopa.backoffice.web.model.stations.BrokerDto;
import it.pagopa.selfcare.pagopa.backoffice.web.model.stations.BrokerResource;

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

}
