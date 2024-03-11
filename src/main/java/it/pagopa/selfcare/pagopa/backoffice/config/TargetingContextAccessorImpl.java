package it.pagopa.selfcare.pagopa.backoffice.config;

import com.azure.spring.cloud.feature.management.filters.TargetingFilter;
import com.azure.spring.cloud.feature.management.targeting.TargetingContext;
import com.azure.spring.cloud.feature.management.targeting.TargetingContextAccessor;
import com.azure.spring.cloud.feature.management.targeting.TargetingEvaluationOptions;
import org.springframework.context.annotation.Bean;

import java.util.ArrayList;

public class TargetingContextAccessorImpl implements TargetingContextAccessor {

    @Override
    public void configureTargetingContext(TargetingContext context) {
        context.setUserId("Jeff");
        ArrayList<String> groups = new ArrayList<String>();
        groups.add("Ring0");
        context.setGroups(groups);
    }

    @Bean
    public TargetingFilter targetingFilter(TargetingContextAccessor contextAccessor) {
        return new TargetingFilter(contextAccessor, new TargetingEvaluationOptions().setIgnoreCase(true));
    }
}
