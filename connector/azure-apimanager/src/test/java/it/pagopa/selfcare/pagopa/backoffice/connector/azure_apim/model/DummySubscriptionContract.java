package it.pagopa.selfcare.pagopa.backoffice.connector.azure_apim.model;

import com.azure.core.http.rest.PagedFlux;
import com.azure.core.http.rest.PagedIterable;
import com.azure.core.http.rest.PagedResponse;
import com.azure.resourcemanager.apimanagement.fluent.models.SubscriptionContractInner;
import com.azure.resourcemanager.apimanagement.models.SubscriptionContract;
import com.azure.resourcemanager.apimanagement.models.SubscriptionState;
import lombok.Setter;
import java.time.OffsetDateTime;

@Setter
public class DummySubscriptionContract implements SubscriptionContract {

    private String id;
    private String secondaryKey;
    private String primaryKey;
    private String displayName;

    private String name;



    @Override
    public String id() {
        return id;
    }

    @Override
    public String name() {
        return name;
    }

    @Override
    public String type() {
        return null;
    }

    @Override
    public String ownerId() {
        return null;
    }

    @Override
    public String scope() {
        return null;
    }

    @Override
    public String displayName() {
        return displayName;
    }

    @Override
    public SubscriptionState state() {
        return null;
    }

    @Override
    public OffsetDateTime createdDate() {
        return null;
    }

    @Override
    public OffsetDateTime startDate() {
        return null;
    }

    @Override
    public OffsetDateTime expirationDate() {
        return null;
    }

    @Override
    public OffsetDateTime endDate() {
        return null;
    }

    @Override
    public OffsetDateTime notificationDate() {
        return null;
    }

    @Override
    public String primaryKey() {
        return primaryKey;
    }

    @Override
    public String secondaryKey() {
        return secondaryKey;
    }

    @Override
    public String stateComment() {
        return null;
    }

    @Override
    public Boolean allowTracing() {
        return null;
    }

    @Override
    public SubscriptionContractInner innerModel() {
        return null;
    }

//List<String> l;
//    public PagedIterable<SubscriptionContract> getPagedIterableSubscriptionContract() {
//
//        Mono<PagedResponse<SubscriptionContract>> contractMono = new Mono<PagedResponse<SubscriptionContract>>() {
//            @Override
//            public void subscribe(CoreSubscriber<? super PagedResponse<SubscriptionContract>> actual) {
//
//            }
//        };
//        PagedIterable<SubscriptionContract> subscriptionContracts = new PagedIterable<>(new PagedFlux<>(() -> {
//
//            return contractMono;
//        }));
//
//        return subscriptionContracts;
//    }
//
//    ;

//
//    public PagedIterable<SubscriptionContract> getDummySubscriptionContract() {
//        return new DummySubscriptionContract(pagedFluxInstance);
//
//    }
}
