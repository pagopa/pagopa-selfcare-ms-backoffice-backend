package it.pagopa.selfcare.pagopa.backoffice.config.azure;

import com.azure.spring.cloud.feature.management.filters.PercentageFilter;
import com.azure.spring.cloud.feature.management.filters.TargetingFilter;
import com.azure.spring.cloud.feature.management.filters.TimeWindowFilter;
import com.azure.spring.cloud.feature.management.targeting.TargetingContextAccessor;
import com.azure.spring.cloud.feature.management.targeting.TargetingEvaluationOptions;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FeatureFilterConfigurations {

    @Value("${azure.resource-manager.api-manager.client-id}")
    private String clientId;

    @Value("${azure.resource-manager.api-manager.client-secret}")
    private String clientSecret;

    @Value("${azure.resource-manager.api-manager.tenant-id}")
    private String tenantId;

    private final OperationFeatureFilter operationFeatureFilter;

    public FeatureFilterConfigurations(OperationFeatureFilter operationFeatureFilter) {
        this.operationFeatureFilter = operationFeatureFilter;
    }

    @Bean
    public PercentageFilter percentageFilter() {
        return new PercentageFilter();
    }

    @Bean
    public TimeWindowFilter timeWindowFilter() {
        return new TimeWindowFilter();
    }

    @Bean(name = "Operation")
    public OperationFeatureFilter operation() {
        return operationFeatureFilter;
    }

    @Bean(name = "Microsoft.Targeting")
    public TargetingFilter targetingFilter(TargetingContextAccessor contextAccessor) {
        return new TargetingFilter(contextAccessor, new TargetingEvaluationOptions().setIgnoreCase(true));
    }

    @Bean
    public TargetingContextAccessor targetingContextAccessor() {
        return new TargetingContextAccessorImpl();
    }


}
