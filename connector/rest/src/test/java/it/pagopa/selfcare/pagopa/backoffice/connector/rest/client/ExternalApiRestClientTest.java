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
import it.pagopa.selfcare.pagopa.backoffice.connector.model.institution.InstitutionInfo;
import it.pagopa.selfcare.pagopa.backoffice.connector.model.product.Product;
import it.pagopa.selfcare.pagopa.backoffice.connector.rest.RestTestUtils;
import it.pagopa.selfcare.pagopa.backoffice.connector.rest.config.ExternalApiRestClientConfigTest;
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
    
    @BeforeEach
    void beforeEach(){
        SelfCareUser selfCareUser = SelfCareUser.builder("id")
                .email("test@example.com")
                .name("name")
                .surname("surname")
                .build();
        TestSecurityContextHolder.setAuthentication(new TestingAuthenticationToken(selfCareUser, null));
    }
    
    @AfterEach
    void afterEach(){
        TestSecurityContextHolder.clearContext();
    }


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
        //then
        assertNotNull(response);
        assertNull(response.getAddress());
        assertNull(response.getDescription());
        assertNull(response.getDigitalAddress());
        assertNull(response.getId());
        assertNull(response.getExternalId());
        assertNull(response.getTaxCode());
        assertNull(response.getZipCode());
    }

    @Test
    void getInstitutions_fullyValued() {
        //given
        String productId = "productId";
        //when
        List<InstitutionInfo> institutions = restClient.getInstitutions(productId);
        //then
        assertNotNull(institutions);
        assertFalse(institutions.isEmpty());
        assertEquals(1, institutions.size());
        checkNotNullFields(institutions.get(0));
    }

    @Test
    void getInstitutions_fullyNull() {
        //given
        String productId = "productId1";
        //when
        List<InstitutionInfo> institutions = restClient.getInstitutions(productId);
        //then
        assertNotNull(institutions);
        assertFalse(institutions.isEmpty());
        assertNotNull(institutions.get(0));
        checkNullFields(institutions.get(0));
    }

    @Test
    void getInstitutions_fullyEmpty() {
        //given
        String productId = "productId2";
        //when
        List<InstitutionInfo> institutionInfos = restClient.getInstitutions(productId);
        //then
        assertNotNull(institutionInfos);
        assertTrue(institutionInfos.isEmpty());

    }
    
    @Test
    void getInstitutionUserProducts_fullyValued(){
        //given
        String institutionId = testCase2instIdMap.get(TestCase.FULLY_VALUED);
        //when
        List<Product> products = restClient.getInstitutionUserProducts(institutionId);
        //then
        assertNotNull(products);
        assertFalse(products.isEmpty());
        checkNotNullFields(products.get(0));
    }
    
    @Test
    void getInstitutionUserProducts_fullyNull(){
        //given
        String institutionId = testCase2instIdMap.get(TestCase.FULLY_NULL);
        //when
        List<Product> products = restClient.getInstitutionUserProducts(institutionId);
        //then
        assertNotNull(products);
        assertFalse(products.isEmpty());
        assertNotNull(products.get(0));
        checkNullFields(products.get(0));
    }
    
    @Test
    void getInstitutionUserProducts_fullyEmpty(){
        //given
        String institutionId = testCase2instIdMap.get(TestCase.EMPTY_RESULT);
        //when
        List<Product> products = restClient.getInstitutionUserProducts(institutionId);
        //then
        assertNotNull(products);
        assertTrue(products.isEmpty());
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
    
    private void checkNullFields(Object o){
        org.springframework.util.ReflectionUtils.doWithFields(o.getClass(),
                f -> {
                    f.setAccessible(true);
                    assertNull(f.get(o), "The field " + f.getName() + " of the input object of type " + o.getClass() + " is null!");
                });
    }

    private void checkNotNullFieldsAttributes(Attribute model) {
        Field[] fields = model.getClass().getFields();
        for (Field field : fields) {
            assertNotNull(field);
        }
    }

}
