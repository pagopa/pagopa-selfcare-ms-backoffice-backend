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
import it.pagopa.selfcare.pagopa.backoffice.connector.model.channel.*;
import it.pagopa.selfcare.pagopa.backoffice.connector.rest.RestTestUtils;
import it.pagopa.selfcare.pagopa.backoffice.connector.rest.config.ApiConfigRestClientConfigTest;
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

import static it.pagopa.selfcare.pagopa.TestUtils.mockInstance;
import static org.junit.jupiter.api.Assertions.*;

@ContextConfiguration(initializers = ApiConfigRestClientTest.RandomPortInitializer.class,
        classes = {
                ApiConfigRestClientTest.Config.class,
                ApiConfigRestClientConfigTest.class,
                FeignAutoConfiguration.class,
                HttpMessageConvertersAutoConfiguration.class,
                HttpClientConfiguration.class})
@TestPropertySource(
        properties = {
                "logging.level.it.pagopa.selfcare.pagopa.connector.rest=DEBUG",
                "spring.application.name=pagopa-selfcare-connector-rest"
        })
class ApiConfigRestClientTest {


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
                    String.format("PAGOPA_APIM_SERVICE_URL=%s",
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
            .options(RestTestUtils.getWireMockConfiguration("stubs/channels"))
            .build();

    @Order(2)
    @RegisterExtension
    static SpringExtension springExtension = new SpringExtension();

    @Autowired
    private ApiConfigRestClient restClient;

    private static final Map<ApiConfigRestClientTest.TestCase, String> testCasePspCodeMap = new EnumMap<>(ApiConfigRestClientTest.TestCase.class) {{
        put(ApiConfigRestClientTest.TestCase.FULLY_VALUED, "pspCode1");
        put(ApiConfigRestClientTest.TestCase.EMPTY_RESULT, "pspCode2");
    }};

    private static final Map<ApiConfigRestClientTest.TestCase, String> testCaseChannelCodeMap = new EnumMap<>(ApiConfigRestClientTest.TestCase.class) {{
        put(ApiConfigRestClientTest.TestCase.FULLY_VALUED, "channelcode1");
        put(ApiConfigRestClientTest.TestCase.EMPTY_RESULT, "channelcode2");
    }};

    private static final Map<ApiConfigRestClientTest.TestCase, HashMap<String,String>> testCaseChannelCodeMPspCodeMap = new EnumMap<>(ApiConfigRestClientTest.TestCase.class) {{
        put(ApiConfigRestClientTest.TestCase.FULLY_VALUED, new HashMap<String, String>() {{
            put("channelCode", "channelcode1");
            put("pspCode", "pspcode1");
        }});
    }};

    private static final Map<TestCase, Map<String, Object>> testCase3ChannelDto = new EnumMap(TestCase.class) {{
        ChannelDetails channelDetails = new ChannelDetails();
        channelDetails.setPassword("password");
        channelDetails.setNewPassword("newPassword");
        channelDetails.setProtocol(Protocol.HTTP);
        channelDetails.setIp("127.0.0.1");
        channelDetails.setPort(Long.parseLong("8080"));
        channelDetails.setService("service");
        channelDetails.setBrokerPspCode("psp");
        channelDetails.setProxyEnabled(true);
        channelDetails.setProxyHost("127.0.0.1");
        channelDetails.setProxyPort(Long.parseLong("8090"));
        channelDetails.setProxyUsername("username");
        channelDetails.setProxyPassword("setProxyPassword");
        channelDetails.setTargetHost("setTargetHost");
        channelDetails.setTargetPort(Long.parseLong("8888"));
        channelDetails.setTargetPath("setTargetPath");
        channelDetails.setThreadNumber(Long.parseLong("1"));
        channelDetails.setTimeoutA(Long.parseLong("1"));
        channelDetails.setTimeoutB(Long.parseLong("2"));
        channelDetails.setTimeoutC(Long.parseLong("3"));
        channelDetails.setNpmService("setNpmService");
        channelDetails.setNewFaultCode(false);
        channelDetails.setRedirectIp("127.0.0.3");
        channelDetails.setRedirectPath("setRedirectPath");
        channelDetails.setRedirectPort(Long.parseLong("8989"));
        channelDetails.setRedirectQueryString("/setRedirectQueryString");
        channelDetails.setRedirectProtocol(Protocol.HTTP);
        channelDetails.setPaymentModel(PaymentModel.IMMEDIATE);
        channelDetails.setServPlugin("setServPlugin");
        channelDetails.setRtPush(true);
        channelDetails.setOnUs(true);
        channelDetails.setCardChart(true);
        channelDetails.setRecovery(true);
        channelDetails.setDigitalStampBrand(true);
        channelDetails.setFlagIo(true);
        channelDetails.setAgid(true);
        channelDetails.setBrokerDescription("setBrokerDescription");
        channelDetails.setEnabled(true);
        channelDetails.setChannelCode("setChannelCode");
        put(TestCase.FULLY_VALUED, channelDetails);

    }};

    private static final Map<TestCase, Map<String, Object>> testCase2instIdMap = new EnumMap<>(TestCase.class) {{
        put(TestCase.FULLY_VALUED, new HashMap<String, Object>() {{
            put("page", 2);
            put("limit", 16);
            put("code", "code1");
            put("ordering", "DESC");
        }});
        put(TestCase.FULLY_NULL, new HashMap<String, Object>() {{
            put("page", 2);
            put("limit", null);
            put("code", null);
            put("ordering", null);
        }});
        put(TestCase.EMPTY_RESULT, new HashMap<String, Object>() {{
            put("page", 3);
            put("limit", 10);
            put("code", "code");
            put("ordering", "ASC");
        }});
    }};

    private static final Map<TestCase, String> testCasePspChannelPaymentTypesMap = new EnumMap<>(ApiConfigRestClientTest.TestCase.class) {{
        put(ApiConfigRestClientTest.TestCase.FULLY_VALUED, "channelcode1");
        put(ApiConfigRestClientTest.TestCase.EMPTY_RESULT, "channelcode2");
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
    void getChannels_fullyNull() {
        // given
        TestCase testCase = TestCase.FULLY_NULL;
        Integer page = (Integer) testCase2instIdMap.get(testCase).get("page");
        Integer limit = (Integer) testCase2instIdMap.get(testCase).get("limit");
        String code = (String) testCase2instIdMap.get(testCase).get("code");
        String ordering = (String) testCase2instIdMap.get(testCase).get("ordering");
        String xRequestId = "1";
        // when
        Channels response = restClient.getChannels(limit, page, code, ordering, xRequestId);
        assertNotNull(response);
        assertNull(response.getChannelList());
        assertNull(response.getPageInfo());
    }

    @Test
    void getChannels_fullyValued() {
        // given
        TestCase testCase = TestCase.FULLY_VALUED;
        Integer page = (Integer) testCase2instIdMap.get(testCase).get("page");
        Integer limit = (Integer) testCase2instIdMap.get(testCase).get("limit");
        String sortBy = (String) testCase2instIdMap.get(testCase).get("sortBy");
        String code = (String) testCase2instIdMap.get(testCase).get("code");
        String ordering = (String) testCase2instIdMap.get(testCase).get("ordering");
        String requestId = UUID.randomUUID().toString();
        // when
        Channels response = restClient.getChannels(limit, page, code, ordering, requestId);

        //then
        assertNotNull(response);
        assertNotNull(response.getPageInfo());
        assertNotNull(response.getChannelList());
        assertEquals(1, response.getChannelList().size());
        checkNotNullFields(response.getChannelList().get(0));
        Channel channel = response.getChannelList().get(0);
        response.getChannelList().forEach(this::checkNotNullFieldsAttributes);
        assertNotNull(channel.getChannelCode());
        assertNotNull(channel.getEnabled());
        assertNotNull(channel.getBrokerDescription());
        assertNotNull(response.getPageInfo());
        assertNotNull(response.getPageInfo().getPage());
        assertNotNull(response.getPageInfo().getLimit());
        assertNotNull(response.getPageInfo().getTotalPages());
        assertNotNull(response.getPageInfo().getItemsFound());
    }

    @Test
    void getPspChannels_fullyValued() {
        // given
        TestCase testCase = TestCase.FULLY_VALUED;
        String pspCode = testCasePspCodeMap.get(testCase);
        String xRequestId = "1";
        // when
        PspChannels response = restClient.getPspChannels(pspCode, xRequestId);
        assertNotNull(response);
        assertNotNull(response.getChannelsList());
        assertNotNull(response.getChannelsList().get(0));
        assertNotNull(response.getChannelsList().get(0).getChannelCode());
        assertNotNull(response.getChannelsList().get(0).getEnabled());
    }

    @Test
    void getPspChannels_fullyEmpty() {
        // given
        TestCase testCase = TestCase.EMPTY_RESULT;
        String pspCode = testCasePspCodeMap.get(testCase);
        String xRequestId = "1";
        // when
        PspChannels response = restClient.getPspChannels(pspCode, xRequestId);
        assertNotNull(response);
        assertNotNull(response.getChannelsList());
        assertTrue(response.getChannelsList().isEmpty());
    }

    @Test
    void getChannelDetails_fullyValued() {
        // given
        TestCase testCase = TestCase.FULLY_VALUED;
        String pspCode = testCaseChannelCodeMap.get(testCase);
        String xRequestId = "1";
        // when
        ChannelDetails response = restClient.getChannelDetails(pspCode, xRequestId);
        assertNotNull(response);
        assertNotNull(response.getPassword());
        assertNotNull(response.getNewPassword());
        assertNotNull(response.getProtocol());
        assertNotNull(response.getIp());
        assertNotNull(response.getPort());
        assertNotNull(response.getService());
        assertNotNull(response.getBrokerPspCode());
        assertNotNull(response.getProxyEnabled());
        assertNotNull(response.getProxyHost());
        assertNotNull(response.getProxyPort());
        assertNotNull(response.getProxyUsername());
        assertNotNull(response.getProxyPassword());
        assertNotNull(response.getTargetHost());
        assertNotNull(response.getTargetPort());
        assertNotNull(response.getTargetPath());
        assertNotNull(response.getThreadNumber());
        assertNotNull(response.getTimeoutA());
    }

    @Test
    void getChannelDetails_fullyEmpty() {
        // given
        TestCase testCase = TestCase.EMPTY_RESULT;
        String pspCode = testCaseChannelCodeMap.get(testCase);
        String xRequestId = "1";
        // when
        ChannelDetails response = restClient.getChannelDetails(pspCode, xRequestId);
        assertNotNull(response);
    }


    @Test
    void createChannel_fullyValued() {
        // given
        TestCase testCase = TestCase.FULLY_VALUED;
        ChannelDetails channelDetails = (ChannelDetails) testCase3ChannelDto.get(testCase);
        String requestId = UUID.randomUUID().toString();
        // when
        ChannelDetails response = restClient.createChannel(channelDetails, requestId);

        //then
        assertNotNull(response.getPassword());
        assertNotNull(response.getNewPassword());
        assertNotNull(response.getProtocol());
        assertNotNull(response.getIp());
        assertNotNull(response.getPort());
        assertNotNull(response.getService());
        assertNotNull(response.getBrokerPspCode());
        assertNotNull(response.getProxyEnabled());
        assertNotNull(response.getProxyHost());
        assertNotNull(response.getProxyPort());
        assertNotNull(response.getProxyUsername());
        assertNotNull(response.getProxyPassword());
        assertNotNull(response.getTargetHost());
        assertNotNull(response.getTargetPort());
        assertNotNull(response.getTargetPath());
        assertNotNull(response.getThreadNumber());
        assertNotNull(response.getTimeoutA());

    }

    @Test
    void createChannelPaymentType_fullyValued() {
        // given
        TestCase testCase = TestCase.FULLY_VALUED;
        String channelCode = testCasePspChannelPaymentTypesMap.get(testCase);
        PspChannelPaymentTypes pspChannelPaymentTypes = new PspChannelPaymentTypes();
        pspChannelPaymentTypes.setPaymentTypeList(List.of("paymentType"));
        String requestId = UUID.randomUUID().toString();
        // when
        PspChannelPaymentTypes response = restClient.createChannelPaymentType(pspChannelPaymentTypes, channelCode, requestId);

        //then
        assertNotNull(response);
        assertFalse(response.getPaymentTypeList().isEmpty());


    }

    @Test
    void createChannelPaymentType_fullyEmpty() {
        // given
        TestCase testCase = TestCase.EMPTY_RESULT;
        String channelCode = testCasePspChannelPaymentTypesMap.get(testCase);
        PspChannelPaymentTypes pspChannelPaymentTypes = new PspChannelPaymentTypes();
        pspChannelPaymentTypes.setPaymentTypeList(List.of("paymentType"));
        String requestId = UUID.randomUUID().toString();
        // when
        PspChannelPaymentTypes response = restClient.createChannelPaymentType(pspChannelPaymentTypes, channelCode, requestId);

        //then
        assertNotNull(response);
        assertTrue(response.getPaymentTypeList().isEmpty());


    }

    @Test
    void getPaymentTypes_fullyValued() {
        // given
        String requestId = UUID.randomUUID().toString();
        // when
        PaymentTypes response = restClient.getPaymentTypes(requestId);

        //then
        assertNotNull(response);
        assertFalse(response.getPaymentTypeList().isEmpty());
    }

    @Test
    void deleteChannelPaymentType_fullyValued() {
        // given
        TestCase testCase = TestCase.FULLY_VALUED;
        String requestId = UUID.randomUUID().toString();
        String channelCode = testCaseChannelCodeMPspCodeMap.get(testCase).get("channelCode");
        String pspCode = testCaseChannelCodeMPspCodeMap.get(testCase).get("pspCode");
        // when
        PspChannelPaymentTypes response = restClient.deleteChannelPaymentType(channelCode, pspCode, requestId);

        //then
        assertNotNull(response);

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
