package it.pagopa.selfcare.pagopa.backoffice.web.model.institutions;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.util.Set;

import static it.pagopa.selfcare.pagopa.TestUtils.mockInstance;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CompanyInformationsResourceTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        ValidatorFactory validatorFactory = Validation.buildDefaultValidatorFactory();
        validator = validatorFactory.getValidator();
    }

    @Test
    void validateNotNullFields() {
        // given
        CompanyInformationsResource companyInformationsResource = mockInstance(new CompanyInformationsResource());
        // when
        Set<ConstraintViolation<Object>> violations = validator.validate(companyInformationsResource);
        // then
        assertTrue(violations.isEmpty());
    }

}
