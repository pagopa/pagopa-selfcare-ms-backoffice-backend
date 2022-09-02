package it.pagopa.selfcare.pagopa.backoffice.core;

import it.pagopa.selfcare.pagopa.backoffice.connector.api.ApiManagerConnector;
import it.pagopa.selfcare.pagopa.backoffice.connector.exception.ResourceNotFoundException;
import it.pagopa.selfcare.pagopa.backoffice.connector.model.CreateInstitutionSubscription;
import it.pagopa.selfcare.pagopa.backoffice.connector.model.UserSubscription;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class ApiManagementServiceImpl implements ApiManagementService {

    private final ApiManagerConnector apiManagerConnector;

    @Autowired
    public ApiManagementServiceImpl(ApiManagerConnector apiManagerConnector) {
        this.apiManagerConnector = apiManagerConnector;
    }


    @Override
    public void createUserSubscription(String userId, CreateInstitutionSubscription subscription) {
        log.trace("createUserSubscription start");
        log.debug("createUserSubscription userId = {}, subscription = {}", userId, subscription);
        try {
            apiManagerConnector.createSubscription(userId, subscription);
        } catch (RuntimeException e) {
            
        }
    }

    @Override
    public UserSubscription getUserSubscription(String userId) throws ResourceNotFoundException {
        log.trace("getUserSubscription start");
        log.debug("getUserSubscription userId = {}", userId);
        UserSubscription subscription = null;
        try {
            subscription = apiManagerConnector.getUserSubscription(userId);
        } catch (RuntimeException e) {
            throw new ResourceNotFoundException(String.format("No subscription found for %s userId", userId));
        }
        log.debug("getUserSubscription subscription = {}", subscription);
        log.trace("getUserSubscription end");
        return subscription;
    }
}
