package it.pagopa.selfcare.pagopa.backoffice.config.azure;

import com.azure.spring.cloud.feature.management.filters.PercentageFilter;
import com.azure.spring.cloud.feature.management.filters.TargetingFilter;
import com.azure.spring.cloud.feature.management.filters.TimeWindowFilter;
import com.azure.spring.cloud.feature.management.targeting.TargetingContextAccessor;
import com.azure.spring.cloud.feature.management.targeting.TargetingEvaluationOptions;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FeatureFilterConfigurations {

    @Bean
    public PercentageFilter percentageFilter() {
        return new PercentageFilter();
    }

    @Bean
    public TimeWindowFilter timeWindowFilter() {
        return new TimeWindowFilter();
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
