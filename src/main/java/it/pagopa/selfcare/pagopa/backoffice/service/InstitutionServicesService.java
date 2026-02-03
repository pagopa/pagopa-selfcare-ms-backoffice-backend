package it.pagopa.selfcare.pagopa.backoffice.service;

import it.pagopa.selfcare.pagopa.backoffice.client.ExternalApiClient;
import it.pagopa.selfcare.pagopa.backoffice.entity.InstitutionRTPServiceEntity;
import it.pagopa.selfcare.pagopa.backoffice.exception.AppError;
import it.pagopa.selfcare.pagopa.backoffice.exception.AppException;
import it.pagopa.selfcare.pagopa.backoffice.model.institutions.ServiceConsentRequest;
import it.pagopa.selfcare.pagopa.backoffice.model.institutions.ServiceConsentResponse;
import it.pagopa.selfcare.pagopa.backoffice.model.institutions.ServiceId;
import it.pagopa.selfcare.pagopa.backoffice.model.institutions.client.Institution;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneId;

@Service
public class InstitutionServicesService {
    private final MongoRepository<InstitutionRTPServiceEntity, String> rtpServiceRepository;
    private final ExternalApiClient externalApiClient;

    @Autowired
    public InstitutionServicesService(MongoRepository<InstitutionRTPServiceEntity, String> rtpServiceRepository,
                                      ExternalApiClient externalApiClient){
        this.rtpServiceRepository = rtpServiceRepository;
        this.externalApiClient = externalApiClient;
    }

    public ServiceConsentResponse saveServiceConsent(ServiceConsentRequest serviceConsentRequest,
                                                     ServiceId serviceId,
                                                     String institutionId){
        ServiceConsentResponse response;

        switch (serviceId) {
            case RTP:
                Institution institution = externalApiClient.getInstitution(institutionId);

                if(institution == null)
                {
                    throw new AppException(AppError.INSTITUTION_NOT_FOUND);
                }
                InstitutionRTPServiceEntity entity = InstitutionRTPServiceEntity.builder()
                        .id(institution.getId())
                        .institutionTaxCode(institution.getTaxCode())
                        .consent(serviceConsentRequest.getConsent().toString())
                        .name(institution.getDescription())
                        .consentDate(Instant.now())
                        .build();
                rtpServiceRepository.save(entity);
                response = new ServiceConsentResponse(serviceConsentRequest.getConsent(),
                        entity.getConsentDate().atZone(ZoneId.of("Europe/Rome")).toOffsetDateTime());
                break;
            case UNKNOWN:
            default:
                throw new AppException(AppError.SERVICE_NOT_FOUND);
        }

        return response;
    }
}
