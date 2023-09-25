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
import it.pagopa.selfcare.pagopa.backoffice.connector.model.channel.Channel;
import it.pagopa.selfcare.pagopa.backoffice.connector.model.channel.ChannelDetailsList;
import it.pagopa.selfcare.pagopa.backoffice.connector.model.station.StationDetailsList;
import it.pagopa.selfcare.pagopa.backoffice.connector.rest.RestTestUtils;
import it.pagopa.selfcare.pagopa.backoffice.connector.rest.config.ApiConfigSelfcareIntegrationRestClientConfigTest;
import it.pagopa.selfcare.pagopa.backoffice.connector.security.SelfCareUser;
import lombok.SneakyThrows;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
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
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.test.context.TestSecurityContextHolder;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.support.TestPropertySourceUtils;

import java.lang.reflect.Field;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

@ContextConfiguration(initializers = ApiConfigSelfcareIntegrationRestClientTest.RandomPortInitializer.class,
        classes = {
                ApiConfigSelfcareIntegrationRestClientTest.Config.class,
                ApiConfigSelfcareIntegrationRestClientConfigTest.class,
                FeignAutoConfiguration.class,
                HttpMessageConvertersAutoConfiguration.class,
                HttpClientConfiguration.class})
@TestPropertySource(
        properties = {
                "logging.level.it.pagopa.selfcare.pagopa.connector.rest=DEBUG",
                "spring.application.name=pagopa-selfcare-connector-rest"
        })
class ApiConfigSelfcareIntegrationRestClientTest {


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
                    String.format("PAGOPA_SELFCARE_INTEGRATION_APIM_SERVICE_URL=%s",
                            wm.getRuntimeInfo().getHttpBaseUrl())
            );
        }
    }

    private enum TestCase {
        FULLY_VALUED,
        FULLY_NULL,
        FULLY_EMPTY
    }

    @Order(1)
    @RegisterExtension
    static WireMockExtension wm = WireMockExtension.newInstance()
            .options(RestTestUtils.getWireMockConfiguration("stubs/channels"))
            .build();

    @Order(2)
    @RegisterExtension
    static SpringExtension springExtension = new SpringExtension();

    @Autowired
    private ApiConfigSelfcareIntegrationRestClient restClient;

    private static final Map<ApiConfigSelfcareIntegrationRestClientTest.TestCase, String> testCaseBrokerCodeMap = new EnumMap<>(ApiConfigSelfcareIntegrationRestClientTest.TestCase.class) {{
        put(ApiConfigSelfcareIntegrationRestClientTest.TestCase.FULLY_VALUED, "brokerCode1");
        put(ApiConfigSelfcareIntegrationRestClientTest.TestCase.FULLY_EMPTY, "brokerCode2");
        put(ApiConfigSelfcareIntegrationRestClientTest.TestCase.FULLY_NULL, "brokerCode3");

    }};


    @BeforeEach
    void beforeEach() {
        SelfCareUser selfCareUser = SelfCareUser.builder("id")
                .email("test@example.com")
                .name("name")
                .surname("surname")
                .build();
        TestSecurityContextHolder.setAuthentication(new TestingAuthenticationToken(selfCareUser, null));
    }

    @AfterEach
    void afterEach() {
        TestSecurityContextHolder.clearContext();
    }



    @Test
    void getStationsDetailsListByBroker_fullyValued() {
        // given
        TestCase testCase = TestCase.FULLY_VALUED;
        Integer page = 0;
        Integer limit = 2;
        String brokerCode = testCaseBrokerCodeMap.get(testCase);
        String stationId = "stationId";
        String xRequestId = "1";
        // when
        StationDetailsList response = restClient.getStationsDetailsListByBroker(brokerCode,stationId,limit,page);
        assertNotNull(response);
        assertNotNull(response.getStationsDetailsList());
        assertNotNull(response.getStationsDetailsList().get(0));
        assertNotNull(response.getStationsDetailsList().get(0).getEnabled());
    }

    @Test
    void getStationsDetailsListByBroker_fullyEmpty() {
        // given
        TestCase testCase = TestCase.FULLY_EMPTY;
        Integer page = 0;
        Integer limit = 2;
        String brokerCode = testCaseBrokerCodeMap.get(testCase);
        String stationId = "stationId";
        String xRequestId = "1";
        // when
        StationDetailsList response = restClient.getStationsDetailsListByBroker(brokerCode,stationId,limit,page);
        assertNotNull(response);
        assertNotNull(response.getStationsDetailsList());
        assertTrue(response.getStationsDetailsList().isEmpty());
    }


    @Test
    void getChannelDetailsListByBroker_fullyValued() {
        // given
        TestCase testCase = TestCase.FULLY_VALUED;
        Integer page = 0;
        Integer limit = 2;
        String brokerCode = testCaseBrokerCodeMap.get(testCase);
        String channelId = "channelId";
        String xRequestId = "1";
        // when
        ChannelDetailsList response = restClient.getChannelDetailsListByBroker(brokerCode,channelId,limit,page);
        assertNotNull(response);
        assertNotNull(response.getChannelDetailsList());
        assertNotNull(response.getChannelDetailsList().get(0));
        assertNotNull(response.getChannelDetailsList().get(0).getEnabled());
    }

    @Test
    void getChannelDetailsListByBroker_fullyEmpty() {
        // given
        TestCase testCase = TestCase.FULLY_EMPTY;
        Integer page = 0;
        Integer limit = 2;
        String brokerCode = testCaseBrokerCodeMap.get(testCase);
        String channelId = "channelId";
        String xRequestId = "1";
        // when
        ChannelDetailsList response = restClient.getChannelDetailsListByBroker(brokerCode,channelId,limit,page);
        assertNotNull(response);
        assertNotNull(response.getChannelDetailsList());
        assertTrue(response.getChannelDetailsList().isEmpty());
    }

    private void checkNotNullFields(Object o, String... excludedFields) {
        Set<String> excludedFieldsSet = new HashSet<>(Arrays.asList(excludedFields));
        org.springframework.util.ReflectionUtils.doWithFields(o.getClass(),
                f -> {
                    f.setAccessible(true);
                    assertNotNull(f.get(o), "The field " + f.getName() + " of the input object of type " + o.getClass() + " is null!");
                },
                f -> !excludedFieldsSet.contains(f.getName()));
    }

    private void checkNullFields(Object o) {
        org.springframework.util.ReflectionUtils.doWithFields(o.getClass(),
                f -> {
                    f.setAccessible(true);
                    assertNull(f.get(o), "The field " + f.getName() + " of the input object of type " + o.getClass() + " is null!");
                });
    }

    private void checkNotNullFieldsAttributes(Channel model) {
        Field[] fields = model.getClass().getFields();
        for (Field field : fields) {
            assertNotNull(field);
        }
    }

}
