package it.pagopa.selfcare.pagopa.backoffice.service;

import it.pagopa.selfcare.pagopa.backoffice.client.ApiConfigClient;
import it.pagopa.selfcare.pagopa.backoffice.model.configuration.PaymentType;
import it.pagopa.selfcare.pagopa.backoffice.model.configuration.PaymentTypes;
import it.pagopa.selfcare.pagopa.backoffice.model.configuration.WfespPluginConfs;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.validation.Valid;
import java.util.stream.Stream;

@Slf4j
@Service
public class ConfigurationService {

    private final ApiConfigClient apiConfigClient;

    @Autowired
    public ConfigurationService(ApiConfigClient apiConfigClient) {
        this.apiConfigClient = apiConfigClient;
    }

    public PaymentTypes getPaymentTypes() {
        PaymentTypes paymentTypes = apiConfigClient.getPaymentTypes();
        if(paymentTypes != null && paymentTypes.getPaymentTypeList() != null)
        {
            PaymentType paymentTypeAny = new PaymentType();
            paymentTypeAny.setDescription("");
            paymentTypeAny.setPaymentTypeCode("ANY");
            paymentTypes.setPaymentTypeList(Stream.concat(paymentTypes.getPaymentTypeList().stream(),
                    Stream.of(paymentTypeAny)).toList());
        }
        return paymentTypes;
    }

    public WfespPluginConfs getWfespPlugins() {
        return apiConfigClient.getWfespPlugins();
    }
}
