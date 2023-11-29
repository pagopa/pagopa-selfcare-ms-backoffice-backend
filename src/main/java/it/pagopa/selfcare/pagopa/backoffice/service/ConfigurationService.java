package it.pagopa.selfcare.pagopa.backoffice.service;

import it.pagopa.selfcare.pagopa.backoffice.client.ApiConfigClient;
import it.pagopa.selfcare.pagopa.backoffice.model.configuration.PaymentTypes;
import it.pagopa.selfcare.pagopa.backoffice.model.configuration.WfespPluginConfs;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class ConfigurationService {

    @Autowired
    private ApiConfigClient apiConfigClient;

    public PaymentTypes getPaymentTypes() {
        return apiConfigClient.getPaymentTypes();
    }

    public WfespPluginConfs getWfespPlugins() {
        return apiConfigClient.getWfespPlugins();
    }
}
