package it.pagopa.selfcare.pagopa.backoffice.connector.azure_apim.model;

import com.azure.core.http.HttpHeaders;
import com.azure.core.http.HttpRequest;
import com.azure.core.http.rest.Response;
import com.azure.resourcemanager.apimanagement.fluent.models.SubscriptionKeysContractInner;
import com.azure.resourcemanager.apimanagement.models.SubscriptionKeysContract;

public class DummyKeyContract implements Response<SubscriptionKeysContract> {
    @Override
    public int getStatusCode() {
        return 0;
    }

    @Override
    public HttpHeaders getHeaders() {
        return null;
    }

    @Override
    public HttpRequest getRequest() {
        return null;
    }

    @Override
    public SubscriptionKeysContract getValue() {
        return new SubscriptionKeysContract() {
            @Override
            public String primaryKey() {
                return "primaryKey";
            }

            @Override
            public String secondaryKey() {
                return "secondaryKey";
            }

            @Override
            public SubscriptionKeysContractInner innerModel() {
                return null;
            }
        };
    }
}
