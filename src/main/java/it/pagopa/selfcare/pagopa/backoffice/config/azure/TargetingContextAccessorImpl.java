package it.pagopa.selfcare.pagopa.backoffice.config.azure;

import com.azure.spring.cloud.feature.management.targeting.TargetingContext;
import com.azure.spring.cloud.feature.management.targeting.TargetingContextAccessor;
import it.pagopa.selfcare.pagopa.backoffice.model.SelfCareUser;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.ArrayList;

public class TargetingContextAccessorImpl implements TargetingContextAccessor {

    @Override
    public void configureTargetingContext(TargetingContext context) {
//        var selfcareUser = ((SelfCareUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal());
//        context.setUserId(selfcareUser.getEmail());
//        ArrayList<String> groups = new ArrayList<>();
//        groups.add(getGroup(selfcareUser));
//        context.setGroups(groups);
        context.setUserId("pagopa@altoadigeriscossioni.it");
        ArrayList<String> groups = new ArrayList<>();
        groups.add("altoadigeriscossioni.it");
        context.setGroups(groups);
    }

    private static String getGroup(SelfCareUser selfcareUser) {
        try {
            return "@" + selfcareUser.getEmail().split("@")[1];
        }catch (Exception e){
            return "";
        }
    }
}
