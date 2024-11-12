package it.pagopa.selfcare.pagopa.backoffice.repository;

import it.pagopa.selfcare.pagopa.backoffice.entity.BrokerIbansEntity;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BrokerIbansCustomRepository {

    void updateBrokerIbanList(String brokerCode, List<BrokerIbansEntity> ibansEntityList);
}