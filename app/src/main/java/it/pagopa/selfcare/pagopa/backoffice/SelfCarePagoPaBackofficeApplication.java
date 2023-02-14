package it.pagopa.selfcare.pagopa.backoffice;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class SelfCarePagoPaBackofficeApplication {

    public static void main(String[] args) {
        SpringApplication.run(SelfCarePagoPaBackofficeApplication.class, args);
    }
}
