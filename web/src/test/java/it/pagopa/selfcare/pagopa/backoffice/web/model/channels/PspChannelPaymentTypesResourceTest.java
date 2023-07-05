package it.pagopa.selfcare.pagopa.backoffice.web.model.channels;

import it.pagopa.selfcare.pagopa.backoffice.connector.model.PageInfo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.lang.annotation.Annotation;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static it.pagopa.selfcare.pagopa.TestUtils.mockInstance;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PspChannelPaymentTypesResourceTest {

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
       // toCheckMap.put("paymentTypeList", NotNull.class);

        PspChannelPaymentTypesResource model = new PspChannelPaymentTypesResource();

        //when
        Set<ConstraintViolation<Object>> violations = validator.validate(model);
        // then
        assertTrue(violations.isEmpty());
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
        PspChannelPaymentTypesResource pspChannelPaymentTypesResource = mockInstance(new PspChannelPaymentTypesResource());
        pspChannelPaymentTypesResource.setPaymentTypeList(List.of("paymentType"));

        // when
        Set<ConstraintViolation<Object>> violations = validator.validate(pspChannelPaymentTypesResource);
        // then
        assertTrue(violations.isEmpty());
    }

}
