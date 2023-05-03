package it.pagopa.selfcare.pagopa.backoffice.web.model.channels;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.lang.annotation.Annotation;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static it.pagopa.selfcare.pagopa.TestUtils.mockInstance;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class WrapperChannelDetailsResourceTest {
    private Validator validator;


    @BeforeEach
    void setUp() {
        ValidatorFactory validatorFactory = Validation.buildDefaultValidatorFactory();
        validator = validatorFactory.getValidator();
    }

    @Test
    void validateNullFields() {
        //given
        HashMap<String, Class<? extends Annotation>> toCheckMap = new HashMap<>();
        toCheckMap.put("password", NotBlank.class);
        toCheckMap.put("protocol", NotNull.class);
        toCheckMap.put("port", NotNull.class);
        toCheckMap.put("brokerPspCode", NotBlank.class);
        toCheckMap.put("proxyPort", Min.class);
        toCheckMap.put("proxyPassword", NotNull.class);
        toCheckMap.put("threadNumber", NotNull.class);
        toCheckMap.put("timeoutA", NotNull.class);
        toCheckMap.put("timeoutB", NotNull.class);
        toCheckMap.put("timeoutC", NotNull.class);
        toCheckMap.put("redirectPort", Min.class);
        toCheckMap.put("channelList", NotNull.class);
        toCheckMap.put("agid", NotNull.class);
        toCheckMap.put("onUs", NotNull.class);
        toCheckMap.put("recovery", NotNull.class);
        toCheckMap.put("rtPush", NotNull.class);
        toCheckMap.put("cardChart", NotNull.class);
        toCheckMap.put("digitalStampBrand", NotNull.class);
        toCheckMap.put("paymentModel", NotNull.class);
        toCheckMap.put("channelCode", NotBlank.class);
        toCheckMap.put("flagIo", NotBlank.class);
        toCheckMap.put("servPlugin", NotBlank.class);
        toCheckMap.put("redirectProtocol", NotNull.class);
        toCheckMap.put("redirectQueryString", NotBlank.class);
        toCheckMap.put("id", NotNull.class);
        toCheckMap.put("type", NotBlank.class);
        toCheckMap.put("createdAt", NotBlank.class);
        toCheckMap.put("modifiedAt", NotBlank.class);
        toCheckMap.put("modifiedBy", NotNull.class);
        toCheckMap.put("modifiedByOpt", NotBlank.class);
        toCheckMap.put("note", NotBlank.class);

        WrapperChannelDetailsResource model = new WrapperChannelDetailsResource();

        //when
        Set<ConstraintViolation<Object>> violations = validator.validate(model);
        // then
        assertFalse(violations.isEmpty());
        List<ConstraintViolation<Object>> filteredViolations = violations.stream()
                .filter(violation -> {
                    Class<? extends Annotation> annotationToCheck = toCheckMap.get(violation.getPropertyPath().toString());
                    return !violation.getConstraintDescriptor().getAnnotation().annotationType().equals(annotationToCheck);
                })
                .collect(Collectors.toList());
        assertTrue(filteredViolations.isEmpty());
    }

    @Test
    void validateNotNullFields() {
        // given
        WrapperChannelDetailsResource wrapperChannelDetailsResource = mockInstance(new WrapperChannelDetailsResource());

        // when
        Set<ConstraintViolation<Object>> violations = validator.validate(wrapperChannelDetailsResource);

        // then
        assertTrue(violations.isEmpty());
    }
}
