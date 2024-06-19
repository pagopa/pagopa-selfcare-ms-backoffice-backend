package it.pagopa.selfcare.pagopa.backoffice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableCaching
@EnableFeignClients
@EnableRetry
@EnableAsync
public class SelfCarePagoPaBackofficeApplication {

    public static void main(String[] args) {
        SpringApplication.run(SelfCarePagoPaBackofficeApplication.class, args);
    }
}
