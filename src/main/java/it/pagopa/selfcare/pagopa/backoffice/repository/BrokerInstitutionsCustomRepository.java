package it.pagopa.selfcare.pagopa.backoffice.repository;

import it.pagopa.selfcare.pagopa.backoffice.entity.BrokerInstitutionEntity;

public interface BrokerInstitutionsCustomRepository {

    void updateBrokerInstitutionsList(String brokerCode, BrokerInstitutionEntity institution);
}