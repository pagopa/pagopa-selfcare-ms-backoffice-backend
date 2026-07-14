package it.pagopa.selfcare.pagopa.backoffice.util;

import it.pagopa.selfcare.pagopa.backoffice.model.iban.IbanOperation;
import it.pagopa.selfcare.pagopa.backoffice.model.iban.IbanOperationType;
import javax.validation.ConstraintValidatorContext;
import javax.validation.ConstraintValidatorContext.ConstraintViolationBuilder;
import javax.validation.ConstraintValidatorContext.ConstraintViolationBuilder.NodeBuilderCustomizableContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ValidityDateRequiredForCreateValidatorTest {

    private ValidityDateRequiredForCreateValidator validator;

    @Mock
    private ConstraintValidatorContext context;

    @Mock
    private ConstraintViolationBuilder violationBuilder;

    @Mock
    private NodeBuilderCustomizableContext nodeBuilder;

    @BeforeEach
    void setUp() {
        validator = new ValidityDateRequiredForCreateValidator();
    }

    @Test
    @DisplayName("Should return true when target value is null (standard JSR behavior)")
    void isValid_NullValue_ReturnsTrue() {
        boolean result = validator.isValid(null, context);
        assertThat(result).isTrue();
        verifyNoInteractions(context);
    }

    @Test
    @DisplayName("Should return true for CREATE operation with a valid validity date")
    void isValid_CreateWithDate_ReturnsTrue() {
        IbanOperation op = IbanOperation.builder()
                .type(IbanOperationType.CREATE)
                .validityDate("2026-12-31")
                .build();

        boolean result = validator.isValid(op, context);

        assertThat(result).isTrue();
        verifyNoInteractions(context);
    }

    @Test
    @DisplayName("Should return false for CREATE operation when validity date is missing")
    void isValid_CreateWithoutDate_ReturnsFalse() {
        IbanOperation op = IbanOperation.builder()
                .type(IbanOperationType.CREATE)
                .validityDate(null)
                .build();

        when(context.buildConstraintViolationWithTemplate(anyString())).thenReturn(violationBuilder);
        when(violationBuilder.addPropertyNode(anyString())).thenReturn(nodeBuilder);

        boolean result = validator.isValid(op, context);

        assertThat(result).isFalse();
        verify(context).disableDefaultConstraintViolation();
        verify(context).buildConstraintViolationWithTemplate("validityDate is required for CREATE");
        verify(violationBuilder).addPropertyNode("validityDate");
        verify(nodeBuilder).addConstraintViolation();
    }

    @Test
    @DisplayName("Should return true for UPDATE/DELETE operation without validity date")
    void isValid_UpdateWithoutDate_ReturnsTrue() {
        IbanOperation op = IbanOperation.builder()
                .type(IbanOperationType.UPDATE)
                .validityDate(null)
                .build();

        boolean result = validator.isValid(op, context);

        assertThat(result).isTrue();
        verifyNoInteractions(context);
    }

    @Test
    @DisplayName("Should return false for UPDATE/DELETE operation if a validity date is provided")
    void isValid_UpdateWithDate_ReturnsFalse() {
        IbanOperation op = IbanOperation.builder()
                .type(IbanOperationType.UPDATE)
                .validityDate("2026-12-31")
                .build();

        when(context.buildConstraintViolationWithTemplate(anyString())).thenReturn(violationBuilder);
        when(violationBuilder.addPropertyNode(anyString())).thenReturn(nodeBuilder);

        boolean result = validator.isValid(op, context);

        assertThat(result).isFalse();
        verify(context).disableDefaultConstraintViolation();
        verify(context).buildConstraintViolationWithTemplate("validityDate is not allowed for UPDATE or DELETE");
        verify(violationBuilder).addPropertyNode("validityDate");
        verify(nodeBuilder).addConstraintViolation();
    }
}