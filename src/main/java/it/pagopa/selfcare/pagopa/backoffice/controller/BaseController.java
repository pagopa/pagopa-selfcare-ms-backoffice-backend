package it.pagopa.selfcare.pagopa.backoffice.controller;

import com.azure.spring.cloud.feature.management.FeatureManager;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import it.pagopa.selfcare.pagopa.backoffice.model.AppInfo;
import it.pagopa.selfcare.pagopa.backoffice.model.featureflags.FeatureFlags;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@RestController()
@Configuration
@Slf4j
public class BaseController  {
    @Value("${info.application.name}")
    private String name;

    @Value("${info.application.version}")
    private String version;

    @Value("${info.properties.environment}")
    private String environment;

    private final FeatureManager featureManager;

    public BaseController(FeatureManager featureManager) {
        this.featureManager = featureManager;
    }


    @Operation(summary = "health check", description = "Return OK if application is started", security = {@SecurityRequirement(name = "JWT")}, tags = {"Home"})
    @GetMapping(value = "/info")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<AppInfo> healthCheck() {
        // Used just for health checking
        AppInfo info = AppInfo.builder().name(name).version(version).environment(environment).build();
        return ResponseEntity.status(HttpStatus.OK).body(info);
    }

    @Operation(summary = "Return an Azure Feature Flag", description = "Return the value of the flag given the name", security = {@SecurityRequirement(name = "JWT")}, tags = {"Home"})
    @GetMapping(value = "/flags/{name}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Boolean> getFeatureFlag(@PathVariable("name") String name) {
        boolean flag = featureManager.isEnabled(name);
        return ResponseEntity.status(HttpStatus.OK).body(flag);
    }

    @Operation(summary = "Return all Azure Feature Flags", description = "Return a map with all feature flags", security = {@SecurityRequirement(name = "JWT")}, tags = {"Home"})
    @GetMapping(value = "/flags")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<FeatureFlags> getFeatureFlags() {
        Set<String> featureFlagNames = featureManager.getAllFeatureNames();
        Map<String, Boolean> map = new HashMap<>();
        for (var flagName : featureFlagNames) {
            boolean value = featureManager.isEnabled(flagName);
            map.put(flagName, value);
        }
        return ResponseEntity.status(HttpStatus.OK).body(FeatureFlags.builder().flags(map).build());
    }
}
