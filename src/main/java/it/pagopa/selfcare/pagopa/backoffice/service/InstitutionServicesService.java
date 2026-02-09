package it.pagopa.selfcare.pagopa.backoffice.service;

import it.pagopa.selfcare.pagopa.backoffice.client.ExternalApiClient;
import it.pagopa.selfcare.pagopa.backoffice.config.InstitutionServicesConfig;
import it.pagopa.selfcare.pagopa.backoffice.entity.InstitutionRTPServiceEntity;
import it.pagopa.selfcare.pagopa.backoffice.exception.AppError;
import it.pagopa.selfcare.pagopa.backoffice.exception.AppException;
import it.pagopa.selfcare.pagopa.backoffice.model.institutions.*;
import it.pagopa.selfcare.pagopa.backoffice.model.institutions.client.Institution;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.ZoneId;
import java.util.List;

@Service
public class InstitutionServicesService {
    private final MongoRepository<InstitutionRTPServiceEntity, String> rtpServiceRepository;
    private final ExternalApiClient externalApiClient;
    private final InstitutionServicesConfig servicesConfig;
    private static final String ORIGIN_IPA = "IPA";

    @Autowired
    public InstitutionServicesService(MongoRepository<InstitutionRTPServiceEntity, String> rtpServiceRepository,
                                      ExternalApiClient externalApiClient, InstitutionServicesConfig servicesConfig){
        this.rtpServiceRepository = rtpServiceRepository;
        this.externalApiClient = externalApiClient;
        this.servicesConfig = servicesConfig;
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
                if(!institution.getOrigin().equals(ORIGIN_IPA)){
                    throw new AppException(AppError.FORBIDDEN);
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

    public ServiceConsentsResponse getServiceConsents(String institutionId) {
        Institution institution = externalApiClient.getInstitution(institutionId);
        if (institution == null) {
            throw new AppException(AppError.INSTITUTION_NOT_FOUND);
        }

        // RTP
        InstitutionRTPServiceEntity rtpService = rtpServiceRepository.findById(institution.getId())
                .orElse(InstitutionRTPServiceEntity.builder()
                        .consent(servicesConfig.getDefaultConsents().get(ServiceId.RTP).name())
                        .consentDate(Instant.EPOCH)
                        .build());
        ServiceConsentInfo rtpConsentInfo = new ServiceConsentInfo(ServiceId.RTP, ServiceConsent.valueOf(rtpService.getConsent()),
                rtpService.getConsentDate().atZone(ZoneId.of("Europe/Rome")).toOffsetDateTime());

        return new ServiceConsentsResponse(List.of(rtpConsentInfo));
    }
}
