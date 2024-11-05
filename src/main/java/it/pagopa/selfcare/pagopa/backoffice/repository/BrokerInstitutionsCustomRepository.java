package it.pagopa.selfcare.pagopa.backoffice.repository;

import it.pagopa.selfcare.pagopa.backoffice.entity.BrokerInstitutionEntity;

import java.util.List;

public interface BrokerInstitutionsCustomRepository {

    void updateBrokerInstitutionsList(String brokerCode, List<BrokerInstitutionEntity> institutions);
}