package it.pagopa.selfcare.pagopa.backoffice.config;

import net.javacrumbs.shedlock.spring.annotation.EnableSchedulerLock;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@Configuration
@EnableScheduling
@EnableAsync
@EnableSchedulerLock(defaultLockAtMostFor = "10m")
@ConditionalOnProperty(name = "cron.job.schedule.enabled", matchIfMissing = true)
public class SchedulerConfig {
}
