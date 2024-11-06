package it.pagopa.selfcare.pagopa.backoffice.repository;

import it.pagopa.selfcare.pagopa.backoffice.entity.BrokerInstitutionEntity;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BrokerInstitutionsCustomRepository {

    void updateBrokerInstitutionsList(String brokerCode, List<BrokerInstitutionEntity> institutions);
}