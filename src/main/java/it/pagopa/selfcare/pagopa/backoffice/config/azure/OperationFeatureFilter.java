package it.pagopa.selfcare.pagopa.backoffice.config.azure;

import com.azure.spring.cloud.feature.management.filters.FeatureFilter;
import com.azure.spring.cloud.feature.management.models.FeatureFilterEvaluationContext;
import it.pagopa.selfcare.pagopa.backoffice.model.SelfCareUser;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class OperationFeatureFilter implements FeatureFilter {

    @Override
    public boolean evaluate(FeatureFilterEvaluationContext context) {
        try {
            var selfcareUser = ((SelfCareUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal());
            String domainEmail = (String) context.getParameters().get("email");
            return selfcareUser.getEmail().endsWith(domainEmail);
        } catch (RuntimeException e) {
            log.warn("Custom Operation FeatureFilter", e);
            return false;
        }
    }

}
