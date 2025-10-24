package it.pagopa.selfcare.pagopa.backoffice.service;

import it.pagopa.selfcare.pagopa.backoffice.client.ApiConfigClient;
import it.pagopa.selfcare.pagopa.backoffice.model.configuration.PaymentType;
import it.pagopa.selfcare.pagopa.backoffice.model.configuration.PaymentTypes;
import it.pagopa.selfcare.pagopa.backoffice.model.configuration.WfespPluginConfs;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.validation.Valid;

@Slf4j
@Service
public class ConfigurationService {

    @Autowired
    private ApiConfigClient apiConfigClient;

    public PaymentTypes getPaymentTypes() {
        @Valid PaymentTypes paymentTypes = apiConfigClient.getPaymentTypes();
        if(paymentTypes != null && paymentTypes.getPaymentTypeList() != null && !paymentTypes.getPaymentTypeList().isEmpty())
        {
            PaymentType paymentTypeAny = new PaymentType();
            paymentTypeAny.setDescription("");
            paymentTypeAny.setPaymentTypeCode("ANY");
            paymentTypes.getPaymentTypeList().add(paymentTypeAny);
        }
        return paymentTypes;
    }

    public WfespPluginConfs getWfespPlugins() {
        return apiConfigClient.getWfespPlugins();
    }
}
