package it.pagopa.selfcare.pagopa.backoffice.connector.rest.client;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.github.tomakehurst.wiremock.junit5.WireMockExtension;
import it.pagopa.selfcare.pagopa.backoffice.connector.model.institution.Attribute;
import it.pagopa.selfcare.pagopa.backoffice.connector.model.institution.Institution;
import it.pagopa.selfcare.pagopa.backoffice.connector.rest.RestTestUtils;
import it.pagopa.selfcare.pagopa.backoffice.connector.rest.config.ExternalApiRestClientConfigTest;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.http.HttpMessageConvertersAutoConfiguration;
import org.springframework.cloud.commons.httpclient.HttpClientConfiguration;
import org.springframework.cloud.openfeign.FeignAutoConfiguration;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.support.TestPropertySourceUtils;

import java.lang.reflect.Field;
import java.util.EnumMap;
import java.util.Map;
import java.util.TimeZone;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
@ContextConfiguration(initializers = ExternalApiRestClientTest.RandomPortInitializer.class,
        classes = {
                ExternalApiRestClientTest.Config.class,
                ExternalApiRestClientConfigTest.class,
                FeignAutoConfiguration.class,
                HttpMessageConvertersAutoConfiguration.class,
                HttpClientConfiguration.class})
@TestPropertySource(
        properties = {
                "logging.level.it.pagopa.selfcare.pagopa.connector.rest=DEBUG",
                "spring.application.name=pagopa-selfcare-connector-rest"
        })
class ExternalApiRestClientTest {
    public static class Config {
        @Bean
        @Primary
        public ObjectMapper objectMapper() {
            ObjectMapper mapper = new ObjectMapper();
            mapper.registerModule(new JavaTimeModule());
            mapper.registerModule(new Jdk8Module());
            mapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
            mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
            mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            mapper.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.NONE);
            mapper.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);
            mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
            mapper.setTimeZone(TimeZone.getDefault());
            return mapper;
        }
    }

    public static class RandomPortInitializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {
        @SneakyThrows
        @Override
        public void initialize(ConfigurableApplicationContext applicationContext) {
            TestPropertySourceUtils.addInlinedPropertiesToEnvironment(applicationContext,
                    String.format("EXTERNAL_API_SERVICE_URL=%s",
                            wm.getRuntimeInfo().getHttpBaseUrl())
            );
        }
    }


    private enum TestCase {
        FULLY_VALUED,
        FULLY_NULL,
        EMPTY_RESULT
    }

    @Order(1)
    @RegisterExtension
    static WireMockExtension wm = WireMockExtension.newInstance()
            .options(RestTestUtils.getWireMockConfiguration("stubs/external"))
            .build();

    @Order(2)
    @RegisterExtension
    static SpringExtension springExtension = new SpringExtension();

    @Autowired
    private ExternalApiRestClient restClient;
    
    private static final Map<TestCase, String> testCase2instIdMap = new EnumMap<>(TestCase.class) {{
        put(TestCase.FULLY_VALUED, "institutionId1");
        put(TestCase.FULLY_NULL, "institutionId2");
        put(TestCase.EMPTY_RESULT, "institutionId3");
    }};



    @Test
    void getInstitution_fullyValued() {
        // given
        String id = testCase2instIdMap.get(TestCase.FULLY_VALUED);
        // when
        Institution response = restClient.getInstitution(id);
        assertNotNull(response);
        checkNotNullFields(response);
        response.getAttributes().forEach(this::checkNotNullFieldsAttributes);

    }


    @Test
    void getInstitution_fullyNull() {
        // given
        String id = testCase2instIdMap.get(TestCase.FULLY_NULL);
        // when
        Institution response = restClient.getInstitution(id);
        assertNotNull(response);
        assertNull(response.getAddress());
        assertNull(response.getDescription());
        assertNull(response.getDigitalAddress());
        assertNull(response.getId());
        assertNull(response.getExternalId());
        assertNull(response.getTaxCode());
        assertNull(response.getZipCode());
    }
    
    private void checkNotNullFields(Institution model){
        Field[] fields = model.getClass().getFields();
        for (Field field: fields){
            assertNotNull(field);
        }
    }
    private void checkNotNullFieldsAttributes(Attribute model){
        Field[] fields = model.getClass().getFields();
        for (Field field: fields){
            assertNotNull(field);
        }
    }

}
