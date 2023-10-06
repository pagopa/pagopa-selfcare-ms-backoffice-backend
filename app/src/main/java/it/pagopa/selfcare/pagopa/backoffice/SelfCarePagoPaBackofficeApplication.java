package it.pagopa.selfcare.pagopa.backoffice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class SelfCarePagoPaBackofficeApplication {

    public static void main(String[] args) {
        SpringApplication.run(SelfCarePagoPaBackofficeApplication.class, args);
    }
}
