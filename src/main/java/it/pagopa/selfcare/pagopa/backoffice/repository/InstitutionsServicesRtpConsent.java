package it.pagopa.selfcare.pagopa.backoffice.repository;

import it.pagopa.selfcare.pagopa.backoffice.entity.InstitutionServiceRptConsentEntity;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface InstitutionsServicesRtpConsent extends MongoRepository<InstitutionServiceRptConsentEntity, String> {

}