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

class InstitutionResourceTest {
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
        toCheckMap.put("mailAddress", NotBlank.class);
        toCheckMap.put("address", NotBlank.class);
        toCheckMap.put("zipCode", NotBlank.class);
        toCheckMap.put("fiscalCode", NotBlank.class);
        toCheckMap.put("institutionType", NotBlank.class);
        toCheckMap.put("origin", NotBlank.class);
        toCheckMap.put("originId", NotBlank.class);
        toCheckMap.put("status", NotBlank.class);
        toCheckMap.put("userRole", NotBlank.class);
        toCheckMap.put("userProductRoles", NotNull.class);

        InstitutionResource model = new InstitutionResource();
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
        InstitutionResource institutionResource = mockInstance(new InstitutionResource());
        institutionResource.setUserProductRoles(Set.of("string"));

        // when
        Set<ConstraintViolation<Object>> violations = validator.validate(institutionResource);
        // then
        assertTrue(violations.isEmpty());
    }
    
}
