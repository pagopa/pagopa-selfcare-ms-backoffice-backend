package it.pagopa.selfcare.pagopa.backoffice.connector.aws_ses.config;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@Configuration
@Import(AwsSesConfig.class)
public class AwsSesConfigTest {
    // no tests needed
}
