package it.pagopa.selfcare.pagopa.backoffice.web.model.institutions;

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
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class InstitutionDetailResourceTest {

    private Validator validator;


    @BeforeEach
    void setUp() {
        ValidatorFactory validatorFactory = Validation.buildDefaultValidatorFactory();
        validator = validatorFactory.getValidator();
    }

    @Test
    void validateNullFields(){
        //given
        HashMap<String, Class<? extends Annotation>> toCheckMap = new HashMap<>();
        toCheckMap.put("id", NotBlank.class);
        toCheckMap.put("description", NotBlank.class);
        toCheckMap.put("externalId", NotBlank.class);
        toCheckMap.put("digitalAddress", NotBlank.class);
        toCheckMap.put("address", NotBlank.class);
        toCheckMap.put("zipCode", NotBlank.class);
        toCheckMap.put("taxCode", NotBlank.class);
        toCheckMap.put("origin", NotBlank.class);
        toCheckMap.put("originId", NotBlank.class);
        toCheckMap.put("institutionType", NotNull.class);
        toCheckMap.put("attributes", NotNull.class);

        InstitutionDetailResource model = new InstitutionDetailResource();
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
        InstitutionDetailResource institutionResource = mockInstance(new InstitutionDetailResource());
        institutionResource.setAttributes(List.of(mockInstance(new AttributeResource())));

        // when
        Set<ConstraintViolation<Object>> violations = validator.validate(institutionResource);
        // then
        assertTrue(violations.isEmpty());
    }

}
